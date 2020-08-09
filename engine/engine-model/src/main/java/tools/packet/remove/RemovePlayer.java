package tools.packet.remove;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RemovePlayer(Integer characterId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.REMOVE_PLAYER_FROM_MAP;
   }
}