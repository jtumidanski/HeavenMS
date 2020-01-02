package scripting.event;

import scripting.event.scheduler.EventScriptScheduler;

public class EventScheduledFuture {
   Runnable r;
   EventScriptScheduler ess;

   public EventScheduledFuture(Runnable r, EventScriptScheduler ess) {
      this.r = r;
      this.ess = ess;
   }

   public void cancel(boolean dummy) {   // will always implement "non-interrupt if running" regardless of boolean value
      ess.cancelEntry(r);
   }
}
