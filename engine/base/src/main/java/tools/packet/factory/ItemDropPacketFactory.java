package tools.packet.factory;

import net.opcodes.SendOpcode;
import tools.FilePrinter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketInput;
import tools.packet.item.drop.DropItemFromMapObject;
import tools.packet.item.drop.UpdateMapItemObject;

public class ItemDropPacketFactory extends AbstractPacketFactory {
   private static ItemDropPacketFactory instance;

   public static ItemDropPacketFactory getInstance() {
      if (instance == null) {
         instance = new ItemDropPacketFactory();
      }
      return instance;
   }

   private ItemDropPacketFactory() {
   }

   @Override
   public byte[] create(PacketInput packetInput) {
      if (packetInput instanceof UpdateMapItemObject) {
         return create(this::updateMapItemObject, packetInput);
      } else if (packetInput instanceof DropItemFromMapObject) {
         return create(this::dropItemFromMapObject, packetInput);
      }
      FilePrinter.printError(FilePrinter.PACKET_LOGS + "generic.txt", "Trying to handle invalid input " + packetInput.toString());
      return new byte[0];
   }

   protected byte[] updateMapItemObject(UpdateMapItemObject packet) {
      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.DROP_ITEM_FROM_MAPOBJECT.getValue());
      mplew.write(2);
      mplew.writeInt(packet.getMapItem().getObjectId());
      mplew.writeBool(packet.getMapItem().getMeso() > 0);
      mplew.writeInt(packet.getMapItem().getItemId());
      mplew.writeInt(packet.isGiveOwnership() ? 0 : -1);
      mplew.write(packet.getMapItem().hasExpiredOwnershipTime() ? 2 : packet.getMapItem().getDropType());
      mplew.writePos(packet.getMapItem().getPosition());
      mplew.writeInt(packet.isGiveOwnership() ? 0 : -1);

      if (packet.getMapItem().getMeso() == 0) {
         addExpirationTime(mplew, packet.getMapItem().getItem().expiration());
      }
      mplew.write(packet.getMapItem().isPlayerDrop() ? 0 : 1);
      return mplew.getPacket();
   }

   protected byte[] dropItemFromMapObject(DropItemFromMapObject packet) {
      int dropType = packet.getMapItem().getDropType();
      if (packet.getMapItem().hasClientsideOwnership(packet.getCharacter()) && dropType < 3) {
         dropType = 2;
      }

      final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
      mplew.writeShort(SendOpcode.DROP_ITEM_FROM_MAPOBJECT.getValue());
      mplew.write(packet.getMod());
      mplew.writeInt(packet.getMapItem().getObjectId());
      mplew.writeBool(packet.getMapItem().getMeso() > 0); // 1 mesos, 0 item, 2 and above all item meso bag,
      mplew.writeInt(packet.getMapItem().getItemId()); // drop object ID
      mplew.writeInt(packet.getMapItem().getClientsideOwnerId()); // owner charid/partyid :)
      mplew.write(dropType); // 0 = timeout for non-owner, 1 = timeout for non-owner's party, 2 = FFA, 3 = explosive/FFA
      mplew.writePos(packet.getDropTo());
      mplew.writeInt(packet.getMapItem().getDropper().getObjectId()); // dropper oid, found thanks to Li Jixue

      if (packet.getMod() != 2) {
         mplew.writePos(packet.getDropFrom());
         mplew.writeShort(0);//Fh?
      }
      if (packet.getMapItem().getMeso() == 0) {
         addExpirationTime(mplew, packet.getMapItem().getItem().expiration());
      }
      mplew.write(packet.getMapItem().isPlayerDrop() ? 0 : 1); //pet EQP pickup
      return mplew.getPacket();
   }
}