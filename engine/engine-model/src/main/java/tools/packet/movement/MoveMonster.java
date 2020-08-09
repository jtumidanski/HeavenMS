package tools.packet.movement;

import java.awt.Point;
import java.util.List;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MoveMonster(int objectId, boolean skillPossible, int skill, int skillId, int skillLevel,
                          int option, Point startPosition, List<Byte> movementList) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MOVE_MONSTER;
   }
}