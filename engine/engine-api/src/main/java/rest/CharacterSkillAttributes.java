package rest;

public class CharacterSkillAttributes implements AttributeResult {
   private Integer level;

   private Integer masterLevel;

   private Long expiration;

   public Integer getLevel() {
      return level;
   }

   public void setLevel(Integer level) {
      this.level = level;
   }

   public Integer getMasterLevel() {
      return masterLevel;
   }

   public void setMasterLevel(Integer masterLevel) {
      this.masterLevel = masterLevel;
   }

   public Long getExpiration() {
      return expiration;
   }

   public void setExpiration(Long expiration) {
      this.expiration = expiration;
   }
}
