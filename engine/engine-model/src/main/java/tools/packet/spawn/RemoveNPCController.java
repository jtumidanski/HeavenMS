package tools.packet.spawn;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RemoveNPCController(Integer objectId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPAWN_NPC_REQUEST_CONTROLLER;
   }
}