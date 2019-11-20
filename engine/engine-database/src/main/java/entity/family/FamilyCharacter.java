package entity.family;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "family_character", indexes = {
      @Index(name = "cid_familyid", columnList = "cid,familyId")
})
public class FamilyCharacter implements Serializable {
   private static final long serialVersionUID = 1L;

   @Id
   @Column(name = "cid")
   private Integer characterId;

   @Column(nullable = false)
   private Integer familyId;

   @Column(nullable = false)
   private Integer seniorId;

   @Column(nullable = false)
   private Integer reputation;

   @Column(nullable = false)
   private Integer todaysRep;

   @Column(nullable = false)
   private Integer totalReputation;

   @Column(nullable = false)
   private Integer repToSenior;

   @Column(nullable = false)
   private String precepts;

   @Column(nullable = false)
   private Long lastResetTime;

   public FamilyCharacter() {
   }

   public Integer getCharacterId() {
      return characterId;
   }

   public void setCharacterId(Integer characterId) {
      this.characterId = characterId;
   }

   public Integer getFamilyId() {
      return familyId;
   }

   public void setFamilyId(Integer familyId) {
      this.familyId = familyId;
   }

   public Integer getSeniorId() {
      return seniorId;
   }

   public void setSeniorId(Integer seniorId) {
      this.seniorId = seniorId;
   }

   public Integer getReputation() {
      return reputation;
   }

   public void setReputation(Integer reputation) {
      this.reputation = reputation;
   }

   public Integer getTodaysRep() {
      return todaysRep;
   }

   public void setTodaysRep(Integer todaysRep) {
      this.todaysRep = todaysRep;
   }

   public Integer getTotalReputation() {
      return totalReputation;
   }

   public void setTotalReputation(Integer totalReputation) {
      this.totalReputation = totalReputation;
   }

   public Integer getRepToSenior() {
      return repToSenior;
   }

   public void setRepToSenior(Integer repToSenior) {
      this.repToSenior = repToSenior;
   }

   public String getPrecepts() {
      return precepts;
   }

   public void setPrecepts(String precepts) {
      this.precepts = precepts;
   }

   public Long getLastResetTime() {
      return lastResetTime;
   }

   public void setLastResetTime(Long lastResetTime) {
      this.lastResetTime = lastResetTime;
   }
}
