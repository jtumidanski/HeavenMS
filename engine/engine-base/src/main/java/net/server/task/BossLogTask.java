package net.server.task;

import server.expeditions.MapleExpeditionBossLog;

public class BossLogTask implements Runnable {
   @Override
   public void run() {
      MapleExpeditionBossLog.resetBossLogTable();
   }
}
