package tools.packet.guild;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record CreateGuildMessage(String masterCharacterName, String guildName) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GUILD_OPERATION;
   }
}