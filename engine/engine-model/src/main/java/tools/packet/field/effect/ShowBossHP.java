package tools.packet.field.effect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowBossHP(Integer objectId, Integer currentHP, Integer maximumHP, Byte tagColor,
                         Byte tagBackgroundColor) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FIELD_EFFECT;
   }
}