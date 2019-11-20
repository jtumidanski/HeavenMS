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
package scripting.reactor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import client.MapleClient;
import client.database.provider.ReactorDropProvider;
import scripting.AbstractScriptManager;
import server.maps.MapleReactor;
import server.maps.ReactorDropEntry;
import tools.DatabaseConnection;
import tools.FilePrinter;

/**
 * @author Lerk
 */
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

   public void untouch(MapleClient c, MapleReactor reactor) {
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
            ((Invocable) iv).invokeFunction("untouch");
         }
      } catch (final ScriptException | NoSuchMethodException | NullPointerException ute) {
         FilePrinter.printError(FilePrinter.REACTOR + reactor.getId() + ".txt", ute);
      }
   }
}