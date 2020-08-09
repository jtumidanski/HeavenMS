package tools.packet.foreigneffect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowCombo(int count) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SHOW_COMBO;
   }
}