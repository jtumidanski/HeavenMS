package net.server.channel.packet.wedding;

public class TakeRegistryItemsPacket extends BaseWeddingPacket {
   private final Integer itemPosition;

   public TakeRegistryItemsPacket(Byte mode, Integer itemPosition) {
      super(mode);
      this.itemPosition = itemPosition;
   }

   public Integer itemPosition() {
      return itemPosition;
   }
}
