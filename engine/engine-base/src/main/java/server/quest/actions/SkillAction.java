package server.quest.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.MapleCharacter;
import client.SkillFactory;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuestActionType;

public class SkillAction extends MapleQuestAction {
   private Map<Integer, SkillData> skillData = new HashMap<>();

   public SkillAction(int questId, MapleData data) {
      super(questId, MapleQuestActionType.SKILL);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      for (MapleData sEntry : data) {
         byte skillLevel = 0;
         int skillId = MapleDataTool.getInt(sEntry.getChildByPath("id"));
         MapleData skillLevelData = sEntry.getChildByPath("skillLevel");
         if (skillLevelData != null) {
            skillLevel = (byte) MapleDataTool.getInt(skillLevelData);
         }
         int masterLevel = MapleDataTool.getInt(sEntry.getChildByPath("masterLevel"));
         List<Integer> jobs = new ArrayList<>();

         MapleData applicableJobs = sEntry.getChildByPath("job");
         if (applicableJobs != null) {
            for (MapleData applicableJob : applicableJobs.getChildren()) {
               jobs.add(MapleDataTool.getInt(applicableJob));
            }
         }

         skillData.put(skillId, new SkillData(skillId, skillLevel, masterLevel, jobs));
      }
   }

   @Override
   public void run(MapleCharacter chr, Integer extSelection) {
      for (SkillData skill : skillData.values()) {
         SkillFactory.getSkill(skill.id()).ifPresent(skill1 -> {
            boolean shouldLearn = false;

            if (skill.jobsContains(chr.getJob()) || skill1.isBeginnerSkill()) {
               shouldLearn = true;
            }

            byte skillLevel = (byte) Math.max(skill.level(), chr.getSkillLevel(skill1));
            int masterLevel = Math.max(skill.masterLevel(), chr.getMasterLevel(skill1));
            if (shouldLearn) {
               chr.changeSkillLevel(skill1, skillLevel, masterLevel, -1);
            }
         });
      }
   }
} 