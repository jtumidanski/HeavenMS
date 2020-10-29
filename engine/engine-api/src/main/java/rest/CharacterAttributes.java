package rest;

public class CharacterAttributes implements AttributeResult {
   private String name;

   private Integer accountId;

   private Integer mapId;

   private Integer jobId;

   private Integer level;

   private Integer experience;

   private Integer fame;

   private Integer meso;

   private Boolean gm;

   private Integer gender;

   private Integer x;

   private Integer y;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Integer getAccountId() {
      return accountId;
   }

   public void setAccountId(Integer accountId) {
      this.accountId = accountId;
   }

   public Integer getMapId() {
      return mapId;
   }

   public void setMapId(Integer mapId) {
      this.mapId = mapId;
   }

   public Integer getJobId() {
      return jobId;
   }

   public void setJobId(Integer jobId) {
      this.jobId = jobId;
   }

   public Integer getLevel() {
      return level;
   }

   public void setLevel(Integer level) {
      this.level = level;
   }

   public Integer getExperience() {
      return experience;
   }

   public void setExperience(Integer experience) {
      this.experience = experience;
   }

   public Integer getFame() {
      return fame;
   }

   public void setFame(Integer fame) {
      this.fame = fame;
   }

   public Integer getMeso() {
      return meso;
   }

   public void setMeso(Integer meso) {
      this.meso = meso;
   }

   public Boolean isGm() {
      return gm;
   }

   public void setGm(Boolean gm) {
      this.gm = gm;
   }

   public Integer getGender() {
      return gender;
   }

   public void setGender(Integer gender) {
      this.gender = gender;
   }

   public Integer getX() {
      return x;
   }

   public void setX(Integer x) {
      this.x = x;
   }

   public Integer getY() {
      return y;
   }

   public void setY(Integer y) {
      this.y = y;
   }
}
