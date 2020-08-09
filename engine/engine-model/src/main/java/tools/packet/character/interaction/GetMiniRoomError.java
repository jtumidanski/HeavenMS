package tools.packet.character.interaction;

import net.opcodes.SendOpcode;
import tools.packet.MiniRoomError;
import tools.packet.PacketInput;

public record GetMiniRoomError(MiniRoomError status) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.PLAYER_INTERACTION;
   }
}