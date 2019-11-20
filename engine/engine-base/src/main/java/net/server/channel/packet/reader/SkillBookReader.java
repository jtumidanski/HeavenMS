package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.SkillBookPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class SkillBookReader implements PacketReader<SkillBookPacket> {
   @Override
   public SkillBookPacket read(SeekableLittleEndianAccessor accessor) {
      accessor.readInt();
      short slot = accessor.readShort();
      int itemId = accessor.readInt();
      return new SkillBookPacket(slot, itemId);
   }
}
