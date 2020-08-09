package tools.packet.alliance;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GetAlliancePlayerInfo(Integer allianceId, Integer playerId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.ALLIANCE_OPERATION;
   }
}
