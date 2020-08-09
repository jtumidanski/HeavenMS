package database.provider;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import accessor.AbstractQueryExecutor;
import client.database.data.NxCouponData;
import entity.nx.NXCoupon;

public class NxCouponProvider extends AbstractQueryExecutor {
   private static NxCouponProvider instance;

   public static NxCouponProvider getInstance() {
      if (instance == null) {
         instance = new NxCouponProvider();
      }
      return instance;
   }

   private NxCouponProvider() {
   }

   public List<NxCouponData> getCoupons(EntityManager session) {
      List<NXCoupon> coupons = session.createQuery("FROM NXCoupon n", NXCoupon.class).getResultList();
      return coupons.stream().map(result -> new NxCouponData(result.getCouponId(), result.getRate())).collect(Collectors.toList());
   }

   //TODO JDT - this needs to be figured out.
   public List<NxCouponData> getActiveCoupons(EntityManager session) {
      Calendar c = Calendar.getInstance();
      int weekDay = c.get(Calendar.DAY_OF_WEEK);
      int hourDay = c.get(Calendar.HOUR_OF_DAY);
      int weekdayMask = (1 << weekDay);
//      TypedQuery<Integer> query = session.createQuery("SELECT n.couponid FROM NXCoupon n WHERE n.activeday & :weekdayMask = :weekdayMask AND n.starthour <= :startHour AND n.endhour > :endHour", Integer.class);
//      query.setParameter("weekdayMask", weekdayMask);
//      query.setParameter("startHour", hourDay);
//      query.setParameter("endHour", hourDay);
//      return query.getResultList().stream().map(result -> new NxCouponData(result, 0)).collect(Collectors.toList());
      return Collections.emptyList();
   }
}