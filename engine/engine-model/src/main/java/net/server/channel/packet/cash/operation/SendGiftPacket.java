package net.server.channel.packet.cash.operation;

public class SendGiftPacket extends BaseCashOperationPacket {
   private final Integer birthday;

   private final Integer sn;

   private final String characterName;

   private final String message;

   public SendGiftPacket(Integer action, Integer birthday, Integer sn, String characterName, String message) {
      super(action);
      this.birthday = birthday;
      this.sn = sn;
      this.characterName = characterName;
      this.message = message;
   }

   public Integer birthday() {
      return birthday;
   }

   public Integer sn() {
      return sn;
   }

   public String characterName() {
      return characterName;
   }

   public String message() {
      return message;
   }
}
