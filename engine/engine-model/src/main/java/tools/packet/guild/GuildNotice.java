package tools.packet.guild;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GuildNotice(Integer guildId, String notice) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GUILD_OPERATION;
   }
}