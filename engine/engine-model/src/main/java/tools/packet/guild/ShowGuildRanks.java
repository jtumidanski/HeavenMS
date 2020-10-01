package tools.packet.guild;

import java.util.List;

import client.database.data.GuildRankData;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowGuildRanks(Integer npcId, List<GuildRankData> ranks) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GUILD_OPERATION;
   }
}