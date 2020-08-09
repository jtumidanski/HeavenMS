package tools.packet.message;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ClearAvatarMegaphone() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CLEAR_AVATAR_MEGAPHONE;
   }
}