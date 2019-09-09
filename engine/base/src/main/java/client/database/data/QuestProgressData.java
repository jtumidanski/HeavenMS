package client.database.data;

public class QuestProgressData {
   private int questStatusId;

   private int progressId;

   private String progress;

   public QuestProgressData(int questStatusId, int progressId, String progress) {
      this.questStatusId = questStatusId;
      this.progressId = progressId;
      this.progress = progress;
   }

   public int getQuestStatusId() {
      return questStatusId;
   }

   public int getProgressId() {
      return progressId;
   }

   public String getProgress() {
      return progress;
   }
}
