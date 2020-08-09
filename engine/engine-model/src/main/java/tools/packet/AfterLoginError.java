package tools.packet;

import net.opcodes.SendOpcode;

public record AfterLoginError(Integer reason) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SELECT_CHARACTER_BY_VAC;
   }
}