package tools.packet.guild;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record NewGuildMember(Integer guildId, Integer characterId, String name, Integer jobId, Integer level,
                             Integer guildRank, Boolean online) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GUILD_OPERATION;
   }
}