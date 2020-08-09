package tools.packet.character;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record FacialExpression(Integer characterId, Integer expression) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FACIAL_EXPRESSION;
   }
}