package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "temp_data", indexes = {
      @Index(name = "mobId", columnList = "dropperId")
})
public class TempData implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   private Integer dropperId;

   @Id
   private Integer itemId = 0;

   @Column(nullable = false, name = "minimum_quantity")
   private Integer minimumQuantity;

   @Column(nullable = false, name = "maximum_quantity")
   private Integer maximumQuantity;

   @Column(nullable = false)
   private Integer questId = 0;

   @Column(nullable = false)
   private Integer chance = 0;

   public TempData() {
   }

   public Integer getDropperId() {
      return dropperId;
   }

   public void setDropperId(Integer dropperId) {
      this.dropperId = dropperId;
   }

   public Integer getItemId() {
      return itemId;
   }

   public void setItemId(Integer itemId) {
      this.itemId = itemId;
   }

   public Integer getMinimumQuantity() {
      return minimumQuantity;
   }

   public void setMinimumQuantity(Integer minimumQuantity) {
      this.minimumQuantity = minimumQuantity;
   }

   public Integer getMaximumQuantity() {
      return maximumQuantity;
   }

   public void setMaximumQuantity(Integer maximumQuantity) {
      this.maximumQuantity = maximumQuantity;
   }

   public Integer getQuestId() {
      return questId;
   }

   public void setQuestId(Integer questId) {
      this.questId = questId;
   }

   public Integer getChance() {
      return chance;
   }

   public void setChance(Integer chance) {
      this.chance = chance;
   }
}
