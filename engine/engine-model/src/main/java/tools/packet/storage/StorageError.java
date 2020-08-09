package tools.packet.storage;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record StorageError(Byte theType) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.STORAGE;
   }
}