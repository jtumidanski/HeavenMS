package tools.packet.character.npc;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RemovePlayerNPC(Integer objectId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.IMITATED_NPC_DATA;
   }
}