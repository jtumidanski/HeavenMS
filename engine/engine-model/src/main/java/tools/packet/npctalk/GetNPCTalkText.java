package tools.packet.npctalk;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GetNPCTalkText(Integer npcId, String talk, String theDef) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.NPC_TALK;
   }
}