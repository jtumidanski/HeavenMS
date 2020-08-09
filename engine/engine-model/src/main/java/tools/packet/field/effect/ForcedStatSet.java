package tools.packet.field.effect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ForcedStatSet() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FORCED_STAT_SET;
   }
}