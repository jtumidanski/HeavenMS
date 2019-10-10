package tools.packet.factory;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import client.MapleCharacter;
import net.opcodes.SendOpcode;
import server.channel.PlayerInteractionAction;
import server.maps.MapleMiniGame;
import server.maps.MaplePlayerShopItem;
import server.maps.MaplePlayerShopSoldItem;
import server.maps.MapleSoldItem;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.character.interaction.GetHiredMerchant;
import tools.packet.character.interaction.GetMatchCard;
import tools.packet.character.interaction.GetMatchCardStart;
import tools.packet.character.interaction.GetMiniGame;
import tools.packet.character.interaction.GetMiniGameDenyTie;
import tools.packet.character.interaction.GetMiniGameReady;
import tools.packet.character.interaction.GetMiniGameRequestTie;
import tools.packet.character.interaction.GetMiniGameSkipOwner;
import tools.packet.character.interaction.GetMiniGameSkipVisitor;
import tools.packet.character.interaction.GetMiniGameStart;
import tools.packet.character.interaction.GetMiniGameUnReady;
import tools.packet.character.interaction.GetMiniRoomError;
import tools.packet.character.interaction.GetPlayerShop;
import tools.packet.character.interaction.GetTradeMeso;
import tools.packet.character.interaction.GetTradeResult;
import tools.packet.character.interaction.GetTradeStart;
import tools.packet.character.interaction.LeaveHiredMerchant;
import tools.packet.character.interaction.MatchCardSelect;
import tools.packet.character.interaction.MerchantChat;
import tools.packet.character.interaction.MerchantMaintenanceMessage;
import tools.packet.character.interaction.MerchantOwnerLeave;
import tools.packet.character.interaction.MerchantOwnerMaintenanceLeave;
import tools.packet.character.interaction.MerchantVisitorAdd;
import tools.packet.character.interaction.MerchantVisitorLeave;
import tools.packet.character.interaction.MiniGameClose;
import tools.packet.character.interaction.MiniGameMoveOmok;
import tools.packet.character.interaction.MiniGameNewVisitor;
import tools.packet.character.interaction.MiniGameOwnerWin;
import tools.packet.character.interaction.MiniGameRemoveVisitor;
import tools.packet.character.interaction.MiniGameTie;
import tools.packet.character.interaction.MiniGameVisitorWin;
import tools.packet.character.interaction.NewMatchCardVisitor;
import tools.packet.character.interaction.PlayerShopChat;
import tools.packet.character.interaction.PlayerShopErrorMessage;
import tools.packet.character.interaction.PlayerShopItemUpdate;
import tools.packet.character.interaction.PlayerShopNewVisitor;
import tools.packet.character.interaction.PlayerShopOwnerUpdate;
import tools.packet.character.interaction.PlayerShopRemoveVisitor;
import tools.packet.character.interaction.TradeChat;
import tools.packet.character.interaction.TradeConfirmation;
import tools.packet.character.interaction.TradeInvite;
import tools.packet.character.interaction.TradeItemAdd;
import tools.packet.character.interaction.TradePartnerAdd;
import tools.packet.character.interaction.UpdateHiredMerchant;

public class PlayerInteractionPacketFactory extends AbstractPacketFactory {
   private static PlayerInteractionPacketFactory instance;

   public static PlayerInteractionPacketFactory getInstance() {
      if (instance == null) {
         instance = new PlayerInteractionPacketFactory();
      }
      return instance;
   }

   private PlayerInteractionPacketFactory() {
      registry.setHandler(GetHiredMerchant.class, packet -> create(PlayerInteractionAction.ROOM, this::getHiredMerchant, packet));
      registry.setHandler(UpdateHiredMerchant.class, packet -> create(PlayerInteractionAction.UPDATE_MERCHANT, this::updateHiredMerchant, packet));
      registry.setHandler(MerchantChat.class, packet -> create(PlayerInteractionAction.CHAT, this::hiredMerchantChat, packet));
      registry.setHandler(MerchantVisitorLeave.class, packet -> create(PlayerInteractionAction.EXIT, this::hiredMerchantVisitorLeave, packet));
      registry.setHandler(MerchantOwnerLeave.class, packet -> create(PlayerInteractionAction.REAL_CLOSE_MERCHANT, this::hiredMerchantOwnerLeave, packet));
      registry.setHandler(MerchantOwnerMaintenanceLeave.class, packet -> create(PlayerInteractionAction.REAL_CLOSE_MERCHANT, this::hiredMerchantOwnerMaintenanceLeave, packet));
      registry.setHandler(MerchantMaintenanceMessage.class, packet -> create(PlayerInteractionAction.ROOM, this::hiredMerchantMaintenanceMessage, packet, 5));
      registry.setHandler(LeaveHiredMerchant.class, packet -> create(PlayerInteractionAction.EXIT, this::leaveHiredMerchant, packet));
      registry.setHandler(MerchantVisitorAdd.class, packet -> create(PlayerInteractionAction.VISIT, this::hiredMerchantVisitorAdd, packet));
      registry.setHandler(PlayerShopChat.class, packet -> create(PlayerInteractionAction.CHAT, this::getPlayerShopChat, packet));
      registry.setHandler(PlayerShopNewVisitor.class, packet -> create(PlayerInteractionAction.VISIT, this::getPlayerShopNewVisitor, packet));
      registry.setHandler(PlayerShopRemoveVisitor.class, packet -> create(PlayerInteractionAction.EXIT, this::getPlayerShopRemoveVisitor, packet, 4));
      registry.setHandler(TradePartnerAdd.class, packet -> create(PlayerInteractionAction.VISIT, this::getTradePartnerAdd, packet));
      registry.setHandler(TradeInvite.class, packet -> create(PlayerInteractionAction.INVITE, this::tradeInvite, packet));
      registry.setHandler(GetTradeMeso.class, packet -> create(PlayerInteractionAction.SET_MESO, this::getTradeMesoSet, packet, 8));
      registry.setHandler(TradeItemAdd.class, packet -> create(PlayerInteractionAction.SET_ITEMS, this::getTradeItemAdd, packet));
      registry.setHandler(PlayerShopItemUpdate.class, packet -> create(PlayerInteractionAction.UPDATE_MERCHANT, this::getPlayerShopItemUpdate, packet));
      registry.setHandler(PlayerShopOwnerUpdate.class, packet -> create(PlayerInteractionAction.UPDATE_PLAYERSHOP, this::getPlayerShopOwnerUpdate, packet));
      registry.setHandler(GetPlayerShop.class, packet -> create(PlayerInteractionAction.ROOM, this::getPlayerShop, packet));
      registry.setHandler(GetTradeStart.class, packet -> create(PlayerInteractionAction.ROOM, this::getTradeStart, packet));
      registry.setHandler(TradeConfirmation.class, packet -> create(PlayerInteractionAction.CONFIRM, packet, 3));
      registry.setHandler(GetTradeResult.class, packet -> create(PlayerInteractionAction.EXIT, this::getTradeResult, packet, 5));
      registry.setHandler(GetMiniGame.class, packet -> create(PlayerInteractionAction.ROOM, this::getMiniGame, packet));
      registry.setHandler(GetMiniGameReady.class, packet -> create(PlayerInteractionAction.READY, packet, 3));
      registry.setHandler(GetMiniGameUnReady.class, packet -> create(PlayerInteractionAction.UN_READY, packet, 3));
      registry.setHandler(GetMiniGameStart.class, packet -> create(PlayerInteractionAction.START, this::getMiniGameStart, packet, 4));
      registry.setHandler(GetMiniGameSkipOwner.class, packet -> create(PlayerInteractionAction.SKIP, this::getMiniGameSkipOwner, packet, 4));
      registry.setHandler(GetMiniGameRequestTie.class, packet -> create(PlayerInteractionAction.REQUEST_TIE, packet));
      registry.setHandler(GetMiniGameDenyTie.class, packet -> create(PlayerInteractionAction.ANSWER_TIE, packet));
      registry.setHandler(GetMiniRoomError.class, packet -> create(PlayerInteractionAction.ROOM, this::getMiniRoomError, packet, 5));
      registry.setHandler(GetMiniGameSkipVisitor.class, packet -> create(PlayerInteractionAction.SKIP, packet));
      registry.setHandler(MiniGameMoveOmok.class, packet -> create(PlayerInteractionAction.MOVE_OMOK, this::getMiniGameMoveOmok, packet, 12));
      registry.setHandler(MiniGameNewVisitor.class, packet -> create(PlayerInteractionAction.VISIT, this::getMiniGameNewVisitor, packet));
      registry.setHandler(MiniGameRemoveVisitor.class, packet -> create(PlayerInteractionAction.EXIT, this::getMiniGameRemoveVisitor, packet, 3));
      registry.setHandler(MiniGameOwnerWin.class, packet -> create(PlayerInteractionAction.GET_RESULT, this::getMiniGameOwnerWin, packet));
      registry.setHandler(MiniGameVisitorWin.class, packet -> create(PlayerInteractionAction.GET_RESULT, this::getMiniGameVisitorWin, packet));
      registry.setHandler(MiniGameTie.class, packet -> create(PlayerInteractionAction.GET_RESULT, this::getMiniGameTie, packet));
      registry.setHandler(MiniGameClose.class, packet -> create(PlayerInteractionAction.EXIT, this::getMiniGameClose, packet, 5));
      registry.setHandler(GetMatchCard.class, packet -> create(PlayerInteractionAction.ROOM, this::getMatchCard, packet));
      registry.setHandler(GetMatchCardStart.class, packet -> create(PlayerInteractionAction.START, this::getMatchCardStart, packet));
      registry.setHandler(NewMatchCardVisitor.class, packet -> create(PlayerInteractionAction.VISIT, this::getMatchCardNewVisitor, packet));
      registry.setHandler(MatchCardSelect.class, packet -> create(PlayerInteractionAction.SELECT_CARD, this::getMatchCardSelect, packet, 6));
      registry.setHandler(TradeChat.class, packet -> create(PlayerInteractionAction.CHAT, this::getTradeChat, packet));
      registry.setHandler(PlayerShopErrorMessage.class, packet -> create(PlayerInteractionAction.EXIT, this::shopErrorMessage, packet));
   }

   protected <T extends PacketInput> byte[] create(PlayerInteractionAction subOp, BiConsumer<MaplePacketLittleEndianWriter, T> decorator, PacketInput packetInput, Integer size) {
      return create((Function<T, byte[]>) castInput -> {
         final MaplePacketLittleEndianWriter writer = newWriter(size);
         writer.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
         writer.write(subOp.getValue());
         if (decorator != null) {
            decorator.accept(writer, castInput);
         }
         return writer.getPacket();
      }, packetInput);
   }

   protected <T extends PacketInput> byte[] create(PlayerInteractionAction subOp, PacketInput packetInput, Integer size) {
      return create(subOp, null, packetInput, size);
   }

   protected <T extends PacketInput> byte[] create(PlayerInteractionAction subOp, BiConsumer<MaplePacketLittleEndianWriter, T> decorator, PacketInput packetInput) {
      return create(subOp, decorator, packetInput, MaplePacketLittleEndianWriter.DEFAULT_SIZE);
   }

   protected <T extends PacketInput> byte[] create(PlayerInteractionAction subOp, PacketInput packetInput) {
      return create(subOp, null, packetInput, MaplePacketLittleEndianWriter.DEFAULT_SIZE);
   }

   /*
    * Possible things for ENTRUSTED_SHOP_CHECK_RESULT
    * 0x0E = 00 = Renaming Failed - Can't find the merchant, 01 = Renaming successful
    * 0x10 = Changes channel to the store (Store is open at Channel 1, do you want to change channels?)
    * 0x11 = You cannot sell any items when managing.. blabla
    * 0x12 = FKING POPUP LOL
    */
   protected void getHiredMerchant(MaplePacketLittleEndianWriter writer, GetHiredMerchant packet) {//Thanks Dustin
      writer.write(0x05);
      writer.write(0x04);
      writer.writeShort(packet.getHiredMerchant().getVisitorSlotThreadsafe(packet.getCharacter()) + 1);
      writer.writeInt(packet.getHiredMerchant().getItemId());
      writer.writeMapleAsciiString("Hired Merchant");

      MapleCharacter[] visitors = packet.getHiredMerchant().getVisitors();
      for (int i = 0; i < 3; i++) {
         if (visitors[i] != null) {
            writer.write(i + 1);
            addCharLook(writer, visitors[i], false);
            writer.writeMapleAsciiString(visitors[i].getName());
         }
      }
      writer.write(-1);
      if (packet.getHiredMerchant().isOwner(packet.getCharacter())) {
         List<Pair<String, Byte>> msgList = packet.getHiredMerchant().getMessages();

         writer.writeShort(msgList.size());
         for (Pair<String, Byte> stringBytePair : msgList) {
            writer.writeMapleAsciiString(stringBytePair.getLeft());
            writer.write(stringBytePair.getRight());
         }
      } else {
         writer.writeShort(0);
      }
      writer.writeMapleAsciiString(packet.getHiredMerchant().getOwner());
      if (packet.getHiredMerchant().isOwner(packet.getCharacter())) {
         writer.writeShort(0);
         writer.writeShort(packet.getHiredMerchant().getTimeOpen());
         writer.write(packet.isFirstTime() ? 1 : 0);
         List<MapleSoldItem> sold = packet.getHiredMerchant().getSold();
         writer.write(sold.size());
         for (MapleSoldItem s : sold) {
            writer.writeInt(s.itemId());
            writer.writeShort(s.quantity());
            writer.writeInt(s.mesos());
            writer.writeMapleAsciiString(s.buyer());
         }
         writer.writeInt(packet.getCharacter().getMerchantMeso());//:D?
      }
      writer.writeMapleAsciiString(packet.getHiredMerchant().getDescription());
      writer.write(0x10); //TODO SLOTS, which is 16 for most stores...slotMax
      writer.writeInt(packet.getHiredMerchant().isOwner(packet.getCharacter()) ? packet.getCharacter().getMerchantMeso() : packet.getCharacter().getMeso());
      writer.write(packet.getHiredMerchant().getItems().size());
      if (packet.getHiredMerchant().getItems().isEmpty()) {
         writer.write(0);//Hmm??
      } else {
         for (MaplePlayerShopItem item : packet.getHiredMerchant().getItems()) {
            writer.writeShort(item.bundles());
            writer.writeShort(item.item().quantity());
            writer.writeInt(item.price());
            addItemInfo(writer, item.item(), true);
         }
      }
   }

   protected void updateHiredMerchant(MaplePacketLittleEndianWriter writer, UpdateHiredMerchant packet) {
      writer.writeInt(packet.getHiredMerchant().isOwner(packet.getCharacter()) ? packet.getCharacter().getMerchantMeso() : packet.getCharacter().getMeso());
      writer.write(packet.getHiredMerchant().getItems().size());
      for (MaplePlayerShopItem item : packet.getHiredMerchant().getItems()) {
         writer.writeShort(item.bundles());
         writer.writeShort(item.item().quantity());
         writer.writeInt(item.price());
         addItemInfo(writer, item.item(), true);
      }
   }

   protected void hiredMerchantChat(MaplePacketLittleEndianWriter writer, MerchantChat packet) {
      writer.write(PlayerInteractionAction.CHAT_THING.getValue());
      writer.write(packet.slot());
      writer.writeMapleAsciiString(packet.message());
   }

   protected void hiredMerchantVisitorLeave(MaplePacketLittleEndianWriter writer, MerchantVisitorLeave packet) {
      if (packet.slot() != 0) {
         writer.write(packet.slot());
      }
   }

   protected void hiredMerchantOwnerLeave(MaplePacketLittleEndianWriter writer, MerchantOwnerLeave packet) {
      writer.write(0);
   }

   protected void hiredMerchantOwnerMaintenanceLeave(MaplePacketLittleEndianWriter writer, MerchantOwnerMaintenanceLeave packet) {
      writer.write(5);
   }

   protected void hiredMerchantMaintenanceMessage(MaplePacketLittleEndianWriter writer, MerchantMaintenanceMessage packet) {
      writer.write(0x00);
      writer.write(0x12);
   }

   protected void leaveHiredMerchant(MaplePacketLittleEndianWriter writer, LeaveHiredMerchant packet) {
      writer.write(packet.slot());
      writer.write(packet.status());
   }

   protected void hiredMerchantVisitorAdd(MaplePacketLittleEndianWriter writer, MerchantVisitorAdd packet) {
      writer.write(packet.getSlot());
      addCharLook(writer, packet.getCharacter(), false);
      writer.writeMapleAsciiString(packet.getCharacter().getName());
   }

   protected void getPlayerShopNewVisitor(MaplePacketLittleEndianWriter writer, PlayerShopNewVisitor packet) {
      writer.write(packet.getSlot());
      addCharLook(writer, packet.getCharacter(), false);
      writer.writeMapleAsciiString(packet.getCharacter().getName());
   }

   protected void getPlayerShopRemoveVisitor(MaplePacketLittleEndianWriter writer, PlayerShopRemoveVisitor packet) {
      if (packet.slot() != 0) {
         writer.writeShort(packet.slot());
      }
   }

   protected void getTradePartnerAdd(MaplePacketLittleEndianWriter writer, TradePartnerAdd packet) {
      writer.write(1);
      addCharLook(writer, packet.getCharacter(), false);
      writer.writeMapleAsciiString(packet.getCharacter().getName());
   }

   protected void tradeInvite(MaplePacketLittleEndianWriter writer, TradeInvite packet) {
      writer.write(3);
      writer.writeMapleAsciiString(packet.getCharacter().getName());
      writer.write(new byte[]{(byte) 0xB7, (byte) 0x50, 0, 0});
   }

   protected void getTradeMesoSet(MaplePacketLittleEndianWriter writer, GetTradeMeso packet) {
      writer.write(packet.number());
      writer.writeInt(packet.meso());
   }

   protected void getTradeItemAdd(MaplePacketLittleEndianWriter writer, TradeItemAdd packet) {
      writer.write(packet.number());
      writer.write(packet.item().position());
      addItemInfo(writer, packet.item(), true);
   }

   protected void getPlayerShopItemUpdate(MaplePacketLittleEndianWriter writer, PlayerShopItemUpdate packet) {
      writer.write(packet.getPlayerShop().getItems().size());
      for (MaplePlayerShopItem item : packet.getPlayerShop().getItems()) {
         writer.writeShort(item.bundles());
         writer.writeShort(item.item().quantity());
         writer.writeInt(item.price());
         addItemInfo(writer, item.item(), true);
      }
   }

   protected void getPlayerShopOwnerUpdate(MaplePacketLittleEndianWriter writer, PlayerShopOwnerUpdate packet) {
      writer.write(packet.position());
      writer.writeShort(packet.soldItem().quantity());
      writer.writeMapleAsciiString(packet.soldItem().buyer());
   }

   protected void getPlayerShop(MaplePacketLittleEndianWriter writer, GetPlayerShop packet) {
      writer.write(4);
      writer.write(4);
      writer.write(packet.isOwner() ? 0 : 1);

      if (packet.isOwner()) {
         List<MaplePlayerShopSoldItem> sold = packet.getPlayerShop().getSold();
         writer.write(sold.size());
         for (MaplePlayerShopSoldItem s : sold) {
            writer.writeInt(s.itemId());
            writer.writeShort(s.quantity());
            writer.writeInt(s.mesos());
            writer.writeMapleAsciiString(s.buyer());
         }
      } else {
         writer.write(0);
      }

      addCharLook(writer, packet.getPlayerShop().getOwner(), false);
      writer.writeMapleAsciiString(packet.getPlayerShop().getOwner().getName());

      MapleCharacter[] visitors = packet.getPlayerShop().getVisitors();
      for (int i = 0; i < 3; i++) {
         if (visitors[i] != null) {
            writer.write(i + 1);
            addCharLook(writer, visitors[i], false);
            writer.writeMapleAsciiString(visitors[i].getName());
         }
      }

      writer.write(0xFF);
      writer.writeMapleAsciiString(packet.getPlayerShop().getDescription());
      List<MaplePlayerShopItem> items = packet.getPlayerShop().getItems();
      writer.write(0x10);  //TODO SLOTS, which is 16 for most stores...slotMax
      writer.write(items.size());
      for (MaplePlayerShopItem item : items) {
         writer.writeShort(item.bundles());
         writer.writeShort(item.item().quantity());
         writer.writeInt(item.price());
         addItemInfo(writer, item.item(), true);
      }
   }

   protected void getTradeStart(MaplePacketLittleEndianWriter writer, GetTradeStart packet) {
      writer.write(3);
      writer.write(2);
      writer.write(packet.getNumber());
      if (packet.getNumber() == 1) {
         writer.write(0);
         addCharLook(writer, packet.getTrade().getPartner().getChr(), false);
         writer.writeMapleAsciiString(packet.getTrade().getPartner().getChr().getName());
      }
      writer.write(packet.getNumber());
      addCharLook(writer, packet.getCharacter(), false);
      writer.writeMapleAsciiString(packet.getCharacter().getName());
      writer.write(0xFF);
   }

   /**
    * Possible values for <code>operation</code>:<br> 2: Trade cancelled by the
    * other character<br> 7: Trade successful<br> 8: Trade unsuccessful<br>
    * 9: Cannot carry more one-of-a-kind items<br> 12: Cannot trade on different maps<br>
    * 13: Cannot trade, game files damaged<br>
    *
    * @return
    */
   protected void getTradeResult(MaplePacketLittleEndianWriter writer, GetTradeResult packet) {
      writer.write(packet.number());
      writer.write(packet.operation());
   }

   protected void getMiniGame(MaplePacketLittleEndianWriter writer, GetMiniGame packet) {
      writer.write(1);
      writer.write(0);
      writer.write(packet.isOwner() ? 0 : 1);
      writer.write(0);
      addCharLook(writer, packet.getMiniGame().getOwner(), false);
      writer.writeMapleAsciiString(packet.getMiniGame().getOwner().getName());
      if (packet.getMiniGame().getVisitor() != null) {
         MapleCharacter visitor = packet.getMiniGame().getVisitor();
         writer.write(1);
         addCharLook(writer, visitor, false);
         writer.writeMapleAsciiString(visitor.getName());
      }
      writer.write(0xFF);
      writer.write(0);
      writer.writeInt(1);
      writer.writeInt(packet.getMiniGame().getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.WIN, true));
      writer.writeInt(packet.getMiniGame().getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.TIE, true));
      writer.writeInt(packet.getMiniGame().getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.LOSS, true));
      writer.writeInt(packet.getMiniGame().getOwnerScore());
      if (packet.getMiniGame().getVisitor() != null) {
         MapleCharacter visitor = packet.getMiniGame().getVisitor();
         writer.write(1);
         writer.writeInt(1);
         writer.writeInt(visitor.getMiniGamePoints(MapleMiniGame.MiniGameResult.WIN, true));
         writer.writeInt(visitor.getMiniGamePoints(MapleMiniGame.MiniGameResult.TIE, true));
         writer.writeInt(visitor.getMiniGamePoints(MapleMiniGame.MiniGameResult.LOSS, true));
         writer.writeInt(packet.getMiniGame().getVisitorScore());
      }
      writer.write(0xFF);
      writer.writeMapleAsciiString(packet.getMiniGame().getDescription());
      writer.write(packet.getPiece());
      writer.write(0);
   }

   protected void getMiniGameStart(MaplePacketLittleEndianWriter writer, GetMiniGameStart packet) {
      writer.write(packet.loser());
   }

   protected void getMiniGameSkipOwner(MaplePacketLittleEndianWriter writer, GetMiniGameSkipOwner packet) {
      writer.write(0x01);
   }

   protected void getMiniRoomError(MaplePacketLittleEndianWriter writer, GetMiniRoomError packet) {
      writer.write(0);
      writer.write(packet.status().getValue());
   }

   protected void getMiniGameMoveOmok(MaplePacketLittleEndianWriter writer, MiniGameMoveOmok packet) {
      writer.writeInt(packet.move1());
      writer.writeInt(packet.move2());
      writer.write(packet.move3());
   }

   protected void getMiniGameNewVisitor(MaplePacketLittleEndianWriter writer, MiniGameNewVisitor packet) {
      writer.write(packet.getSlot());
      addCharLook(writer, packet.getCharacter(), false);
      writer.writeMapleAsciiString(packet.getCharacter().getName());
      writer.writeInt(1);
      writer.writeInt(packet.getCharacter().getMiniGamePoints(MapleMiniGame.MiniGameResult.WIN, true));
      writer.writeInt(packet.getCharacter().getMiniGamePoints(MapleMiniGame.MiniGameResult.TIE, true));
      writer.writeInt(packet.getCharacter().getMiniGamePoints(MapleMiniGame.MiniGameResult.LOSS, true));
      writer.writeInt(packet.getMiniGame().getVisitorScore());
   }

   protected void getMiniGameRemoveVisitor(MaplePacketLittleEndianWriter writer, MiniGameRemoveVisitor packet) {
      writer.write(1);
   }

   protected void getMiniGameResult(MaplePacketLittleEndianWriter writer, MapleMiniGame game, int tie, int result, int forfeit) {
      int matchResultType;
      if (tie == 0 && forfeit != 1) {
         matchResultType = 0;
      } else if (tie != 0) {
         matchResultType = 1;
      } else {
         matchResultType = 2;
      }

      writer.write(matchResultType);
      writer.writeBool(result == 2); // host/visitor wins

      boolean omok = game.isOmok();
      if (matchResultType == 1) {
         writer.write(0);
         writer.writeShort(0);
         writer.writeInt(game.getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.WIN, omok)); // wins
         writer.writeInt(game.getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.TIE, omok)); // ties
         writer.writeInt(game.getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.LOSS, omok)); // losses
         writer.writeInt(game.getOwnerScore()); // points

         writer.writeInt(0); // unknown
         writer.writeInt(game.getVisitor().getMiniGamePoints(MapleMiniGame.MiniGameResult.WIN, omok)); // wins
         writer.writeInt(game.getVisitor().getMiniGamePoints(MapleMiniGame.MiniGameResult.TIE, omok)); // ties
         writer.writeInt(game.getVisitor().getMiniGamePoints(MapleMiniGame.MiniGameResult.LOSS, omok)); // losses
         writer.writeInt(game.getVisitorScore()); // points
         writer.write(0);
      } else {
         writer.writeInt(0);
         writer.writeInt(game.getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.WIN, omok)); // wins
         writer.writeInt(game.getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.TIE, omok)); // ties
         writer.writeInt(game.getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.LOSS, omok)); // losses
         writer.writeInt(game.getOwnerScore()); // points
         writer.writeInt(0);
         writer.writeInt(game.getVisitor().getMiniGamePoints(MapleMiniGame.MiniGameResult.WIN, omok)); // wins
         writer.writeInt(game.getVisitor().getMiniGamePoints(MapleMiniGame.MiniGameResult.TIE, omok)); // ties
         writer.writeInt(game.getVisitor().getMiniGamePoints(MapleMiniGame.MiniGameResult.LOSS, omok)); // losses
         writer.writeInt(game.getVisitorScore()); // points
      }
   }

   protected void getMiniGameOwnerWin(MaplePacketLittleEndianWriter writer, MiniGameOwnerWin packet) {
      getMiniGameResult(writer, packet.getGame(), 0, 1, packet.isForfeit() ? 1 : 0);
   }

   protected void getMiniGameVisitorWin(MaplePacketLittleEndianWriter writer, MiniGameVisitorWin packet) {
      getMiniGameResult(writer, packet.getGame(), 0, 2, packet.isForfeit() ? 1 : 0);
   }

   protected void getMiniGameTie(MaplePacketLittleEndianWriter writer, MiniGameTie packet) {
      getMiniGameResult(writer, packet.getGame(), 1, 3, 0);
   }

   protected void getMiniGameClose(MaplePacketLittleEndianWriter writer, MiniGameClose packet) {
      writer.writeBool(packet.visitor());
      writer.write(packet.theType()); /* 2 : CRASH 3 : The room has been closed 4 : You have left the room 5 : You have been expelled  */
   }

   protected void getMatchCard(MaplePacketLittleEndianWriter writer, GetMatchCard packet) {
      writer.write(2);
      writer.write(2);
      writer.write(packet.isOwner() ? 0 : 1);
      writer.write(0);
      addCharLook(writer, packet.getMiniGame().getOwner(), false);
      writer.writeMapleAsciiString(packet.getMiniGame().getOwner().getName());
      if (packet.getMiniGame().getVisitor() != null) {
         MapleCharacter visitor = packet.getMiniGame().getVisitor();
         writer.write(1);
         addCharLook(writer, visitor, false);
         writer.writeMapleAsciiString(visitor.getName());
      }
      writer.write(0xFF);
      writer.write(0);
      writer.writeInt(2);
      writer.writeInt(packet.getMiniGame().getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.WIN, false));
      writer.writeInt(packet.getMiniGame().getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.TIE, false));
      writer.writeInt(packet.getMiniGame().getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.LOSS, false));

      //set vs
      writer.writeInt(packet.getMiniGame().getOwnerScore());
      if (packet.getMiniGame().getVisitor() != null) {
         MapleCharacter visitor = packet.getMiniGame().getVisitor();
         writer.write(1);
         writer.writeInt(2);
         writer.writeInt(visitor.getMiniGamePoints(MapleMiniGame.MiniGameResult.WIN, false));
         writer.writeInt(visitor.getMiniGamePoints(MapleMiniGame.MiniGameResult.TIE, false));
         writer.writeInt(visitor.getMiniGamePoints(MapleMiniGame.MiniGameResult.LOSS, false));
         writer.writeInt(packet.getMiniGame().getVisitorScore());
      }
      writer.write(0xFF);
      writer.writeMapleAsciiString(packet.getMiniGame().getDescription());
      writer.write(packet.getPiece());
      writer.write(0);
   }

   protected void getMatchCardStart(MaplePacketLittleEndianWriter writer, GetMatchCardStart packet) {
      writer.write(packet.getLoser());

      int last;
      if (packet.getMiniGame().getMatchesToWin() > 10) {
         last = 30;
      } else if (packet.getMiniGame().getMatchesToWin() > 6) {
         last = 20;
      } else {
         last = 12;
      }

      writer.write(last);
      for (int i = 0; i < last; i++) {
         writer.writeInt(packet.getMiniGame().getCardId(i));
      }
   }

   protected void getMatchCardNewVisitor(MaplePacketLittleEndianWriter writer, NewMatchCardVisitor packet) {
      writer.write(packet.getSlot());
      addCharLook(writer, packet.getCharacter(), false);
      writer.writeMapleAsciiString(packet.getCharacter().getName());
      writer.writeInt(1);
      writer.writeInt(packet.getCharacter().getMiniGamePoints(MapleMiniGame.MiniGameResult.WIN, false));
      writer.writeInt(packet.getCharacter().getMiniGamePoints(MapleMiniGame.MiniGameResult.TIE, false));
      writer.writeInt(packet.getCharacter().getMiniGamePoints(MapleMiniGame.MiniGameResult.LOSS, false));
      writer.writeInt(packet.getMiniGame().getVisitorScore());
   }

   protected void getMatchCardSelect(MaplePacketLittleEndianWriter writer, MatchCardSelect packet) {
      writer.write(packet.turn());
      if (packet.turn() == 1) {
         writer.write(packet.slot());
      } else if (packet.turn() == 0) {
         writer.write(packet.slot());
         writer.write(packet.firstSlot());
         writer.write(packet.theType());
      }
   }

   protected void getPlayerShopChat(MaplePacketLittleEndianWriter writer, PlayerShopChat packet) {
      writer.write(PlayerInteractionAction.CHAT_THING.getValue());
      writer.write(packet.slot());
      writer.writeMapleAsciiString(packet.name() + " : " + packet.chat());
   }

   protected void getTradeChat(MaplePacketLittleEndianWriter writer, TradeChat packet) {
      writer.write(PlayerInteractionAction.CHAT_THING.getValue());
      writer.write(packet.owner() ? 0 : 1);
      writer.writeMapleAsciiString(packet.name() + " : " + packet.chat());
   }

   protected void shopErrorMessage(MaplePacketLittleEndianWriter writer, PlayerShopErrorMessage packet) {
      writer.write(packet.theType());
      writer.write(packet.error());
   }
}