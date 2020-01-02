package net.server.task;

import net.server.Server;

/**
 * Thread responsible for maintaining coupons EXP & DROP effects active
 */
public class CouponTask implements Runnable {
   @Override
   public void run() {
      Server.getInstance().updateActiveCoupons();
      Server.getInstance().commitActiveCoupons();
   }
}
