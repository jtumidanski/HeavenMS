package client.database.data;

public class NxCouponData {
   private int couponId;

   private int rate;

   public NxCouponData(int couponId, int rate) {
      this.couponId = couponId;
      this.rate = rate;
   }

   public int getCouponId() {
      return couponId;
   }

   public int getRate() {
      return rate;
   }
}
