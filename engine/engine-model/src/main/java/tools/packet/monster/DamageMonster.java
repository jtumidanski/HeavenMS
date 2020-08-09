package tools.packet.monster;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record DamageMonster(Integer objectId, Integer damage) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.DAMAGE_MONSTER;
   }
}