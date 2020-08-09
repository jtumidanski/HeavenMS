package tools.packet.field.effect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record PlaySound(String env) implements PacketInput {
   public Integer mode() {
      return 4;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.FIELD_EFFECT;
   }
}