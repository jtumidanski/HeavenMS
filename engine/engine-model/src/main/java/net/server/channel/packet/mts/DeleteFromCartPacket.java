package net.server.channel.packet.mts;

public class DeleteFromCartPacket extends BaseMTSPacket {
   private final Integer itemId;

   public DeleteFromCartPacket(Boolean available, Byte operation, Integer itemId) {
      super(available, operation);
      this.itemId = itemId;
   }

   public Integer itemId() {
      return itemId;
   }
}
