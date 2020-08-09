package net.server.channel.packet.interaction;

public class TakeItemBackPlayerInteractionPacket extends BasePlayerInteractionPacket {
   private final Integer slot;

   public TakeItemBackPlayerInteractionPacket(Byte mode, Integer slot) {
      super(mode);
      this.slot = slot;
   }

   public Integer slot() {
      return slot;
   }
}
