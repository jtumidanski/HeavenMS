package tools.packet.alliance;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record AllianceMemberOnline(Integer allianceId, Integer guildId, Integer characterId,
                                   Boolean online) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.ALLIANCE_OPERATION;
   }
}
