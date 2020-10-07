package server.quest.requirements;

import client.MapleCharacter;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuestRequirementType;
import tools.I18nMessage;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class MesoRequirement extends MapleQuestRequirement {
   private int meso = 0;

   public MesoRequirement(int questId, MapleData data) {
      super(questId, MapleQuestRequirementType.MESO);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      meso = MapleDataTool.getInt(data);
   }

   @Override
   public boolean check(MapleCharacter chr, Integer npcId) {
      if (chr.getMeso() >= meso) {
         return true;
      } else {
         MessageBroadcaster.getInstance()
               .sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("QUEST_COMPLETE_FAIL_NOT_ENOUGH_MESO"));
         return false;
      }
   }
}
