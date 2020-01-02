package server.quest.requirements;

import client.MapleCharacter;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuest;
import server.quest.MapleQuestRequirementType;

public class MonsterBookCountRequirement extends MapleQuestRequirement {
   private int reqCards;


   public MonsterBookCountRequirement(MapleQuest quest, MapleData data) {
      super(MapleQuestRequirementType.MONSTER_BOOK);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      reqCards = MapleDataTool.getInt(data);
   }


   @Override
   public boolean check(MapleCharacter chr, Integer npcId) {
      return chr.getMonsterBook().getTotalCards() >= reqCards;
   }
}
