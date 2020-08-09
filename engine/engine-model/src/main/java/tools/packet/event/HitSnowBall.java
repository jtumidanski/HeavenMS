package tools.packet.event;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record HitSnowBall(Integer what, Integer damage) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.HIT_SNOWBALL;
   }
}