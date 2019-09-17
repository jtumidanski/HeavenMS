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
import tools.FilePrinter;

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
         FilePrinter.printError(FilePrinter.MAP_SCRIPT + scriptName + ".txt", ute);
      }
      return false;
   }
}