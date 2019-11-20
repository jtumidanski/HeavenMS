package tools.packet.factory;

import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import tools.StringUtil;
import tools.data.output.MaplePacketLittleEndianWriter;

abstract class AbstractCashShopPacketFactory extends AbstractPacketFactory {
   protected void addCashItemInformation(final MaplePacketLittleEndianWriter writer, Item item, int accountId) {
      addCashItemInformation(writer, item, accountId, null);
   }

   protected void addCashItemInformation(final MaplePacketLittleEndianWriter writer, Item item, int accountId, String giftMessage) {
      boolean isGift = giftMessage != null;
      boolean isRing = false;
      Equip equip = null;
      if (item.inventoryType().equals(MapleInventoryType.EQUIP)) {
         equip = (Equip) item;
         isRing = equip.ringId() > -1;
      }
      writer.writeLong(item.petId() > -1 ? item.petId() : isRing ? equip.ringId() : item.cashId());
      if (!isGift) {
         writer.writeInt(accountId);
         writer.writeInt(0);
      }
      writer.writeInt(item.id());
      if (!isGift) {
         writer.writeInt(item.sn());
         writer.writeShort(item.quantity());
      }
      writer.writeAsciiString(StringUtil.getRightPaddedStr(item.giftFrom(), '\0', 13));
      if (isGift) {
         writer.writeAsciiString(StringUtil.getRightPaddedStr(giftMessage, '\0', 73));
         return;
      }
      addExpirationTime(writer, item.expiration());
      writer.writeLong(0);
   }
}
