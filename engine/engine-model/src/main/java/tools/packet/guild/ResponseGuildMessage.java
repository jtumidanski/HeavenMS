package tools.packet.guild;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ResponseGuildMessage(Byte code, String targetName) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GUILD_OPERATION;
   }
}