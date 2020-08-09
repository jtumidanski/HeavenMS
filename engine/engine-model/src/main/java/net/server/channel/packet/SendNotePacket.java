package net.server.channel.packet;

import net.server.MaplePacket;

public class SendNotePacket extends BaseNoteActionPacket implements MaplePacket {
   private final String characterName;

   private final String message;

   public SendNotePacket(Integer action, String characterName, String message) {
      super(action);
      this.characterName = characterName;
      this.message = message;
   }

   public String characterName() {
      return characterName;
   }

   public String message() {
      return message;
   }
}
