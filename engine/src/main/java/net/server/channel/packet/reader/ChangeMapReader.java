package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.ChangeMapPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class ChangeMapReader implements PacketReader<ChangeMapPacket> {
   @Override
   public ChangeMapPacket read(SeekableLittleEndianAccessor accessor) {
      boolean cashShop = accessor.available() == 0;
      byte fromDying = -1;
      int targetId = -1;
      String startWarp = "";
      boolean wheel = false;

      if (!cashShop) {
         fromDying = accessor.readByte(); // 1 = from dying 0 = regular portals
         targetId = accessor.readInt();
         startWarp = accessor.readMapleAsciiString();
         accessor.readByte();
         wheel = accessor.readShort() > 0;

      }
      return new ChangeMapPacket(cashShop, fromDying, targetId, startWarp, wheel);
   }
}
