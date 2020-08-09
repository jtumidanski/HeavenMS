package tools.packet.buff;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GiveFinalAttack(Integer skillId, Integer time) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.GIVE_BUFF;
   }
}
