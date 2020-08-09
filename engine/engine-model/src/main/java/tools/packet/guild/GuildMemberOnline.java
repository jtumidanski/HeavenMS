package tools.packet.guild;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GuildMemberOnline(Integer guildId, Integer characterId, Boolean online) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GUILD_OPERATION;
   }
}