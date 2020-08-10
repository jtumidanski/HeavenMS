package server.quest.requirements;

import java.util.ArrayList;
import java.util.List;

import client.MapleCharacter;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuest;
import server.quest.MapleQuestRequirementType;

public class InfoExRequirement extends MapleQuestRequirement {
   private List<String> infoExpected = new ArrayList<>();
   private int questId;


   public InfoExRequirement(MapleQuest quest, MapleData data) {
      super(MapleQuestRequirementType.INFO_EX);
      questId = quest.getId();
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      // Because we have to...
      for (MapleData infoEx : data.getChildren()) {
         MapleData value = infoEx.getChildByPath("value");
         infoExpected.add(MapleDataTool.getString(value, ""));
      }
   }


   @Override
   public boolean check(MapleCharacter chr, Integer npcId) {
      return true;
   }

   public List<String> getInfo() {
      return infoExpected;
   }
}