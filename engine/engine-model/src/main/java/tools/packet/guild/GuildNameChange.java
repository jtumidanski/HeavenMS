package tools.packet.guild;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GuildNameChange(Integer characterId, String guildName) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GUILD_NAME_CHANGED;
   }
}