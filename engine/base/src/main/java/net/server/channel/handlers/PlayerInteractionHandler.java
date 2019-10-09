/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package net.server.channel.handlers;

import java.awt.Point;
import java.util.Arrays;

import client.MapleCharacter;
import client.MapleClient;
import client.autoban.AutobanFactory;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.inventory.manipulator.MapleKarmaManipulator;
import client.processor.ItemProcessor;
import constants.GameConstants;
import constants.ItemConstants;
import constants.ServerConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.ChatPlayerInteraction;
import net.server.channel.packet.interaction.AddItemPlayerInteractionPacket;
import net.server.channel.packet.interaction.AnswerTiePlayerInteractionPacket;
import net.server.channel.packet.interaction.BanPlayerPlayerInteractionPacket;
import net.server.channel.packet.interaction.BaseCreatePlayerInteractionPacket;
import net.server.channel.packet.interaction.BasePlayerInteractionPacket;
import net.server.channel.packet.interaction.BuyPlayerInteractionPacket;
import net.server.channel.packet.interaction.CancelExitAfterGamePlayerInteractionPacket;
import net.server.channel.packet.interaction.CloseMerchantPlayerInteractionPacket;
import net.server.channel.packet.interaction.ConfirmPlayerInteractionPacket;
import net.server.channel.packet.interaction.CreateMatchCardPlayerInteractionPacket;
import net.server.channel.packet.interaction.CreateOmokPlayerInteractionPacket;
import net.server.channel.packet.interaction.CreateShopPlayerInteractionPacket;
import net.server.channel.packet.interaction.DeclinePlayerInteractionPacket;
import net.server.channel.packet.interaction.ExitAfterGamePlayerInteractionPacket;
import net.server.channel.packet.interaction.ExpelPlayerInteractionPacket;
import net.server.channel.packet.interaction.ForfeitPlayerInteractionPacket;
import net.server.channel.packet.interaction.InvitePlayerInteractionPacket;
import net.server.channel.packet.interaction.MerchantMaintenanceOffPlayerInteractionPacket;
import net.server.channel.packet.interaction.MerchantOrganizePlayerInteractionPacket;
import net.server.channel.packet.interaction.MesoMerchantPlayerInteractionPacket;
import net.server.channel.packet.interaction.OmokMovePlayerInteractionPacket;
import net.server.channel.packet.interaction.OpenCashPlayerInteractionPacket;
import net.server.channel.packet.interaction.OpenStorePlayerInteractionPacket;
import net.server.channel.packet.interaction.ReadyPlayerInteractionPacket;
import net.server.channel.packet.interaction.RemoveItemPlayerInteractionPacket;
import net.server.channel.packet.interaction.RequestTiePlayerInteractionPacket;
import net.server.channel.packet.interaction.SelectCardPlayerInteractionPacket;
import net.server.channel.packet.interaction.SetItemsPlayerInteractionPacket;
import net.server.channel.packet.interaction.SetMesoPlayerInteractionPacket;
import net.server.channel.packet.interaction.SkipPlayerInteractionPacket;
import net.server.channel.packet.interaction.StartPlayerInteractionPacket;
import net.server.channel.packet.interaction.TakeItemBackPlayerInteractionPacket;
import net.server.channel.packet.interaction.UnReadyPlayerInteractionPacket;
import net.server.channel.packet.interaction.VisitPlayerInteractionPacket;
import net.server.channel.packet.reader.PlayerInteractionReader;
import server.MapleItemInformationProvider;
import server.MapleTrade;
import server.MapleTradeResult;
import server.channel.PlayerInteractionAction;
import server.maps.FieldLimit;
import server.maps.MapleHiredMerchant;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleMiniGame;
import server.maps.MapleMiniGame.MiniGameType;
import server.maps.MaplePlayerShop;
import server.maps.MaplePlayerShopItem;
import server.maps.MaplePortal;
import tools.FilePrinter;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.MiniRoomError;
import tools.packet.character.box.AddMatchCard;
import tools.packet.character.box.AddOmokBox;
import tools.packet.character.box.UpdatePlayerShopBox;
import tools.packet.character.interaction.GetHiredMerchant;
import tools.packet.character.interaction.GetMatchCardStart;
import tools.packet.character.interaction.GetMiniGameDenyTie;
import tools.packet.character.interaction.GetMiniGameReady;
import tools.packet.character.interaction.GetMiniGameRequestTie;
import tools.packet.character.interaction.GetMiniGameSkipOwner;
import tools.packet.character.interaction.GetMiniGameSkipVisitor;
import tools.packet.character.interaction.GetMiniGameStart;
import tools.packet.character.interaction.GetMiniGameUnReady;
import tools.packet.character.interaction.GetMiniRoomError;
import tools.packet.character.interaction.MatchCardSelect;
import tools.packet.character.interaction.MerchantOwnerMaintenanceLeave;
import tools.packet.character.interaction.MiniGameClose;
import tools.packet.character.interaction.PlayerShopItemUpdate;
import tools.packet.character.interaction.TradeItemAdd;
import tools.packet.character.interaction.UpdateHiredMerchant;
import tools.packet.shop.UpdateHiredMerchantBox;
import tools.packet.spawn.SpawnHiredMerchant;
import tools.packet.stat.EnableActions;

/**
 * @author Matze
 * @author Ronan - concurrency safety & reviewed minigames
 */
public final class PlayerInteractionHandler extends AbstractPacketHandler<BasePlayerInteractionPacket> {
   @Override
   public Class<PlayerInteractionReader> getReaderClass() {
      return PlayerInteractionReader.class;
   }

   private int establishMiniroomStatus(MapleCharacter chr, boolean isMinigame) {
      if (isMinigame && FieldLimit.CANNOTMINIGAME.check(chr.getMap().getFieldLimit())) {
         return 11;
      }

      if (chr.getChalkboard() != null) {
         return 13;
      }

      if (chr.getEventInstance() != null) {
         return 5;
      }

      return 0;
   }

   private boolean isTradeOpen(MapleCharacter chr) {
      if (chr.getTrade() != null) {   // thanks to Rien dev team
         //Apparently there is a dupe exploit that causes racing conditions when saving/retrieving from the db with stuff like trade open.
         PacketCreator.announce(chr, new EnableActions());
         return true;
      }

      return false;
   }

   @Override
   public void handlePacket(BasePlayerInteractionPacket packet, MapleClient client) {
      if (!client.tryAcquireClient()) {    // thanks GabrielSin for pointing dupes within player interactions
         PacketCreator.announce(client, new EnableActions());
         return;
      }

      try {
         final MapleCharacter chr = client.getPlayer();
         if (packet instanceof BaseCreatePlayerInteractionPacket) {
            createAction(client, chr, (BaseCreatePlayerInteractionPacket) packet);
         } else if (packet instanceof InvitePlayerInteractionPacket) {
            inviteAction(chr, ((InvitePlayerInteractionPacket) packet).otherCharacterId());
         } else if (packet instanceof DeclinePlayerInteractionPacket) {
            declineAction(chr);
         } else if (packet instanceof VisitPlayerInteractionPacket) {
            visitAction(client, chr, ((VisitPlayerInteractionPacket) packet).objectId(), ((VisitPlayerInteractionPacket) packet).password());
         } else if (packet instanceof ChatPlayerInteraction) { // chat lol
            chatAction(client, chr, ((ChatPlayerInteraction) packet).message());
         } else if (packet.mode() == PlayerInteractionAction.EXIT.getValue()) {
            exitAction(chr);
         } else if (packet instanceof OpenStorePlayerInteractionPacket) {
            openStoreAction(client, packet.mode(), chr);
         } else if (packet instanceof OpenCashPlayerInteractionPacket) {
            openCashAction(client, packet.mode(), chr, ((OpenCashPlayerInteractionPacket) packet).birthday());
         } else if (packet instanceof ReadyPlayerInteractionPacket) {
            readyAction(chr);
         } else if (packet instanceof UnReadyPlayerInteractionPacket) {
            unReadyAction(chr);
         } else if (packet instanceof StartPlayerInteractionPacket) {
            startAction(chr);
         } else if (packet instanceof ForfeitPlayerInteractionPacket) {
            forfeitAction(chr);
         } else if (packet instanceof RequestTiePlayerInteractionPacket) {
            requestTieAction(chr);
         } else if (packet instanceof AnswerTiePlayerInteractionPacket) {
            answerTieAction(chr, ((AnswerTiePlayerInteractionPacket) packet).answer());
         } else if (packet instanceof SkipPlayerInteractionPacket) {
            skipAction(chr);
         } else if (packet instanceof OmokMovePlayerInteractionPacket) {
            omokMoveAction(chr, ((OmokMovePlayerInteractionPacket) packet).x(),
                  ((OmokMovePlayerInteractionPacket) packet).y(),
                  ((OmokMovePlayerInteractionPacket) packet).theType());
         } else if (packet instanceof SelectCardPlayerInteractionPacket) {
            selectCardAction(chr, ((SelectCardPlayerInteractionPacket) packet).turn(), ((SelectCardPlayerInteractionPacket) packet).slot());
         } else if (packet instanceof SetMesoPlayerInteractionPacket) {
            setMesoAction(chr, ((SetMesoPlayerInteractionPacket) packet).amount());
         } else if (packet instanceof SetItemsPlayerInteractionPacket) {
            setItemsAction(client, chr, ((SetItemsPlayerInteractionPacket) packet).slotType(),
                  ((SetItemsPlayerInteractionPacket) packet).position(),
                  ((SetItemsPlayerInteractionPacket) packet).quantity(),
                  ((SetItemsPlayerInteractionPacket) packet).targetSlot());
         } else if (packet instanceof ConfirmPlayerInteractionPacket) {
            confirmAction(chr);
         } else if (packet instanceof AddItemPlayerInteractionPacket) {
            addItemAction(client, chr, ((AddItemPlayerInteractionPacket) packet).slotType(),
                  ((AddItemPlayerInteractionPacket) packet).slot(),
                  ((AddItemPlayerInteractionPacket) packet).bundles(),
                  ((AddItemPlayerInteractionPacket) packet).perBundle(),
                  ((AddItemPlayerInteractionPacket) packet).price());
         } else if (packet instanceof RemoveItemPlayerInteractionPacket) {
            removeItemAction(client, chr, ((RemoveItemPlayerInteractionPacket) packet).slot());
         } else if (packet instanceof MesoMerchantPlayerInteractionPacket) {
            merchantMesoAction(chr);
         } else if (packet instanceof MerchantOrganizePlayerInteractionPacket) {
            merchantOrganizeAction(client, chr);
         } else if (packet instanceof BuyPlayerInteractionPacket) {
            buyAction(client, chr, ((BuyPlayerInteractionPacket) packet).itemId(), ((BuyPlayerInteractionPacket) packet).quantity());
         } else if (packet instanceof TakeItemBackPlayerInteractionPacket) {
            takeItemBackAction(client, chr, ((TakeItemBackPlayerInteractionPacket) packet).slot());
         } else if (packet instanceof CloseMerchantPlayerInteractionPacket) {
            closeMerchantAction(chr);
         } else if (packet instanceof MerchantMaintenanceOffPlayerInteractionPacket) {
            maintenanceOffAction(client, chr);
         } else if (packet instanceof BanPlayerPlayerInteractionPacket) {
            banPlayerAction(chr, ((BanPlayerPlayerInteractionPacket) packet).name());
         } else if (packet instanceof ExpelPlayerInteractionPacket) {
            expelAction(chr);
         } else if (packet instanceof ExitAfterGamePlayerInteractionPacket) {
            exitAfterGameAction(chr);
         } else if (packet instanceof CancelExitAfterGamePlayerInteractionPacket) {
            cancelAfterGameAction(chr);
         }
      } finally {
         client.releaseClient();
      }
   }

   private void cancelAfterGameAction(MapleCharacter chr) {
      MapleMiniGame miniGame = chr.getMiniGame();
      if (miniGame != null) {
         miniGame.setQuitAfterGame(chr, false);
      }
   }

   private void exitAfterGameAction(MapleCharacter chr) {
      MapleMiniGame miniGame = chr.getMiniGame();
      if (miniGame != null) {
         miniGame.setQuitAfterGame(chr, true);
      }
   }

   private void expelAction(MapleCharacter chr) {
      MapleMiniGame miniGame = chr.getMiniGame();
      if (miniGame != null && miniGame.isOwner(chr)) {
         MapleCharacter visitor = miniGame.getVisitor();

         if (visitor != null) {
            visitor.closeMiniGame(false);
            PacketCreator.announce(visitor, new MiniGameClose(true, 5));
         }
      }
   }

   private void banPlayerAction(MapleCharacter chr, String name) {
      MaplePlayerShop shop = chr.getPlayerShop();
      if (shop != null && shop.isOwner(chr)) {
         shop.banPlayer(name);
      }
   }

   private void maintenanceOffAction(MapleClient c, MapleCharacter chr) {
      if (isTradeOpen(chr)) {
         return;
      }

      MapleHiredMerchant merchant = chr.getHiredMerchant();
      if (merchant != null) {
         if (merchant.isOwner(chr)) {
            if (merchant.getItems().isEmpty()) {
               merchant.closeOwnerMerchant(chr);
            } else {
               merchant.clearMessages();
               merchant.setOpen(true);
               MasterBroadcaster.getInstance().sendToAllInMap(merchant.getMap(), new UpdateHiredMerchantBox(merchant.getOwnerId(), merchant.getObjectId(), merchant.getDescription(), merchant.getItemId(), merchant.getShopRoomInfo()));
            }
         }
      }

      chr.setHiredMerchant(null);
      PacketCreator.announce(c, new EnableActions());
   }

   private void closeMerchantAction(MapleCharacter chr) {
      if (isTradeOpen(chr)) {
         return;
      }

      MapleHiredMerchant merchant = chr.getHiredMerchant();
      if (merchant != null) {
         merchant.closeOwnerMerchant(chr);
      }
   }

   private void takeItemBackAction(MapleClient c, MapleCharacter chr, int slot) {
      if (isTradeOpen(chr)) {
         return;
      }

      MapleHiredMerchant merchant = chr.getHiredMerchant();
      if (merchant != null && merchant.isOwner(chr)) {
         if (merchant.isOpen()) {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You can't take it with the store open.");
            return;
         }

         if (slot >= merchant.getItems().size() || slot < 0) {
            AutobanFactory.PACKET_EDIT.alert(chr, chr.getName() + " tried to packet edit with a hired merchant.");
            FilePrinter.printError(FilePrinter.EXPLOITS + chr.getName() + ".txt", chr.getName() + " tried to remove item at slot " + slot);
            c.disconnect(true, false);
            return;
         }

         merchant.takeItemBack(slot, chr);
      }
   }

   private void buyAction(MapleClient c, MapleCharacter chr, int itemid, short quantity) {
      if (isTradeOpen(chr)) {
         return;
      }

      if (quantity < 1) {
         AutobanFactory.PACKET_EDIT.alert(chr, chr.getName() + " tried to packet edit with a hired merchant and or player shop.");
         FilePrinter.printError(FilePrinter.EXPLOITS + chr.getName() + ".txt", chr.getName() + " tried to buy item " + itemid + " with quantity " + quantity);
         c.disconnect(true, false);
         return;
      }
      MaplePlayerShop shop = chr.getPlayerShop();
      MapleHiredMerchant merchant = chr.getHiredMerchant();
      if (shop != null && shop.isVisitor(chr)) {
         if (shop.buy(c, itemid, quantity)) {
            MasterBroadcaster.getInstance().sendToShop(shop, new PlayerShopItemUpdate(shop));
         }
      } else if (merchant != null && !merchant.isOwner(chr)) {
         merchant.buy(c, itemid, quantity);
         merchant.broadcastToVisitorsThreadsafe(PacketCreator.create(new UpdateHiredMerchant(chr, merchant)));
      }
   }

   private void merchantOrganizeAction(MapleClient c, MapleCharacter chr) {
      MapleHiredMerchant merchant = chr.getHiredMerchant();
      if (merchant == null || !merchant.isOwner(chr)) {
         return;
      }

      merchant.withdrawMesos(chr);
      merchant.clearInexistentItems();

      if (merchant.getItems().isEmpty()) {
         merchant.closeOwnerMerchant(chr);
         return;
      }
      PacketCreator.announce(c, new UpdateHiredMerchant(chr, merchant));
   }

   private void merchantMesoAction(MapleCharacter chr) {
      MapleHiredMerchant merchant = chr.getHiredMerchant();
      if (merchant == null) {
         return;
      }

      merchant.withdrawMesos(chr);
   }

   private void removeItemAction(MapleClient c, MapleCharacter chr, int slot) {
      if (isTradeOpen(chr)) {
         return;
      }

      MaplePlayerShop shop = chr.getPlayerShop();
      if (shop != null && shop.isOwner(chr)) {
         if (shop.isOpen()) {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You can't take it with the store open.");
            return;
         }

         if (slot >= shop.getItems().size() || slot < 0) {
            AutobanFactory.PACKET_EDIT.alert(chr, chr.getName() + " tried to packet edit with a player shop.");
            FilePrinter.printError(FilePrinter.EXPLOITS + chr.getName() + ".txt", chr.getName() + " tried to remove item at slot " + slot);
            c.disconnect(true, false);
            return;
         }

         shop.takeItemBack(slot, chr);
      }
   }

   private void addItemAction(MapleClient c, MapleCharacter chr, byte slotType, short slot, short bundles, short perBundle, int price) {
      if (isTradeOpen(chr)) {
         return;
      }

      MapleInventoryType ivType = MapleInventoryType.getByType(slotType);
      Item ivItem = chr.getInventory(ivType).getItem(slot);

      if (ivItem == null || ItemProcessor.getInstance().isUntradeable(ivItem)) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "Could not perform shop operation with that item.");
         PacketCreator.announce(c, new EnableActions());
         return;
      } else if (MapleItemInformationProvider.getInstance().isUnmerchable(ivItem.id())) {
         if (ItemConstants.isPet(ivItem.id())) {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "Pets are not allowed to be sold on the Player Store.");
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "Cash items are not allowed to be sold on the Player Store.");
         }

         PacketCreator.announce(c, new EnableActions());
         return;
      }

      if (ItemConstants.isRechargeable(ivItem.id())) {
         perBundle = 1;
         bundles = 1;
      } else if (ivItem.quantity() < (bundles * perBundle)) {     // thanks GabrielSin for finding a dupe here
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "Could not perform shop operation with that item.");
         PacketCreator.announce(c, new EnableActions());
         return;
      }

      if (perBundle <= 0 || perBundle * bundles > 2000 || bundles <= 0 || price <= 0 || price > Integer.MAX_VALUE) {
         AutobanFactory.PACKET_EDIT.alert(chr, chr.getName() + " tried to packet edit with hired merchants.");
         FilePrinter.printError(FilePrinter.EXPLOITS + chr.getName() + ".txt", chr.getName() + " might of possibly packet edited Hired Merchants\nperBundle: " + perBundle + "\nperBundle * bundles (This multiplied cannot be greater than 2000): " + perBundle * bundles + "\nbundles: " + bundles + "\nprice: " + price);
         return;
      }

      Item sellItem = ivItem.copy();
      if (!ItemConstants.isRechargeable(ivItem.id())) {
         sellItem.quantity_$eq(perBundle);
      }

      MaplePlayerShopItem shopItem = new MaplePlayerShopItem(sellItem, bundles, price);
      MaplePlayerShop shop = chr.getPlayerShop();
      MapleHiredMerchant merchant = chr.getHiredMerchant();
      if (shop != null && shop.isOwner(chr)) {
         if (shop.isOpen() || !shop.addItem(shopItem)) { // thanks Vcoc for pointing an exploit with unlimited shop slots
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You can't sell it anymore.");
            return;
         }

         if (ItemConstants.isRechargeable(ivItem.id())) {
            MapleInventoryManipulator.removeFromSlot(c, ivType, slot, ivItem.quantity(), true);
         } else {
            MapleInventoryManipulator.removeFromSlot(c, ivType, slot, (short) (bundles * perBundle), true);
         }

         PacketCreator.announce(c, new PlayerShopItemUpdate(shop));
      } else if (merchant != null && merchant.isOwner(chr)) {
         if (ivType.equals(MapleInventoryType.CASH) && merchant.isPublished()) {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "Cash items are only allowed to be sold when first opening the store.");
            return;
         }

         if (merchant.isOpen() || !merchant.addItem(shopItem)) { // thanks Vcoc for pointing an exploit with unlimited shop slots
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You can't sell it anymore.");
            return;
         }

         if (ItemConstants.isRechargeable(ivItem.id())) {
            MapleInventoryManipulator.removeFromSlot(c, ivType, slot, ivItem.quantity(), true);
         } else {
            MapleInventoryManipulator.removeFromSlot(c, ivType, slot, (short) (bundles * perBundle), true);
         }

         PacketCreator.announce(c, new UpdateHiredMerchant(chr, merchant));

         if (ServerConstants.USE_ENFORCE_MERCHANT_SAVE) {
            chr.saveCharToDB(false);
         }

         merchant.saveItems(false);   // thanks Masterrulax for realizing yet another dupe with merchants/Fredrick
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You can't sell without owning a shop.");
      }
   }

   private void confirmAction(MapleCharacter chr) {
      MapleTrade.completeTrade(chr);
   }

   private void setItemsAction(MapleClient c, MapleCharacter chr, byte slotType, short pos, short quantity, byte targetSlot) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      MapleInventoryType ivType = MapleInventoryType.getByType(slotType);
      Item item = chr.getInventory(ivType).getItem(pos);

      if (targetSlot < 1 || targetSlot > 9) {
         System.out.println("[Hack] " + chr.getName() + " Trying to dupe on trade slot.");
         PacketCreator.announce(c, new EnableActions());
         return;
      }

      if (item == null) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "Invalid item description.");
         PacketCreator.announce(c, new EnableActions());
         return;
      }

      if (ii.isUnmerchable(item.id())) {
         if (ItemConstants.isPet(item.id())) {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "Pets are not allowed to be traded.");
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "Cash items are not allowed to be traded.");
         }

         PacketCreator.announce(c, new EnableActions());
         return;
      }

      if (quantity < 1 || quantity > item.quantity()) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You don't have enough quantity of the item.");
         PacketCreator.announce(c, new EnableActions());
         return;
      }

      MapleTrade trade = chr.getTrade();
      if (trade != null) {
         if ((quantity <= item.quantity() && quantity >= 0) || ItemConstants.isRechargeable(item.id())) {
            if (ii.isDropRestricted(item.id())) { // ensure that undroppable items do not make it to the trade window
               if (!MapleKarmaManipulator.hasKarmaFlag(item)) {
                  MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "That item is untradeable.");
                  PacketCreator.announce(c, new EnableActions());
                  return;
               }
            }

            MapleInventory inv = chr.getInventory(ivType);
            inv.lockInventory();
            try {
               Item checkItem = chr.getInventory(ivType).getItem(pos);
               if (checkItem != item || checkItem.position() != item.position()) {
                  MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "Invalid item description.");
                  PacketCreator.announce(c, new EnableActions());
                  return;
               }

               Item tradeItem = item.copy();
               if (ItemConstants.isRechargeable(item.id())) {
                  quantity = item.quantity();
               }

               tradeItem.quantity_$eq(quantity);
               tradeItem.position_(targetSlot);

               if (trade.addItem(tradeItem)) {
                  MapleInventoryManipulator.removeFromSlot(c, ivType, item.position(), quantity, true);

                  PacketCreator.announce(trade.getChr(), new TradeItemAdd((byte) 0, tradeItem));
                  if (trade.getPartner() != null) {
                     PacketCreator.announce(trade.getPartner().getChr(), new TradeItemAdd((byte) 1, tradeItem));
                  }
               }
            } catch (Exception e) {
               FilePrinter.printError(FilePrinter.TRADE_EXCEPTION, e, "Player '" + chr + "' tried to add " + ii.getName(item.id()) + " qty. " + item.quantity() + " in trade (slot " + targetSlot + ") then exception occurred.");
            } finally {
               inv.unlockInventory();
            }
         }
      }
   }

   private void setMesoAction(MapleCharacter chr, int amount) {
      chr.getTrade().setMeso(amount);
   }

   private void selectCardAction(MapleCharacter chr, int turn, int slot) {
      MapleMiniGame game = chr.getMiniGame();
      int firstslot = game.getFirstSlot();
      if (turn == 1) {
         game.setFirstSlot(slot);
         if (game.isOwner(chr)) {
            MasterBroadcaster.getInstance().sendToGameVisitor(game, new MatchCardSelect(turn, slot, firstslot, turn));
         } else {
            MasterBroadcaster.getInstance().sendToGameOwner(game, new MatchCardSelect(turn, slot, firstslot, turn));
         }
      } else if ((game.getCardId(firstslot)) == (game.getCardId(slot))) {
         if (game.isOwner(chr)) {
            MasterBroadcaster.getInstance().sendToGamers(game, new MatchCardSelect(turn, slot, firstslot, 2));
            game.setOwnerPoints();
         } else {
            MasterBroadcaster.getInstance().sendToGamers(game, new MatchCardSelect(turn, slot, firstslot, 3));
            game.setVisitorPoints();
         }
      } else if (game.isOwner(chr)) {
         MasterBroadcaster.getInstance().sendToGamers(game, new MatchCardSelect(turn, slot, firstslot, 0));
      } else {
         MasterBroadcaster.getInstance().sendToGamers(game, new MatchCardSelect(turn, slot, firstslot, 1));
      }
   }

   private void omokMoveAction(MapleCharacter chr, int x, int y, int type) {
      chr.getMiniGame().setPiece(x, y, type, chr);
   }

   private void skipAction(MapleCharacter chr) {
      MapleMiniGame game = chr.getMiniGame();
      if (game.isOwner(chr)) {
         MasterBroadcaster.getInstance().sendToGamers(game, new GetMiniGameSkipOwner());
      } else {
         MasterBroadcaster.getInstance().sendToGamers(game, new GetMiniGameSkipVisitor());
      }
   }

   private void answerTieAction(MapleCharacter chr, boolean answer) {
      MapleMiniGame game = chr.getMiniGame();
      if (answer) {
         game.minigameMatchDraw();
      } else {
         game.denyTie(chr);

         if (game.isOwner(chr)) {
            MasterBroadcaster.getInstance().sendToGameVisitor(game, new GetMiniGameDenyTie());
         } else {
            MasterBroadcaster.getInstance().sendToGameOwner(game, new GetMiniGameDenyTie());
         }
      }
   }

   private void requestTieAction(MapleCharacter chr) {
      MapleMiniGame game = chr.getMiniGame();
      if (!game.isTieDenied(chr)) {
         if (game.isOwner(chr)) {
            MasterBroadcaster.getInstance().sendToGameVisitor(game, new GetMiniGameRequestTie());
         } else {
            MasterBroadcaster.getInstance().sendToGameOwner(game, new GetMiniGameRequestTie());
         }
      }
   }

   private void forfeitAction(MapleCharacter chr) {
      MapleMiniGame game = chr.getMiniGame();
      if (game.getGameType().equals(MiniGameType.OMOK)) {
         if (game.isOwner(chr)) {
            game.minigameMatchVisitorWins(true);
         } else {
            game.minigameMatchOwnerWins(true);
         }
      } else if (game.getGameType().equals(MiniGameType.MATCH_CARD)) {
         if (game.isOwner(chr)) {
            game.minigameMatchVisitorWins(true);
         } else {
            game.minigameMatchOwnerWins(true);
         }
      }
   }

   private void startAction(MapleCharacter chr) {
      MapleMiniGame game = chr.getMiniGame();
      if (game.getGameType().equals(MiniGameType.OMOK)) {
         game.minigameMatchStarted();
         MasterBroadcaster.getInstance().sendToGamers(game, new GetMiniGameStart(game.getLoser()));
         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new AddOmokBox(game.getOwner(), 2, 1));
      } else if (game.getGameType().equals(MiniGameType.MATCH_CARD)) {
         game.minigameMatchStarted();
         game.shuffleList();
         MasterBroadcaster.getInstance().sendToGamers(game, new GetMatchCardStart(game, game.getLoser()));
         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new AddMatchCard(game.getOwner(), 2, 1));
      }
   }

   private void unReadyAction(MapleCharacter chr) {
      MapleMiniGame game = chr.getMiniGame();
      MasterBroadcaster.getInstance().sendToGamers(game, new GetMiniGameUnReady());
   }

   private void readyAction(MapleCharacter chr) {
      MapleMiniGame game = chr.getMiniGame();
      MasterBroadcaster.getInstance().sendToGamers(game, new GetMiniGameReady());
   }

   private void openStoreAction(MapleClient c, byte mode, MapleCharacter chr) {
      if (isTradeOpen(chr)) {
         return;
      }

      if (!canPlaceStore(chr)) {    // thanks Ari for noticing player shops overlapping on opening time
         return;
      }

      MaplePlayerShop shop = chr.getPlayerShop();
      MapleHiredMerchant merchant = chr.getHiredMerchant();
      if (shop != null && shop.isOwner(chr)) {
         if (ServerConstants.USE_ERASE_PERMIT_ON_OPENSHOP) {
            try {
               MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, shop.getItemId(), 1, true, false);
            } catch (RuntimeException ignored) {
            } // fella does not have a player shop permit...
         }

         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new UpdatePlayerShopBox(shop));
         shop.setOpen(true);
      } else if (merchant != null && merchant.isOwner(chr)) {
         chr.setHasMerchant(true);
         merchant.setOpen(true);
         chr.getMap().addMapObject(merchant);
         chr.setHiredMerchant(null);
         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new SpawnHiredMerchant(merchant));
      }
   }

   private void openCashAction(MapleClient c, byte mode, MapleCharacter chr, int birthday) {
      if (isTradeOpen(chr)) {
         return;
      }

      if (!CashOperationHandler.checkBirthday(c, birthday)) { // birthday check here found thanks to lucasziron
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "Please check again the birthday date.");
         return;
      }

      PacketCreator.announce(c, new MerchantOwnerMaintenanceLeave());

      if (!canPlaceStore(chr)) {    // thanks Ari for noticing player shops overlapping on opening time
         return;
      }

      MaplePlayerShop shop = chr.getPlayerShop();
      MapleHiredMerchant merchant = chr.getHiredMerchant();
      if (shop != null && shop.isOwner(chr)) {
         if (ServerConstants.USE_ERASE_PERMIT_ON_OPENSHOP) {
            try {
               MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, shop.getItemId(), 1, true, false);
            } catch (RuntimeException ignored) {
            } // fella does not have a player shop permit...
         }

         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new UpdatePlayerShopBox(shop));
         shop.setOpen(true);
      } else if (merchant != null && merchant.isOwner(chr)) {
         chr.setHasMerchant(true);
         merchant.setOpen(true);
         chr.getMap().addMapObject(merchant);
         chr.setHiredMerchant(null);
         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new SpawnHiredMerchant(merchant));
      }
   }

   private void exitAction(MapleCharacter chr) {
      if (chr.getTrade() != null) {
         MapleTrade.cancelTrade(chr, MapleTradeResult.PARTNER_CANCEL);
      } else {
         chr.closePlayerShop();
         chr.closeMiniGame(false);
         chr.closeHiredMerchant(true);
      }
   }

   private void chatAction(MapleClient c, MapleCharacter chr, String message) {
      MapleHiredMerchant merchant = chr.getHiredMerchant();
      if (chr.getTrade() != null) {
         chr.getTrade().chat(message);
      } else if (chr.getPlayerShop() != null) { //mini game
         MaplePlayerShop shop = chr.getPlayerShop();
         if (shop != null) {
            shop.chat(c, message);
         }
      } else if (chr.getMiniGame() != null) {
         MapleMiniGame game = chr.getMiniGame();
         if (game != null) {
            game.chat(c, message);
         }
      } else if (merchant != null) {
         merchant.sendMessage(chr, message);
      }
   }

   private void visitAction(MapleClient c, MapleCharacter chr, int oid, String pw) {
      if (chr.getTrade() != null && chr.getTrade().getPartner() != null) {
         if (!chr.getTrade().isFullTrade() && !chr.getTrade().getPartner().isFullTrade()) {
            MapleTrade.visitTrade(chr, chr.getTrade().getPartner().getChr());
         } else {
            PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.FULL_CAPACITY));
         }
      } else {
         if (isTradeOpen(chr)) {
            return;
         }

         MapleMapObject ob = chr.getMap().getMapObject(oid);
         if (ob instanceof MaplePlayerShop) {
            MaplePlayerShop shop = (MaplePlayerShop) ob;
            shop.visitShop(chr);
         } else if (ob instanceof MapleMiniGame) {
            MapleMiniGame game = (MapleMiniGame) ob;
            if (game.checkPassword(pw)) {
               if (game.hasFreeSlot() && !game.isVisitor(chr)) {
                  game.addVisitor(chr);
                  chr.setMiniGame(game);
                  switch (game.getGameType()) {
                     case OMOK:
                        game.sendOmok(c, game.getPieceType());
                        break;
                     case MATCH_CARD:
                        game.sendMatchCard(c, game.getPieceType());
                        break;
                  }
               } else {
                  PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.FULL_CAPACITY));
               }
            } else {
               PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.INCORRECT_PASSWORD));
            }
         } else if (ob instanceof MapleHiredMerchant && chr.getHiredMerchant() == null) {
            MapleHiredMerchant merchant = (MapleHiredMerchant) ob;
            merchant.visitShop(chr);
         }
      }
   }

   private void declineAction(MapleCharacter chr) {
      MapleTrade.declineTrade(chr);
   }

   private void inviteAction(MapleCharacter chr, int otherCid) {
      MapleCharacter other = chr.getMap().getCharacterById(otherCid);
      if (other == null || chr.getId() == other.getId()) {
         return;
      }

      MapleTrade.inviteTrade(chr, other);
   }

   private void createAction(MapleClient c, MapleCharacter chr, BaseCreatePlayerInteractionPacket packet) {
      if (!chr.isAlive()) {    // thanks GabrielSin for pointing this
         PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.CANT_WHILE_DEAD));
         return;
      }

      byte createType = packet.createType();
      if (createType == 3) {  // trade
         MapleTrade.startTrade(chr);
      } else if (createType == 1 && packet instanceof CreateOmokPlayerInteractionPacket) { // omok mini game
         omokMiniGame(c, chr, ((CreateOmokPlayerInteractionPacket) packet).description(),
               ((CreateOmokPlayerInteractionPacket) packet).hasPassword(),
               ((CreateOmokPlayerInteractionPacket) packet).password(),
               ((CreateOmokPlayerInteractionPacket) packet).theType());
      } else if (createType == 2 && packet instanceof CreateMatchCardPlayerInteractionPacket) { // matchcard
         matchcard(c, chr, ((CreateMatchCardPlayerInteractionPacket) packet).description(),
               ((CreateMatchCardPlayerInteractionPacket) packet).hasPassword(),
               ((CreateMatchCardPlayerInteractionPacket) packet).password(),
               ((CreateMatchCardPlayerInteractionPacket) packet).theType());
      } else if ((createType == 4 || createType == 5) && packet instanceof CreateShopPlayerInteractionPacket) { // shop
         shop(c, chr, ((CreateShopPlayerInteractionPacket) packet).description(), ((CreateShopPlayerInteractionPacket) packet).itemId());
      }
   }

   private void shop(MapleClient c, MapleCharacter chr, String description, int itemId) {
      if (!GameConstants.isFreeMarketRoom(chr.getMapId())) {
         PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.STORES_ONLY_IN_FM));
         return;
      }

      int status = establishMiniroomStatus(chr, false);
      if (status > 0) {
         PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.fromValue(status)));
         return;
      }

      if (!canPlaceStore(chr)) {
         return;
      }

      if (chr.getInventory(MapleInventoryType.CASH).countById(itemId) < 1) {
         PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.UNABLE_TO_WITH_CHARACTER));
         return;
      }

      if (ItemConstants.isPlayerShop(itemId)) {
         MaplePlayerShop shop = new MaplePlayerShop(chr, description, itemId);
         chr.setPlayerShop(shop);
         chr.getMap().addMapObject(shop);
         shop.sendShop(c);
         c.getWorldServer().registerPlayerShop(shop);
         //c.announce(MaplePacketCreator.getPlayerShopRemoveVisitor(1));
      } else if (ItemConstants.isHiredMerchant(itemId)) {
         MapleHiredMerchant merchant = new MapleHiredMerchant(chr, description, itemId);
         chr.setHiredMerchant(merchant);
         c.getWorldServer().registerHiredMerchant(merchant);
         chr.getClient().getChannelServer().addHiredMerchant(chr.getId(), merchant);
         PacketCreator.announce(chr, new GetHiredMerchant(chr, merchant, true));
      }
   }

   private void matchcard(MapleClient c, MapleCharacter chr, String description, boolean hasPassword, String password, int type) {
      int status = establishMiniroomStatus(chr, true);
      if (status > 0) {
         PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.fromValue(status)));
         return;
      }

      if (type > 2) {
         type = 2;
      } else if (type < 0) {
         type = 0;
      }
      if (!chr.haveItem(4080100)) {
         PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.UNABLE_TO_WITH_CHARACTER));
         return;
      }

      MapleMiniGame game = new MapleMiniGame(chr, description, password);
      game.setPieceType(type);
      if (type == 0) {
         game.setMatchesToWin(6);
      } else if (type == 1) {
         game.setMatchesToWin(10);
      } else if (type == 2) {
         game.setMatchesToWin(15);
      }
      game.setGameType(MiniGameType.MATCH_CARD);
      chr.setMiniGame(game);
      chr.getMap().addMapObject(game);
      MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new AddMatchCard(chr, 1, 0));
      game.sendMatchCard(c, type);
   }

   private void omokMiniGame(MapleClient c, MapleCharacter chr, String description, boolean hasPassword, String password, int type) {
      int status = establishMiniroomStatus(chr, true);
      if (status > 0) {
         PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.fromValue(status)));
         return;
      }

      if (type > 11) {
         type = 11;
      } else if (type < 0) {
         type = 0;
      }
      if (!chr.haveItem(4080000 + type)) {
         PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.UNABLE_TO_WITH_CHARACTER));
         return;
      }

      MapleMiniGame game = new MapleMiniGame(chr, description, password);
      chr.setMiniGame(game);
      game.setPieceType(type);
      game.setGameType(MiniGameType.OMOK);
      chr.getMap().addMapObject(game);
      MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new AddOmokBox(chr, 1, 0));
      game.sendOmok(c, type);
   }

   private boolean canPlaceStore(MapleCharacter chr) {
      try {
         for (MapleMapObject mmo : chr.getMap().getMapObjectsInRange(chr.getPosition(), 23000, Arrays.asList(MapleMapObjectType.HIRED_MERCHANT, MapleMapObjectType.PLAYER))) {
            if (mmo instanceof MapleCharacter) {
               MapleCharacter mc = (MapleCharacter) mmo;
               if (mc.getId() == chr.getId()) {
                  continue;
               }

               MaplePlayerShop shop = mc.getPlayerShop();
               if (shop != null && shop.isOwner(mc)) {
                  PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.CANT_ESTABLISH_MINI_ROOM));
                  return false;
               }
            } else {
               PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.CANT_ESTABLISH_MINI_ROOM));
               return false;
            }
         }

         Point cpos = chr.getPosition();
         MaplePortal portal = chr.getMap().findClosestTeleportPortal(cpos);
         if (portal != null && portal.getPosition().distance(cpos) < 120.0) {
            PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.NOT_NEAR_PORTAL));
            return false;
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return true;
   }
}
