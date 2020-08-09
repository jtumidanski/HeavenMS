package net.server.channel.packet.ring;

public class BreakEngagementPacket extends BaseRingPacket {
   private final Integer itemId;

   public BreakEngagementPacket(Byte mode, Integer itemId) {
      super(mode);
      this.itemId = itemId;
   }

   public Integer itemId() {
      return itemId;
   }
}
