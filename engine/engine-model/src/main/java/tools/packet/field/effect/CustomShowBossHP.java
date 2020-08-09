package tools.packet.field.effect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record CustomShowBossHP(Byte call, Integer objectId, Long currentHP, Long maximumHP, Byte tagColor,
                               Byte tagBackgroundColor) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FIELD_EFFECT;
   }
}