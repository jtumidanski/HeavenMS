package net.server.task;

import client.processor.npc.DueyProcessor;
import client.processor.npc.FredrickProcessor;

public class DueyFredrickTask implements Runnable {
   @Override
   public void run() {
      FredrickProcessor.runFredrickSchedule();
      DueyProcessor.runDueyExpireSchedule();
   }
}
