package tools.packet.monster.carnival;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MonsterCarnivalMessage(Byte message) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MONSTER_CARNIVAL_MESSAGE;
   }
}