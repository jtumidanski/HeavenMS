package server.quest.requirements;

import java.util.ArrayList;
import java.util.List;

import client.MapleCharacter;
import client.MapleJob;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuest;
import server.quest.MapleQuestRequirementType;

public class JobRequirement extends MapleQuestRequirement {
   List<Integer> jobs = new ArrayList<>();

   public JobRequirement(MapleQuest quest, MapleData data) {
      super(MapleQuestRequirementType.JOB);
      processData(data);
   }

   @Override
   public void processData(MapleData data) {
      for (MapleData jobEntry : data.getChildren()) {
         jobs.add(MapleDataTool.getInt(jobEntry));
      }
   }

   @Override
   public boolean check(MapleCharacter chr, Integer npcId) {
      for (Integer job : jobs) {
         if (chr.getJob().equals(MapleJob.getById(job)) || chr.isGM()) {
            return true;
         }
      }
      return false;
   }
}
