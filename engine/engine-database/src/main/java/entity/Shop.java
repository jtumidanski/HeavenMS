package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "shops")
public class Shop implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer shopId;

   @Column(nullable = false)
   private Integer npcId;

   public Shop() {
   }

   public Integer getShopId() {
      return shopId;
   }

   public void setShopId(Integer shopId) {
      this.shopId = shopId;
   }

   public Integer getNpcId() {
      return npcId;
   }

   public void setNpcId(Integer npcId) {
      this.npcId = npcId;
   }
}
