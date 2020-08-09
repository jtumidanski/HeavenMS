package tools.packet.family;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record LoadFamily() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FAMILY_PRIVILEGE_LIST;
   }
}