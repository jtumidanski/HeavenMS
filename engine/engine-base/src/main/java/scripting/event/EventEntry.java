package scripting.event;

import javax.script.Invocable;

public class EventEntry {
   private Invocable invocable;

   private EventManager eventManager;

   public EventEntry(Invocable iv, EventManager em) {
      this.invocable = iv;
      this.eventManager = em;
   }

   public Invocable getInvocable() {
      return invocable;
   }

   public EventManager getEventManager() {
      return eventManager;
   }
}
