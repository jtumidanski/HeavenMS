package tools.packet.npctalk;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record AskQuiz(Integer speakerTypeId, Integer speakerTemplateId, Integer resCode, String title,
                      String problemText, String hintText, Integer minInput, Integer maxInput,
                      Integer remainInitialQuiz) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.NPC_TALK;
   }
}