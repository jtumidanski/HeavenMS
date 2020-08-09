package tools.packet.monster;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record DamageMonsterFriendly(Integer objectId, Integer damage, Integer remainingHp,
                                    Integer maximumHp) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.DAMAGE_MONSTER;
   }
}