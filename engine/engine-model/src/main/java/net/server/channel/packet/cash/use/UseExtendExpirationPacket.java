package net.server.channel.packet.cash.use;

public class UseExtendExpirationPacket extends AbstractUseCashItemPacket {
   public UseExtendExpirationPacket(Short position, Integer itemId) {
      super(position, itemId);
   }
}
