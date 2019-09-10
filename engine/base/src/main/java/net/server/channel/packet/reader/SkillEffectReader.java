package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.channel.packet.SkillEffectPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class SkillEffectReader implements PacketReader<SkillEffectPacket> {
   @Override
   public SkillEffectPacket read(SeekableLittleEndianAccessor accessor) {
      int skillId = accessor.readInt();
      int level = accessor.readByte();
      byte flags = accessor.readByte();
      int speed = accessor.readByte();
      byte aids = accessor.readByte();//Mmmk
      return new SkillEffectPacket(skillId, level, flags, speed, aids);
   }
}
