package rest;

public class QuestUpdateAttributes implements AttributeResult {
   private Integer questId;

   private Integer questStatusId;

   private Integer infoNumber;

   private String progress;

   private Boolean infoUpdate;

   private Integer npcId;

   private Long completionTime;

   private String delayType;

   public int getQuestId() {
      return questId;
   }

   public void setQuestId(int questId) {
      this.questId = questId;
   }

   public int getQuestStatusId() {
      return questStatusId;
   }

   public void setQuestStatusId(int questStatusId) {
      this.questStatusId = questStatusId;
   }

   public int getInfoNumber() {
      return infoNumber;
   }

   public void setInfoNumber(int infoNumber) {
      this.infoNumber = infoNumber;
   }

   public String getProgress() {
      return progress;
   }

   public void setProgress(String progress) {
      this.progress = progress;
   }

   public String getDelayType() {
      return delayType;
   }

   public void setDelayType(String delayType) {
      this.delayType = delayType;
   }

   public boolean isInfoUpdate() {
      return infoUpdate;
   }

   public void setInfoUpdate(boolean infoUpdate) {
      this.infoUpdate = infoUpdate;
   }

   public int getNpcId() {
      return npcId;
   }

   public void setNpcId(int npcId) {
      this.npcId = npcId;
   }

   public Long getCompletionTime() {
      return completionTime;
   }

   public void setCompletionTime(Long completionTime) {
      this.completionTime = completionTime;
   }
}
