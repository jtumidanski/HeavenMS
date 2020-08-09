package tools.packet.ui;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowOXQuiz(Integer questionSet, Integer questionId, Boolean askQuestion) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.OX_QUIZ;
   }
}