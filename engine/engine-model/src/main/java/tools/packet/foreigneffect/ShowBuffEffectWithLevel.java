package tools.packet.foreigneffect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowBuffEffectWithLevel(Integer characterId, Integer skillId, Integer skillLevel, Integer effectId,
                                      Byte direction) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SHOW_FOREIGN_EFFECT;
   }
}