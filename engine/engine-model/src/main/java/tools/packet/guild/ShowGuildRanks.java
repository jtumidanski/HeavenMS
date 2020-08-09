package tools.packet.guild;

import java.util.List;

import client.database.data.GuildData;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowGuildRanks(Integer npcId, List<GuildData> ranks) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GUILD_OPERATION;
   }
}