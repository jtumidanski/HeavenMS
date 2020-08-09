package tools.packet.guild;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record UpdateGuildPoints(Integer guildId, Integer points) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GUILD_OPERATION;
   }
}