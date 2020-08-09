package tools.packet;

import java.util.Set;

import net.opcodes.SendOpcode;
import tools.Pair;

public record SetNPCScriptable(Set<Pair<Integer, String>> descriptions) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SET_NPC_SCRIPTABLE;
   }
}