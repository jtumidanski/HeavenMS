package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "gifts")
public class Gift implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer id;

   @Column(nullable = false)
   private Integer giftedTo;

   @Column(nullable = false)
   private String giftedFrom;

   @Column(nullable = false)
   private String message;

   @Column(nullable = false)
   private Integer sn;

   @Column(nullable = false)
   private Integer ringId;

   public Gift() {
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getGiftedTo() {
      return giftedTo;
   }

   public void setGiftedTo(Integer to) {
      this.giftedTo = to;
   }

   public String getGiftedFrom() {
      return giftedFrom;
   }

   public void setGiftedFrom(String from) {
      this.giftedFrom = from;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public Integer getSn() {
      return sn;
   }

   public void setSn(Integer sn) {
      this.sn = sn;
   }

   public Integer getRingId() {
      return ringId;
   }

   public void setRingId(Integer ringId) {
      this.ringId = ringId;
   }
}
