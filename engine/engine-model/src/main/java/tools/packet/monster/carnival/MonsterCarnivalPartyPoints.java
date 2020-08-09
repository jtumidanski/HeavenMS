package tools.packet.monster.carnival;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MonsterCarnivalPartyPoints(int team, int currentPoints, int totalPoints) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MONSTER_CARNIVAL_PARTY_CP;
   }
}