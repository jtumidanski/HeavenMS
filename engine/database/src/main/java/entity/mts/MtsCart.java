package entity.mts;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mts_cart")
public class MtsCart implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer id;

   @Column(nullable = false, name = "cid")
   private Integer characterId;

   @Column(nullable = false)
   private Integer itemId;

   public MtsCart() {
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getCharacterId() {
      return characterId;
   }

   public void setCharacterId(Integer characterId) {
      this.characterId = characterId;
   }

   public Integer getItemId() {
      return itemId;
   }

   public void setItemId(Integer itemId) {
      this.itemId = itemId;
   }
}
