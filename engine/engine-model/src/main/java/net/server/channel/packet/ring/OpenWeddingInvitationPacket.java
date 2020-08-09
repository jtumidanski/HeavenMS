package net.server.channel.packet.ring;

public class OpenWeddingInvitationPacket extends BaseRingPacket {
   private final Byte slot;

   private final Integer invitationId;

   public OpenWeddingInvitationPacket(Byte mode, Byte slot, Integer invitationId) {
      super(mode);
      this.slot = slot;
      this.invitationId = invitationId;
   }

   public Byte slot() {
      return slot;
   }

   public Integer invitationId() {
      return invitationId;
   }
}
