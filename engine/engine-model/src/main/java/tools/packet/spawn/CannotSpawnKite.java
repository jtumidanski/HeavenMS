package tools.packet.spawn;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record CannotSpawnKite() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CANNOT_SPAWN_KITE;
   }
}