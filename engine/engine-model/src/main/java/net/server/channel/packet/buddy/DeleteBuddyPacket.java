package net.server.channel.packet.buddy;

public class DeleteBuddyPacket extends BaseBuddyPacket {
   private final Integer otherCharacterId;

   public DeleteBuddyPacket(Integer mode, Integer otherCharacterId) {
      super(mode);
      this.otherCharacterId = otherCharacterId;
   }

   public Integer otherCharacterId() {
      return otherCharacterId;
   }
}
