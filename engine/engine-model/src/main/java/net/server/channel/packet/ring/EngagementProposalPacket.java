package net.server.channel.packet.ring;

public class EngagementProposalPacket extends BaseRingPacket {
   private final String name;

   private final Integer itemId;

   public EngagementProposalPacket(Byte mode, String name, Integer itemId) {
      super(mode);
      this.name = name;
      this.itemId = itemId;
   }

   public String name() {
      return name;
   }

   public Integer itemId() {
      return itemId;
   }
}
