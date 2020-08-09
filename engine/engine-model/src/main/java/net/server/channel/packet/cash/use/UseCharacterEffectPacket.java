package net.server.channel.packet.cash.use;

public class UseCharacterEffectPacket extends AbstractUseCashItemPacket {
   public UseCharacterEffectPacket(Short position, Integer itemId) {
      super(position, itemId);
   }
}
