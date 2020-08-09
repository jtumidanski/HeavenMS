package tools.packet.monster.carnival;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MonsterCarnivalStart(Integer team, Integer freePoints, Integer totalPoints, Integer teamFreePoints,
                                   Integer teamTotalPoints, Integer oppositionFreePoints,
                                   Integer oppositionTotalPoints) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MONSTER_CARNIVAL_START;
   }
}