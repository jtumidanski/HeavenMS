package net.server.channel.packet.interaction;

public class SetItemsPlayerInteractionPacket extends BasePlayerInteractionPacket {
   private final Byte slotType;

   private final Short position;

   private final Short quantity;

   private final Byte targetSlot;

   public SetItemsPlayerInteractionPacket(Byte mode, Byte slotType, Short position, Short quantity, Byte targetSlot) {
      super(mode);
      this.slotType = slotType;
      this.position = position;
      this.quantity = quantity;
      this.targetSlot = targetSlot;
   }

   public Byte slotType() {
      return slotType;
   }

   public Short position() {
      return position;
   }

   public Short quantity() {
      return quantity;
   }

   public Byte targetSlot() {
      return targetSlot;
   }
}
