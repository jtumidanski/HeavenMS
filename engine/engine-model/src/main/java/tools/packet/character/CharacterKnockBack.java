package tools.packet.character;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record CharacterKnockBack() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.LEFT_KNOCK_BACK;
   }
}