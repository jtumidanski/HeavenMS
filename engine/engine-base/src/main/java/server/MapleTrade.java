package server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import client.MapleCharacter;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.manipulator.MapleInventoryManipulator;
import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import config.YamlConfig;
import constants.MapleInventoryType;
import net.server.coordinator.world.MapleInviteCoordinator;
import tools.PacketCreator;
import tools.Pair;
import tools.packet.character.interaction.GetTradeResult;
import tools.packet.character.interaction.TradeConfirmation;

public class MapleTrade {

   private Optional<MapleTrade> partnerTrade = Optional.empty();
   private List<Item> items = new ArrayList<>();
   private List<Item> exchangeItems;
   private int meso = 0;
   private int exchangeMeso;
   private AtomicBoolean locked = new AtomicBoolean(false);
   private MapleCharacter owner;
   private byte number;
   private boolean fullTrade = false;

   public MapleTrade(byte number, MapleCharacter owner) {
      this.owner = owner;
      this.number = number;
   }

   public void lockTrade() {
      getPartnerTrade().ifPresent(partner -> {
         locked.set(true);
         PacketCreator.announce(partner.getOwner(), new TradeConfirmation());
      });
   }

   public void fetchExchangedItems() {
      getPartnerTrade().ifPresent(partner -> {
         exchangeItems = partner.getItems();
         exchangeMeso = partner.getMeso();
      });
   }

   public void clear() {
      meso = 0;
      if (items != null) {
         items.clear();
      }
      exchangeMeso = 0;
      if (exchangeItems != null) {
         exchangeItems.clear();
      }
   }

   public void cancel(byte result) {
      boolean show = YamlConfig.config.server.USE_DEBUG;

      items.forEach(item -> MapleInventoryManipulator.addFromDrop(owner.getClient(), item, show));
      if (meso > 0) {
         owner.gainMeso(meso, show, true, show);
      }

      clear();
      PacketCreator.announce(owner, new GetTradeResult(number, result));
   }

   public boolean isLocked() {
      return locked.get();
   }

   public int getMeso() {
      return meso;
   }

   public void setMeso(int meso) {
      if (locked.get()) {
         throw new RuntimeException("Trade is locked.");
      }
      if (meso < 0) {
         LoggerUtil
               .printError(LoggerOriginator.ENGINE, LogType.EXPLOITS, "[Hack] " + owner.getName() + " Trying to trade < 0 mesos");
         return;
      }
      if (owner.getMeso() >= meso) {
         owner.gainMeso(-meso, false, true, false);
         this.meso += meso;
      }
   }

   public boolean addItem(Item item) {
      synchronized (items) {
         if (items.size() > 9) {
            return false;
         }
         boolean alreadyAtPosition = items.stream().anyMatch(it -> it.position() == item.position());
         if (alreadyAtPosition) {
            return false;
         }

         items.add(item);
      }

      return true;
   }

   public Optional<MapleTrade> getPartnerTrade() {
      return partnerTrade;
   }

   public Optional<MapleCharacter> getPartnerCharacter() {
      return partnerTrade.map(MapleTrade::getOwner);
   }

   public void setPartnerTrade(MapleTrade partnerTrade) {
      if (locked.get()) {
         return;
      }
      this.partnerTrade = Optional.ofNullable(partnerTrade);
   }

   public MapleCharacter getOwner() {
      return owner;
   }

   public List<Item> getItems() {
      return new LinkedList<>(items);
   }

   public int getExchangeMesos() {
      return exchangeMeso;
   }

   public List<Item> getExchangeItems() {
      return new ArrayList<>(exchangeItems);
   }

   public byte getNumber() {
      return number;
   }

   public boolean fitsMeso() {
      return owner.canHoldMeso(exchangeMeso - MapleTradeUtil.getFee(exchangeMeso));
   }

   public boolean fitsInInventory() {
      List<Pair<Item, MapleInventoryType>> tradeItems = exchangeItems.stream()
            .map(item -> new Pair<>(item, item.inventoryType()))
            .collect(Collectors.toList());
      return MapleInventory.checkSpotsAndOwnership(owner, tradeItems);
   }

   public boolean fitsUniquesInInventory() {
      List<Integer> exchangeItemIds = exchangeItems.stream().map(Item::id).collect(Collectors.toList());
      return owner.canHoldUniques(exchangeItemIds);
   }

   public synchronized boolean checkTradeCompleteHandshake(boolean updateSelf) {
      MapleTrade self, other;

      if (updateSelf) {
         self = this;
         other = this.getPartnerTrade().orElseThrow();
      } else {
         self = this.getPartnerTrade().orElseThrow();
         other = this;
      }

      if (self.isLocked()) {
         return false;
      }

      self.lockTrade();
      return other.isLocked();
   }

   public synchronized void tradeCancelHandshake(boolean updateSelf, byte result) {
      byte selfResult, partnerResult;
      MapleTrade self;

      byte[] pairedResult = tradeResultsPair(result);
      selfResult = pairedResult[0];
      partnerResult = pairedResult[1];

      if (updateSelf) {
         self = this;
      } else {
         self = this.getPartnerTrade().orElseThrow();
      }

      cancelTradeInternal(self.getOwner(), selfResult, partnerResult);
   }

   public boolean isFullTrade() {
      return fullTrade;
   }

   public void setFullTrade(boolean fullTrade) {
      this.fullTrade = fullTrade;
   }

   private byte[] tradeResultsPair(byte result) {
      byte selfResult, partnerResult;

      if (result == MapleTradeResult.PARTNER_CANCEL.getValue()) {
         partnerResult = result;
         selfResult = MapleTradeResult.NO_RESPONSE.getValue();
      } else if (result == MapleTradeResult.UNSUCCESSFUL_UNIQUE_ITEM_LIMIT.getValue()) {
         partnerResult = MapleTradeResult.UNSUCCESSFUL.getValue();
         selfResult = result;
      } else {
         partnerResult = result;
         selfResult = result;
      }

      return new byte[]{selfResult, partnerResult};
   }

   private void cancelTradeInternal(MapleCharacter chr, byte selfResult, byte partnerResult) {
      if (chr.getTrade().isEmpty()) {
         return;
      }

      MapleTrade trade = chr.getTrade().get();
      trade.cancel(selfResult);
      if (trade.getPartnerTrade().isPresent()) {
         MapleTrade tradePartner = trade.getPartnerTrade().get();
         tradePartner.cancel(partnerResult);
         tradePartner.getOwner().setTrade(null);
         MapleInviteCoordinator
               .answerInvite(MapleInviteCoordinator.InviteType.TRADE, trade.getOwner().getId(), tradePartner.getOwner().getId(),
                     false);
         MapleInviteCoordinator
               .answerInvite(MapleInviteCoordinator.InviteType.TRADE, tradePartner.getOwner().getId(), trade.getOwner().getId(),
                     false);
      }
      chr.setTrade(null);
   }
}