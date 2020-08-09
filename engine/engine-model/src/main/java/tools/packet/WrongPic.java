package tools.packet;

import net.opcodes.SendOpcode;

public record WrongPic() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CHECK_SPW_RESULT;
   }
}