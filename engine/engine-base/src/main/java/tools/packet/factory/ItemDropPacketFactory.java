package tools.packet.factory;

import tools.data.output.MaplePacketLittleEndianWriter;
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
      Handler.handle(UpdateMapItemObject.class).decorate(this::updateMapItemObject).register(registry);
      Handler.handle(DropItemFromMapObject.class).decorate(this::dropItemFromMapObject).register(registry);
   }

   protected void updateMapItemObject(MaplePacketLittleEndianWriter writer, UpdateMapItemObject packet) {
      writer.write(2);
      writer.writeInt(packet.getMapItem().objectId());
      writer.writeBool(packet.getMapItem().getMeso() > 0);
      writer.writeInt(packet.getMapItem().getItemId());
      writer.writeInt(packet.isGiveOwnership() ? 0 : -1);
      writer.write(packet.getMapItem().hasExpiredOwnershipTime() ? 2 : packet.getMapItem().getDropType());
      writer.writePos(packet.getMapItem().position());
      writer.writeInt(packet.isGiveOwnership() ? 0 : -1);

      if (packet.getMapItem().getMeso() == 0) {
         addExpirationTime(writer, packet.getMapItem().getItem().expiration());
      }
      writer.write(packet.getMapItem().isPlayerDrop() ? 0 : 1);
   }

   protected void dropItemFromMapObject(MaplePacketLittleEndianWriter writer, DropItemFromMapObject packet) {
      int dropType = packet.getMapItem().getDropType();
      if (packet.getMapItem().hasClientSideOwnership(packet.getCharacter()) && dropType < 3) {
         dropType = 2;
      }

      writer.write(packet.getMod());
      writer.writeInt(packet.getMapItem().objectId());
      writer.writeBool(packet.getMapItem().getMeso() > 0); // 1 mesos, 0 item, 2 and above all item meso bag,
      writer.writeInt(packet.getMapItem().getItemId()); // drop object ID
      writer.writeInt(packet.getMapItem().getClientSideOwnerId()); // owner char id/party id :)
      writer.write(dropType); // 0 = timeout for non-owner, 1 = timeout for non-owner's party, 2 = FFA, 3 = explosive/FFA
      writer.writePos(packet.getDropTo());
      writer.writeInt(packet.getMapItem().getDropper().objectId()); // dropper oid

      if (packet.getMod() != 2) {
         writer.writePos(packet.getDropFrom());
         writer.writeShort(0);//Fh?
      }
      if (packet.getMapItem().getMeso() == 0) {
         addExpirationTime(writer, packet.getMapItem().getItem().expiration());
      }
      writer.write(packet.getMapItem().isPlayerDrop() ? 0 : 1); //pet EQP pickup
   }
}