package net.server.channel.packet.mts;

public class CancelSalePacket extends BaseMTSPacket {
   private final Integer itemId;

   public CancelSalePacket(Boolean available, Byte operation, Integer itemId) {
      super(available, operation);
      this.itemId = itemId;
   }

   public Integer itemId() {
      return itemId;
   }
}
