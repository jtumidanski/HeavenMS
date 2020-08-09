package net.server.channel.packet.messenger;

public class MessengerInvite extends BaseMessengerPacket {
   private final String input;

   public MessengerInvite(Byte mode, String input) {
      super(mode);
      this.input = input;
   }

   public String input() {
      return input;
   }
}
