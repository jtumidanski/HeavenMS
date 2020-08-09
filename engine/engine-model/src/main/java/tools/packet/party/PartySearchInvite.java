package tools.packet.party;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record PartySearchInvite(Integer partyId, String fromCharacterName) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.PARTY_OPERATION;
   }
}