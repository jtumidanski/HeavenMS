package tools.packet.character.interaction;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GetMiniGameRequestTie() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.PLAYER_INTERACTION;
   }
}