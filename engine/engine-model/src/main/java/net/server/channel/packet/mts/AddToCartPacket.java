package net.server.channel.packet.mts;

public class AddToCartPacket extends BaseMTSPacket {
   private final Integer itemId;

   public AddToCartPacket(Boolean available, Byte operation, Integer itemId) {
      super(available, operation);
      this.itemId = itemId;
   }

   public Integer itemId() {
      return itemId;
   }
}
