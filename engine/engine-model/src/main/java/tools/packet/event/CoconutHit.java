package tools.packet.event;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record CoconutHit(Boolean spawn, Integer id, Integer theType) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.COCONUT_HIT;
   }
}