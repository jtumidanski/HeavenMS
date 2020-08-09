package tools.packet.pet;

import java.util.List;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record PetExceptionList(Integer characterId, Integer petId, Byte petIndex,
                               List<Integer> exclusionList) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.PET_EXCEPTION_LIST;
   }
}