package tools.packet.foreigneffect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowGuideHint(Integer hint) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.TALK_GUIDE;
   }
}