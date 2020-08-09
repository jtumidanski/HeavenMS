package net.server.channel.packet.party;

import net.server.MaplePacket;

public class DenyPartyRequestPacket implements MaplePacket {
   private final String message;

   public DenyPartyRequestPacket(String message) {
      this.message = message;
   }

   public String message() {
      return message;
   }
}
