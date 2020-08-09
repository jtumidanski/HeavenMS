package tools.packet.field.effect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowEffect(String env) implements PacketInput {
   public Integer mode() {
      return 3;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.FIELD_EFFECT;
   }
}