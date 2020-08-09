package tools.packet.message;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record NotifyJobAdvance(Integer theType, Integer job, String characterName) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.NOTIFY_JOB_CHANGE;
   }
}