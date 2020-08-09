package tools.packet.party;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record UpdatePartyMemberHp(Integer characterId, Integer currentHp, Integer maximumHp) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.PARTY_OPERATION;
   }
}