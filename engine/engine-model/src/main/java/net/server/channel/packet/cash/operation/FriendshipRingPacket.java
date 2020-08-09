package net.server.channel.packet.cash.operation;

public class FriendshipRingPacket extends BaseCashOperationPacket {
   private final Integer birthday;

   private final Integer payment;

   private final Integer sn;

   private final String sentTo;

   private final String text;

   public FriendshipRingPacket(Integer action, Integer birthday, Integer payment, Integer sn, String sentTo, String text) {
      super(action);
      this.birthday = birthday;
      this.payment = payment;
      this.sn = sn;
      this.sentTo = sentTo;
      this.text = text;
   }

   public Integer birthday() {
      return birthday;
   }

   public Integer payment() {
      return payment;
   }

   public Integer sn() {
      return sn;
   }

   public String sentTo() {
      return sentTo;
   }

   public String text() {
      return text;
   }
}
