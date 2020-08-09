package tools.packet.character.interaction;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MiniGameMoveOmok(Integer move1, Integer move2, Integer move3) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.PLAYER_INTERACTION;
   }
}