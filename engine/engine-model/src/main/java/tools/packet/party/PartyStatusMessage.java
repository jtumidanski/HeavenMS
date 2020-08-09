package tools.packet.party;

import java.util.Optional;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record PartyStatusMessage(Integer message, Optional<String> fromCharacterName) implements PacketInput {
   public PartyStatusMessage(Integer message) {
      this(message, Optional.empty());
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.PARTY_OPERATION;
   }
}