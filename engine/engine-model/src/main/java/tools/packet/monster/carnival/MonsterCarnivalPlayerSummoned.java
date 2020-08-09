package tools.packet.monster.carnival;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MonsterCarnivalPlayerSummoned(String name, Integer tab, Integer number) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MONSTER_CARNIVAL_SUMMON;
   }
}