package tools.packet.parcel;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RemoveDueyItem(Boolean remove, Integer packageId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.PARCEL;
   }
}