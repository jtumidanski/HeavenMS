package net.server.channel.packet.cash.use;

public class UseItemTagPacket extends AbstractUseCashItemPacket {
   private final int slot;

   public UseItemTagPacket(short position, int itemId, int slot) {
      super(position, itemId);
      this.slot = slot;
   }

   public int slot() {
      return slot;
   }
}
