package tools.packet.alliance;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record DisbandAlliance(Integer allianceId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.ALLIANCE_OPERATION;
   }
}
