package tools.packet.field.effect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RemoveWeather() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.BLOW_WEATHER;
   }
}