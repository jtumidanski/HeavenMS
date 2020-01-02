package scripting.portal;

import java.util.HashMap;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;

import client.MapleClient;
import scripting.AbstractScriptManager;
import server.maps.MaplePortal;
import tools.FilePrinter;

public class PortalScriptManager extends AbstractScriptManager {

   private static PortalScriptManager instance = new PortalScriptManager();
   private Map<String, ScriptEngine> scripts = new HashMap<>();

   private PortalScriptManager() {
   }

   public static PortalScriptManager getInstance() {
      return instance;
   }

   private ScriptEngine getPortalScript(String scriptName) {
      String scriptPath = "script/src/main/groovy/portal/" + scriptName + ".groovy";
      ScriptEngine iv = scripts.get(scriptPath);
      if (iv != null) {
         return iv;
      }

      iv = getScriptEngine(scriptPath);
      if (iv == null) {
         return null;
      }
      scripts.put(scriptPath, iv);
      return iv;
   }

   public boolean executePortalScript(MaplePortal portal, MapleClient c) {
      try {
         ScriptEngine iv = getPortalScript(portal.getScriptName());
         if (iv != null) {
            return (boolean) ((Invocable) iv).invokeFunction("enter", new PortalPlayerInteraction(c, portal));
         }
      } catch (Exception ute) {
         FilePrinter.printError(FilePrinter.PORTAL + portal.getScriptName() + ".txt", ute);
      }
      return false;
   }

   public void reloadPortalScripts() {
      scripts.clear();
   }
}