package net.server.channel.packet.cash.use;

public class UseMapleTvPacket extends AbstractUseCashItemPacket {
   private final Boolean megaMessenger;

   private final Boolean ear;

   private final String characterName;

   private final String[] messages;

   public UseMapleTvPacket(Short position, Integer itemId, Boolean megaMessenger, Boolean ear, String characterName, String[] messages) {
      super(position, itemId);
      this.megaMessenger = megaMessenger;
      this.ear = ear;
      this.characterName = characterName;
      this.messages = messages;
   }

   public Boolean megaMessenger() {
      return megaMessenger;
   }

   public Boolean ear() {
      return ear;
   }

   public String characterName() {
      return characterName;
   }

   public String[] messages() {
      return messages;
   }
}
