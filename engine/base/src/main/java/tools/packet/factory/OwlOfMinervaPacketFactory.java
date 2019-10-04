package tools.packet.factory;

import client.MapleCharacter;
import client.inventory.MapleInventoryType;
import constants.ItemConstants;
import net.opcodes.SendOpcode;
import server.maps.AbstractMapleMapObject;
import server.maps.MapleHiredMerchant;
import server.maps.MaplePlayerShop;
import server.maps.MaplePlayerShopItem;
import tools.FilePrinter;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
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
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof GetOwlMessage) {
         return create(this::getOwlMessage, packetInput);
      } else if (packetInput instanceof OwlOfMinervaResult) {
         return create(this::owlOfMinerva, packetInput);
      } else if (packetInput instanceof GetOwlOpen) {
         return create(this::getOwlOpen, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
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
   protected byte[] getOwlMessage(GetOwlMessage packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
      mplew.writeShort(SendOpcode.SHOP_LINK_RESULT.getValue());
      mplew.write(packet.message());
      return mplew.getPacket();
   }

   protected byte[] owlOfMinerva(OwlOfMinervaResult packet) {
      byte itemType = ItemConstants.getInventoryType(packet.getItemId()).getType();

      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.SHOP_SCANNER_RESULT.getValue()); // header.
      mplew.write(6);
      mplew.writeInt(0);
      mplew.writeInt(packet.getItemId());
      mplew.writeInt(packet.getHmsAvailable().size());
      for (Pair<MaplePlayerShopItem, AbstractMapleMapObject> hme : packet.getHmsAvailable()) {
         MaplePlayerShopItem item = hme.getLeft();
         AbstractMapleMapObject mo = hme.getRight();

         if (mo instanceof MaplePlayerShop) {
            MaplePlayerShop ps = (MaplePlayerShop) mo;
            MapleCharacter owner = ps.getOwner();

            mplew.writeMapleAsciiString(owner.getName());
            mplew.writeInt(owner.getMapId());
            mplew.writeMapleAsciiString(ps.getDescription());
            mplew.writeInt(item.bundles());
            mplew.writeInt(item.item().quantity());
            mplew.writeInt(item.price());
            mplew.writeInt(owner.getId());
            mplew.write(owner.getClient().getChannel() - 1);
         } else {
            MapleHiredMerchant hm = (MapleHiredMerchant) mo;

            mplew.writeMapleAsciiString(hm.getOwner());
            mplew.writeInt(hm.getMapId());
            mplew.writeMapleAsciiString(hm.getDescription());
            mplew.writeInt(item.bundles());
            mplew.writeInt(item.item().quantity());
            mplew.writeInt(item.price());
            mplew.writeInt(hm.getOwnerId());
            mplew.write(hm.getChannel() - 1);
         }

         mplew.write(itemType);
         if (itemType == MapleInventoryType.EQUIP.getType()) {
            addItemInfo(mplew, item.item(), true);
         }
      }
      return mplew.getPacket();
   }

   protected byte[] getOwlOpen(GetOwlOpen packet) {
      MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

      mplew.writeShort(SendOpcode.SHOP_SCANNER_RESULT.getValue());
      mplew.write(7);
      mplew.write(packet.leaderboards().size());
      for (Integer i : packet.leaderboards()) {
         mplew.writeInt(i);
      }

      return mplew.getPacket();
   }
}