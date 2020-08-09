package net.server.channel.packet.messenger;

public class MessengerChat extends BaseMessengerPacket {
   private final String input;

   public MessengerChat(Byte mode, String input) {
      super(mode);
      this.input = input;
   }

   public String input() {
      return input;
   }
}
