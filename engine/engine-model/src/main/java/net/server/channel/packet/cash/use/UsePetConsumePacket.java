package net.server.channel.packet.cash.use;

public class UsePetConsumePacket extends AbstractUseCashItemPacket {
   public UsePetConsumePacket(Short position, Integer itemId) {
      super(position, itemId);
   }
}
