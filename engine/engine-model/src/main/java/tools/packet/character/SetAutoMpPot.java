package tools.packet.character;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record SetAutoMpPot(Integer itemId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.AUTO_MP_POT;
   }
}