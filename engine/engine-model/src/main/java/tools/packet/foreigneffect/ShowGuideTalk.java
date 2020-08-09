package tools.packet.foreigneffect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowGuideTalk(String talk) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.TALK_GUIDE;
   }
}