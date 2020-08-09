package tools.packet.guild;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GuildQuestWaitingNotice(Byte channel, Integer position) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GUILD_OPERATION;
   }
}