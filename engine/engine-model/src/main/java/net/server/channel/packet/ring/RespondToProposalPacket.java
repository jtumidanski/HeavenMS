package net.server.channel.packet.ring;

public class RespondToProposalPacket extends BaseRingPacket {
   private final Boolean accepted;

   private final String name;

   private final Integer itemId;

   public RespondToProposalPacket(Byte mode, Boolean accepted, String name, Integer itemId) {
      super(mode);
      this.accepted = accepted;
      this.name = name;
      this.itemId = itemId;
   }

   public Boolean accepted() {
      return accepted;
   }

   public String name() {
      return name;
   }

   public Integer itemId() {
      return itemId;
   }
}
