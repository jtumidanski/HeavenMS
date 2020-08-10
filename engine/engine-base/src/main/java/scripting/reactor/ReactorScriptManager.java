package scripting.reactor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import client.MapleClient;
import database.DatabaseConnection;
import database.provider.ReactorDropProvider;
import scripting.AbstractScriptManager;
import server.maps.MapleReactor;
import server.maps.ReactorDropEntry;
import tools.FilePrinter;

public class ReactorScriptManager extends AbstractScriptManager {

   private static ReactorScriptManager instance = new ReactorScriptManager();
   private Map<Integer, List<ReactorDropEntry>> drops = new HashMap<>();

   public static ReactorScriptManager getInstance() {
      return instance;
   }

   public void onHit(MapleClient c, MapleReactor reactor) {
      try {
         ScriptEngine iv = getScriptEngine("reactor/" + reactor.getId(), c);
         if (iv == null) {
            return;
         }

         ReactorActionManager rm = new ReactorActionManager(c, reactor, iv);
         iv.put("rm", rm);
         ((Invocable) iv).invokeFunction("hit");
      } catch (final NoSuchMethodException ignored) {
      } //do nothing, hit is OPTIONAL

      catch (final ScriptException | NullPointerException e) {
         FilePrinter.printError(FilePrinter.REACTOR + reactor.getId() + ".txt", e);
      }
   }

   public void act(MapleClient c, MapleReactor reactor) {
      try {
         ScriptEngine iv = getScriptEngine("reactor/" + reactor.getId(), c);
         if (iv == null) {
            return;
         }

         ReactorActionManager rm = new ReactorActionManager(c, reactor, iv);
         iv.put("rm", rm);
         ((Invocable) iv).invokeFunction("act");
      } catch (final ScriptException | NoSuchMethodException | NullPointerException e) {
         FilePrinter.printError(FilePrinter.REACTOR + reactor.getId() + ".txt", e);
      }
   }

   public List<ReactorDropEntry> getDrops(int rid) {
      List<ReactorDropEntry> ret = drops.get(rid);
      if (ret == null) {
         ret = DatabaseConnection.getInstance().withConnectionResult(connection -> ReactorDropProvider.getInstance().getDropsForReactor(connection, rid)).orElseThrow();
         drops.put(rid, ret);
      }
      return ret;
   }

   public void clearDrops() {
      drops.clear();
   }

   public void touch(MapleClient c, MapleReactor reactor) {
      touching(c, reactor, true);
   }

   public void release(MapleClient c, MapleReactor reactor) {
      touching(c, reactor, false);
   }

   private void touching(MapleClient c, MapleReactor reactor, boolean touching) {
      try {
         ScriptEngine iv = getScriptEngine("reactor/" + reactor.getId(), c);
         if (iv == null) {
            return;
         }

         ReactorActionManager rm = new ReactorActionManager(c, reactor, iv);
         iv.put("rm", rm);
         if (touching) {
            ((Invocable) iv).invokeFunction("touch");
         } else {
            ((Invocable) iv).invokeFunction("release");
         }
      } catch (final ScriptException | NoSuchMethodException | NullPointerException ute) {
         FilePrinter.printError(FilePrinter.REACTOR + reactor.getId() + ".txt", ute);
      }
   }
}