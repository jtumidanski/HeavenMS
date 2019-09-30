package tools.packet.factory;

import java.util.List;

import client.MapleCharacter;
import net.opcodes.SendOpcode;
import net.server.channel.handlers.PlayerInteractionHandler;
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
         return create(this::getHiredMerchant, packetInput);
      } else if (packetInput instanceof UpdateHiredMerchant) {
         return create(this::updateHiredMerchant, packetInput);
      } else if (packetInput instanceof MerchantChat) {
         return create(this::hiredMerchantChat, packetInput);
      } else if (packetInput instanceof MerchantVisitorLeave) {
         return create(this::hiredMerchantVisitorLeave, packetInput);
      } else if (packetInput instanceof MerchantOwnerLeave) {
         return create(this::hiredMerchantOwnerLeave, packetInput);
      } else if (packetInput instanceof MerchantOwnerMaintenanceLeave) {
         return create(this::hiredMerchantOwnerMaintenanceLeave, packetInput);
      } else if (packetInput instanceof MerchantMaintenanceMessage) {
         return create(this::hiredMerchantMaintenanceMessage, packetInput);
      } else if (packetInput instanceof LeaveHiredMerchant) {
         return create(this::leaveHiredMerchant, packetInput);
      } else if (packetInput instanceof MerchantVisitorAdd) {
         return create(this::hiredMerchantVisitorAdd, packetInput);
      } else if (packetInput instanceof PlayerShopChat) {
         return create(this::getPlayerShopChat, packetInput);
      } else if (packetInput instanceof PlayerShopNewVisitor) {
         return create(this::getPlayerShopNewVisitor, packetInput);
      } else if (packetInput instanceof PlayerShopRemoveVisitor) {
         return create(this::getPlayerShopRemoveVisitor, packetInput);
      } else if (packetInput instanceof TradePartnerAdd) {
         return create(this::getTradePartnerAdd, packetInput);
      } else if (packetInput instanceof TradeInvite) {
         return create(this::tradeInvite, packetInput);
      } else if (packetInput instanceof GetTradeMeso) {
         return create(this::getTradeMesoSet, packetInput);
      } else if (packetInput instanceof TradeItemAdd) {
         return create(this::getTradeItemAdd, packetInput);
      } else if (packetInput instanceof PlayerShopItemUpdate) {
         return create(this::getPlayerShopItemUpdate, packetInput);
      } else if (packetInput instanceof PlayerShopOwnerUpdate) {
         return create(this::getPlayerShopOwnerUpdate, packetInput);
      } else if (packetInput instanceof GetPlayerShop) {
         return create(this::getPlayerShop, packetInput);
      } else if (packetInput instanceof GetTradeStart) {
         return create(this::getTradeStart, packetInput);
      } else if (packetInput instanceof TradeConfirmation) {
         return create(this::getTradeConfirmation, packetInput);
      } else if (packetInput instanceof GetTradeResult) {
         return create(this::getTradeResult, packetInput);
      } else if (packetInput instanceof GetMiniGame) {
         return create(this::getMiniGame, packetInput);
      } else if (packetInput instanceof GetMiniGameReady) {
         return create(this::getMiniGameReady, packetInput);
      } else if (packetInput instanceof GetMiniGameUnReady) {
         return create(this::getMiniGameUnReady, packetInput);
      } else if (packetInput instanceof GetMiniGameStart) {
         return create(this::getMiniGameStart, packetInput);
      } else if (packetInput instanceof GetMiniGameSkipOwner) {
         return create(this::getMiniGameSkipOwner, packetInput);
      } else if (packetInput instanceof GetMiniGameRequestTie) {
         return create(this::getMiniGameRequestTie, packetInput);
      } else if (packetInput instanceof GetMiniGameDenyTie) {
         return create(this::getMiniGameDenyTie, packetInput);
      } else if (packetInput instanceof GetMiniRoomError) {
         return create(this::getMiniRoomError, packetInput);
      } else if (packetInput instanceof GetMiniGameSkipVisitor) {
         return create(this::getMiniGameSkipVisitor, packetInput);
      } else if (packetInput instanceof MiniGameMoveOmok) {
         return create(this::getMiniGameMoveOmok, packetInput);
      } else if (packetInput instanceof MiniGameNewVisitor) {
         return create(this::getMiniGameNewVisitor, packetInput);
      } else if (packetInput instanceof MiniGameRemoveVisitor) {
         return create(this::getMiniGameRemoveVisitor, packetInput);
      } else if (packetInput instanceof MiniGameOwnerWin) {
         return create(this::getMiniGameOwnerWin, packetInput);
      } else if (packetInput instanceof MiniGameVisitorWin) {
         return create(this::getMiniGameVisitorWin, packetInput);
      } else if (packetInput instanceof MiniGameTie) {
         return create(this::getMiniGameTie, packetInput);
      } else if (packetInput instanceof MiniGameClose) {
         return create(this::getMiniGameClose, packetInput);
      } else if (packetInput instanceof GetMatchCard) {
         return create(this::getMatchCard, packetInput);
      } else if (packetInput instanceof GetMatchCardStart) {
         return create(this::getMatchCardStart, packetInput);
      } else if (packetInput instanceof NewMatchCardVisitor) {
         return create(this::getMatchCardNewVisitor, packetInput);
      } else if (packetInput instanceof MatchCardSelect) {
         return create(this::getMatchCardSelect, packetInput);
      } else if (packetInput instanceof TradeChat) {
         return create(this::getTradeChat, packetInput);
      } else if (packetInput instanceof PlayerShopErrorMessage) {
         return create(this::shopErrorMessage, packetInput);
      }

      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   /*
    * Possible things for ENTRUSTED_SHOP_CHECK_RESULT
    * 0x0E = 00 = Renaming Failed - Can't find the merchant, 01 = Renaming successful
    * 0x10 = Changes channel to the store (Store is open at Channel 1, do you want to change channels?)
    * 0x11 = You cannot sell any items when managing.. blabla
    * 0x12 = FKING POPUP LOL
    */
   protected byte[] getHiredMerchant(GetHiredMerchant packet) {//Thanks Dustin
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.ROOM.getCode());
      mplew.write(0x05);
      mplew.write(0x04);
      mplew.writeShort(packet.getHiredMerchant().getVisitorSlotThreadsafe(packet.getCharacter()) + 1);
      mplew.writeInt(packet.getHiredMerchant().getItemId());
      mplew.writeMapleAsciiString("Hired Merchant");

      MapleCharacter[] visitors = packet.getHiredMerchant().getVisitors();
      for (int i = 0; i < 3; i++) {
         if (visitors[i] != null) {
            mplew.write(i + 1);
            addCharLook(mplew, visitors[i], false);
            mplew.writeMapleAsciiString(visitors[i].getName());
         }
      }
      mplew.write(-1);
      if (packet.getHiredMerchant().isOwner(packet.getCharacter())) {
         List<Pair<String, Byte>> msgList = packet.getHiredMerchant().getMessages();

         mplew.writeShort(msgList.size());
         for (Pair<String, Byte> stringBytePair : msgList) {
            mplew.writeMapleAsciiString(stringBytePair.getLeft());
            mplew.write(stringBytePair.getRight());
         }
      } else {
         mplew.writeShort(0);
      }
      mplew.writeMapleAsciiString(packet.getHiredMerchant().getOwner());
      if (packet.getHiredMerchant().isOwner(packet.getCharacter())) {
         mplew.writeShort(0);
         mplew.writeShort(packet.getHiredMerchant().getTimeOpen());
         mplew.write(packet.isFirstTime() ? 1 : 0);
         List<MapleSoldItem> sold = packet.getHiredMerchant().getSold();
         mplew.write(sold.size());
         for (MapleSoldItem s : sold) {
            mplew.writeInt(s.itemId());
            mplew.writeShort(s.quantity());
            mplew.writeInt(s.mesos());
            mplew.writeMapleAsciiString(s.buyer());
         }
         mplew.writeInt(packet.getCharacter().getMerchantMeso());//:D?
      }
      mplew.writeMapleAsciiString(packet.getHiredMerchant().getDescription());
      mplew.write(0x10); //TODO SLOTS, which is 16 for most stores...slotMax
      mplew.writeInt(packet.getHiredMerchant().isOwner(packet.getCharacter()) ? packet.getCharacter().getMerchantMeso() : packet.getCharacter().getMeso());
      mplew.write(packet.getHiredMerchant().getItems().size());
      if (packet.getHiredMerchant().getItems().isEmpty()) {
         mplew.write(0);//Hmm??
      } else {
         for (MaplePlayerShopItem item : packet.getHiredMerchant().getItems()) {
            mplew.writeShort(item.bundles());
            mplew.writeShort(item.item().quantity());
            mplew.writeInt(item.price());
            addItemInfo(mplew, item.item(), true);
         }
      }
      return mplew.getPacket();
   }

   protected byte[] updateHiredMerchant(UpdateHiredMerchant packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.UPDATE_MERCHANT.getCode());
      mplew.writeInt(packet.getHiredMerchant().isOwner(packet.getCharacter()) ? packet.getCharacter().getMerchantMeso() : packet.getCharacter().getMeso());
      mplew.write(packet.getHiredMerchant().getItems().size());
      for (MaplePlayerShopItem item : packet.getHiredMerchant().getItems()) {
         mplew.writeShort(item.bundles());
         mplew.writeShort(item.item().quantity());
         mplew.writeInt(item.price());
         addItemInfo(mplew, item.item(), true);
      }
      return mplew.getPacket();
   }

   protected byte[] hiredMerchantChat(MerchantChat packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.CHAT.getCode());
      mplew.write(PlayerInteractionHandler.Action.CHAT_THING.getCode());
      mplew.write(packet.slot());
      mplew.writeMapleAsciiString(packet.message());
      return mplew.getPacket();
   }

   protected byte[] hiredMerchantVisitorLeave(MerchantVisitorLeave packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.EXIT.getCode());
      if (packet.slot() != 0) {
         mplew.write(packet.slot());
      }
      return mplew.getPacket();
   }

   protected byte[] hiredMerchantOwnerLeave(MerchantOwnerLeave packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.REAL_CLOSE_MERCHANT.getCode());
      mplew.write(0);
      return mplew.getPacket();
   }

   protected byte[] hiredMerchantOwnerMaintenanceLeave(MerchantOwnerMaintenanceLeave packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.REAL_CLOSE_MERCHANT.getCode());
      mplew.write(5);
      return mplew.getPacket();
   }

   protected byte[] hiredMerchantMaintenanceMessage(MerchantMaintenanceMessage packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(5);
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.ROOM.getCode());
      mplew.write(0x00);
      mplew.write(0x12);
      return mplew.getPacket();
   }

   protected byte[] leaveHiredMerchant(LeaveHiredMerchant packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.EXIT.getCode());
      mplew.write(packet.slot());
      mplew.write(packet.status());
      return mplew.getPacket();
   }

   protected byte[] hiredMerchantVisitorAdd(MerchantVisitorAdd packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.VISIT.getCode());
      mplew.write(packet.getSlot());
      addCharLook(mplew, packet.getCharacter(), false);
      mplew.writeMapleAsciiString(packet.getCharacter().getName());
      return mplew.getPacket();
   }

   protected byte[] getPlayerShopNewVisitor(PlayerShopNewVisitor packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.VISIT.getCode());
      mplew.write(packet.getSlot());
      addCharLook(mplew, packet.getCharacter(), false);
      mplew.writeMapleAsciiString(packet.getCharacter().getName());
      return mplew.getPacket();
   }

   protected byte[] getPlayerShopRemoveVisitor(PlayerShopRemoveVisitor packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(4);
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.EXIT.getCode());
      if (packet.slot() != 0) {
         mplew.writeShort(packet.slot());
      }
      return mplew.getPacket();
   }

   protected byte[] getTradePartnerAdd(TradePartnerAdd packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.VISIT.getCode());
      mplew.write(1);
      addCharLook(mplew, packet.getCharacter(), false);
      mplew.writeMapleAsciiString(packet.getCharacter().getName());
      return mplew.getPacket();
   }

   protected byte[] tradeInvite(TradeInvite packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.INVITE.getCode());
      mplew.write(3);
      mplew.writeMapleAsciiString(packet.getCharacter().getName());
      mplew.write(new byte[]{(byte) 0xB7, (byte) 0x50, 0, 0});
      return mplew.getPacket();
   }

   protected byte[] getTradeMesoSet(GetTradeMeso packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(8);
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.SET_MESO.getCode());
      mplew.write(packet.number());
      mplew.writeInt(packet.meso());
      return mplew.getPacket();
   }

   protected byte[] getTradeItemAdd(TradeItemAdd packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.SET_ITEMS.getCode());
      mplew.write(packet.number());
      mplew.write(packet.item().position());
      addItemInfo(mplew, packet.item(), true);
      return mplew.getPacket();
   }

   protected byte[] getPlayerShopItemUpdate(PlayerShopItemUpdate packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.UPDATE_MERCHANT.getCode());
      mplew.write(packet.getPlayerShop().getItems().size());
      for (MaplePlayerShopItem item : packet.getPlayerShop().getItems()) {
         mplew.writeShort(item.bundles());
         mplew.writeShort(item.item().quantity());
         mplew.writeInt(item.price());
         addItemInfo(mplew, item.item(), true);
      }
      return mplew.getPacket();
   }

   protected byte[] getPlayerShopOwnerUpdate(PlayerShopOwnerUpdate packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.UPDATE_PLAYERSHOP.getCode());
      mplew.write(packet.position());
      mplew.writeShort(packet.soldItem().quantity());
      mplew.writeMapleAsciiString(packet.soldItem().buyer());

      return mplew.getPacket();
   }

   protected byte[] getPlayerShop(GetPlayerShop packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.ROOM.getCode());
      mplew.write(4);
      mplew.write(4);
      mplew.write(packet.isOwner() ? 0 : 1);

      if (packet.isOwner()) {
         List<MaplePlayerShopSoldItem> sold = packet.getPlayerShop().getSold();
         mplew.write(sold.size());
         for (MaplePlayerShopSoldItem s : sold) {
            mplew.writeInt(s.itemId());
            mplew.writeShort(s.quantity());
            mplew.writeInt(s.mesos());
            mplew.writeMapleAsciiString(s.buyer());
         }
      } else {
         mplew.write(0);
      }

      addCharLook(mplew, packet.getPlayerShop().getOwner(), false);
      mplew.writeMapleAsciiString(packet.getPlayerShop().getOwner().getName());

      MapleCharacter[] visitors = packet.getPlayerShop().getVisitors();
      for (int i = 0; i < 3; i++) {
         if (visitors[i] != null) {
            mplew.write(i + 1);
            addCharLook(mplew, visitors[i], false);
            mplew.writeMapleAsciiString(visitors[i].getName());
         }
      }

      mplew.write(0xFF);
      mplew.writeMapleAsciiString(packet.getPlayerShop().getDescription());
      List<MaplePlayerShopItem> items = packet.getPlayerShop().getItems();
      mplew.write(0x10);  //TODO SLOTS, which is 16 for most stores...slotMax
      mplew.write(items.size());
      for (MaplePlayerShopItem item : items) {
         mplew.writeShort(item.bundles());
         mplew.writeShort(item.item().quantity());
         mplew.writeInt(item.price());
         addItemInfo(mplew, item.item(), true);
      }
      return mplew.getPacket();
   }

   protected byte[] getTradeStart(GetTradeStart packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.ROOM.getCode());
      mplew.write(3);
      mplew.write(2);
      mplew.write(packet.getNumber());
      if (packet.getNumber() == 1) {
         mplew.write(0);
         addCharLook(mplew, packet.getTrade().getPartner().getChr(), false);
         mplew.writeMapleAsciiString(packet.getTrade().getPartner().getChr().getName());
      }
      mplew.write(packet.getNumber());
      addCharLook(mplew, packet.getCharacter(), false);
      mplew.writeMapleAsciiString(packet.getCharacter().getName());
      mplew.write(0xFF);
      return mplew.getPacket();
   }

   protected byte[] getTradeConfirmation(TradeConfirmation packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.CONFIRM.getCode());
      return mplew.getPacket();
   }

   /**
    * Possible values for <code>operation</code>:<br> 2: Trade cancelled by the
    * other character<br> 7: Trade successful<br> 8: Trade unsuccessful<br>
    * 9: Cannot carry more one-of-a-kind items<br> 12: Cannot trade on different maps<br>
    * 13: Cannot trade, game files damaged<br>
    *
    * @return
    */
   protected byte[] getTradeResult(GetTradeResult packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(5);
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.EXIT.getCode());
      mplew.write(packet.number());
      mplew.write(packet.operation());
      return mplew.getPacket();
   }

   protected byte[] getMiniGame(GetMiniGame packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.ROOM.getCode());
      mplew.write(1);
      mplew.write(0);
      mplew.write(packet.isOwner() ? 0 : 1);
      mplew.write(0);
      addCharLook(mplew, packet.getMiniGame().getOwner(), false);
      mplew.writeMapleAsciiString(packet.getMiniGame().getOwner().getName());
      if (packet.getMiniGame().getVisitor() != null) {
         MapleCharacter visitor = packet.getMiniGame().getVisitor();
         mplew.write(1);
         addCharLook(mplew, visitor, false);
         mplew.writeMapleAsciiString(visitor.getName());
      }
      mplew.write(0xFF);
      mplew.write(0);
      mplew.writeInt(1);
      mplew.writeInt(packet.getMiniGame().getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.WIN, true));
      mplew.writeInt(packet.getMiniGame().getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.TIE, true));
      mplew.writeInt(packet.getMiniGame().getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.LOSS, true));
      mplew.writeInt(packet.getMiniGame().getOwnerScore());
      if (packet.getMiniGame().getVisitor() != null) {
         MapleCharacter visitor = packet.getMiniGame().getVisitor();
         mplew.write(1);
         mplew.writeInt(1);
         mplew.writeInt(visitor.getMiniGamePoints(MapleMiniGame.MiniGameResult.WIN, true));
         mplew.writeInt(visitor.getMiniGamePoints(MapleMiniGame.MiniGameResult.TIE, true));
         mplew.writeInt(visitor.getMiniGamePoints(MapleMiniGame.MiniGameResult.LOSS, true));
         mplew.writeInt(packet.getMiniGame().getVisitorScore());
      }
      mplew.write(0xFF);
      mplew.writeMapleAsciiString(packet.getMiniGame().getDescription());
      mplew.write(packet.getPiece());
      mplew.write(0);
      return mplew.getPacket();
   }

   protected byte[] getMiniGameReady(GetMiniGameReady packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.READY.getCode());
      return mplew.getPacket();
   }

   protected byte[] getMiniGameUnReady(GetMiniGameUnReady packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.UN_READY.getCode());
      return mplew.getPacket();
   }

   protected byte[] getMiniGameStart(GetMiniGameStart packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(4);
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.START.getCode());
      mplew.write(packet.loser());
      return mplew.getPacket();
   }

   protected byte[] getMiniGameSkipOwner(GetMiniGameSkipOwner packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(4);
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.SKIP.getCode());
      mplew.write(0x01);
      return mplew.getPacket();
   }

   protected byte[] getMiniGameRequestTie(GetMiniGameRequestTie packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.REQUEST_TIE.getCode());
      return mplew.getPacket();
   }

   protected byte[] getMiniGameDenyTie(GetMiniGameDenyTie packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.ANSWER_TIE.getCode());
      return mplew.getPacket();
   }

   protected byte[] getMiniRoomError(GetMiniRoomError packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(5);
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.ROOM.getCode());
      mplew.write(0);
      mplew.write(packet.status().getValue());
      return mplew.getPacket();
   }

   protected byte[] getMiniGameSkipVisitor(GetMiniGameSkipVisitor packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(4);
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.writeShort(PlayerInteractionHandler.Action.SKIP.getCode());
      return mplew.getPacket();
   }

   protected byte[] getMiniGameMoveOmok(MiniGameMoveOmok packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(12);
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.MOVE_OMOK.getCode());
      mplew.writeInt(packet.move1());
      mplew.writeInt(packet.move2());
      mplew.write(packet.move3());
      return mplew.getPacket();
   }

   protected byte[] getMiniGameNewVisitor(MiniGameNewVisitor packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.VISIT.getCode());
      mplew.write(packet.getSlot());
      addCharLook(mplew, packet.getCharacter(), false);
      mplew.writeMapleAsciiString(packet.getCharacter().getName());
      mplew.writeInt(1);
      mplew.writeInt(packet.getCharacter().getMiniGamePoints(MapleMiniGame.MiniGameResult.WIN, true));
      mplew.writeInt(packet.getCharacter().getMiniGamePoints(MapleMiniGame.MiniGameResult.TIE, true));
      mplew.writeInt(packet.getCharacter().getMiniGamePoints(MapleMiniGame.MiniGameResult.LOSS, true));
      mplew.writeInt(packet.getMiniGame().getVisitorScore());
      return mplew.getPacket();
   }

   protected byte[] getMiniGameRemoveVisitor(MiniGameRemoveVisitor packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.EXIT.getCode());
      mplew.write(1);
      return mplew.getPacket();
   }

   protected byte[] getMiniGameResult(MapleMiniGame game, int tie, int result, int forfeit) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.GET_RESULT.getCode());

      int matchResultType;
      if (tie == 0 && forfeit != 1) {
         matchResultType = 0;
      } else if (tie != 0) {
         matchResultType = 1;
      } else {
         matchResultType = 2;
      }

      mplew.write(matchResultType);
      mplew.writeBool(result == 2); // host/visitor wins

      boolean omok = game.isOmok();
      if (matchResultType == 1) {
         mplew.write(0);
         mplew.writeShort(0);
         mplew.writeInt(game.getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.WIN, omok)); // wins
         mplew.writeInt(game.getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.TIE, omok)); // ties
         mplew.writeInt(game.getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.LOSS, omok)); // losses
         mplew.writeInt(game.getOwnerScore()); // points

         mplew.writeInt(0); // unknown
         mplew.writeInt(game.getVisitor().getMiniGamePoints(MapleMiniGame.MiniGameResult.WIN, omok)); // wins
         mplew.writeInt(game.getVisitor().getMiniGamePoints(MapleMiniGame.MiniGameResult.TIE, omok)); // ties
         mplew.writeInt(game.getVisitor().getMiniGamePoints(MapleMiniGame.MiniGameResult.LOSS, omok)); // losses
         mplew.writeInt(game.getVisitorScore()); // points
         mplew.write(0);
      } else {
         mplew.writeInt(0);
         mplew.writeInt(game.getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.WIN, omok)); // wins
         mplew.writeInt(game.getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.TIE, omok)); // ties
         mplew.writeInt(game.getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.LOSS, omok)); // losses
         mplew.writeInt(game.getOwnerScore()); // points
         mplew.writeInt(0);
         mplew.writeInt(game.getVisitor().getMiniGamePoints(MapleMiniGame.MiniGameResult.WIN, omok)); // wins
         mplew.writeInt(game.getVisitor().getMiniGamePoints(MapleMiniGame.MiniGameResult.TIE, omok)); // ties
         mplew.writeInt(game.getVisitor().getMiniGamePoints(MapleMiniGame.MiniGameResult.LOSS, omok)); // losses
         mplew.writeInt(game.getVisitorScore()); // points
      }

      return mplew.getPacket();
   }

   protected byte[] getMiniGameOwnerWin(MiniGameOwnerWin packet) {
      return getMiniGameResult(packet.getGame(), 0, 1, packet.isForfeit() ? 1 : 0);
   }

   protected byte[] getMiniGameVisitorWin(MiniGameVisitorWin packet) {
      return getMiniGameResult(packet.getGame(), 0, 2, packet.isForfeit() ? 1 : 0);
   }

   protected byte[] getMiniGameTie(MiniGameTie packet) {
      return getMiniGameResult(packet.getGame(), 1, 3, 0);
   }

   protected byte[] getMiniGameClose(MiniGameClose packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(5);
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.EXIT.getCode());
      mplew.writeBool(packet.visitor());
      mplew.write(packet.theType()); /* 2 : CRASH 3 : The room has been closed 4 : You have left the room 5 : You have been expelled  */
      return mplew.getPacket();
   }

   protected byte[] getMatchCard(GetMatchCard packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.ROOM.getCode());
      mplew.write(2);
      mplew.write(2);
      mplew.write(packet.isOwner() ? 0 : 1);
      mplew.write(0);
      addCharLook(mplew, packet.getMiniGame().getOwner(), false);
      mplew.writeMapleAsciiString(packet.getMiniGame().getOwner().getName());
      if (packet.getMiniGame().getVisitor() != null) {
         MapleCharacter visitor = packet.getMiniGame().getVisitor();
         mplew.write(1);
         addCharLook(mplew, visitor, false);
         mplew.writeMapleAsciiString(visitor.getName());
      }
      mplew.write(0xFF);
      mplew.write(0);
      mplew.writeInt(2);
      mplew.writeInt(packet.getMiniGame().getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.WIN, false));
      mplew.writeInt(packet.getMiniGame().getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.TIE, false));
      mplew.writeInt(packet.getMiniGame().getOwner().getMiniGamePoints(MapleMiniGame.MiniGameResult.LOSS, false));

      //set vs
      mplew.writeInt(packet.getMiniGame().getOwnerScore());
      if (packet.getMiniGame().getVisitor() != null) {
         MapleCharacter visitor = packet.getMiniGame().getVisitor();
         mplew.write(1);
         mplew.writeInt(2);
         mplew.writeInt(visitor.getMiniGamePoints(MapleMiniGame.MiniGameResult.WIN, false));
         mplew.writeInt(visitor.getMiniGamePoints(MapleMiniGame.MiniGameResult.TIE, false));
         mplew.writeInt(visitor.getMiniGamePoints(MapleMiniGame.MiniGameResult.LOSS, false));
         mplew.writeInt(packet.getMiniGame().getVisitorScore());
      }
      mplew.write(0xFF);
      mplew.writeMapleAsciiString(packet.getMiniGame().getDescription());
      mplew.write(packet.getPiece());
      mplew.write(0);
      return mplew.getPacket();
   }

   protected byte[] getMatchCardStart(GetMatchCardStart packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.START.getCode());
      mplew.write(packet.getLoser());

      int last;
      if (packet.getMiniGame().getMatchesToWin() > 10) {
         last = 30;
      } else if (packet.getMiniGame().getMatchesToWin() > 6) {
         last = 20;
      } else {
         last = 12;
      }

      mplew.write(last);
      for (int i = 0; i < last; i++) {
         mplew.writeInt(packet.getMiniGame().getCardId(i));
      }
      return mplew.getPacket();
   }

   protected byte[] getMatchCardNewVisitor(NewMatchCardVisitor packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.VISIT.getCode());
      mplew.write(packet.getSlot());
      addCharLook(mplew, packet.getCharacter(), false);
      mplew.writeMapleAsciiString(packet.getCharacter().getName());
      mplew.writeInt(1);
      mplew.writeInt(packet.getCharacter().getMiniGamePoints(MapleMiniGame.MiniGameResult.WIN, false));
      mplew.writeInt(packet.getCharacter().getMiniGamePoints(MapleMiniGame.MiniGameResult.TIE, false));
      mplew.writeInt(packet.getCharacter().getMiniGamePoints(MapleMiniGame.MiniGameResult.LOSS, false));
      mplew.writeInt(packet.getMiniGame().getVisitorScore());
      return mplew.getPacket();
   }

   protected byte[] getMatchCardSelect(MatchCardSelect packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(6);
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.SELECT_CARD.getCode());
      mplew.write(packet.turn());
      if (packet.turn() == 1) {
         mplew.write(packet.slot());
      } else if (packet.turn() == 0) {
         mplew.write(packet.slot());
         mplew.write(packet.firstSlot());
         mplew.write(packet.theType());
      }
      return mplew.getPacket();
   }

   protected byte[] getPlayerShopChat(PlayerShopChat packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.CHAT.getCode());
      mplew.write(PlayerInteractionHandler.Action.CHAT_THING.getCode());
      mplew.write(packet.slot());
      mplew.writeMapleAsciiString(packet.name() + " : " + packet.chat());
      return mplew.getPacket();
   }

   protected byte[] getTradeChat(TradeChat packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(PlayerInteractionHandler.Action.CHAT.getCode());
      mplew.write(PlayerInteractionHandler.Action.CHAT_THING.getCode());
      mplew.write(packet.owner() ? 0 : 1);
      mplew.writeMapleAsciiString(packet.name() + " : " + packet.chat());
      return mplew.getPacket();
   }

   protected byte[] shopErrorMessage(PlayerShopErrorMessage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.PLAYER_INTERACTION.getValue());
      mplew.write(0x0A);
      mplew.write(packet.theType());
      mplew.write(packet.error());
      return mplew.getPacket();
   }
}