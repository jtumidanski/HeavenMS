package client.database.provider;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.NxCouponData;

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

   public List<NxCouponData> getCoupons(Connection connection) {
      String sql = "SELECT couponid, rate FROM nxcoupons";
      return getList(connection, sql, ps -> {
      }, rs -> {
         List<NxCouponData> couponData = new ArrayList<>();
         while (rs.next()) {
            int cid = rs.getInt("couponid");
            int rate = rs.getInt("rate");
            couponData.add(new NxCouponData(cid, rate));
         }
         return couponData;
      });
   }

   public List<NxCouponData> getActiveCoupons(Connection connection) {
      Calendar c = Calendar.getInstance();
      int weekDay = c.get(Calendar.DAY_OF_WEEK);
      int hourDay = c.get(Calendar.HOUR_OF_DAY);
      int weekdayMask = (1 << weekDay);
      String sql = "SELECT couponid FROM nxcoupons WHERE (activeday & ?) = ? AND starthour <= ? AND endhour > ?";
      return getList(connection, sql, ps -> {
         ps.setInt(1, weekdayMask);
         ps.setInt(2, weekdayMask);
         ps.setInt(3, hourDay);
         ps.setInt(4, hourDay);
      }, rs -> {
         List<NxCouponData> couponData = new ArrayList<>();
         while (rs != null && rs.next()) {
            couponData.add(new NxCouponData(rs.getInt("couponid"), 0));
         }
         return couponData;
      });
   }
}