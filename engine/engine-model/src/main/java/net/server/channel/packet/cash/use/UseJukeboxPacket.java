package net.server.channel.packet.cash.use;

public class UseJukeboxPacket extends AbstractUseCashItemPacket {
   public UseJukeboxPacket(Short position, Integer itemId) {
      super(position, itemId);
   }
}
