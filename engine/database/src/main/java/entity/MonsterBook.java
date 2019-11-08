package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "monsterbook")
public class MonsterBook implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @Column(name = "charid")
   private Integer characterId;

   @Column(nullable = false)
   private Integer cardId;

   @Column(nullable = false)
   private Integer level = 1;

   public MonsterBook() {
   }

   public Integer getCharacterId() {
      return characterId;
   }

   public void setCharacterId(Integer characterId) {
      this.characterId = characterId;
   }

   public Integer getCardId() {
      return cardId;
   }

   public void setCardId(Integer cardId) {
      this.cardId = cardId;
   }

   public Integer getLevel() {
      return level;
   }

   public void setLevel(Integer level) {
      this.level = level;
   }
}
