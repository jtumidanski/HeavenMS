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
      registry.setHandler(UpdateHiredMerchantBox.class, packet -> this.updateHiredMerchantBox((UpdateHiredMerchantBox) packet));
      registry.setHandler(GetNPCShop.class, packet -> this.getNPCShop((GetNPCShop) packet));
      registry.setHandler(ConfirmShopTransaction.class, packet -> this.shopTransaction((ConfirmShopTransaction) packet));
      registry.setHandler(ShowHiredMerchantBox.class, packet -> this.hiredMerchantBox((ShowHiredMerchantBox) packet));
      registry.setHandler(RetrieveFirstMessage.class, packet -> this.retrieveFirstMessage((RetrieveFirstMessage) packet));
      registry.setHandler(RemoteChannelChange.class, packet -> this.remoteChannelChange((RemoteChannelChange) packet));
      registry.setHandler(DestroyHiredMerchantBox.class, packet -> this.removeHiredMerchantBox((DestroyHiredMerchantBox) packet));
   }

   protected byte[] updateHiredMerchantBox(UpdateHiredMerchantBox packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.UPDATE_HIRED_MERCHANT.getValue());
      mplew.writeInt(packet.ownerId());
      mplew.write(5);
      mplew.writeInt(packet.objectId());
      mplew.writeMapleAsciiString(packet.description());
      mplew.write(packet.itemId() % 100);
      mplew.write(packet.roomInto());    // visitor capacity here, thanks GabrielSin!
      return mplew.getPacket();
   }

   // someone thought it was a good idea to handle floating point representation through packets ROFL
   protected int doubleToShortBits(double d) {
      return (int) (Double.doubleToLongBits(d) >> 48);
   }

   protected byte[] getNPCShop(GetNPCShop packet) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.OPEN_NPC_SHOP.getValue());
      mplew.writeInt(packet.getShopId());
      mplew.writeShort(packet.getShopItems().size()); // item count
      for (MapleShopItem item : packet.getShopItems()) {
         mplew.writeInt(item.itemId());
         mplew.writeInt(item.price());
         mplew.writeInt(item.price() == 0 ? item.pitch() : 0); //Perfect Pitch
         mplew.writeInt(0); //Can be used x minutes after purchase
         mplew.writeInt(0); //Hmm
         if (!ItemConstants.isRechargeable(item.itemId())) {
            mplew.writeShort(1); // stacksize o.o
            mplew.writeShort(item.buyable());
         } else {
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.writeShort(doubleToShortBits(ii.getUnitPrice(item.itemId())));
            mplew.writeShort(ii.getSlotMax(packet.getClient(), item.itemId()));
         }
      }
      return mplew.getPacket();
   }

   protected byte[] shopTransaction(ConfirmShopTransaction packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.CONFIRM_SHOP_TRANSACTION.getValue());
      mplew.write(packet.operation().getValue());
      return mplew.getPacket();
   }

   protected byte[] hiredMerchantBox(ShowHiredMerchantBox packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ENTRUSTED_SHOP_CHECK_RESULT.getValue()); // header.
      mplew.write(0x07);
      return mplew.getPacket();
   }

   protected byte[] retrieveFirstMessage(RetrieveFirstMessage packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ENTRUSTED_SHOP_CHECK_RESULT.getValue()); // header.
      mplew.write(0x09);
      return mplew.getPacket();
   }

   protected byte[] remoteChannelChange(RemoteChannelChange packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.ENTRUSTED_SHOP_CHECK_RESULT.getValue()); // header.
      mplew.write(0x10);
      mplew.writeInt(0);//No idea yet
      mplew.write(packet.channelId());
      return mplew.getPacket();
   }

   protected byte[] removeHiredMerchantBox(DestroyHiredMerchantBox packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.DESTROY_HIRED_MERCHANT.getValue());
      mplew.writeInt(packet.ownerId());
      return mplew.getPacket();
   }
}