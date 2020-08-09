package tools.packet.guild;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GuildRankTitleChange(Integer guildId, String[] ranks) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GUILD_OPERATION;
   }
}