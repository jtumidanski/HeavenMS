package net.server.channel.packet.cash.use;

public class UseOwlSearchPacket extends AbstractUseCashItemPacket {
   private final Integer searchedItemId;

   public UseOwlSearchPacket(Short position, Integer itemId, Integer searchedItemId) {
      super(position, itemId);
      this.searchedItemId = searchedItemId;
   }

   public Integer searchedItemId() {
      return searchedItemId;
   }
}
