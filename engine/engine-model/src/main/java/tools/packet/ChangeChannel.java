package tools.packet;

import java.net.InetAddress;

import net.opcodes.SendOpcode;

public record ChangeChannel(InetAddress inetAddress, Integer port) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CHANGE_CHANNEL;
   }
}