package net.server.channel.packet.mts;

public class BuyAuctionItemPacket extends BaseMTSPacket {
   private final Integer itemId;

   public BuyAuctionItemPacket(Boolean available, Byte operation, Integer itemId) {
      super(available, operation);
      this.itemId = itemId;
   }

   public Integer itemId() {
      return itemId;
   }
}
