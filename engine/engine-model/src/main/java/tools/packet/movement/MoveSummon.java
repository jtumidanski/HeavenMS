package tools.packet.movement;

import java.awt.Point;
import java.util.List;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MoveSummon(Integer characterId, Integer objectId, Point startPosition,
                         List<Byte> movementList) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MOVE_SUMMON;
   }
}