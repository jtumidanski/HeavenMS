package tools.packet.event;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record CoconutScore(Integer firstTeam, Integer secondTeam) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.COCONUT_SCORE;
   }
}