package net.server.channel.packet;

import net.server.MaplePacket;
import net.server.channel.packet.interaction.BasePlayerInteractionPacket;

public class ChatPlayerInteraction extends BasePlayerInteractionPacket implements MaplePacket {
   private final String message;

   public ChatPlayerInteraction(Byte mode, String message) {
      super(mode);
      this.message = message;
   }

   public String message() {
      return message;
   }
}
