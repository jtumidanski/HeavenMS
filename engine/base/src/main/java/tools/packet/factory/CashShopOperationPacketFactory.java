package tools.packet.factory;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import client.inventory.Item;
import net.opcodes.SendOpcode;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.Gift;
import tools.packet.PacketInput;
import tools.packet.cashshop.CashShopOperationSubOp;
import tools.packet.cashshop.SendMapleLife;
import tools.packet.cashshop.SendMapleLifeError;
import tools.packet.cashshop.SendMapleNameLifeError;
import tools.packet.cashshop.ShowCash;
import tools.packet.cashshop.operation.DeleteCashItem;
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
      registry.setHandler(ShowWorldTransferSuccess.class, packet -> create(CashShopOperationSubOp.WORLD_TRANSFER_SUCCESS, this::showWorldTransferSuccess, packet));
      registry.setHandler(ShowNameChangeSuccess.class, packet -> create(CashShopOperationSubOp.NAME_CHANGE_SUCCESS, this::showNameChangeSuccess, packet));
      registry.setHandler(ShowCouponRedeemSuccess.class, packet -> create(CashShopOperationSubOp.COUPON_REDEEMED_SUCCESS, this::showCouponRedeemedItems, packet));
      registry.setHandler(ShowBoughtCashPackageSuccess.class, packet -> create(CashShopOperationSubOp.BOUGHT_CASH_PACKAGE_SUCCESS, this::showBoughtCashPackage, packet));
      registry.setHandler(ShowBoughtQuestItem.class, packet -> create(CashShopOperationSubOp.BOUGHT_QUEST_ITEM_SUCCESS, this::showBoughtQuestItem, packet));
      registry.setHandler(ShowBoughtCashItem.class, packet -> create(CashShopOperationSubOp.BOUGHT_CASH_ITEM_SUCCESS, this::showBoughtCashItem, packet));
      registry.setHandler(ShowBoughtCashRing.class, packet -> create(CashShopOperationSubOp.BOUGHT_CASH_RING_SUCCESS, this::showBoughtCashRing, packet));
      registry.setHandler(ShowCashShopMessage.class, packet -> create(CashShopOperationSubOp.CASH_SHOP_MESSAGE, this::showCashShopMessage, packet));
      registry.setHandler(ShowCashInventory.class, packet -> create(CashShopOperationSubOp.CASH_INVENTORY, this::showCashInventory, packet));
      registry.setHandler(ShowGifts.class, packet -> create(CashShopOperationSubOp.GIFTS, this::showGifts, packet));
      registry.setHandler(ShowGiftSucceed.class, packet -> create(CashShopOperationSubOp.GIFT_SUCCEED, this::showGiftSucceed, packet)); //0x5D, Couldn't n)t
      registry.setHandler(ShowBoughtInventorySlots.class, packet -> create(CashShopOperationSubOp.BOUGHT_INVENTORY_SLOTS, this::showBoughtInventorySlots, packet));
      registry.setHandler(ShowBoughtStorageSlots.class, packet -> create(CashShopOperationSubOp.BOUGHT_STORAGE_SLOTS, this::showBoughtStorageSlots, packet));
      registry.setHandler(ShowBoughtCharacterSlots.class, packet -> create(CashShopOperationSubOp.BOUGHT_CHARACTER_SLOTS, this::showBoughtCharacterSlot, packet));
      registry.setHandler(TakeFromCashInventory.class, packet -> create(CashShopOperationSubOp.TAKE_FROM_CASH_INVENTORY, this::takeFromCashInventory, packet));
      registry.setHandler(DeleteCashItem.class, packet -> create(CashShopOperationSubOp.DELETE_CASH_ITEM, this::deleteCashItem, packet));
      registry.setHandler(RefundCashItem.class, packet -> create(CashShopOperationSubOp.REFUND_CASH_ITEM, this::refundCashItem, packet));
      registry.setHandler(PutIntoCashInventory.class, packet -> create(CashShopOperationSubOp.PUT_INTO_CASH_INVENTORY, this::putIntoCashInventory, packet));
      registry.setHandler(ShowCash.class, packet -> create(SendOpcode.QUERY_CASH_RESULT, this::showCash, packet));
      registry.setHandler(SendMapleLife.class, packet -> create(SendOpcode.MAPLELIFE_RESULT, this::sendMapleLifeCharacterInfo, packet));
      registry.setHandler(SendMapleNameLifeError.class, packet -> create(SendOpcode.MAPLELIFE_RESULT, this::sendMapleLifeNameError, packet));
      registry.setHandler(SendMapleLifeError.class, packet -> create(SendOpcode.MAPLELIFE_ERROR, this::sendMapleLifeError, packet));
      registry.setHandler(ShowWishList.class, packet -> {
         CashShopOperationSubOp subOp = ((ShowWishList) packet).update() ? CashShopOperationSubOp.SHOW_WISHLIST_UPDATE : CashShopOperationSubOp.SHOW_WISHLIST;
         return create(subOp, this::showWishList, packet);
      });
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

   protected void showCash(MaplePacketLittleEndianWriter writer, ShowCash packet) {
      writer.writeInt(packet.nxCredit());
      writer.writeInt(packet.maplePoint());
      writer.writeInt(packet.nxPrepaid());
   }

   protected void sendMapleLifeCharacterInfo(MaplePacketLittleEndianWriter writer, SendMapleLife packet) {
      writer.writeInt(0);
   }

   protected void sendMapleLifeNameError(MaplePacketLittleEndianWriter writer, SendMapleNameLifeError packet) {
      writer.writeInt(2);
      writer.writeInt(3);
      writer.write(0);
   }

   protected void sendMapleLifeError(MaplePacketLittleEndianWriter writer, SendMapleLifeError packet) {
      writer.write(0);
      writer.writeInt(packet.code());
   }
}