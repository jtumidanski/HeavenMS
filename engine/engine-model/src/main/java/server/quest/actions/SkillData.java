package server.quest.actions;

import java.util.List;

import client.MapleJob;

public record SkillData(int id, int level, int masterLevel, List<Integer> jobs) {
   public Boolean jobsContains(MapleJob job) {
      return jobs.contains(job.getId());
   }
}
