package net.server.channel.packet.cash.use;

public class UseItemBagPacket extends AbstractUseCashItemPacket {
   public UseItemBagPacket(Short position, Integer itemId) {
      super(position, itemId);
   }
}
