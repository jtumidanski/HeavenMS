package tools.packet.family;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record FamilySummonRequest(String familyName, String characterNameFrom) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FAMILY_SUMMON_REQUEST;
   }
}