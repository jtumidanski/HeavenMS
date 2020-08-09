package tools.packet.spawn;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record SpawnGuide(Boolean spawn) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPAWN_GUIDE;
   }
}