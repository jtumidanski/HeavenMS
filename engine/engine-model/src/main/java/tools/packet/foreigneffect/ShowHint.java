package tools.packet.foreigneffect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowHint(String hint, Integer width, Integer height) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.PLAYER_HINT;
   }
}