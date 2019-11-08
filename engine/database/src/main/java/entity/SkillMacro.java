package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "skillmacros")
public class SkillMacro implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue
   private Integer id;

   @Column(nullable = false)
   private Integer characterId;

   @Column(nullable = false)
   private Integer position;

   @Column(nullable = false)
   private Integer skill1;

   @Column(nullable = false)
   private Integer skill2;

   @Column(nullable = false)
   private Integer skill3;

   @Column
   private String name;

   @Column(nullable = false)
   private Integer shout;

   public SkillMacro() {
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

   public Integer getPosition() {
      return position;
   }

   public void setPosition(Integer position) {
      this.position = position;
   }

   public Integer getSkill1() {
      return skill1;
   }

   public void setSkill1(Integer skill1) {
      this.skill1 = skill1;
   }

   public Integer getSkill2() {
      return skill2;
   }

   public void setSkill2(Integer skill2) {
      this.skill2 = skill2;
   }

   public Integer getSkill3() {
      return skill3;
   }

   public void setSkill3(Integer skill3) {
      this.skill3 = skill3;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Integer getShout() {
      return shout;
   }

   public void setShout(Integer shout) {
      this.shout = shout;
   }
}
