package tools.packet.pin;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record PinCodePacket(PinOperation operation) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CHECK_PIN_CODE;
   }
}