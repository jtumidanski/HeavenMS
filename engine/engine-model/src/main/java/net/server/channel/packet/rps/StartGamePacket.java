package net.server.channel.packet.rps;

public class StartGamePacket extends BaseRPSActionPacket {
   public StartGamePacket(Boolean available, Byte mode) {
      super(available, mode);
   }
}
