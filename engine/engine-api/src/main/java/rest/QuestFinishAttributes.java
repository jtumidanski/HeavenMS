package rest;

public class QuestFinishAttributes implements AttributeResult {
   private Integer npcId;

   private Short nextQuestId;

   public Integer getNpcId() {
      return npcId;
   }

   public void setNpcId(Integer npcId) {
      this.npcId = npcId;
   }

   public Short getNextQuestId() {
      return nextQuestId;
   }

   public void setNextQuestId(Short nextQuestId) {
      this.nextQuestId = nextQuestId;
   }
}
