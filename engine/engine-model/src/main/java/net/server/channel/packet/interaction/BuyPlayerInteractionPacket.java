package net.server.channel.packet.interaction;

public class BuyPlayerInteractionPacket extends BasePlayerInteractionPacket {
   private final Integer itemId;

   private final Short quantity;

   public BuyPlayerInteractionPacket(Byte mode, Integer itemId, Short quantity) {
      super(mode);
      this.itemId = itemId;
      this.quantity = quantity;
   }

   public Integer itemId() {
      return itemId;
   }

   public Short quantity() {
      return quantity;
   }
}
