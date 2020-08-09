package tools.packet.monster;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record HealMonster(Integer objectId, Integer heal, Integer currentHp, Integer maximumHp) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.DAMAGE_MONSTER;
   }
}