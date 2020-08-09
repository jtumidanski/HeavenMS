package net.server.channel.packet.cash.use;

public class UseHammerPacket extends AbstractUseCashItemPacket {
   private final int slot;

   public UseHammerPacket(short position, int itemId, int slot) {
      super(position, itemId);
      this.slot = slot;
   }

   public int slot() {
      return slot;
   }
}
