package net.server.channel.packet.mts;

public class TransferItemPacket extends BaseMTSPacket {
   private final Integer itemId;

   public TransferItemPacket(Boolean available, Byte operation, Integer itemId) {
      super(available, operation);
      this.itemId = itemId;
   }

   public Integer itemId() {
      return itemId;
   }
}
