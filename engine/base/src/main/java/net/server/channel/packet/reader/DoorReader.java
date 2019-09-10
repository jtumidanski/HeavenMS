package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.DoorPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class DoorReader implements PacketReader<DoorPacket> {
   @Override
   public DoorPacket read(SeekableLittleEndianAccessor accessor) {
      int ownerId = accessor.readInt();
      boolean backWarp = accessor.readByte() == 1; // specifies if backwarp or not, 1 town to target, 0 target to town
      return new DoorPacket(ownerId, backWarp);
   }
}
