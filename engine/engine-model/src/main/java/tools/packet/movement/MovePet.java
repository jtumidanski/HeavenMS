package tools.packet.movement;

import java.util.List;

import net.opcodes.SendOpcode;
import server.movement.LifeMovementFragment;
import tools.packet.PacketInput;

public record MovePet(int characterId, int petId, byte slot,
                      List<LifeMovementFragment> movementList) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MOVE_PET;
   }
}