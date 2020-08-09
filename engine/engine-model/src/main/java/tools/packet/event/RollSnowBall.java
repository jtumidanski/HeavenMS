package tools.packet.event;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RollSnowBall(Boolean enterMap, Integer state, Integer firstSnowmanHP, Integer firstSnowBallPosition,
                           Integer secondSnowmanHP, Integer secondSnowBallPosition) implements PacketInput {
   public RollSnowBall(Boolean enterMap, Integer state) {
      this(enterMap, state, 0, 0, 0, 0);
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SNOWBALL_STATE;
   }
}