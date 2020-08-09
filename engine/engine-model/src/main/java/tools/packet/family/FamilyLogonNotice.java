package tools.packet.family;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record FamilyLogonNotice(String characterName, Boolean loggedIn) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FAMILY_NOTIFY_LOGIN_OR_LOGOUT;
   }
}