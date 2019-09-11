package net.server.channel.packet.reader;

import net.server.PacketReader;
import net.server.SkillMacro;
import net.server.channel.packet.SkillMacroPacket;
import tools.data.input.SeekableLittleEndianAccessor;

public class SkillMacroReader implements PacketReader<SkillMacroPacket> {
   @Override
   public SkillMacroPacket read(SeekableLittleEndianAccessor accessor) {
      int num = accessor.readByte();
      SkillMacro[] macros = new SkillMacro[num];
      for (int i = 0; i < num; i++) {
         String name = accessor.readMapleAsciiString();
         int shout = accessor.readByte();
         int skill1 = accessor.readInt();
         int skill2 = accessor.readInt();
         int skill3 = accessor.readInt();
         macros[i] = new SkillMacro(name, shout, skill1, skill2, skill3, i);
      }
      return new SkillMacroPacket(macros);
   }
}
