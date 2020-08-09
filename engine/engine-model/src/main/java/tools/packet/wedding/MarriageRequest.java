package tools.packet.wedding;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MarriageRequest(String name, Integer characterId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MARRIAGE_REQUEST;
   }
}