package tools.packet.monster;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record CatchMonsterFailure(Integer message) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.BRIDLE_MOB_CATCH_FAIL;
   }
}