package tools.packet.guild;

import java.util.List;

import client.database.data.GlobalUserRank;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowPlayerRanks(Integer npcId, List<GlobalUserRank> ranks) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GUILD_OPERATION;
   }
}