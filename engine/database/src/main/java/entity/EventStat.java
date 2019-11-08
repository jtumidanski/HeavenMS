package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "eventstats")
public class EventStat implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   private Integer characterId;

   @Column(nullable = false)
   private String name;

   @Column(nullable = false)
   private Integer info;

   public EventStat() {
   }

   public Integer getCharacterId() {
      return characterId;
   }

   public void setCharacterId(Integer characterId) {
      this.characterId = characterId;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Integer getInfo() {
      return info;
   }

   public void setInfo(Integer info) {
      this.info = info;
   }
}
