package tools.packet.spawn;

import java.awt.Point;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record SpawnPortal(Integer townId, Integer targetId, Point position) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPAWN_PORTAL;
   }
}