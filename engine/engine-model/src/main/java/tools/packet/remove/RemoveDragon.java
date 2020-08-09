package tools.packet.remove;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RemoveDragon(Integer characterId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.REMOVE_DRAGON;
   }
}