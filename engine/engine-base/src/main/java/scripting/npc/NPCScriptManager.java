package scripting.npc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import client.MapleCharacter;
import client.MapleClient;
import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import net.server.world.MaplePartyCharacter;
import scripting.AbstractScriptManager;
import server.ScriptedItem;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.SimpleMessage;
import tools.packet.stat.EnableActions;

public class NPCScriptManager extends AbstractScriptManager {

   private static NPCScriptManager instance = new NPCScriptManager();
   private Map<MapleClient, NPCConversationManager> cms = new HashMap<>();
   private Map<MapleClient, ScriptEngine> scripts = new HashMap<>();

   public static NPCScriptManager getInstance() {
      return instance;
   }

   public boolean isNpcScriptAvailable(MapleClient c, String fileName) {
      ScriptEngine iv = null;
      if (fileName != null) {
         iv = getScriptEngine("npc/" + fileName, c);
      }

      return iv != null;
   }

   public boolean start(MapleClient c, int npc, MapleCharacter chr) {
      return start(c, npc, -1, chr);
   }

   public boolean start(MapleClient c, int npc, int oid, MapleCharacter chr) {
      return start(c, npc, oid, null, chr);
   }

   public boolean start(MapleClient c, int npc, String fileName, MapleCharacter chr) {
      return start(c, npc, -1, fileName, chr);
   }

   public boolean start(MapleClient c, int npc, int oid, String fileName, MapleCharacter chr) {
      return start(c, npc, oid, fileName, chr, false, "cm");
   }

   public boolean start(MapleClient c, ScriptedItem scriptItem, MapleCharacter chr) {
      return start(c, scriptItem.npc(), -1, scriptItem.script(), chr, true, "im");
   }

   public void start(String filename, MapleClient c, int npc, List<MaplePartyCharacter> partyCharacters) {
      try {
         NPCConversationManager cm = new NPCConversationManager(c, npc, partyCharacters, true);
         cm.dispose();
         if (cms.containsKey(c)) {
            return;
         }
         cms.put(c, cm);
         ScriptEngine iv = getScriptEngine("npc/" + filename, c);

         if (iv == null) {
            MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, SimpleMessage.from(npc + ""));
            cm.dispose();
            return;
         }
         iv.put("cm", cm);
         scripts.put(c, iv);
         try {
            ((Invocable) iv).invokeFunction("start", partyCharacters);
         } catch (final NoSuchMethodException e) {
            try {
               ((Invocable) iv).invokeFunction("start", partyCharacters);
            } catch (final NoSuchMethodException e1) {
               e1.printStackTrace();
            }
         }

      } catch (final Exception ute) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.NPC, ute, String.format("NPC [%d]", npc));
         dispose(c);
      }
   }

   private boolean start(MapleClient c, int npc, int oid, String fileName, MapleCharacter chr, boolean itemScript, String engineName) {
      try {
         NPCConversationManager cm = new NPCConversationManager(c, npc, oid, fileName, itemScript);
         if (cms.containsKey(c)) {
            dispose(c);
         }
         if (c.canClickNPC()) {
            cms.put(c, cm);
            ScriptEngine iv = null;
            if (!itemScript) {
               if (fileName != null) {
                  iv = getScriptEngine("npc/" + fileName, c);
               }
            } else {
               if (fileName != null) {
                  iv = getScriptEngine("item/" + fileName, c);
               }
            }
            if (iv == null) {
               iv = getScriptEngine("npc/" + npc, c);
               cm.resetItemScript();
            }
            if (iv == null) {
               dispose(c);
               return false;
            }
            iv.put(engineName, cm);
            scripts.put(c, iv);
            c.setClickedNPC();
            try {
               ((Invocable) iv).invokeFunction("start");
            } catch (final NoSuchMethodException e) {
               e.printStackTrace();
               try {
                  ((Invocable) iv).invokeFunction("start", chr);
               } catch (final NoSuchMethodException e1) {
                  e1.printStackTrace();
               }
            }
         } else {
            PacketCreator.announce(c, new EnableActions());
         }
         return true;
      } catch (final Exception ute) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.NPC, ute, String.format("NPC [%d]", npc));
         dispose(c);

         return false;
      }
   }

   public void action(MapleClient c, byte mode, byte type, int selection) {
      ScriptEngine iv = scripts.get(c);
      if (iv != null) {
         try {
            c.setClickedNPC();
            ((Invocable) iv).invokeFunction("action", mode, type, selection);
         } catch (ScriptException | NoSuchMethodException t) {
            if (getCM(c) != null) {
               LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.NPC, t, String.format("NPC [%d]", getCM(c).getNpc()));
            }
            dispose(c);
         }
      }
   }

   public void dispose(NPCConversationManager cm) {
      MapleClient c = cm.getClient();
      c.getPlayer().setCS(false);
      c.getPlayer().setNpcCoolDown(System.currentTimeMillis());
      cms.remove(c);
      scripts.remove(c);

      String scriptFolder = (cm.isItemScript() ? "item" : "npc");
      if (cm.getScriptName() != null) {
         resetContext(scriptFolder + "/" + cm.getScriptName(), c);
      } else {
         resetContext(scriptFolder + "/" + cm.getNpc(), c);
      }

      c.getPlayer().flushDelayedUpdateQuests();
   }

   public void dispose(MapleClient c) {
      NPCConversationManager cm = cms.get(c);
      if (cm != null) {
         dispose(cm);
      }
   }

   public NPCConversationManager getCM(MapleClient c) {
      return cms.get(c);
   }

}
