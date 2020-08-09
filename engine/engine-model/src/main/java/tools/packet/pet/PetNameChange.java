package tools.packet.pet;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record PetNameChange(Integer characterId, String newName, Integer slot) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.PET_NAME_CHANGE;
   }
}