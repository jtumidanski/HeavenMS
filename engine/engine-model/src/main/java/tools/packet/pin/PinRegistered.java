package tools.packet.pin;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record PinRegistered() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.UPDATE_PIN_CODE;
   }
}