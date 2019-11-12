package entity.nx;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "nxcoupons")
public class NXCoupon implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   @Column(nullable = false)
   private Integer id;

   @Column(nullable = false)
   private Integer couponId = 0;

   @Column(nullable = false)
   private Integer rate = 0;

   @Column(nullable = false)
   private Integer activeDay = 0;

   @Column(nullable = false)
   private Integer startHour = 0;

   @Column(nullable = false)
   private Integer endHour = 0;

   public NXCoupon() {
   }

   public static long getSerialVersionUID() {
      return serialVersionUID;
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getCouponId() {
      return couponId;
   }

   public void setCouponId(Integer couponId) {
      this.couponId = couponId;
   }

   public Integer getRate() {
      return rate;
   }

   public void setRate(Integer rate) {
      this.rate = rate;
   }

   public Integer getActiveDay() {
      return activeDay;
   }

   public void setActiveDay(Integer activeDay) {
      this.activeDay = activeDay;
   }

   public Integer getStartHour() {
      return startHour;
   }

   public void setStartHour(Integer startHour) {
      this.startHour = startHour;
   }

   public Integer getEndHour() {
      return endHour;
   }

   public void setEndHour(Integer endHour) {
      this.endHour = endHour;
   }
}
