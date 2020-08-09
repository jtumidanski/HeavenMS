package net.server.channel.packet.rps;

public class LeavePacket extends BaseRPSActionPacket {
   public LeavePacket(Boolean available, Byte mode) {
      super(available, mode);
   }
}
