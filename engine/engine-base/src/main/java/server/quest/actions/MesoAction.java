package server.quest.actions;

import client.MapleCharacter;
import config.YamlConfig;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuest;
import server.quest.MapleQuestActionType;

public class MesoAction extends MapleQuestAction {
   int mesos;

   public MesoAction(MapleQuest quest, MapleData data) {
      super(MapleQuestActionType.MESO, quest);
      questID = quest.getId();
      processData(data);
   }

   public static void runAction(MapleCharacter chr, int gain) {
      if (gain < 0) {
         chr.gainMeso(gain, true, false, true);
      } else {
         if (!YamlConfig.config.server.USE_QUEST_RATE) {
            chr.gainMeso(gain * chr.getMesoRate(), true, false, true);
         } else {
            chr.gainMeso(gain * chr.getQuestMesoRate(), true, false, true);
         }
      }
   }

   @Override
   public void processData(MapleData data) {
      mesos = MapleDataTool.getInt(data);
   }

   @Override
   public void run(MapleCharacter chr, Integer extSelection) {
      runAction(chr, mesos);
   }
} 
