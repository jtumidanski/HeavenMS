package tools.packet.parcel;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record DueyParcel(Boolean quick) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.PARCEL;
   }
}