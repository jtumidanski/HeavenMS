package net.server.channel.packet.cash.use;

public class UseIncubatorPacket extends AbstractUseCashItemPacket {
   private final byte itemType;

   private final int slot;

   public UseIncubatorPacket(short position, int itemId, byte itemType, int slot) {
      super(position, itemId);
      this.itemType = itemType;
      this.slot = slot;
   }

   public byte itemType() {
      return itemType;
   }

   public int slot() {
      return slot;
   }
}
