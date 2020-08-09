package tools.packet.alliance;

import net.opcodes.SendOpcode;
import net.server.guild.MapleAlliance;
import tools.packet.PacketInput;

public record AddGuildToAlliance(MapleAlliance alliance, Integer newGuildId, Integer worldId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.ALLIANCE_OPERATION;
   }
}
