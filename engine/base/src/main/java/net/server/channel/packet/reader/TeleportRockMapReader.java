package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.AddTeleportRockMapPacket;
import net.server.channel.packet.BaseTeleportRockMapPacket;
import net.server.channel.packet.DeleteTeleportRockMapPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class TeleportRockMapReader implements PacketReader<BaseTeleportRockMapPacket> {
   @Override
   public BaseTeleportRockMapPacket read(SeekableLittleEndianAccessor accessor) {
      byte type = accessor.readByte();
      boolean vip = accessor.readByte() == 1;
      if (type == 0x00) {
         int mapId = accessor.readInt();
         return new DeleteTeleportRockMapPacket(type, vip, mapId);
      } else if (type == 0x01) {
         return new AddTeleportRockMapPacket(type, vip);
      }
      return null;
   }
}
