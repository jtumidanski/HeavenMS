package tools.packet.movement;

import java.util.List;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MovePlayer(Integer characterId, List<Byte> movementList) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MOVE_PLAYER;
   }
}