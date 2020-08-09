package tools.packet.ui;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record OpenUI(Byte ui) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.OPEN_UI;
   }
}