package net.server.channel.packet.interaction;

public class AddItemPlayerInteractionPacket extends BasePlayerInteractionPacket {
   private final Byte slotType;

   private final Short slot;

   private final Short bundles;

   private final Short perBundle;

   private final Integer price;

   public AddItemPlayerInteractionPacket(Byte mode, Byte slotType, Short slot, Short bundles, Short perBundle, Integer price) {
      super(mode);
      this.slotType = slotType;
      this.slot = slot;
      this.bundles = bundles;
      this.perBundle = perBundle;
      this.price = price;
   }

   public Byte slotType() {
      return slotType;
   }

   public Short slot() {
      return slot;
   }

   public Short bundles() {
      return bundles;
   }

   public Short perBundle() {
      return perBundle;
   }

   public Integer price() {
      return price;
   }
}
