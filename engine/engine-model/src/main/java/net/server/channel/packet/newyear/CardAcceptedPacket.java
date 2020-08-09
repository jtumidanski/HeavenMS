package net.server.channel.packet.newyear;

public class CardAcceptedPacket extends BaseNewYearCardPacket {
   private final Integer cardId;

   public CardAcceptedPacket(Byte reqMode, Integer cardId) {
      super(reqMode);
      this.cardId = cardId;
   }

   public Integer cardId() {
      return cardId;
   }
}
