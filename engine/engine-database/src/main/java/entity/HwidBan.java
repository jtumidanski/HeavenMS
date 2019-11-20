package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "hwidbans", indexes = {
      @Index(name = "hwid_2", columnList = "hwid", unique = true)
})
public class HwidBan implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   private Integer hwidBanId;

   @Column(nullable = false)
   private String hwid;

   public HwidBan() {
   }

   public Integer getHwidBanId() {
      return hwidBanId;
   }

   public void setHwidBanId(Integer hwidBanId) {
      this.hwidBanId = hwidBanId;
   }

   public String getHwid() {
      return hwid;
   }

   public void setHwid(String hwid) {
      this.hwid = hwid;
   }
}
