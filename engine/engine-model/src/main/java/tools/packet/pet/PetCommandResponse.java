package tools.packet.pet;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record PetCommandResponse(int characterId, byte index, boolean talk, int animation,
                                 boolean balloonType) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.PET_COMMAND;
   }
}