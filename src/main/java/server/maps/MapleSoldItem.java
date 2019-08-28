package server.maps;

public class MapleSoldItem {
   private int itemId, mesos;
   private short quantity;
   private String buyer;

   public MapleSoldItem(String buyer, int itemId, short quantity, int mesos) {
      this.buyer = buyer;
      this.itemId = itemId;
      this.quantity = quantity;
      this.mesos = mesos;
   }

   public String getBuyer() {
      return buyer;
   }

   public int getItemId() {
      return itemId;
   }

   public short getQuantity() {
      return quantity;
   }

   public int getMesos() {
      return mesos;
   }
}
