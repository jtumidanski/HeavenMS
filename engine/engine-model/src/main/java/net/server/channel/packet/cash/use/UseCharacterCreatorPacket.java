package net.server.channel.packet.cash.use;

public class UseCharacterCreatorPacket extends AbstractUseCashItemPacket {
   private final String name;

   private final Integer face;

   private final Integer hair;

   private final Integer hairColor;

   private final Integer skin;

   private final Integer gender;

   private final Integer jobId;

   private final Integer improveSp;

   public UseCharacterCreatorPacket(Short position, Integer itemId, String name, Integer face, Integer hair, Integer hairColor, Integer skin, Integer gender, Integer jobId, Integer improveSp) {
      super(position, itemId);
      this.name = name;
      this.face = face;
      this.hair = hair;
      this.hairColor = hairColor;
      this.skin = skin;
      this.gender = gender;
      this.jobId = jobId;
      this.improveSp = improveSp;
   }

   public String name() {
      return name;
   }

   public Integer face() {
      return face;
   }

   public Integer hair() {
      return hair;
   }

   public Integer hairColor() {
      return hairColor;
   }

   public Integer skin() {
      return skin;
   }

   public Integer gender() {
      return gender;
   }

   public Integer jobId() {
      return jobId;
   }

   public Integer improveSp() {
      return improveSp;
   }
}
