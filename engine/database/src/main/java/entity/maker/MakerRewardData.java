package entity.maker;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "makerrewarddata")
public class MakerRewardData implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   private Integer itemId;

   @Id
   private Integer rewardId;

   @Column(nullable = false)
   private Integer quantity;

   @Column(nullable = false)
   private Integer prob = 100;

   public MakerRewardData() {
   }

   public Integer getItemId() {
      return itemId;
   }

   public void setItemId(Integer itemId) {
      this.itemId = itemId;
   }

   public Integer getRewardId() {
      return rewardId;
   }

   public void setRewardId(Integer rewardId) {
      this.rewardId = rewardId;
   }

   public Integer getQuantity() {
      return quantity;
   }

   public void setQuantity(Integer quantity) {
      this.quantity = quantity;
   }

   public Integer getProb() {
      return prob;
   }

   public void setProb(Integer prob) {
      this.prob = prob;
   }
}
