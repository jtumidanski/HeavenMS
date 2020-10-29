package scripting.quest;

import builder.InputObjectBuilder;
import builder.ResultObjectBuilder;
import client.MapleClient;
import rest.CharacterAttributes;
import rest.RestService;
import rest.UriBuilder;
import rest.builders.CharacterAttributesBuilder;
import scripting.npc.NPCConversationManager;
import server.MapleItemInformationProvider;
import server.processor.QuestProcessor;

public class QuestActionManager extends NPCConversationManager {
   private final boolean start; // this is if the script in question is start or end
   private final int questId;

   public QuestActionManager(MapleClient c, int questId, int npc, boolean start) {
      super(c, npc, null);
      this.questId = questId;
      this.start = start;
   }

   public int getQuestId() {
      return questId;
   }

   public boolean isStart() {
      return start;
   }

   @Override
   public void dispose() {
      QuestScriptManager.getInstance().dispose(this, getClient());
   }

   public boolean forceStartQuest() {
      return forceStartQuest(questId);
   }

   public boolean forceCompleteQuest() {
      return forceCompleteQuest(questId);
   }

   // For compatibility with some older scripts...
   public void startQuest() {
      forceStartQuest();
   }

   // For compatibility with some older scripts...
   public void completeQuest() {
      forceCompleteQuest();
   }

   @Override
   public void gainExp(int gain) {
      UriBuilder.service(RestService.MASTER).path("characters").path(getPlayer().getId())
            .getRestClient()
            .update(new InputObjectBuilder().setData(new ResultObjectBuilder(CharacterAttributes.class, getPlayer().getId())
                  .setAttribute(new CharacterAttributesBuilder().setExperience(gain))
                  .build()));
   }

   @Override
   public void gainMeso(int gain) {
      UriBuilder.service(RestService.MASTER).path("characters").path(getPlayer().getId())
            .getRestClient()
            .update(new InputObjectBuilder().setData(new ResultObjectBuilder(CharacterAttributes.class, getPlayer().getId())
                  .setAttribute(new CharacterAttributesBuilder().setMeso(gain))
                  .build()));
   }

   public String getMedalName() {  // usable only for medal quests (id 299XX)
      int medalId = QuestProcessor.getInstance().getQuestMedalId(questId);
      return MapleItemInformationProvider.getInstance().getName(medalId);
   }
}
