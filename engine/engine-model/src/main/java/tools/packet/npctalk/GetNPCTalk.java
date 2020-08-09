package tools.packet.npctalk;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GetNPCTalk(Integer npcId, Byte messageType, String talk, String endBytes,
                         Byte speaker) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.NPC_TALK;
   }
}