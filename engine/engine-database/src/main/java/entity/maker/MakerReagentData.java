package entity.maker;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "makerreagentdata")
public class MakerReagentData implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   private Integer itemId;

   @Column(nullable = false)
   private String stat;

   @Column(nullable = false)
   private Integer value;

   public MakerReagentData() {
   }

   public Integer getItemId() {
      return itemId;
   }

   public void setItemId(Integer itemId) {
      this.itemId = itemId;
   }

   public String getStat() {
      return stat;
   }

   public void setStat(String stat) {
      this.stat = stat;
   }

   public Integer getValue() {
      return value;
   }

   public void setValue(Integer value) {
      this.value = value;
   }
}
