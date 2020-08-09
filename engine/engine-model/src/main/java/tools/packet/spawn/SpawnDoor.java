package tools.packet.spawn;

import java.awt.Point;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record SpawnDoor(Integer ownerId, Point position, Boolean launched) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPAWN_DOOR;
   }
}