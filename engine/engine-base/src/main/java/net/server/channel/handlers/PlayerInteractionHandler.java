package net.server.channel.handlers;

import java.awt.Point;
import java.util.Arrays;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.autoban.AutoBanFactory;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.inventory.manipulator.MapleKarmaManipulator;
import client.processor.ItemProcessor;
import config.YamlConfig;
import constants.game.GameConstants;
import constants.inventory.ItemConstants;
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
import server.processor.MapleTradeProcessor;
import tools.I18nMessage;
import tools.LoggerOriginator;
import tools.LoggerUtil;
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
import tools.packet.character.interaction.GetTradeMeso;
import tools.packet.character.interaction.MatchCardSelect;
import tools.packet.character.interaction.MerchantOwnerMaintenanceLeave;
import tools.packet.character.interaction.MiniGameClose;
import tools.packet.character.interaction.PlayerShopItemUpdate;
import tools.packet.character.interaction.TradeItemAdd;
import tools.packet.character.interaction.UpdateHiredMerchant;
import tools.packet.shop.UpdateHiredMerchantBox;
import tools.packet.spawn.SpawnHiredMerchant;
import tools.packet.stat.EnableActions;

public final class PlayerInteractionHandler extends AbstractPacketHandler<BasePlayerInteractionPacket> {
   @Override
   public Class<PlayerInteractionReader> getReaderClass() {
      return PlayerInteractionReader.class;
   }

   private int establishMiniRoomStatus(MapleCharacter chr, boolean isMiniGame) {
      if (isMiniGame && FieldLimit.CANNOT_MINI_GAME.check(chr.getMap().getFieldLimit())) {
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
      if (chr.getTrade().isPresent()) {
         //Apparently there is a dupe exploit that causes racing conditions when saving/retrieving from the db with stuff like trade open.
         PacketCreator.announce(chr, new EnableActions());
         return true;
      }

      return false;
   }

   @Override
   public void handlePacket(BasePlayerInteractionPacket packet, MapleClient client) {
      if (!client.tryAcquireClient()) {
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
               MasterBroadcaster.getInstance().sendToAllInMap(merchant.getMap(), new UpdateHiredMerchantBox(merchant.getOwnerId(), merchant.objectId(), merchant.getDescription(), merchant.getItemId(), merchant.getShopRoomInfo()));
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
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("PLAYER_INTERACTION_CANNOT_TAKE_BACK_WITH_STORE_OPEN"));
            return;
         }

         if (slot >= merchant.getItems().size() || slot < 0) {
            AutoBanFactory.PACKET_EDIT.alert(chr, chr.getName() + " tried to packet edit with a hired merchant.");
            LoggerUtil.printError(LoggerOriginator.EXPLOITS, chr.getName() + " tried to remove item at slot " + slot);
            c.disconnect(true, false);
            return;
         }

         merchant.takeItemBack(slot, chr);
      }
   }

   private void buyAction(MapleClient c, MapleCharacter chr, int itemId, short quantity) {
      if (isTradeOpen(chr)) {
         return;
      }

      if (quantity < 1) {
         AutoBanFactory.PACKET_EDIT.alert(chr, chr.getName() + " tried to packet edit with a hired merchant and or player shop.");
         LoggerUtil.printError(LoggerOriginator.EXPLOITS, chr.getName() + " tried to buy item " + itemId + " with quantity " + quantity);
         c.disconnect(true, false);
         return;
      }
      MaplePlayerShop shop = chr.getPlayerShop();
      MapleHiredMerchant merchant = chr.getHiredMerchant();
      if (shop != null && shop.isVisitor(chr)) {
         if (shop.buy(c, itemId, quantity)) {
            MasterBroadcaster.getInstance().sendToShop(shop, new PlayerShopItemUpdate(shop));
         }
      } else if (merchant != null && !merchant.isOwner(chr)) {
         merchant.buy(c, itemId, quantity);
         merchant.broadcastToVisitorsThreadsafe(PacketCreator.create(new UpdateHiredMerchant(chr, merchant)));
      }
   }

   private void merchantOrganizeAction(MapleClient c, MapleCharacter chr) {
      MapleHiredMerchant merchant = chr.getHiredMerchant();
      if (merchant == null || !merchant.isOwner(chr)) {
         return;
      }

      merchant.withdrawMesos(chr);
      merchant.clearNonExistentItems();

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
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("PLAYER_INTERACTION_CANNOT_TAKE_BACK_WITH_STORE_OPEN"));
            return;
         }

         if (slot >= shop.getItems().size() || slot < 0) {
            AutoBanFactory.PACKET_EDIT.alert(chr, chr.getName() + " tried to packet edit with a player shop.");
            LoggerUtil.printError(LoggerOriginator.EXPLOITS, chr.getName() + " tried to remove item at slot " + slot);
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

      if (ivItem == null || ItemProcessor.getInstance().isUnableToBeTraded(ivItem)) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("PLAYER_INTERACTION_SHOP_OPERATION_ERROR"));
         PacketCreator.announce(c, new EnableActions());
         return;
      } else if (MapleItemInformationProvider.getInstance().isUnmerchable(ivItem.id())) {
         if (ItemConstants.isPet(ivItem.id())) {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("PLAYER_INTERACTION_SHOP_NO_PETS"));
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("PLAYER_INTERACTION_SHOP_NO_CASH_ITEMS"));
         }

         PacketCreator.announce(c, new EnableActions());
         return;
      }

      if (ItemConstants.isRechargeable(ivItem.id())) {
         perBundle = 1;
         bundles = 1;
      } else if (ivItem.quantity() < (bundles * perBundle)) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("PLAYER_INTERACTION_SHOP_OPERATION_ERROR"));
         PacketCreator.announce(c, new EnableActions());
         return;
      }

      if (perBundle <= 0 || perBundle * bundles > 2000 || bundles <= 0 || price <= 0 || price > Integer.MAX_VALUE) {
         AutoBanFactory.PACKET_EDIT.alert(chr, chr.getName() + " tried to packet edit with hired merchants.");
         LoggerUtil.printError(LoggerOriginator.EXPLOITS, chr.getName() + " might of possibly packet edited Hired Merchants\nperBundle: " + perBundle + "\nperBundle * bundles (This multiplied cannot be greater than 2000): " + perBundle * bundles + "\nbundles: " + bundles + "\nprice: " + price);
         return;
      }

      Item sellItem = ivItem.copy();
      if (!ItemConstants.isRechargeable(ivItem.id())) {
         sellItem = Item.newBuilder(sellItem).setQuantity(perBundle).build();
      }

      MaplePlayerShopItem shopItem = new MaplePlayerShopItem(sellItem, bundles, price);
      MaplePlayerShop shop = chr.getPlayerShop();
      MapleHiredMerchant merchant = chr.getHiredMerchant();
      if (shop != null && shop.isOwner(chr)) {
         if (shop.isOpen() || !shop.addItem(shopItem)) {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("PLAYER_INTERACTION_CANNOT_SELL_ANYMORE"));
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
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("PLAYER_INTERACTION_CANNOT_SELL_WHEN_FIRST_OPENING_STORE"));
            return;
         }

         if (merchant.isOpen() || !merchant.addItem(shopItem)) {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("PLAYER_INTERACTION_CANNOT_SELL_ANYMORE"));
            return;
         }

         if (ItemConstants.isRechargeable(ivItem.id())) {
            MapleInventoryManipulator.removeFromSlot(c, ivType, slot, ivItem.quantity(), true);
         } else {
            MapleInventoryManipulator.removeFromSlot(c, ivType, slot, (short) (bundles * perBundle), true);
         }

         PacketCreator.announce(c, new UpdateHiredMerchant(chr, merchant));

         if (YamlConfig.config.server.USE_ENFORCE_MERCHANT_SAVE) {
            chr.saveCharToDB(false);
         }

         merchant.saveItems(false);
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("PLAYER_INTERACTION_CANNOT_SELL_WITHOUT_SHOP"));
      }
   }

   private void confirmAction(MapleCharacter chr) {
      MapleTradeProcessor.getInstance().completeTrade(chr);
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
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("BAD_ITEM_DESCRIPTION"));
         PacketCreator.announce(c, new EnableActions());
         return;
      }

      if (ii.isUnmerchable(item.id())) {
         if (ItemConstants.isPet(item.id())) {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("TRADE_PET_CANNOT_BE_TRADED"));
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("TRADE_CASH_ITEM_CANNOT_BE_TRADED"));
         }

         PacketCreator.announce(c, new EnableActions());
         return;
      }

      if (quantity < 1 || quantity > item.quantity()) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("TRADE_LACK_QUANTITY"));
         PacketCreator.announce(c, new EnableActions());
         return;
      }

      if (chr.getTrade().isEmpty()) {
         return;
      }

      MapleTrade trade = chr.getTrade().get();
      if ((quantity <= item.quantity() && quantity >= 0) || ItemConstants.isRechargeable(item.id())) {
         if (ii.isDropRestricted(item.id())) { // ensure that undroppable items do not make it to the trade window
            if (!MapleKarmaManipulator.hasKarmaFlag(item)) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("TRADE_GENERIC_ITEM_CANNOT_BE_TRADED"));
               PacketCreator.announce(c, new EnableActions());
               return;
            }
         }

         MapleInventory inv = chr.getInventory(ivType);
         inv.lockInventory();
         try {
            Item checkItem = chr.getInventory(ivType).getItem(pos);
            if (checkItem != item || checkItem.position() != item.position()) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("BAD_ITEM_DESCRIPTION"));
               PacketCreator.announce(c, new EnableActions());
               return;
            }

            Item tradeItem = item.copy();
            if (ItemConstants.isRechargeable(item.id())) {
               quantity = item.quantity();
            }

            tradeItem = Item.newBuilder(tradeItem).setQuantity(quantity).setPosition(targetSlot).build();

            if (trade.addItem(tradeItem)) {
               MapleInventoryManipulator.removeFromSlot(c, ivType, item.position(), quantity, true);

               PacketCreator.announce(trade.getOwner(), new TradeItemAdd((byte) 0, tradeItem));
               Item finalTradeItem = tradeItem;
               trade.getPartnerTrade().ifPresent(partner -> PacketCreator.announce(partner.getOwner(), new TradeItemAdd((byte) 1, finalTradeItem)));
            }
         } catch (Exception e) {
            LoggerUtil.printError(LoggerOriginator.TRADE_EXCEPTION, e, "Player '" + chr + "' tried to add " + ii.getName(item.id()) + " qty. " + item.quantity() + " in trade (slot " + targetSlot + ") then exception occurred.");
         } finally {
            inv.unlockInventory();
         }
      }
   }

   private void setMesoAction(MapleCharacter chr, int amount) {
      chr.getTrade().ifPresent(trade -> {
         trade.setMeso(amount);
         PacketCreator.announce(trade.getOwner(), new GetTradeMeso((byte) 0, trade.getMeso()));
         trade.getPartnerTrade().ifPresent(partner -> PacketCreator.announce(partner.getOwner(), new GetTradeMeso((byte) 1, trade.getMeso())));
      });
   }

   private void selectCardAction(MapleCharacter chr, int turn, int slot) {
      MapleMiniGame game = chr.getMiniGame();
      int firstSlot = game.getFirstSlot();
      if (turn == 1) {
         game.setFirstSlot(slot);
         if (game.isOwner(chr)) {
            MasterBroadcaster.getInstance().sendToGameVisitor(game, new MatchCardSelect(turn, slot, firstSlot, turn));
         } else {
            MasterBroadcaster.getInstance().sendToGameOwner(game, new MatchCardSelect(turn, slot, firstSlot, turn));
         }
      } else if ((game.getCardId(firstSlot)) == (game.getCardId(slot))) {
         if (game.isOwner(chr)) {
            MasterBroadcaster.getInstance().sendToGamers(game, new MatchCardSelect(turn, slot, firstSlot, 2));
            game.setOwnerPoints();
         } else {
            MasterBroadcaster.getInstance().sendToGamers(game, new MatchCardSelect(turn, slot, firstSlot, 3));
            game.setVisitorPoints();
         }
      } else if (game.isOwner(chr)) {
         MasterBroadcaster.getInstance().sendToGamers(game, new MatchCardSelect(turn, slot, firstSlot, 0));
      } else {
         MasterBroadcaster.getInstance().sendToGamers(game, new MatchCardSelect(turn, slot, firstSlot, 1));
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
         game.miniGameMatchDraw();
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
            game.miniGameMatchVisitorWins(true);
         } else {
            game.miniGameMatchOwnerWins(true);
         }
      } else if (game.getGameType().equals(MiniGameType.MATCH_CARD)) {
         if (game.isOwner(chr)) {
            game.miniGameMatchVisitorWins(true);
         } else {
            game.miniGameMatchOwnerWins(true);
         }
      }
   }

   private void startAction(MapleCharacter chr) {
      MapleMiniGame game = chr.getMiniGame();
      if (game.getGameType().equals(MiniGameType.OMOK)) {
         game.miniGameMatchStarted();
         MasterBroadcaster.getInstance().sendToGamers(game, new GetMiniGameStart(game.getLoser()));
         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new AddOmokBox(game.getOwner(), 2, 1));
      } else if (game.getGameType().equals(MiniGameType.MATCH_CARD)) {
         game.miniGameMatchStarted();
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

      if (!canPlaceStore(chr)) {
         return;
      }

      MaplePlayerShop shop = chr.getPlayerShop();
      MapleHiredMerchant merchant = chr.getHiredMerchant();
      if (shop != null && shop.isOwner(chr)) {
         if (YamlConfig.config.server.USE_ERASE_PERMIT_ON_OPENSHOP) {
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

      if (!CashOperationHandler.checkBirthday(c, birthday)) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("CASH_SHOP_BAD_BIRTHDAY"));
         return;
      }

      PacketCreator.announce(c, new MerchantOwnerMaintenanceLeave());

      if (!canPlaceStore(chr)) {
         return;
      }

      MaplePlayerShop shop = chr.getPlayerShop();
      MapleHiredMerchant merchant = chr.getHiredMerchant();
      if (shop != null && shop.isOwner(chr)) {
         if (YamlConfig.config.server.USE_ERASE_PERMIT_ON_OPENSHOP) {
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
      chr.getTrade().ifPresentOrElse(trade -> MapleTradeProcessor.getInstance().cancelTrade(chr, MapleTradeResult.PARTNER_CANCEL),
            () -> {
               chr.closePlayerShop();
               chr.closeMiniGame(false);
               chr.closeHiredMerchant(true);
            });
   }

   private void chatAction(MapleClient c, MapleCharacter chr, String message) {
      MapleHiredMerchant merchant = chr.getHiredMerchant();
      if (chr.getTrade().isPresent()) {
         chr.getTrade().ifPresent(trade -> MapleTradeProcessor.getInstance().chat(trade, message));
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
      if (chr.getTrade().isPresent() && chr.getTrade().flatMap(MapleTrade::getPartnerTrade).isPresent()) {
         MapleTrade trade = chr.getTrade().get();
         Optional<MapleTrade> partnerTrade = trade.getPartnerTrade();
         if (!trade.isFullTrade() && !partnerTrade.map(MapleTrade::isFullTrade).orElse(true)) {
            MapleTradeProcessor.getInstance().visitTrade(chr, partnerTrade.get().getOwner());
         } else {
            PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.FULL_CAPACITY));
         }
      } else {
         if (isTradeOpen(chr)) {
            return;
         }

         MapleMapObject ob = chr.getMap().getMapObject(oid);
         if (ob instanceof MaplePlayerShop shop) {
            shop.visitShop(chr);
         } else if (ob instanceof MapleMiniGame game) {
            if (game.checkPassword(pw)) {
               if (game.hasFreeSlot() && !game.isVisitor(chr)) {
                  game.addVisitor(chr);
                  chr.setMiniGame(game);
                  switch (game.getGameType()) {
                     case OMOK -> game.sendOmok(c, game.getPieceType());
                     case MATCH_CARD -> game.sendMatchCard(c, game.getPieceType());
                  }
               } else {
                  PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.FULL_CAPACITY));
               }
            } else {
               PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.INCORRECT_PASSWORD));
            }
         } else if (ob instanceof MapleHiredMerchant merchant && chr.getHiredMerchant() == null) {
            merchant.visitShop(chr);
         }
      }
   }

   private void declineAction(MapleCharacter chr) {
      MapleTradeProcessor.getInstance().declineTrade(chr);
   }

   private void inviteAction(MapleCharacter chr, int otherCid) {
      MapleCharacter other = chr.getMap().getCharacterById(otherCid);
      if (other == null || chr.getId() == other.getId()) {
         return;
      }

      MapleTradeProcessor.getInstance().inviteTrade(chr, other);
   }

   private void createAction(MapleClient c, MapleCharacter chr, BaseCreatePlayerInteractionPacket packet) {
      if (!chr.isAlive()) {
         PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.CANT_WHILE_DEAD));
         return;
      }

      byte createType = packet.createType();
      if (createType == 3) {  // trade
         MapleTradeProcessor.getInstance().startTrade(chr);
      } else if (createType == 1 && packet instanceof CreateOmokPlayerInteractionPacket) { // omok mini game
         omokMiniGame(c, chr, ((CreateOmokPlayerInteractionPacket) packet).description(),
               ((CreateOmokPlayerInteractionPacket) packet).hasPassword(),
               ((CreateOmokPlayerInteractionPacket) packet).password(),
               ((CreateOmokPlayerInteractionPacket) packet).theType());
      } else if (createType == 2 && packet instanceof CreateMatchCardPlayerInteractionPacket) { // match card
         matchCard(c, chr, ((CreateMatchCardPlayerInteractionPacket) packet).description(),
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

      int status = establishMiniRoomStatus(chr, false);
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

   private void matchCard(MapleClient c, MapleCharacter chr, String description, boolean hasPassword, String password, int type) {
      int status = establishMiniRoomStatus(chr, true);
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
      int status = establishMiniRoomStatus(chr, true);
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
         for (MapleMapObject mmo : chr.getMap().getMapObjectsInRange(chr.position(), 23000, Arrays.asList(MapleMapObjectType.HIRED_MERCHANT, MapleMapObjectType.PLAYER))) {
            if (mmo instanceof MapleCharacter mc) {
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

         Point characterPosition = chr.position();
         MaplePortal portal = chr.getMap().findClosestTeleportPortal(characterPosition);
         if (portal != null && portal.getPosition().distance(characterPosition) < 120.0) {
            PacketCreator.announce(chr, new GetMiniRoomError(MiniRoomError.NOT_NEAR_PORTAL));
            return false;
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return true;
   }
}
