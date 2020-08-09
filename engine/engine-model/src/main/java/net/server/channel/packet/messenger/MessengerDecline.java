package net.server.channel.packet.messenger;

public class MessengerDecline extends BaseMessengerPacket {
   private final String target;

   public MessengerDecline(Byte mode, String target) {
      super(mode);
      this.target = target;
   }

   public String target() {
      return target;
   }
}
