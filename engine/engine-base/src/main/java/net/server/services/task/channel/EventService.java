package net.server.services.task.channel;

import config.YamlConfig;
import net.server.audit.locks.MonitoredLockType;
import net.server.services.BaseScheduler;
import net.server.services.BaseService;

/**
 * @author Ronan
 */
public class EventService extends BaseService {

   private EventScheduler[] eventSchedulers = new EventScheduler[YamlConfig.config.server.CHANNEL_LOCKS];

   public EventService() {
      for (int i = 0; i < YamlConfig.config.server.CHANNEL_LOCKS; i++) {
         eventSchedulers[i] = new EventScheduler();
      }
   }

   public void dispose() {
      for (int i = 0; i < YamlConfig.config.server.CHANNEL_LOCKS; i++) {
         if (eventSchedulers[i] != null) {
            eventSchedulers[i].dispose();
            eventSchedulers[i] = null;
         }
      }
   }

   public void registerEventAction(int mapid, Runnable runAction, long delay) {
      eventSchedulers[getChannelSchedulerIndex(mapid)].registerDelayedAction(runAction, delay);
   }

   private class EventScheduler extends BaseScheduler {

      public EventScheduler() {
         super(MonitoredLockType.CHANNEL_EVENTS);
      }

      public void registerDelayedAction(Runnable runAction, long delay) {
         registerEntry(runAction, runAction, delay);
      }

   }

}
