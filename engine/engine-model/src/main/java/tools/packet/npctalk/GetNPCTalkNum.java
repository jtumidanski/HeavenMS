package tools.packet.npctalk;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GetNPCTalkNum(Integer npcId, String talk, Integer theDef, Integer min,
                            Integer max) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.NPC_TALK;
   }
}