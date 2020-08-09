package tools.packet.alliance;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ChangeAllianceRankTitles(Integer allianceId, String[] ranks) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.ALLIANCE_OPERATION;
   }
}
