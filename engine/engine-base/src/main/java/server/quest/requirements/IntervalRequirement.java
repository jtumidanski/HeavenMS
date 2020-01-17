package server.quest.requirements;

import client.MapleCharacter;
import client.MapleQuestStatus;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuest;
import server.quest.MapleQuestRequirementType;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class IntervalRequirement extends MapleQuestRequirement {
   private int interval = -1;
   private int questID;

   public IntervalRequirement(MapleQuest quest, MapleData data) {
      super(MapleQuestRequirementType.INTERVAL);
      questID = quest.getId();
      processData(data);
   }

   public int getInterval() {
      return interval;
   }

   @Override
   public void processData(MapleData data) {
      interval = MapleDataTool.getInt(data) * 60 * 1000;
   }


   private static String getIntervalTimeLeft(MapleCharacter chr, IntervalRequirement r) {
      StringBuilder str = new StringBuilder();

      long futureTime = chr.getQuest(MapleQuest.getInstance(r.questID)).getCompletionTime() + r.getInterval();
      long leftTime = futureTime - System.currentTimeMillis();

      byte mode = 0;
      if (leftTime / (60 * 1000) > 0) {
         mode++;     //counts minutes

         if (leftTime / (60 * 60 * 1000) > 0) {
            mode++;     //counts hours
         }
      }

      switch (mode) {
         case 2:
            int hours = (int) ((leftTime / (1000 * 60 * 60)));
            str.append(hours).append(" hours, ");

         case 1:
            int minutes = (int) ((leftTime / (1000 * 60)) % 60);
            str.append(minutes).append(" minutes, ");

         default:
            int seconds = (int) (leftTime / 1000) % 60;
            str.append(seconds).append(" seconds");
      }

      return str.toString();
   }

   @Override
   public boolean check(MapleCharacter chr, Integer npcId) {
      boolean check = !chr.getQuest(MapleQuest.getInstance(questID)).getStatus().equals(MapleQuestStatus.Status.COMPLETED);
      boolean check2 = chr.getQuest(MapleQuest.getInstance(questID)).getCompletionTime() <= System.currentTimeMillis() - interval;
      if (check || check2) {
         return true;
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("QUEST_INTERVAL_STATUS").with(getIntervalTimeLeft(chr, this)));
         return false;
      }
   }
}
