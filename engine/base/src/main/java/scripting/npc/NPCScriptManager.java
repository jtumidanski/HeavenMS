/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation version 3 as published by
 the Free Software Foundation. You may not use, modify or distribute
 this program under any other version of the GNU Affero General Public
 License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package scripting.npc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import client.MapleCharacter;
import client.MapleClient;
import net.server.world.MaplePartyCharacter;
import scripting.AbstractScriptManager;
import server.MapleItemInformationProvider.ScriptedItem;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

/**
 * @author Matze
 */
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
      return start(c, scriptItem.getNpc(), -1, scriptItem.getScript(), chr, true, "im");
   }

   public void start(String filename, MapleClient c, int npc, List<MaplePartyCharacter> chrs) {
      try {
         NPCConversationManager cm = new NPCConversationManager(c, npc, chrs, true);
         cm.dispose();
         if (cms.containsKey(c)) {
            return;
         }
         cms.put(c, cm);
         ScriptEngine iv = null;
         iv = getScriptEngine("npc/" + filename, c);

         if (iv == null) {
            MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, npc + "");
            cm.dispose();
            return;
         }
         iv.put("cm", cm);
         scripts.put(c, iv);
         try {
            ((Invocable) iv).invokeFunction("start", chrs);
         } catch (final NoSuchMethodException nsme) {
            try {
               ((Invocable) iv).invokeFunction("start", chrs);
            } catch (final NoSuchMethodException nsma) {
               nsma.printStackTrace();
            }
         }

      } catch (final Exception ute) {
         FilePrinter.printError(FilePrinter.NPC + npc + ".txt", ute);
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
               if (fileName != null) {     // thanks MiLin for drafting NPC-based item scripts
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
            } catch (final NoSuchMethodException nsme) {
               nsme.printStackTrace();
               try {
                  ((Invocable) iv).invokeFunction("start", chr);
               } catch (final NoSuchMethodException nsma) {
                  nsma.printStackTrace();
               }
            }
         } else {
            c.announce(MaplePacketCreator.enableActions());
         }
         return true;
      } catch (final Exception ute) {
         FilePrinter.printError(FilePrinter.NPC + npc + ".txt", ute);
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
               FilePrinter.printError(FilePrinter.NPC + getCM(c).getNpc() + ".txt", t);
            }
            dispose(c);
         }
      }
   }

   public void dispose(NPCConversationManager cm) {
      MapleClient c = cm.getClient();
      c.getPlayer().setCS(false);
      c.getPlayer().setNpcCooldown(System.currentTimeMillis());
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
