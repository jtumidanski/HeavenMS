package scripting.event;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Invocable;
import javax.script.ScriptEngine;

import net.server.channel.Channel;
import scripting.AbstractScriptManager;
import tools.LoggerOriginator;
import tools.LoggerUtil;

public class EventScriptManager extends AbstractScriptManager {

   private static EventEntry fallback;
   private Map<String, EventEntry> events = new ConcurrentHashMap<>();
   private boolean active = false;

   public EventScriptManager(Channel channel, String[] scripts) {
      super();
      for (String script : scripts) {
         if (!script.equals("")) {
            ScriptEngine iv = getScriptEngine("event/" + script);
            events.put(script, new EventEntry((Invocable) iv, new EventManager(channel, (Invocable) iv, script)));
         }
      }
      init();
      fallback = events.remove("0_EXAMPLE");
   }

   public EventManager getEventManager(String event) {
      EventEntry entry = events.get(event);
      if (entry == null) {
         return fallback.getEventManager();
      }
      return entry.getEventManager();
   }

   public boolean isActive() {
      return active;
   }

   public final void init() {
      for (EventEntry entry : events.values()) {
         try {
            ((ScriptEngine) entry.getInvocable()).put("em", entry.getEventManager());
            entry.getInvocable().invokeFunction("init");
         } catch (Exception ex) {
            Logger.getLogger(EventScriptManager.class.getName()).log(Level.SEVERE, null, ex);
            LoggerUtil.printError(LoggerOriginator.EXCEPTION, "Error on script: " + entry.getEventManager().getName());
         }
      }
      active = events.size() > 1; // boot up loads only 1 script
   }

   private void reloadScripts() {
      Set<Entry<String, EventEntry>> eventEntries = new HashSet<>(events.entrySet());
      if (eventEntries.isEmpty()) {
         return;
      }

      Channel channel = eventEntries.iterator().next().getValue().getEventManager().getChannelServer();
      for (Entry<String, EventEntry> entry : eventEntries) {
         String script = entry.getKey();
         ScriptEngine iv = getScriptEngine("event/" + script);
         events.put(script, new EventEntry((Invocable) iv, new EventManager(channel, (Invocable) iv, script)));
      }
   }

   public void reload() {
      cancel();
      reloadScripts();
      init();
   }

   public void cancel() {
      active = false;
      for (EventEntry entry : events.values()) {
         entry.getEventManager().cancel();
      }
   }

   public void dispose() {
      if (events.isEmpty()) {
         return;
      }

      Set<EventEntry> eventEntries = new HashSet<>(events.values());
      events.clear();

      active = false;
      for (EventEntry entry : eventEntries) {
         entry.getEventManager().cancel();
      }
   }
}
