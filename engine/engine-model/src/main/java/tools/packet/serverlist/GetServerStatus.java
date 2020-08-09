package tools.packet.serverlist;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GetServerStatus(ServerStatus status) implements PacketInput {
   public GetServerStatus(Integer status) {
      this(ServerStatus.fromValue(status));
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SERVER_STATUS;
   }
}