package scripting.item;

import client.MapleClient;
import scripting.npc.NPCScriptManager;
import server.ScriptedItem;

public class ItemScriptManager {

   private static ItemScriptManager instance = new ItemScriptManager();

   public static ItemScriptManager getInstance() {
      return instance;
   }

   public void runItemScript(MapleClient c, ScriptedItem scriptItem) {
      NPCScriptManager.getInstance().start(c, scriptItem, null);
   }
}