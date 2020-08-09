package tools.packet.quest.info;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record QuestFinish(Short questId, Integer npcId, Short nextQuestId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.UPDATE_QUEST_INFO;
   }
}