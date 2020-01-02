package server.quest.requirements;

import java.util.Calendar;

import client.MapleCharacter;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuest;
import server.quest.MapleQuestRequirementType;

public class EndDateRequirement extends MapleQuestRequirement {
   private String timeStr;

   public EndDateRequirement(MapleQuest quest, MapleData data) {
      super(MapleQuestRequirementType.END_DATE);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      timeStr = MapleDataTool.getString(data);
   }

   @Override
   public boolean check(MapleCharacter chr, Integer npcId) {
      Calendar cal = Calendar.getInstance();
      cal.set(Integer.parseInt(timeStr.substring(0, 4)), Integer.parseInt(timeStr.substring(4, 6)), Integer.parseInt(timeStr.substring(6, 8)), Integer.parseInt(timeStr.substring(8, 10)), 0);
      return cal.getTimeInMillis() >= System.currentTimeMillis();
   }
}
