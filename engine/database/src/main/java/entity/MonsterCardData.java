package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "monstercarddata")
public class MonsterCardData implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer id;

   @Column(nullable = false)
   private Integer cardId;

   @Column(nullable = false)
   private Integer mobId;

   public MonsterCardData() {
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getCardId() {
      return cardId;
   }

   public void setCardId(Integer cardId) {
      this.cardId = cardId;
   }

   public Integer getMobId() {
      return mobId;
   }

   public void setMobId(Integer mobId) {
      this.mobId = mobId;
   }
}
