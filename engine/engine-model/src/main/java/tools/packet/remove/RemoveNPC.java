package tools.packet.remove;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RemoveNPC(Integer objectId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.REMOVE_NPC;
   }
}