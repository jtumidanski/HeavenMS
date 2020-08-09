package net.server.coordinator.partysearch;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import client.MapleJob;

public class LeaderSearchMetadata {
   private final Integer minLevel;

   private final Integer maxLevel;

   private final Integer jobs;

   private final Map<Integer, MapleJob> jobTable;

   private final List<MapleJob> searchedJobs;

   private Integer reentryCount = 0;

   public LeaderSearchMetadata(Integer minLevel, Integer maxLevel, Integer jobs, Map<Integer, MapleJob> jobTable) {
      this.minLevel = minLevel;
      this.maxLevel = maxLevel;
      this.jobs = jobs;
      this.jobTable = jobTable;
      this.searchedJobs = decodeSearchedJobs(jobs);
   }

   public Integer minLevel() {
      return minLevel;
   }

   public Integer maxLevel() {
      return maxLevel;
   }

   public Integer jobs() {
      return jobs;
   }

   public Map<Integer, MapleJob> jobTable() {
      return jobTable;
   }

   public Integer reentryCount() {
      return reentryCount;
   }

   public void incrementReentryCount() {
      reentryCount += 1;
   }

   public List<MapleJob> searchedJobs() {
      return Collections.unmodifiableList(searchedJobs);
   }

   public List<MapleJob> decodeSearchedJobs(Integer jobsSelected) {
      List<MapleJob> searchedJobs = new LinkedList<>();
      int topByte = (int) ((Math.log(jobsSelected) / Math.log(2)) + 1e-5);
      for (int i = 0; i <= topByte; i++) {
         if (jobsSelected % 2 == 1) {
            MapleJob job = jobTable.get(i);
            if (job != null) {
               searchedJobs.add(job);
            }
         }

         jobsSelected = jobsSelected >> 1;
         if (jobsSelected == 0) {
            break;
         }
      }
      return searchedJobs;
   }
}
