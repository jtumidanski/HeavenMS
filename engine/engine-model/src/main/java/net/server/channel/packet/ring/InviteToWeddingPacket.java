package net.server.channel.packet.ring;

public class InviteToWeddingPacket extends BaseRingPacket {
   private final String name;

   private final Integer marriageId;

   private final Byte slot;

   public InviteToWeddingPacket(Byte mode, String name, Integer marriageId, Byte slot) {
      super(mode);
      this.name = name;
      this.marriageId = marriageId;
      this.slot = slot;
   }

   public String name() {
      return name;
   }

   public Integer marriageId() {
      return marriageId;
   }

   public Byte slot() {
      return slot;
   }
}
