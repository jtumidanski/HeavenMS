package tools.packet.alliance;

import net.opcodes.SendOpcode;
import net.server.guild.MapleAlliance;
import tools.packet.PacketInput;

public record GetAllianceInfo(MapleAlliance alliance) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.ALLIANCE_OPERATION;
   }
}
