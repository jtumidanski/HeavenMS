package net.server.channel.packet.cash.use;

public class UseNameChangePacket extends AbstractUseCashItemPacket {
   public UseNameChangePacket(Short position, Integer itemId) {
      super(position, itemId);
   }
}
