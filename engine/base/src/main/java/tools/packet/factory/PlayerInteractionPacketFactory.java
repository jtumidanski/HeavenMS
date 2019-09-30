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
import tools.FilePrinter;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.playerinteraction.GetHiredMerchant;
import tools.packet.playerinteraction.GetMatchCard;
import tools.packet.playerinteraction.GetMatchCardStart;
import tools.packet.playerinteraction.GetMiniGame;
import tools.packet.playerinteraction.GetMiniGameDenyTie;
import tools.packet.playerinteraction.GetMiniGameReady;
import tools.packet.playerinteraction.GetMiniGameRequestTie;
import tools.packet.playerinteraction.GetMiniGameSkipOwner;
import tools.packet.playerinteraction.GetMiniGameSkipVisitor;
import tools.packet.playerinteraction.GetMiniGameStart;
import tools.packet.playerinteraction.GetMiniGameUnReady;
import tools.packet.playerinteraction.GetMiniRoomError;
import tools.packet.playerinteraction.GetPlayerShop;
import tools.packet.playerinteraction.GetTradeMeso;
import tools.packet.playerinteraction.GetTradeResult;
import tools.packet.playerinteraction.GetTradeStart;
import tools.packet.playerinteraction.LeaveHiredMerchant;
import tools.packet.playerinteraction.MatchCardSelect;
import tools.packet.playerinteraction.MerchantChat;
import tools.packet.playerinteraction.MerchantMaintenanceMessage;
import tools.packet.playerinteraction.MerchantOwnerLeave;
import tools.packet.playerinteraction.MerchantOwnerMaintenanceLeave;
import tools.packet.playerinteraction.MerchantVisitorAdd;
import tools.packet.playerinteraction.MerchantVisitorLeave;
import tools.packet.playerinteraction.MiniGameClose;
import tools.packet.playerinteraction.MiniGameMoveOmok;
import tools.packet.playerinteraction.MiniGameNewVisitor;
import tools.packet.playerinteraction.MiniGameOwnerWin;
import tools.packet.playerinteraction.MiniGameRemoveVisitor;
import tools.packet.playerinteraction.MiniGameTie;
import tools.packet.playerinteraction.MiniGameVisitorWin;
import tools.packet.playerinteraction.NewMatchCardVisitor;
import tools.packet.playerinteraction.PlayerShopChat;
import tools.packet.playerinteraction.PlayerShopErrorMessage;
import tools.packet.playerinteraction.PlayerShopItemUpdate;
import tools.packet.playerinteraction.PlayerShopNewVisitor;
import tools.packet.playerinteraction.PlayerShopOwnerUpdate;
import tools.packet.playerinteraction.PlayerShopRemoveVisitor;
import tools.packet.playerinteraction.TradeChat;
import tools.packet.playerinteraction.TradeConfirmation;
import tools.packet.playerinteraction.TradeInvite;
import tools.packet.playerinteraction.TradeItemAdd;
import tools.packet.playerinteraction.TradePartnerAdd;
import tools.packet.playerinteraction.UpdateHiredMerchant;

public class PlayerInteractionPacketFactory extends AbstractPacketFactory {
   private static PlayerInteractionPacketFactory instance;

   public static PlayerInteractionPacketFactory getInstance() {
      if (instance == null) {
         instance = new PlayerInteractionPacketFactory();
      }
      return instance;
   }

   private PlayerInteractionPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof GetHiredMerchant) {
         return create(PlayerInteractionAction.ROOM, this::getHiredMerchant, packetInput);
      } else if (packetInput instanceof UpdateHiredMerchant) {
         return create(PlayerInteractionAction.UPDATE_MERCHANT, this::updateHiredMerchant, packetInput);
      } else if (packetInput instanceof MerchantChat) {
         return create(PlayerInteractionAction.CHAT, this::hiredMerchantChat, packetInput);
      } else if (packetInput instanceof MerchantVisitorLeave) {
         return create(PlayerInteractionAction.EXIT, this::hiredMerchantVisitorLeave, packetInput);
      } else if (packetInput instanceof MerchantOwnerLeave) {
         return create(PlayerInteractionAction.REAL_CLOSE_MERCHANT, this::hiredMerchantOwnerLeave, packetInput);
      } else if (packetInput instanceof MerchantOwnerMaintenanceLeave) {
         return create(PlayerInteractionAction.REAL_CLOSE_MERCHANT, this::hiredMerchantOwnerMaintenanceLeave, packetInput);
      } else if (packetInput instanceof MerchantMaintenanceMessage) {
         return create(PlayerInteractionAction.ROOM, this::hiredMerchantMaintenanceMessage, packetInput, 5);
      } else if (packetInput instanceof LeaveHiredMerchant) {
         return create(PlayerInteractionAction.EXIT, this::leaveHiredMerchant, packetInput);
      } else if (packetInput instanceof MerchantVisitorAdd) {
         return create(PlayerInteractionAction.VISIT, this::hiredMerchantVisitorAdd, packetInput);
      } else if (packetInput instanceof PlayerShopChat) {
         return create(PlayerInteractionAction.CHAT, this::getPlayerShopChat, packetInput);
      } else if (packetInput instanceof PlayerShopNewVisitor) {
         return create(PlayerInteractionAction.VISIT, this::getPlayerShopNewVisitor, packetInput);
      } else if (packetInput instanceof PlayerShopRemoveVisitor) {
         return create(PlayerInteractionAction.EXIT, this::getPlayerShopRemoveVisitor, packetInput, 4);
      } else if (packetInput instanceof TradePartnerAdd) {
         return create(PlayerInteractionAction.VISIT, this::getTradePartnerAdd, packetInput);
      } else if (packetInput instanceof TradeInvite) {
         return create(PlayerInteractionAction.INVITE, this::tradeInvite, packetInput);
      } else if (packetInput instanceof GetTradeMeso) {
         return create(PlayerInteractionAction.SET_MESO, this::getTradeMesoSet, packetInput, 8);
      } else if (packetInput instanceof TradeItemAdd) {
         return create(PlayerInteractionAction.SET_ITEMS, this::getTradeItemAdd, packetInput);
      } else if (packetInput instanceof PlayerShopItemUpdate) {
         return create(PlayerInteractionAction.UPDATE_MERCHANT, this::getPlayerShopItemUpdate, packetInput);
      } else if (packetInput instanceof PlayerShopOwnerUpdate) {
         return create(PlayerInteractionAction.UPDATE_PLAYERSHOP, this::getPlayerShopOwnerUpdate, packetInput);
      } else if (packetInput instanceof GetPlayerShop) {
         return create(PlayerInteractionAction.ROOM, this::getPlayerShop, packetInput);
      } else if (packetInput instanceof GetTradeStart) {
         return create(PlayerInteractionAction.ROOM, this::getTradeStart, packetInput);
      } else if (packetInput instanceof TradeConfirmation) {
         return create(PlayerInteractionAction.CONFIRM, packetInput, 3);
      } else if (packetInput instanceof GetTradeResult) {
         return create(PlayerInteractionAction.EXIT, this::getTradeResult, packetInput, 5);
      } else if (packetInput instanceof GetMiniGame) {
         return create(PlayerInteractionAction.ROOM, this::getMiniGame, packetInput);
      } else if (packetInput instanceof GetMiniGameReady) {
         return create(PlayerInteractionAction.READY, packetInput, 3);
      } else if (packetInput instanceof GetMiniGameUnReady) {
         return create(PlayerInteractionAction.UN_READY, packetInput, 3);
      } else if (packetInput instanceof GetMiniGameStart) {
         return create(PlayerInteractionAction.START, this::getMiniGameStart, packetInput, 4);
      } else if (packetInput instanceof GetMiniGameSkipOwner) {
         return create(PlayerInteractionAction.SKIP, this::getMiniGameSkipOwner, packetInput, 4);
      } else if (packetInput instanceof GetMiniGameRequestTie) {
         return create(PlayerInteractionAction.REQUEST_TIE, packetInput, 3);
      } else if (packetInput instanceof GetMiniGameDenyTie) {
         return create(PlayerInteractionAction.ANSWER_TIE, packetInput, 3);
      } else if (packetInput instanceof GetMiniRoomError) {
         return create(PlayerInteractionAction.ROOM, this::getMiniRoomError, packetInput, 5);
      } else if (packetInput instanceof GetMiniGameSkipVisitor) {
         return create(PlayerInteractionAction.SKIP, packetInput, 4);
      } else if (packetInput instanceof MiniGameMoveOmok) {
         return create(PlayerInteractionAction.MOVE_OMOK, this::getMiniGameMoveOmok, packetInput, 12);
      } else if (packetInput instanceof MiniGameNewVisitor) {
         return create(PlayerInteractionAction.VISIT, this::getMiniGameNewVisitor, packetInput);
      } else if (packetInput instanceof MiniGameRemoveVisitor) {
         return create(PlayerInteractionAction.EXIT, this::getMiniGameRemoveVisitor, packetInput, 3);
      } else if (packetInput instanceof MiniGameOwnerWin) {
         return create(PlayerInteractionAction.GET_RESULT, this::getMiniGameOwnerWin, packetInput);
      } else if (packetInput instanceof MiniGameVisitorWin) {
         return create(PlayerInteractionAction.GET_RESULT, this::getMiniGameVisitorWin, packetInput);
      } else if (packetInput instanceof MiniGameTie) {
         return create(PlayerInteractionAction.GET_RESULT, this::getMiniGameTie, packetInput);
      } else if (packetInput instanceof MiniGameClose) {
         return create(PlayerInteractionAction.EXIT, this::getMiniGameClose, packetInput, 5);
      } else if (packetInput instanceof GetMatchCard) {
         return create(PlayerInteractionAction.ROOM, this::getMatchCard, packetInput);
      } else if (packetInput instanceof GetMatchCardStart) {
         return create(PlayerInteractionAction.START, this::getMatchCardStart, packetInput);
      } else if (packetInput instanceof NewMatchCardVisitor) {
         return create(PlayerInteractionAction.VISIT, this::getMatchCardNewVisitor, packetInput);
      } else if (packetInput instanceof MatchCardSelect) {
         return create(PlayerInteractionAction.SELECT_CARD, this::getMatchCardSelect, packetInput, 6);
      } else if (packetInput instanceof TradeChat) {
         return create(PlayerInteractionAction.CHAT, this::getTradeChat, packetInput);
      } else if (packetInput instanceof PlayerShopErrorMessage) {
         return create(PlayerInteractionAction.EXIT, this::shopErrorMessage, packetInput);
      }

      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
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