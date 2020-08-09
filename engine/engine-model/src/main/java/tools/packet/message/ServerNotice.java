package tools.packet.message;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ServerNotice(Integer theType, Integer channel, String message, Boolean smegaEar) implements PacketInput {
   public ServerNotice(Integer theType, String message) {
      this(theType, 0, message, false);
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SERVER_MESSAGE;
   }
}