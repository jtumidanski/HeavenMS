package tools.packet.monster;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record CatchMonster(Integer objectId, Byte success) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CATCH_MONSTER;
   }
}