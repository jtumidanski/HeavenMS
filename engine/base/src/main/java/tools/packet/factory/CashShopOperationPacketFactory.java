package tools.packet.factory;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import client.inventory.Item;
import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.cashshop.CashShopOperationSubOp;
import tools.packet.cashshop.SendMapleLife;
import tools.packet.cashshop.SendMapleLifeError;
import tools.packet.cashshop.SendMapleNameLifeError;
import tools.packet.cashshop.ShowCash;
import tools.packet.cashshop.operation.DeleteCashItem;
import tools.packet.Gift;
import tools.packet.PacketInput;
import tools.packet.cashshop.operation.PutIntoCashInventory;
import tools.packet.cashshop.operation.RefundCashItem;
import tools.packet.cashshop.operation.ShowBoughtCashItem;
import tools.packet.cashshop.operation.ShowBoughtCashPackageSuccess;
import tools.packet.cashshop.operation.ShowBoughtCashRing;
import tools.packet.cashshop.operation.ShowBoughtCharacterSlots;
import tools.packet.cashshop.operation.ShowBoughtInventorySlots;
import tools.packet.cashshop.operation.ShowBoughtQuestItem;
import tools.packet.cashshop.operation.ShowBoughtStorageSlots;
import tools.packet.cashshop.operation.ShowCashInventory;
import tools.packet.cashshop.operation.ShowCashShopMessage;
import tools.packet.cashshop.operation.ShowCouponRedeemSuccess;
import tools.packet.cashshop.operation.ShowGiftSucceed;
import tools.packet.cashshop.operation.ShowGifts;
import tools.packet.cashshop.operation.ShowNameChangeSuccess;
import tools.packet.cashshop.operation.ShowWishList;
import tools.packet.cashshop.operation.ShowWorldTransferSuccess;
import tools.packet.cashshop.operation.TakeFromCashInventory;

public class CashShopOperationPacketFactory extends AbstractCashShopPacketFactory {
   private static CashShopOperationPacketFactory instance;

   public static CashShopOperationPacketFactory getInstance() {
      if (instance == null) {
         instance = new CashShopOperationPacketFactory();
      }
      return instance;
   }

   private CashShopOperationPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof ShowWorldTransferSuccess) {
         return create(CashShopOperationSubOp.WORLD_TRANSFER_SUCCESS, this::showWorldTransferSuccess, packetInput);
      } else if (packetInput instanceof ShowNameChangeSuccess) {
         return create(CashShopOperationSubOp.NAME_CHANGE_SUCCESS, this::showNameChangeSuccess, packetInput);
      } else if (packetInput instanceof ShowCouponRedeemSuccess) {
         return create(CashShopOperationSubOp.COUPON_REDEEMED_SUCCESS, this::showCouponRedeemedItems, packetInput);
      } else if (packetInput instanceof ShowBoughtCashPackageSuccess) {
         return create(CashShopOperationSubOp.BOUGHT_CASH_PACKAGE_SUCCESS, this::showBoughtCashPackage, packetInput);
      } else if (packetInput instanceof ShowBoughtQuestItem) {
         return create(CashShopOperationSubOp.BOUGHT_QUEST_ITEM_SUCCESS, this::showBoughtQuestItem, packetInput);
      } else if (packetInput instanceof ShowWishList) {
         CashShopOperationSubOp subOp = ((ShowWishList) packetInput).update() ? CashShopOperationSubOp.SHOW_WISHLIST_UPDATE : CashShopOperationSubOp.SHOW_WISHLIST;
         return create(subOp, this::showWishList, packetInput);
      } else if (packetInput instanceof ShowBoughtCashItem) {
         return create(CashShopOperationSubOp.BOUGHT_CASH_ITEM_SUCCESS, this::showBoughtCashItem, packetInput);
      } else if (packetInput instanceof ShowBoughtCashRing) {
         return create(CashShopOperationSubOp.BOUGHT_CASH_RING_SUCCESS, this::showBoughtCashRing, packetInput);
      } else if (packetInput instanceof ShowCashShopMessage) {
         return create(CashShopOperationSubOp.CASH_SHOP_MESSAGE, this::showCashShopMessage, packetInput, 4);
      } else if (packetInput instanceof ShowCashInventory) {
         return create(CashShopOperationSubOp.CASH_INVENTORY, this::showCashInventory, packetInput);
      } else if (packetInput instanceof ShowGifts) {
         return create(CashShopOperationSubOp.GIFTS, this::showGifts, packetInput);
      } else if (packetInput instanceof ShowGiftSucceed) {
         return create(CashShopOperationSubOp.GIFT_SUCCEED, this::showGiftSucceed, packetInput); //0x5D, Couldn't be sent
      } else if (packetInput instanceof ShowBoughtInventorySlots) {
         return create(CashShopOperationSubOp.BOUGHT_INVENTORY_SLOTS, this::showBoughtInventorySlots, packetInput, 6);
      } else if (packetInput instanceof ShowBoughtStorageSlots) {
         return create(CashShopOperationSubOp.BOUGHT_STORAGE_SLOTS, this::showBoughtStorageSlots, packetInput, 5);
      } else if (packetInput instanceof ShowBoughtCharacterSlots) {
         return create(CashShopOperationSubOp.BOUGHT_CHARACTER_SLOTS, this::showBoughtCharacterSlot, packetInput, 5);
      } else if (packetInput instanceof TakeFromCashInventory) {
         return create(CashShopOperationSubOp.TAKE_FROM_CASH_INVENTORY, this::takeFromCashInventory, packetInput);
      } else if (packetInput instanceof DeleteCashItem) {
         return create(CashShopOperationSubOp.DELETE_CASH_ITEM, this::deleteCashItem, packetInput);
      } else if (packetInput instanceof RefundCashItem) {
         return create(CashShopOperationSubOp.REFUND_CASH_ITEM, this::refundCashItem, packetInput);
      } else if (packetInput instanceof PutIntoCashInventory) {
         return create(CashShopOperationSubOp.PUT_INTO_CASH_INVENTORY, this::putIntoCashInventory, packetInput);
      } else if (packetInput instanceof ShowCash) {
         return create(this::showCash, packetInput);
      } else if (packetInput instanceof SendMapleLife) {
         return create(this::sendMapleLifeCharacterInfo, packetInput);
      } else if (packetInput instanceof SendMapleNameLifeError) {
         return create(this::sendMapleLifeNameError, packetInput);
      } else if (packetInput instanceof SendMapleLifeError) {
         return create(this::sendMapleLifeError, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected <T extends PacketInput> byte[] create(CashShopOperationSubOp subOp, BiConsumer<MaplePacketLittleEndianWriter, T> decorator, PacketInput packetInput, Integer size) {
      return create((Function<T, byte[]>) castInput -> {
         final MaplePacketLittleEndianWriter writer = newWriter(size);
         writer.writeShort(SendOpcode.CASHSHOP_OPERATION.getValue());
         writer.write(subOp.getValue());
         decorator.accept(writer, castInput);
         return writer.getPacket();
      }, packetInput);
   }

   protected <T extends PacketInput> byte[] create(CashShopOperationSubOp subOp, BiConsumer<MaplePacketLittleEndianWriter, T> decorator, PacketInput packetInput) {
      return create(subOp, decorator, packetInput, MaplePacketLittleEndianWriter.DEFAULT_SIZE);
   }

   protected void showWorldTransferSuccess(MaplePacketLittleEndianWriter writer, ShowWorldTransferSuccess packet) {
      addCashItemInformation(writer, packet.item(), packet.accountId());
   }

   protected void showNameChangeSuccess(MaplePacketLittleEndianWriter writer, ShowNameChangeSuccess packet) {
      addCashItemInformation(writer, packet.item(), packet.accountId());
   }

   protected void showCouponRedeemedItems(MaplePacketLittleEndianWriter writer, ShowCouponRedeemSuccess packet) {
      writer.write((byte) packet.cashItems().size());
      for (Item item : packet.cashItems()) {
         addCashItemInformation(writer, item, packet.accountId());
      }
      writer.writeInt(packet.maplePoints());
      writer.writeInt(packet.items().size());
      for (Pair<Integer, Integer> itemPair : packet.items()) {
         int quantity = itemPair.getLeft();
         writer.writeShort((short) quantity); //quantity (0 = 1 for cash items)
         writer.writeShort(0x1F); //0 = ?, >=0x20 = ?, <0x20 = ? (does nothing?)
         writer.writeInt(itemPair.getRight());
      }
      writer.writeInt(packet.mesos());
   }

   protected void showBoughtCashPackage(MaplePacketLittleEndianWriter writer, ShowBoughtCashPackageSuccess packet) {
      writer.write(packet.cashPacket().size());
      for (Item item : packet.cashPacket()) {
         addCashItemInformation(writer, item, packet.accountId());
      }
      writer.writeShort(0);
   }

   protected void showBoughtQuestItem(MaplePacketLittleEndianWriter writer, ShowBoughtQuestItem packet) {
      writer.writeInt(1);
      writer.writeShort(1);
      writer.write(0x0B);
      writer.write(0);
      writer.writeInt(packet.itemId());
   }

   protected void showWishList(MaplePacketLittleEndianWriter writer, ShowWishList packet) {
      packet.sns().forEach(writer::writeInt);
      IntStream.range(0, 10 - packet.sns().size()).forEach(i -> writer.writeInt(0));
   }

   protected void showBoughtCashItem(MaplePacketLittleEndianWriter writer, ShowBoughtCashItem packet) {
      addCashItemInformation(writer, packet.item(), packet.accountId());
   }

   protected void showBoughtCashRing(MaplePacketLittleEndianWriter writer, ShowBoughtCashRing packet) {
      addCashItemInformation(writer, packet.ring(), packet.accountId());
      writer.writeMapleAsciiString(packet.recipient());
      writer.writeInt(packet.ring().id());
      writer.writeShort(1); //quantity
   }

   protected void showCashShopMessage(MaplePacketLittleEndianWriter writer, ShowCashShopMessage packet) {
      writer.write(packet.message().getValue());
   }

   protected void showCashInventory(MaplePacketLittleEndianWriter writer, ShowCashInventory packet) {
      writer.writeShort(packet.items().size());
      for (Item item : packet.items()) {
         addCashItemInformation(writer, item, packet.accountId());
      }
      writer.writeShort(packet.storageSlots());
      writer.writeShort(packet.characterSlots());
   }

   protected void showGifts(MaplePacketLittleEndianWriter writer, ShowGifts packet) {
      writer.writeShort(packet.gifts().size());
      for (Gift gift : packet.gifts()) {
         addCashItemInformation(writer, gift.item(), 0, gift.message());
      }
   }

   protected void showGiftSucceed(MaplePacketLittleEndianWriter writer, ShowGiftSucceed packet) {
      writer.writeMapleAsciiString(packet.to());
      writer.writeInt(packet.itemId());
      writer.writeShort(packet.count());
      writer.writeInt(packet.price());
   }

   protected void showBoughtInventorySlots(MaplePacketLittleEndianWriter writer, ShowBoughtInventorySlots packet) {
      writer.write(packet.inventoryType());
      writer.writeShort(packet.slots());
   }

   protected void showBoughtStorageSlots(MaplePacketLittleEndianWriter writer, ShowBoughtStorageSlots packet) {
      writer.writeShort(packet.slots());
   }

   protected void showBoughtCharacterSlot(MaplePacketLittleEndianWriter writer, ShowBoughtCharacterSlots packet) {
      writer.writeShort(packet.slots());
   }

   protected void takeFromCashInventory(MaplePacketLittleEndianWriter writer, TakeFromCashInventory packet) {
      writer.writeShort(packet.item().position());
      addItemInfo(writer, packet.item(), true);
   }

   protected void deleteCashItem(MaplePacketLittleEndianWriter writer, DeleteCashItem packet) {
      writer.writeLong(packet.item().cashId());
   }

   protected void refundCashItem(MaplePacketLittleEndianWriter writer, RefundCashItem packet) {
      writer.writeLong(packet.item().cashId());
      writer.writeInt(packet.maplePoints());
   }

   protected void putIntoCashInventory(MaplePacketLittleEndianWriter writer, PutIntoCashInventory packet) {
      addCashItemInformation(writer, packet.item(), packet.accountId());
   }

   protected byte[] showCash(ShowCash packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.QUERY_CASH_RESULT.getValue());
      mplew.writeInt(packet.nxCredit());
      mplew.writeInt(packet.maplePoint());
      mplew.writeInt(packet.nxPrepaid());
      return mplew.getPacket();
   }

   protected byte[] sendMapleLifeCharacterInfo(SendMapleLife packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MAPLELIFE_RESULT.getValue());
      mplew.writeInt(0);
      return mplew.getPacket();
   }

   protected byte[] sendMapleLifeNameError(SendMapleNameLifeError packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MAPLELIFE_RESULT.getValue());
      mplew.writeInt(2);
      mplew.writeInt(3);
      mplew.write(0);
      return mplew.getPacket();
   }

   protected byte[] sendMapleLifeError(SendMapleLifeError packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.MAPLELIFE_ERROR.getValue());
      mplew.write(0);
      mplew.writeInt(packet.code());
      return mplew.getPacket();
   }
}