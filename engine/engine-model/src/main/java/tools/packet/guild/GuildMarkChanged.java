package tools.packet.guild;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GuildMarkChanged(Integer characterId, Integer logoBackground, Integer logoBackgroundColor, Integer logo,
                               Integer logoColor) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GUILD_MARK_CHANGED;
   }
}