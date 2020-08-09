package net.server.channel.packet.buddy;

public class AcceptBuddyPacket extends BaseBuddyPacket {
   private final Integer otherCharacterId;

   public AcceptBuddyPacket(Integer mode, Integer otherCharacterId) {
      super(mode);
      this.otherCharacterId = otherCharacterId;
   }

   public Integer otherCharacterId() {
      return otherCharacterId;
   }
}
