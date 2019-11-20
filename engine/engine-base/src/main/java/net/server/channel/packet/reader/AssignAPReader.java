package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.AssignAPPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class AssignAPReader implements PacketReader<AssignAPPacket> {
   @Override
   public AssignAPPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.skip(8);
      byte opt = accessor.readByte();
      int[] types;
      int[] gains;
      if (accessor.available() < 16) {
         types = new int[0];
         gains = new int[0];
      } else {
         types = new int[2];
         gains = new int[2];
         for (int i = 0; i < 2; i++) {
            types[i] = accessor.readInt();
            gains[i] = accessor.readInt();
         }
      }

      return new AssignAPPacket(opt, types, gains);
   }
}
