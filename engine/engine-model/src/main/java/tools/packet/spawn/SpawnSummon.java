package tools.packet.spawn;

import java.awt.Point;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record SpawnSummon(int ownerId, int objectId, int skillId, int skillLevel, Point position, int stance,
                          int movementType, boolean puppet, boolean animated) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPAWN_SPECIAL_MAP_OBJECT;
   }
}