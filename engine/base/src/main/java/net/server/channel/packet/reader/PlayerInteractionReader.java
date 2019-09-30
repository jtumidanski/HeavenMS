package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.handlers.PlayerInteractionHandler;
import net.server.channel.packet.ChatPlayerInteraction;
import net.server.channel.packet.interaction.AddItemPlayerInteractionPacket;
import net.server.channel.packet.interaction.AnswerTiePlayerInteractionPacket;
import net.server.channel.packet.interaction.BanPlayerPlayerInteractionPacket;
import net.server.channel.packet.interaction.BasePlayerInteractionPacket;
import net.server.channel.packet.interaction.BuyPlayerInteractionPacket;
import net.server.channel.packet.interaction.CancelExitAfterGamePlayerInteractionPacket;
import net.server.channel.packet.interaction.CloseMerchantPlayerInteractionPacket;
import net.server.channel.packet.interaction.ConfirmPlayerInteractionPacket;
import net.server.channel.packet.interaction.CreateMatchCardPlayerInteractionPacket;
import net.server.channel.packet.interaction.CreateOmokPlayerInteractionPacket;
import net.server.channel.packet.interaction.CreateShopPlayerInteractionPacket;
import net.server.channel.packet.interaction.CreateTradePlayerInteractionPacket;
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
import server.channel.PlayerInteractionAction;
import tools.data.input.SeekableLittleEndianAccessor;

public class PlayerInteractionReader implements PacketReader<BasePlayerInteractionPacket> {
   @Override
   public BasePlayerInteractionPacket read(SeekableLittleEndianAccessor accessor) {
      byte mode = accessor.readByte();
      if (mode == PlayerInteractionAction.CREATE.getValue()) {
         return readCreate(accessor, mode);
      } else if (mode == PlayerInteractionAction.INVITE.getValue()) {
         return readInvite(accessor, mode);
      } else if (mode == PlayerInteractionAction.DECLINE.getValue()) {
         return readDecline(mode);
      } else if (mode == PlayerInteractionAction.VISIT.getValue()) {
         return readVisit(accessor, mode);
      } else if (mode == PlayerInteractionAction.CHAT.getValue()) {
         return readChat(accessor, mode);
      } else if (mode == PlayerInteractionAction.OPEN_STORE.getValue()) {
         return readOpenStore(accessor, mode);
      } else if (mode == PlayerInteractionAction.OPEN_CASH.getValue()) {
         return readOpenCash(accessor, mode);
      } else if (mode == PlayerInteractionAction.READY.getValue()) {
         return readReady(mode);
      } else if (mode == PlayerInteractionAction.UN_READY.getValue()) {
         return readUnReady(mode);
      } else if (mode == PlayerInteractionAction.START.getValue()) {
         return readStart(mode);
      } else if (mode == PlayerInteractionAction.GIVE_UP.getValue()) {
         return readGiveUp(mode);
      } else if (mode == PlayerInteractionAction.REQUEST_TIE.getValue()) {
         return readRequestTie(mode);
      } else if (mode == PlayerInteractionAction.ANSWER_TIE.getValue()) {
         return readAnswerTie(accessor, mode);
      } else if (mode == PlayerInteractionAction.SKIP.getValue()) {
         return readSkip(mode);
      } else if (mode == PlayerInteractionAction.MOVE_OMOK.getValue()) {
         return readOmokMove(accessor, mode);
      } else if (mode == PlayerInteractionAction.SELECT_CARD.getValue()) {
         return readSelectCard(accessor, mode);
      } else if (mode == PlayerInteractionAction.SET_MESO.getValue()) {
         return readSetMeso(accessor, mode);
      } else if (mode == PlayerInteractionAction.SET_ITEMS.getValue()) {
         return readSetItems(accessor, mode);
      } else if (mode == PlayerInteractionAction.CONFIRM.getValue()) {
         return readConfirm(mode);
      } else if (mode == PlayerInteractionAction.ADD_ITEM.getValue() || mode == PlayerInteractionAction.PUT_ITEM.getValue()) {
         return readAddItem(accessor, mode);
      } else if (mode == PlayerInteractionAction.REMOVE_ITEM.getValue()) {
         return readRemoveItem(accessor, mode);
      } else if (mode == PlayerInteractionAction.MERCHANT_MESO.getValue()) {
         return readMerchantMeso(mode);
      } else if (mode == PlayerInteractionAction.MERCHANT_ORGANIZE.getValue()) {
         return readMerchantOrganize(mode);
      } else if (mode == PlayerInteractionAction.BUY.getValue() || mode == PlayerInteractionAction.MERCHANT_BUY.getValue()) {
         return readBuy(accessor, mode);
      } else if (mode == PlayerInteractionAction.TAKE_ITEM_BACK.getValue()) {
         return readTakeItemBack(accessor, mode);
      } else if (mode == PlayerInteractionAction.CLOSE_MERCHANT.getValue()) {
         return readCloseMerchant(mode);
      } else if (mode == PlayerInteractionAction.MAINTENANCE_OFF.getValue()) {
         return readMaintenanceOff(mode);
      } else if (mode == PlayerInteractionAction.BAN_PLAYER.getValue()) {
         return readBanPlayer(accessor, mode);
      } else if (mode == PlayerInteractionAction.EXPEL.getValue()) {
         return readExpel(mode);
      } else if (mode == PlayerInteractionAction.EXIT_AFTER_GAME.getValue()) {
         return readExitAfterGame(mode);
      } else if (mode == PlayerInteractionAction.CANCEL_EXIT_AFTER_GAME.getValue()) {
         return readCancelExitAfterGame(mode);
      }

      return new BasePlayerInteractionPacket(mode);
   }

   private BasePlayerInteractionPacket readCancelExitAfterGame(byte mode) {
      return new CancelExitAfterGamePlayerInteractionPacket(mode);
   }

   private BasePlayerInteractionPacket readExitAfterGame(byte mode) {
      return new ExitAfterGamePlayerInteractionPacket(mode);
   }

   private BasePlayerInteractionPacket readExpel(byte mode) {
      return new ExpelPlayerInteractionPacket(mode);
   }

   private BasePlayerInteractionPacket readBanPlayer(SeekableLittleEndianAccessor accessor, byte mode) {
      accessor.skip(1);
      String name = accessor.readMapleAsciiString();
      return new BanPlayerPlayerInteractionPacket(mode, name);
   }

   private BasePlayerInteractionPacket readMaintenanceOff(byte mode) {
      return new MerchantMaintenanceOffPlayerInteractionPacket(mode);
   }

   private BasePlayerInteractionPacket readCloseMerchant(byte mode) {
      return new CloseMerchantPlayerInteractionPacket(mode);
   }

   private BasePlayerInteractionPacket readTakeItemBack(SeekableLittleEndianAccessor accessor, byte mode) {
      int slot = accessor.readShort();
      return new TakeItemBackPlayerInteractionPacket(mode, slot);
   }

   private BasePlayerInteractionPacket readBuy(SeekableLittleEndianAccessor accessor, byte mode) {
      int itemId = accessor.readByte();
      short quantity = accessor.readShort();
      return new BuyPlayerInteractionPacket(mode, itemId, quantity);
   }

   private BasePlayerInteractionPacket readMerchantOrganize(byte mode) {
      return new MerchantOrganizePlayerInteractionPacket(mode);
   }

   private BasePlayerInteractionPacket readMerchantMeso(byte mode) {
      return new MesoMerchantPlayerInteractionPacket(mode);
   }

   private BasePlayerInteractionPacket readRemoveItem(SeekableLittleEndianAccessor accessor, byte mode) {
      int slot = accessor.readShort();
      return new RemoveItemPlayerInteractionPacket(mode, slot);
   }

   private BasePlayerInteractionPacket readAddItem(SeekableLittleEndianAccessor accessor, byte mode) {
      byte slotType = accessor.readByte();
      short slot = accessor.readShort();
      short bundles = accessor.readShort();
      short perBundle = accessor.readShort();
      int price = accessor.readInt();
      return new AddItemPlayerInteractionPacket(mode, slotType, slot, bundles, perBundle, price);
   }

   private BasePlayerInteractionPacket readConfirm(byte mode) {
      return new ConfirmPlayerInteractionPacket(mode);
   }

   private BasePlayerInteractionPacket readSetItems(SeekableLittleEndianAccessor accessor, byte mode) {
      byte slotType = accessor.readByte();
      short pos = accessor.readShort();
      short quantity = accessor.readShort();
      byte targetSlot = accessor.readByte();
      return new SetItemsPlayerInteractionPacket(mode, slotType, pos, quantity, targetSlot);
   }

   private BasePlayerInteractionPacket readSetMeso(SeekableLittleEndianAccessor accessor, byte mode) {
      int amount = accessor.readInt();
      return new SetMesoPlayerInteractionPacket(mode, amount);
   }

   private BasePlayerInteractionPacket readSelectCard(SeekableLittleEndianAccessor accessor, byte mode) {
      int turn = accessor.readByte(); // 1st turn = 1; 2nd turn = 0
      int slot = accessor.readByte(); // slot
      return new SelectCardPlayerInteractionPacket(mode, turn, slot);
   }

   private BasePlayerInteractionPacket readOmokMove(SeekableLittleEndianAccessor accessor, byte mode) {
      int x = accessor.readInt(); // x point
      int y = accessor.readInt(); // y point
      int type = accessor.readByte(); // piece ( 1 or 2; Owner has one piece, visitor has another, it switches every game.)
      return new OmokMovePlayerInteractionPacket(mode, x, y, type);
   }

   private BasePlayerInteractionPacket readSkip(byte mode) {
      return new SkipPlayerInteractionPacket(mode);
   }

   private BasePlayerInteractionPacket readAnswerTie(SeekableLittleEndianAccessor accessor, byte mode) {
      return new AnswerTiePlayerInteractionPacket(mode, accessor.readByte() != 0);
   }

   private BasePlayerInteractionPacket readRequestTie(byte mode) {
      return new RequestTiePlayerInteractionPacket(mode);
   }

   private BasePlayerInteractionPacket readGiveUp(byte mode) {
      return new ForfeitPlayerInteractionPacket(mode);
   }

   private BasePlayerInteractionPacket readStart(byte mode) {
      return new StartPlayerInteractionPacket(mode);
   }

   private BasePlayerInteractionPacket readUnReady(byte mode) {
      return new UnReadyPlayerInteractionPacket(mode);
   }

   private BasePlayerInteractionPacket readReady(byte mode) {
      return new ReadyPlayerInteractionPacket(mode);
   }

   private BasePlayerInteractionPacket readOpenCash(SeekableLittleEndianAccessor accessor, byte mode) {
      accessor.readShort();
      int birthday = accessor.readInt();
      return new OpenCashPlayerInteractionPacket(mode, birthday);
   }

   private BasePlayerInteractionPacket readOpenStore(SeekableLittleEndianAccessor accessor, byte mode) {
      accessor.readByte();    //01
      return new OpenStorePlayerInteractionPacket(mode);
   }

   private BasePlayerInteractionPacket readChat(SeekableLittleEndianAccessor accessor, byte mode) {
      String message = accessor.readMapleAsciiString();
      return new ChatPlayerInteraction(mode, message);
   }

   private BasePlayerInteractionPacket readVisit(SeekableLittleEndianAccessor accessor, byte mode) {
      int oid = accessor.readInt();
      accessor.skip(1);
      String pw = accessor.available() > 1 ? accessor.readMapleAsciiString() : "";
      return new VisitPlayerInteractionPacket(mode, oid, pw);
   }

   private BasePlayerInteractionPacket readDecline(byte mode) {
      return new DeclinePlayerInteractionPacket(mode);
   }

   private BasePlayerInteractionPacket readInvite(SeekableLittleEndianAccessor accessor, byte mode) {
      int otherCid = accessor.readInt();
      return new InvitePlayerInteractionPacket(mode, otherCid);
   }

   private BasePlayerInteractionPacket readCreate(SeekableLittleEndianAccessor accessor, byte mode) {
      byte createType = accessor.readByte();
      if (createType == 3) {
         return readCreateTrade(mode, createType);
      } else if (createType == 1) {
         return readCreateOmok(accessor, mode, createType);
      } else if (createType == 2) {
         return readCreateMatchCard(accessor, mode, createType);
      } else if (createType == 4 || createType == 5) {
         return readCreateShop(accessor, mode, createType);
      }
      return new CreateTradePlayerInteractionPacket(mode, createType);
   }

   private BasePlayerInteractionPacket readCreateShop(SeekableLittleEndianAccessor accessor, byte mode, byte createType) {
      String desc = accessor.readMapleAsciiString();
      accessor.skip(3);
      int itemId = accessor.readInt();
      return new CreateShopPlayerInteractionPacket(mode, createType, desc, itemId);
   }

   private interface QuadFunction<T, U, V, W, X> {
      T apply(U u, V v, W w, X x);
   }

   private BasePlayerInteractionPacket produceGame(SeekableLittleEndianAccessor accessor, QuadFunction<BasePlayerInteractionPacket, String, Boolean, String, Integer> supplier) {
      String desc = accessor.readMapleAsciiString();
      String pw;
      boolean hasPassword = accessor.readByte() != 0;

      if (hasPassword) {
         pw = accessor.readMapleAsciiString();
      } else {
         pw = "";
      }

      int type = accessor.readByte();
      return supplier.apply(desc, hasPassword, pw, type);
   }

   private BasePlayerInteractionPacket readCreateMatchCard(SeekableLittleEndianAccessor accessor, byte mode, byte createType) {
      return produceGame(accessor, (desc, hasPassword, pw, type) -> new CreateMatchCardPlayerInteractionPacket(mode, createType, desc, hasPassword, pw, type));
   }

   private BasePlayerInteractionPacket readCreateOmok(SeekableLittleEndianAccessor accessor, byte mode, byte createType) {
      return produceGame(accessor, (desc, hasPassword, pw, type) -> new CreateOmokPlayerInteractionPacket(mode, createType, desc, hasPassword, pw, type));
   }

   private BasePlayerInteractionPacket readCreateTrade(byte mode, byte createType) {
      return new CreateTradePlayerInteractionPacket(mode, createType);
   }
}
