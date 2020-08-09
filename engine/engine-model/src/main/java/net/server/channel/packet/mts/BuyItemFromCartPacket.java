package net.server.channel.packet.mts;

public class BuyItemFromCartPacket extends BaseMTSPacket {
   private final Integer itemId;

   public BuyItemFromCartPacket(Boolean available, Byte operation, Integer itemId) {
      super(available, operation);
      this.itemId = itemId;
   }

   public Integer itemId() {
      return itemId;
   }
}
