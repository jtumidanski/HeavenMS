package tools.packet.npctalk;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record AskSpeedQuiz(Integer speakerTypeId, Integer speakerTemplateId, Integer resCode, Integer theType,
                           Integer answer, Integer correct, Integer remain,
                           Integer remainInitialQuiz) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.NPC_TALK;
   }
}