package tools.packet.field.effect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record TrembleEffect(Integer theType, Integer delay) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FIELD_EFFECT;
   }
}