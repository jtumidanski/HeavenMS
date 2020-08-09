package tools.packet.login;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record LoginFailed(LoginFailedReason reason) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.LOGIN_STATUS;
   }
}