package tools.packet.quest.info;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record QuestExpire(Short questId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.UPDATE_QUEST_INFO;
   }
}