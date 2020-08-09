package tools.packet.quest;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record UpdateQuestInfo(Short questId, Integer npcId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.UPDATE_QUEST_INFO;
   }
}