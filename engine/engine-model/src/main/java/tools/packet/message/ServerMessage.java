package tools.packet.message;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ServerMessage(String message) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SERVER_MESSAGE;
   }
}