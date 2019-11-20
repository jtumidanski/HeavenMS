package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "marriages")
public class Marriage implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer marriageId;

   @Column(nullable = false)
   private Integer husbandId;

   @Column(nullable = false)
   private Integer wifeId;

   public Marriage() {
   }

   public Integer getMarriageId() {
      return marriageId;
   }

   public void setMarriageId(Integer marriageId) {
      this.marriageId = marriageId;
   }

   public Integer getHusbandId() {
      return husbandId;
   }

   public void setHusbandId(Integer husbandId) {
      this.husbandId = husbandId;
   }

   public Integer getWifeId() {
      return wifeId;
   }

   public void setWifeId(Integer wifeId) {
      this.wifeId = wifeId;
   }
}
