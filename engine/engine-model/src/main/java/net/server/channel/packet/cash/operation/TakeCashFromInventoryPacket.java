package net.server.channel.packet.cash.operation;

public class TakeCashFromInventoryPacket extends BaseCashOperationPacket {
   private final Integer itemId;

   public TakeCashFromInventoryPacket(Integer action, Integer itemId) {
      super(action);
      this.itemId = itemId;
   }

   public Integer itemId() {
      return itemId;
   }
}
