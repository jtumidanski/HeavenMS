package tools.packet.monster;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record DamageSummon(Integer characterId, Integer objectId, Integer damage,
                           Integer monsterIdFrom) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.DAMAGE_SUMMON;
   }
}