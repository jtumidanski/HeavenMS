package tools.packet;

import net.opcodes.SendOpcode;

public record SelectWorld(Integer worldId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.LAST_CONNECTED_WORLD;
   }
}