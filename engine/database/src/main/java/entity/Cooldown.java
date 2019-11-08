package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cooldowns")
public class Cooldown implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer id;

   @Column(nullable = false)
   private Integer characterId;

   @Column(nullable = false)
   private Integer skillId;

   @Column(nullable = false)
   private Long length;

   @Column(nullable = false)
   private Long startTime;

   public Cooldown() {
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

   public Integer getSkillId() {
      return skillId;
   }

   public void setSkillId(Integer skillId) {
      this.skillId = skillId;
   }

   public Long getLength() {
      return length;
   }

   public void setLength(Long length) {
      this.length = length;
   }

   public Long getStartTime() {
      return startTime;
   }

   public void setStartTime(Long startTime) {
      this.startTime = startTime;
   }
}
