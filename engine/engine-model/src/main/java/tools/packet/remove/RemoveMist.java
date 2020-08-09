package tools.packet.remove;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RemoveMist(Integer objectId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.REMOVE_MIST;
   }
}