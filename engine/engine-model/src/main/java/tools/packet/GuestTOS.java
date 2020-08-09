package tools.packet;

import net.opcodes.SendOpcode;

public record GuestTOS() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GUEST_ID_LOGIN;
   }
}