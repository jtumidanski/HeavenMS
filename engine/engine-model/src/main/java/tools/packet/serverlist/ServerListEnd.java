package tools.packet.serverlist;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ServerListEnd() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SERVER_LIST;
   }
}