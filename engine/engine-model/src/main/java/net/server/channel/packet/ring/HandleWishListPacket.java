package net.server.channel.packet.ring;

public class HandleWishListPacket extends BaseRingPacket {
   private final Integer amount;

   private final String[] items;

   public HandleWishListPacket(Byte mode, Integer amount, String[] items) {
      super(mode);
      this.amount = amount;
      this.items = items;
   }

   public Integer amount() {
      return amount;
   }

   public String[] items() {
      return items;
   }
}
