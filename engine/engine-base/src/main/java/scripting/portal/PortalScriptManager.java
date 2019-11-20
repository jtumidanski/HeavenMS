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