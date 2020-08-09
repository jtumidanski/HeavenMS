package tools.packet.quest.info;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowQuestComplete(int questId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.QUEST_CLEAR;
   }
}