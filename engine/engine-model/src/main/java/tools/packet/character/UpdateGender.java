package tools.packet.character;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record UpdateGender(Integer gender) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SET_GENDER;
   }
}