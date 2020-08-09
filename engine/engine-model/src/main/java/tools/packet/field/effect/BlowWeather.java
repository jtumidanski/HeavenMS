package tools.packet.field.effect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record BlowWeather(String message, int itemId, boolean active) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FIELD_EFFECT;
   }
}