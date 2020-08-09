package tools.packet.family;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record SendFamilyInvite(Integer characterId, String inviter) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FAMILY_JOIN_REQUEST;
   }
}