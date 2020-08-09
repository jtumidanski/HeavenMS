package net.server.channel.packet.messenger;

public class JoinMessengerPacket extends BaseMessengerPacket {
   private final Integer messengerId;

   public JoinMessengerPacket(Byte mode, Integer messengerId) {
      super(mode);
      this.messengerId = messengerId;
   }

   public Integer messengerId() {
      return messengerId;
   }
}
