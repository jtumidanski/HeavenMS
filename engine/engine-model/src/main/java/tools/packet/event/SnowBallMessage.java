package tools.packet.event;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record SnowBallMessage(Integer team, Integer message) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SNOWBALL_MESSAGE;
   }
}