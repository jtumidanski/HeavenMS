package tools.packet.login;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record PermanentBan(Byte reason) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.LOGIN_STATUS;
   }
}