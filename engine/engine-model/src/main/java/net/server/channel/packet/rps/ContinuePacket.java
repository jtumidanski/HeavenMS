package net.server.channel.packet.rps;

public class ContinuePacket extends BaseRPSActionPacket {
   public ContinuePacket(Boolean available, Byte mode) {
      super(available, mode);
   }
}
