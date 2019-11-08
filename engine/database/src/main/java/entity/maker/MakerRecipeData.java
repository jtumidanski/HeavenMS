package entity.maker;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "makerrecipedata")
public class MakerRecipeData implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   private Integer itemId;

   @Id
   @Column(nullable = false, name = "req_item")
   private Integer requiredItem;

   @Column(nullable = false)
   private Integer count;

   public MakerRecipeData() {
   }

   public Integer getItemId() {
      return itemId;
   }

   public void setItemId(Integer itemId) {
      this.itemId = itemId;
   }

   public Integer getRequiredItem() {
      return requiredItem;
   }

   public void setRequiredItem(Integer requiredItem) {
      this.requiredItem = requiredItem;
   }

   public Integer getCount() {
      return count;
   }

   public void setCount(Integer count) {
      this.count = count;
   }
}
