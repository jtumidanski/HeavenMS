package tools.packet.buff;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowMonsterRiding(Integer characterId, Integer mountId, Integer skillId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GIVE_FOREIGN_BUFF;
   }
}
