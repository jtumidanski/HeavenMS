package net.server.channel.packet.interaction;

public class RemoveItemPlayerInteractionPacket extends BasePlayerInteractionPacket {
   private final Integer slot;

   public RemoveItemPlayerInteractionPacket(Byte mode, Integer slot) {
      super(mode);
      this.slot = slot;
   }

   public Integer slot() {
      return slot;
   }
}
