package tools.packet.factory;

import constants.ItemConstants;
import net.opcodes.SendOpcode;
import server.MapleItemInformationProvider;
import server.MapleShopItem;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.shop.ConfirmShopTransaction;
import tools.packet.shop.DestroyHiredMerchantBox;
import tools.packet.shop.GetNPCShop;
import tools.packet.shop.RemoteChannelChange;
import tools.packet.shop.RetrieveFirstMessage;
import tools.packet.shop.ShowHiredMerchantBox;
import tools.packet.shop.UpdateHiredMerchantBox;

public class ShopPacketFactory extends AbstractPacketFactory {
   private static ShopPacketFactory instance;

   public static ShopPacketFactory getInstance() {
      if (instance == null) {
         instance = new ShopPacketFactory();
      }
      return instance;
   }

   private ShopPacketFactory() {
      registry.setHandler(UpdateHiredMerchantBox.class, packet -> create(SendOpcode.UPDATE_HIRED_MERCHANT, this::updateHiredMerchantBox, packet));
      registry.setHandler(GetNPCShop.class, packet -> create(SendOpcode.OPEN_NPC_SHOP, this::getNPCShop, packet));
      registry.setHandler(ConfirmShopTransaction.class, packet -> create(SendOpcode.CONFIRM_SHOP_TRANSACTION, this::shopTransaction, packet, 3));
      registry.setHandler(ShowHiredMerchantBox.class, packet -> create(SendOpcode.ENTRUSTED_SHOP_CHECK_RESULT, this::hiredMerchantBox, packet));
      registry.setHandler(RetrieveFirstMessage.class, packet -> create(SendOpcode.ENTRUSTED_SHOP_CHECK_RESULT, this::retrieveFirstMessage, packet));
      registry.setHandler(RemoteChannelChange.class, packet -> create(SendOpcode.ENTRUSTED_SHOP_CHECK_RESULT, this::remoteChannelChange, packet));
      registry.setHandler(DestroyHiredMerchantBox.class, packet -> create(SendOpcode.DESTROY_HIRED_MERCHANT, this::removeHiredMerchantBox, packet));
   }

   protected void updateHiredMerchantBox(MaplePacketLittleEndianWriter writer, UpdateHiredMerchantBox packet) {
      writer.writeInt(packet.ownerId());
      writer.write(5);
      writer.writeInt(packet.objectId());
      writer.writeMapleAsciiString(packet.description());
      writer.write(packet.itemId() % 100);
      writer.write(packet.roomInto());    // visitor capacity here, thanks GabrielSin!
   }

   // someone thought it was a good idea to handle floating point representation through packets ROFL
   protected int doubleToShortBits(double d) {
      return (int) (Double.doubleToLongBits(d) >> 48);
   }

   protected void getNPCShop(MaplePacketLittleEndianWriter writer, GetNPCShop packet) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      writer.writeInt(packet.getShopId());
      writer.writeShort(packet.getShopItems().size()); // item count
      for (MapleShopItem item : packet.getShopItems()) {
         writer.writeInt(item.itemId());
         writer.writeInt(item.price());
         writer.writeInt(item.price() == 0 ? item.pitch() : 0); //Perfect Pitch
         writer.writeInt(0); //Can be used x minutes after purchase
         writer.writeInt(0); //Hmm
         if (!ItemConstants.isRechargeable(item.itemId())) {
            writer.writeShort(1); // stacksize o.o
            writer.writeShort(item.buyable());
         } else {
            writer.writeShort(0);
            writer.writeInt(0);
            writer.writeShort(doubleToShortBits(ii.getUnitPrice(item.itemId())));
            writer.writeShort(ii.getSlotMax(packet.getClient(), item.itemId()));
         }
      }
   }

   protected void shopTransaction(MaplePacketLittleEndianWriter writer, ConfirmShopTransaction packet) {
      writer.write(packet.operation().getValue());
   }

   protected void hiredMerchantBox(MaplePacketLittleEndianWriter writer, ShowHiredMerchantBox packet) {
      writer.write(0x07);
   }

   protected void retrieveFirstMessage(MaplePacketLittleEndianWriter writer, RetrieveFirstMessage packet) {
      writer.write(0x09);
   }

   protected void remoteChannelChange(MaplePacketLittleEndianWriter writer, RemoteChannelChange packet) {
      writer.write(0x10);
      writer.writeInt(0);//No idea yet
      writer.write(packet.channelId());
   }

   protected void removeHiredMerchantBox(MaplePacketLittleEndianWriter writer, DestroyHiredMerchantBox packet) {
      writer.writeInt(packet.ownerId());
   }
}