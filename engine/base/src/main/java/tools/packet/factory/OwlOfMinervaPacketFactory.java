package tools.packet.factory;

import client.MapleCharacter;
import client.inventory.MapleInventoryType;
import constants.ItemConstants;
import server.maps.AbstractMapleMapObject;
import server.maps.MapleHiredMerchant;
import server.maps.MaplePlayerShop;
import server.maps.MaplePlayerShopItem;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.owl.GetOwlMessage;
import tools.packet.owl.GetOwlOpen;
import tools.packet.owl.OwlOfMinervaResult;

public class OwlOfMinervaPacketFactory extends AbstractPacketFactory {
   private static OwlOfMinervaPacketFactory instance;

   public static OwlOfMinervaPacketFactory getInstance() {
      if (instance == null) {
         instance = new OwlOfMinervaPacketFactory();
      }
      return instance;
   }

   private OwlOfMinervaPacketFactory() {
      Handler.handle(GetOwlMessage.class).decorate(this::getOwlMessage).size(3).register(registry);
      Handler.handle(OwlOfMinervaResult.class).decorate(this::owlOfMinerva).register(registry);
      Handler.handle(GetOwlOpen.class).decorate(this::getOwlOpen).register(registry);
   }

   // 0: Success
   // 1: The room is already closed.
   // 2: You can't enter the room due to full capacity.
   // 3: Other requests are being fulfilled this minute.
   // 4: You can't do it while you're dead.
   // 7: You are not allowed to trade other items at this point.
   // 17: You may not enter this store.
   // 18: The owner of the store is currently undergoing store maintenance. Please try again in a bit.
   // 23: This can only be used inside the Free Market.
   // default: This character is unable to do it.
   protected void getOwlMessage(MaplePacketLittleEndianWriter writer, GetOwlMessage packet) {
      writer.write(packet.message());
   }

   protected void owlOfMinerva(MaplePacketLittleEndianWriter writer, OwlOfMinervaResult packet) {
      byte itemType = ItemConstants.getInventoryType(packet.getItemId()).getType();
      writer.write(6);
      writer.writeInt(0);
      writer.writeInt(packet.getItemId());
      writer.writeInt(packet.getHmsAvailable().size());
      for (Pair<MaplePlayerShopItem, AbstractMapleMapObject> hme : packet.getHmsAvailable()) {
         MaplePlayerShopItem item = hme.getLeft();
         AbstractMapleMapObject mo = hme.getRight();

         if (mo instanceof MaplePlayerShop) {
            MaplePlayerShop ps = (MaplePlayerShop) mo;
            MapleCharacter owner = ps.getOwner();

            writer.writeMapleAsciiString(owner.getName());
            writer.writeInt(owner.getMapId());
            writer.writeMapleAsciiString(ps.getDescription());
            writer.writeInt(item.bundles());
            writer.writeInt(item.item().quantity());
            writer.writeInt(item.price());
            writer.writeInt(owner.getId());
            writer.write(owner.getClient().getChannel() - 1);
         } else {
            MapleHiredMerchant hm = (MapleHiredMerchant) mo;

            writer.writeMapleAsciiString(hm.getOwner());
            writer.writeInt(hm.getMapId());
            writer.writeMapleAsciiString(hm.getDescription());
            writer.writeInt(item.bundles());
            writer.writeInt(item.item().quantity());
            writer.writeInt(item.price());
            writer.writeInt(hm.getOwnerId());
            writer.write(hm.getChannel() - 1);
         }

         writer.write(itemType);
         if (itemType == MapleInventoryType.EQUIP.getType()) {
            addItemInfo(writer, item.item(), true);
         }
      }
   }

   protected void getOwlOpen(MaplePacketLittleEndianWriter writer, GetOwlOpen packet) {
      writer.write(7);
      writer.write(packet.leaderboards().size());
      for (Integer i : packet.leaderboards()) {
         writer.writeInt(i);
      }
   }
}