package tools.packet.storage;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MesoStorage(Byte slots, Integer meso) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.STORAGE;
   }
}