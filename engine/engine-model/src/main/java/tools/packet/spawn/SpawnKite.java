package tools.packet.spawn;

import java.awt.Point;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record SpawnKite(Integer objectId, Integer itemId, String name, String message, Point position,
                        Integer ft) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPAWN_KITE;
   }
}