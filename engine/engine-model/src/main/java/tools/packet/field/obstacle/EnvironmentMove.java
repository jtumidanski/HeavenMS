package tools.packet.field.obstacle;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record EnvironmentMove(String environment, Integer mode) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FIELD_OBSTACLE_ON_OFF;
   }
}