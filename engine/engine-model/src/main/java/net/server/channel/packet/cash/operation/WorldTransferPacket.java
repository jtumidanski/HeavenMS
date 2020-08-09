package net.server.channel.packet.cash.operation;

public class WorldTransferPacket extends BaseCashOperationPacket {
   private final Integer itemId;

   private final Integer newWorldId;

   public WorldTransferPacket(Integer action, Integer itemId, Integer newWorldId) {
      super(action);
      this.itemId = itemId;
      this.newWorldId = newWorldId;
   }

   public Integer itemId() {
      return itemId;
   }

   public Integer newWorldId() {
      return newWorldId;
   }
}
