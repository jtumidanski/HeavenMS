package database.administrator;

import javax.persistence.EntityManager;

import database.AbstractQueryExecutor;
import entity.nx.NXCoupon;

public class NxCouponAdministrator extends AbstractQueryExecutor {
   private static NxCouponAdministrator instance;

   public static NxCouponAdministrator getInstance() {
      if (instance == null) {
         instance = new NxCouponAdministrator();
      }
      return instance;
   }

   private NxCouponAdministrator() {
   }

   public int create(EntityManager entityManager, int couponId, int rate, int activeDay, int startHour, int endHour) {
      NXCoupon nxCoupon = new NXCoupon();
      nxCoupon.setCouponId(couponId);
      nxCoupon.setRate(rate);
      nxCoupon.setActiveDay(activeDay);
      nxCoupon.setStartHour(startHour);
      nxCoupon.setEndHour(endHour);
      insert(entityManager, nxCoupon);
      return nxCoupon.getId();
   }
}