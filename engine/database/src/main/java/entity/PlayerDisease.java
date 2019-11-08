package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "playerdiseases")
public class PlayerDisease implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer id;

   @Column(nullable = false, name = "charid")
   private Integer characterId;

   @Column(nullable = false)
   private Integer disease;

   @Column(nullable = false)
   private Integer mobSkillId;

   @Column(nullable = false)
   private Integer mobSkillLevel;

   @Column(nullable = false)
   private Integer length = 1;

   public PlayerDisease() {
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

   public Integer getDisease() {
      return disease;
   }

   public void setDisease(Integer disease) {
      this.disease = disease;
   }

   public Integer getMobSkillId() {
      return mobSkillId;
   }

   public void setMobSkillId(Integer mobSkillId) {
      this.mobSkillId = mobSkillId;
   }

   public Integer getMobSkillLevel() {
      return mobSkillLevel;
   }

   public void setMobSkillLevel(Integer mobSkillLevel) {
      this.mobSkillLevel = mobSkillLevel;
   }

   public Integer getLength() {
      return length;
   }

   public void setLength(Integer length) {
      this.length = length;
   }
}
