package tools.packet.movement;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MoveMonsterResponse(Integer objectId, Short moveId, Integer currentMp, Boolean useSkills, Integer skillId,
                                  Integer skillLevel) implements PacketInput {
   public MoveMonsterResponse(Integer objectId, Short moveId, Integer currentMp, Boolean useSkills) {
      this(objectId, moveId, currentMp, useSkills, 0, 0);
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.MOVE_MONSTER_RESPONSE;
   }
}