package server.quest.actions;

import client.MapleCharacter;
import config.YamlConfig;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuest;
import server.quest.MapleQuestActionType;

public class ExpAction extends MapleQuestAction {
   int exp;

   public ExpAction(MapleQuest quest, MapleData data) {
      super(MapleQuestActionType.EXP, quest);
      processData(data);
   }

   public static void runAction(MapleCharacter chr, int gain) {
      if (!YamlConfig.config.server.USE_QUEST_RATE) {
         chr.gainExp(gain * chr.getExpRate(), true, true);
      } else {
         chr.gainExp(gain * chr.getQuestExpRate(), true, true);
      }
   }

   @Override
   public void processData(MapleData data) {
      exp = MapleDataTool.getInt(data);
   }

   @Override
   public void run(MapleCharacter chr, Integer extSelection) {
      runAction(chr, exp);
   }
} 
