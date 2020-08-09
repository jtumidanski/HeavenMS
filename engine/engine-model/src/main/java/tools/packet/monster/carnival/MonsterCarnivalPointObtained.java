package tools.packet.monster.carnival;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MonsterCarnivalPointObtained(Integer currentPoints, Integer totalPoints) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MONSTER_CARNIVAL_OBTAINED_CP;
   }
}