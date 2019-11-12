package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "reactordrops", indexes = {
      @Index(name = "reactorId", columnList = "reactorId")
})
public class ReactorDrop implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer reactorDropId;

   @Column(nullable = false)
   private Integer reactorId;

   @Column(nullable = false)
   private Integer itemId;

   @Column(nullable = false)
   private Integer chance;

   @Column(nullable = false)
   private Integer questId;

   public ReactorDrop() {
   }

   public Integer getReactorDropId() {
      return reactorDropId;
   }

   public void setReactorDropId(Integer reactorDropId) {
      this.reactorDropId = reactorDropId;
   }

   public Integer getReactorId() {
      return reactorId;
   }

   public void setReactorId(Integer reactorId) {
      this.reactorId = reactorId;
   }

   public Integer getItemId() {
      return itemId;
   }

   public void setItemId(Integer itemId) {
      this.itemId = itemId;
   }

   public Integer getChance() {
      return chance;
   }

   public void setChance(Integer chance) {
      this.chance = chance;
   }

   public Integer getQuestId() {
      return questId;
   }

   public void setQuestId(Integer questId) {
      this.questId = questId;
   }
}
