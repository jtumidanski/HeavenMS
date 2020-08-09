package net.server.channel.packet.cash.use;

public class UsePetNameChangePacket extends AbstractUseCashItemPacket {
   private final String newName;

   public UsePetNameChangePacket(Short position, Integer itemId, String newName) {
      super(position, itemId);
      this.newName = newName;
   }

   public String newName() {
      return newName;
   }
}
