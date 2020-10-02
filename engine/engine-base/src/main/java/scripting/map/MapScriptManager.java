package scripting.map;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import client.MapleCharacter;
import client.MapleClient;
import scripting.AbstractScriptManager;
import tools.LoggerOriginator;
import tools.LoggerUtil;

public class MapScriptManager extends AbstractScriptManager {
   private static MapScriptManager instance = new MapScriptManager();
   private Map<String, ScriptEngine> scripts = new HashMap<>();

   private MapScriptManager() {
   }

   public static MapScriptManager getInstance() {
      return instance;
   }

   public void reloadScripts() {
      scripts.clear();
   }

   public boolean scriptExists(String scriptName, boolean firstUser) {
      File scriptFile = new File("script/src/main/groovy/map/" + (firstUser ? "onFirstUserEnter/" : "onUserEnter/") + scriptName + ".js");
      return scriptFile.exists();
   }

   public boolean runMapScript(MapleClient c, String scriptName, boolean firstUser) {
      if (firstUser) {
         MapleCharacter character = c.getPlayer();
         int mapId = character.getMapId();
         if (character.hasEntered(scriptName, mapId)) {
            return false;
         } else {
            character.enteredScript(scriptName, mapId);
         }
      }

      ScriptEngine iv = scripts.get(scriptName);
      if (iv != null) {
         try {
            ((Invocable) iv).invokeFunction("start", new MapScriptMethods(c));
            return true;
         } catch (final ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
         }
      }

      try {
         iv = getScriptEngine("script/src/main/groovy/map/" + scriptName + ".groovy");
         if (iv == null) {
            return false;
         }
         scripts.put(scriptName, iv);
         ((Invocable) iv).invokeFunction("start", new MapScriptMethods(c));
         return true;
      } catch (final Exception ute) {
         LoggerUtil.printError(LoggerOriginator.MAP_SCRIPT, ute, String.format("Script [%s]", scriptName));
      }
      return false;
   }
}