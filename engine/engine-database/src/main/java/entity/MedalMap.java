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
@Table(name = "medalmaps", indexes = {
      @Index(name = "questStatusId", columnList = "questStatusId")
})
public class MedalMap implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private Integer id;

   @Column(nullable = false)
   private Integer characterId;

   @Column(nullable = false)
   private Integer questStatusId;

   @Column(nullable = false)
   private Integer mapId;

   public MedalMap() {
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

   public Integer getQuestStatusId() {
      return questStatusId;
   }

   public void setQuestStatusId(Integer questStatusId) {
      this.questStatusId = questStatusId;
   }

   public Integer getMapId() {
      return mapId;
   }

   public void setMapId(Integer mapId) {
      this.mapId = mapId;
   }
}
