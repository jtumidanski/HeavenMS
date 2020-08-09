package net.server.channel.packet.interaction;

public class CreateShopPlayerInteractionPacket extends BaseCreatePlayerInteractionPacket {
   private final String description;

   private final Integer itemId;

   public CreateShopPlayerInteractionPacket(Byte mode, Byte createType, String description, Integer itemId) {
      super(mode, createType);
      this.description = description;
      this.itemId = itemId;
   }

   public String description() {
      return description;
   }

   public Integer itemId() {
      return itemId;
   }
}
