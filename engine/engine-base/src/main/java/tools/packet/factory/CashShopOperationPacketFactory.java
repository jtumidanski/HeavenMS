package tools.packet.factory;

import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import client.inventory.Item;
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
import tools.packet.cashshop.operation.ShowWishListUpdate;
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
      Handler.handle(ShowWorldTransferSuccess.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopOperationSubOp.WORLD_TRANSFER_SUCCESS, this::showWorldTransferSuccess))
            .register(registry);
      Handler.handle(ShowNameChangeSuccess.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopOperationSubOp.NAME_CHANGE_SUCCESS, this::showNameChangeSuccess))
            .register(registry);
      Handler.handle(ShowCouponRedeemSuccess.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopOperationSubOp.COUPON_REDEEMED_SUCCESS, this::showCouponRedeemedItems))
            .register(registry);
      Handler.handle(ShowBoughtCashPackageSuccess.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopOperationSubOp.BOUGHT_CASH_PACKAGE_SUCCESS, this::showBoughtCashPackage))
            .register(registry);
      Handler.handle(ShowBoughtQuestItem.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopOperationSubOp.BOUGHT_QUEST_ITEM_SUCCESS, this::showBoughtQuestItem))
            .register(registry);
      Handler.handle(ShowBoughtCashItem.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopOperationSubOp.BOUGHT_CASH_ITEM_SUCCESS, this::showBoughtCashItem))
            .register(registry);
      Handler.handle(ShowBoughtCashRing.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopOperationSubOp.BOUGHT_CASH_RING_SUCCESS, this::showBoughtCashRing))
            .register(registry);
      Handler.handle(ShowCashShopMessage.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopOperationSubOp.CASH_SHOP_MESSAGE, this::showCashShopMessage))
            .register(registry);
      Handler.handle(ShowCashInventory.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopOperationSubOp.CASH_INVENTORY, this::showCashInventory))
            .register(registry);
      Handler.handle(ShowGifts.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopOperationSubOp.GIFTS, this::showGifts))
            .register(registry);
      Handler.handle(ShowGiftSucceed.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopOperationSubOp.GIFT_SUCCEED, this::showGiftSucceed))
            .register(registry);
      Handler.handle(ShowBoughtInventorySlots.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopOperationSubOp.BOUGHT_INVENTORY_SLOTS, this::showBoughtInventorySlots))
            .register(registry);
      Handler.handle(ShowBoughtStorageSlots.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopOperationSubOp.BOUGHT_STORAGE_SLOTS, this::showBoughtStorageSlots))
            .register(registry);
      Handler.handle(ShowBoughtCharacterSlots.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopOperationSubOp.BOUGHT_CHARACTER_SLOTS, this::showBoughtCharacterSlot))
            .register(registry);
      Handler.handle(TakeFromCashInventory.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopOperationSubOp.TAKE_FROM_CASH_INVENTORY, this::takeFromCashInventory))
            .register(registry);
      Handler.handle(DeleteCashItem.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopOperationSubOp.DELETE_CASH_ITEM, this::deleteCashItem))
            .register(registry);
      Handler.handle(RefundCashItem.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopOperationSubOp.REFUND_CASH_ITEM, this::refundCashItem))
            .register(registry);
      Handler.handle(PutIntoCashInventory.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopOperationSubOp.PUT_INTO_CASH_INVENTORY, this::putIntoCashInventory))
            .register(registry);
      Handler.handle(ShowCash.class).decorate(this::showCash).register(registry);
      Handler.handle(SendMapleLife.class).decorate(this::sendMapleLifeCharacterInfo).register(registry);
      Handler.handle(SendMapleNameLifeError.class).decorate(this::sendMapleLifeNameError).register(registry);
      Handler.handle(SendMapleLifeError.class).decorate(this::sendMapleLifeError).register(registry);
      Handler.handle(ShowWishList.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopOperationSubOp.SHOW_WISH_LIST, this::showWishList))
            .register(registry);
      Handler.handle(ShowWishListUpdate.class)
            .decorate((writer, packet) -> decorate(writer, packet, CashShopOperationSubOp.SHOW_WISH_LIST_UPDATE, this::showWishList))
            .register(registry);
   }

   protected <T extends PacketInput> void decorate(MaplePacketLittleEndianWriter writer, T packet, CashShopOperationSubOp subOp, BiConsumer<MaplePacketLittleEndianWriter, T> decorator) {
      writer.write(subOp.getValue());
      decorator.accept(writer, packet);
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

   protected void showWishList(MaplePacketLittleEndianWriter writer, ShowWishListUpdate packet) {
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