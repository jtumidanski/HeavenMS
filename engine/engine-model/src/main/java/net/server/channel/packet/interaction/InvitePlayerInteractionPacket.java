package net.server.channel.packet.interaction;

public class InvitePlayerInteractionPacket extends BasePlayerInteractionPacket {
   private final Integer otherCharacterId;

   public InvitePlayerInteractionPacket(Byte mode, Integer otherCharacterId) {
      super(mode);
      this.otherCharacterId = otherCharacterId;
   }

   public Integer otherCharacterId() {
      return otherCharacterId;
   }
}
