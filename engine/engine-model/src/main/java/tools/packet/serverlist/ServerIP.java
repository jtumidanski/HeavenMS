package tools.packet.serverlist;

import java.net.InetAddress;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ServerIP(InetAddress inetAddress, Integer port, Integer clientId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SERVER_IP;
   }
}