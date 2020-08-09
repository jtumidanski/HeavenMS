package net.server.channel.packet.rps;

public class TimeOverPacket extends BaseRPSActionPacket {
   public TimeOverPacket(Boolean available, Byte mode) {
      super(available, mode);
   }
}
