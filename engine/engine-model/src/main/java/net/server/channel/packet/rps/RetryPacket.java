package net.server.channel.packet.rps;

public class RetryPacket extends BaseRPSActionPacket {
   public RetryPacket(Boolean available, Byte mode) {
      super(available, mode);
   }
}
