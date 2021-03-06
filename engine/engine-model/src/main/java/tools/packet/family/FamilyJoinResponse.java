package tools.packet.family;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record FamilyJoinResponse(Boolean accepted, String characterNameAdded) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FAMILY_JOIN_REQUEST_RESULT;
   }
}