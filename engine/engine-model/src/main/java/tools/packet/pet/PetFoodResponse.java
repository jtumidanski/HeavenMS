package tools.packet.pet;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record PetFoodResponse(Integer characterId, Byte index, Boolean success,
                              Boolean balloonType) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.PET_COMMAND;
   }
}