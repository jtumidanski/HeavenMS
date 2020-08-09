package tools.packet.ui;

import net.opcodes.SendOpcode;
import net.server.SkillMacro;
import tools.packet.PacketInput;

public record GetMacros(SkillMacro[] macros) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MACRO_SYS_DATA_INIT;
   }
}