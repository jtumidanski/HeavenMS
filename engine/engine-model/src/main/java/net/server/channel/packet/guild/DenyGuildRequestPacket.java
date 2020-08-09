package net.server.channel.packet.guild;

import net.server.MaplePacket;

public class DenyGuildRequestPacket implements MaplePacket {
   private final String characterName;

   public DenyGuildRequestPacket(String characterName) {
      this.characterName = characterName;
   }

   public String characterName() {
      return characterName;
   }
}
