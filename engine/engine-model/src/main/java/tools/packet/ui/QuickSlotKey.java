package tools.packet.ui;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record QuickSlotKey(byte[] keyMap) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.QUICK_SLOT_INIT;
   }
}