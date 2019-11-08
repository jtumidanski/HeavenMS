package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "macbans", indexes = {
      @Index(name = "mac_2", columnList = "mac", unique = true)
})
public class MacBan implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer macBanId;

   @Column(nullable = false, unique = true)
   private String mac;

   @Column
   private String aid;

   public MacBan() {
   }

   public Integer getMacBanId() {
      return macBanId;
   }

   public void setMacBanId(Integer macBanId) {
      this.macBanId = macBanId;
   }

   public String getMac() {
      return mac;
   }

   public void setMac(String mac) {
      this.mac = mac;
   }

   public String getAid() {
      return aid;
   }

   public void setAid(String aid) {
      this.aid = aid;
   }
}
