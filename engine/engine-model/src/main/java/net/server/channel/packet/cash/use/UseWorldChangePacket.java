package net.server.channel.packet.cash.use;

public class UseWorldChangePacket extends AbstractUseCashItemPacket {
   public UseWorldChangePacket(Short position, Integer itemId) {
      super(position, itemId);
   }
}
