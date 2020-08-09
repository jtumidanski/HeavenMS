package tools.packet.remove;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RemoveKite(Integer objectId, Integer animationType) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.REMOVE_KITE;
   }
}