package tools.packet.field.effect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MapSound(String path) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FIELD_EFFECT;
   }
}