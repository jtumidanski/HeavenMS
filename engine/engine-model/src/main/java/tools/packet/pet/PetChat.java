package tools.packet.pet;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record PetChat(Integer characterId, Byte index, Integer act, String text) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.PET_CHAT;
   }
}