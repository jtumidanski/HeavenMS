package tools.packet.attack;

import java.util.List;
import java.util.Map;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RangedAttack(Integer characterId, Integer skill, Integer skillLevel, Integer stance,
                           Integer numAttackedAndDamage, Integer projectile,
                           Map<Integer, List<Integer>> damage, Integer speed, Integer direction,
                           Integer display) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.RANGED_ATTACK;
   }
}
