package tools.packet.monster;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record KillMonster(Integer objectId, Integer animation) implements PacketInput {
   public KillMonster(Integer objectId, Boolean animation) {
      this(objectId, animation ? 1 : 0);
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.KILL_MONSTER;
   }
}