package tools.packet.monster;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowMonsterHP(Integer objectId, Integer remainingHpPercentage) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SHOW_MONSTER_HP;
   }
}