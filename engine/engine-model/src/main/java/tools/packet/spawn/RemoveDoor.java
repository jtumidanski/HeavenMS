package tools.packet.spawn;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RemoveDoor(Integer ownerId, Boolean town) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.REMOVE_DOOR;
   }
}