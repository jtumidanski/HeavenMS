package tools.packet.alliance;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record UpdateAllianceJobLevel(Integer allianceId, Integer guildId, Integer characterId, Integer level,
                                     Integer jobId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.ALLIANCE_OPERATION;
   }
}
