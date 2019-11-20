package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.HealOvertimePacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class HealOvertimeReader implements PacketReader<HealOvertimePacket> {
   @Override
   public HealOvertimePacket read(SeekableLittleEndianAccessor accessor) {
      accessor.skip(8);
      short healHP = accessor.readShort();
      short healMP = accessor.readShort();
      return new HealOvertimePacket(healHP, healMP);
   }
}
