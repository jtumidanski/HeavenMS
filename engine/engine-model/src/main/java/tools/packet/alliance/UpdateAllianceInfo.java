package tools.packet.alliance;

import net.opcodes.SendOpcode;
import net.server.guild.MapleAlliance;
import tools.packet.PacketInput;

public record UpdateAllianceInfo(MapleAlliance alliance, Integer worldId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.ALLIANCE_OPERATION;
   }
}
