package client.processor;

import constants.MapleJob;

public class MapleJobProcessor {
   private static MapleJobProcessor ourInstance = new MapleJobProcessor();

   public static MapleJobProcessor getInstance() {
      return ourInstance;
   }

   private MapleJobProcessor() {
   }

   public MapleJob getJobStyleInternal(int jobId, byte opt) {
      int jobType = jobId / 100;

      if (jobType == MapleJob.WARRIOR.getId() / 100 || jobType == MapleJob.DAWN_WARRIOR_1.getId() / 100
            || jobType == MapleJob.ARAN1.getId() / 100) {
         return (MapleJob.WARRIOR);
      } else if (jobType == MapleJob.MAGICIAN.getId() / 100 || jobType == MapleJob.BLAZE_WIZARD_1.getId() / 100
            || jobType == MapleJob.EVAN1.getId() / 100) {
         return (MapleJob.MAGICIAN);
      } else if (jobType == MapleJob.BOWMAN.getId() / 100 || jobType == MapleJob.WIND_ARCHER_1.getId() / 100) {
         if (jobId / 10 == MapleJob.CROSSBOWMAN.getId() / 10) {
            return (MapleJob.CROSSBOWMAN);
         } else {
            return (MapleJob.BOWMAN);
         }
      } else if (jobType == MapleJob.THIEF.getId() / 100 || jobType == MapleJob.NIGHT_WALKER_1.getId() / 100) {
         return (MapleJob.THIEF);
      } else if (jobType == MapleJob.PIRATE.getId() / 100 || jobType == MapleJob.THUNDER_BREAKER_1.getId() / 100) {
         if (opt == (byte) 0x80) {
            return (MapleJob.BRAWLER);
         } else {
            return (MapleJob.GUNSLINGER);
         }
      }

      return (MapleJob.BEGINNER);
   }
}
