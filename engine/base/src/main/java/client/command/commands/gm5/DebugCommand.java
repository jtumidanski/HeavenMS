/*
    This file is part of the HeavenMS MapleStory Server, commands OdinMS-based
    Copyleft (L) 2016 - 2018 RonanLana

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

/*
   @Author: Arthur L - Refactored command content into modules
*/
package client.command.commands.gm5;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import server.TimerManager;
import server.life.MapleMonster;
import server.life.SpawnPoint;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MaplePortal;
import server.maps.MapleReactor;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class DebugCommand extends Command {
   private final static String[] debugTypes = {"monster", "packet", "portal", "spawnpoint", "pos", "map", "mobsp", "event", "areas", "reactors", "servercoupons", "playercoupons", "timer", "marriage", "buff", ""};

   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();

      if (params.length < 1) {
         player.yellowMessage("Syntax: !debug <type>");
         return;
      }

      switch (params[0]) {
         case "type":
         case "help":
            StringBuilder msgTypes = new StringBuilder("Available #bdebug types#k:\r\n\r\n");
            for (int i = 0; i < debugTypes.length; i++) {
               msgTypes.append("#L").append(i).append("#").append(debugTypes[i]).append("#l\r\n");
            }

            c.getAbstractPlayerInteraction().npcTalk(9201143, msgTypes.toString());
            break;

         case "monster":
            List<MapleMapObject> monsters = player.getMap().getMapObjectsInRange(player.position(), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.MONSTER));
            for (MapleMapObject monstermo : monsters) {
               MapleMonster monster = (MapleMonster) monstermo;
               MapleCharacter controller = monster.getController();
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Monster ID: " + monster.id() + " Aggro target: " + ((controller != null) ? controller.getName() + " Has aggro: " + monster.isControllerHasAggro() + " Knowns aggro: " + monster.isControllerKnowsAboutAggro() : "<none>"));
            }
            break;

         case "packet":
            //player.getMap().broadcastMessage(MaplePacketCreator.customPacket(joinStringFrom(params, 1)));
            break;

         case "portal":
            MaplePortal portal = player.getMap().findClosestPortal(player.position());
            if (portal != null) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "Closest portal: " + portal.getId() + " '" + portal.getName() + "' Type: " + portal.getType() + " --> toMap: " + portal.getTargetMapId() + " scriptname: '" + portal.getScriptName() + "' state: " + (portal.getPortalState() ? 1 : 0) + ".");
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "There is no portal on this map.");
            }
            break;

         case "spawnpoint":
            SpawnPoint sp = player.getMap().findClosestSpawnpoint(player.position());
            if (sp != null) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "Closest mob spawn point: " + " Position: x " + sp.getPosition().getX() + " y " + sp.getPosition().getY() + " Spawns mobid: '" + sp.getMonsterId() + "' --> canSpawn: " + !sp.getDenySpawn() + " canSpawnRightNow: " + sp.shouldSpawn() + ".");
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "There is no mob spawn point on this map.");
            }
            break;

         case "pos":
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "Current map position: (" + player.position().getX() + ", " + player.position().getY() + ").");
            break;

         case "map":
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "Current map id " + player.getMap().getId() + ", event: '" + ((player.getMap().getEventInstance() != null) ? player.getMap().getEventInstance().getName() : "null") + "'; Players: " + player.getMap().getAllPlayers().size() + ", Mobs: " + player.getMap().countMonsters() + ", Reactors: " + player.getMap().countReactors() + ", Items: " + player.getMap().countItems() + ", Objects: " + player.getMap().getMapObjects().size() + ".");
            break;

         case "mobsp":
            player.getMap().reportMonsterSpawnPoints(player);
            break;

         case "event":
            if (player.getEventInstance() == null) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "Player currently not in an event.");
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "Current event name: " + player.getEventInstance().getName() + ".");
            }
            break;

         case "areas":
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "Configured areas on map " + player.getMapId() + ":");

            byte index = 0;
            for (Rectangle rect : player.getMap().getAreas()) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "Id: " + index + " -> posX: " + rect.getX() + " posY: '" + rect.getY() + "' dX: " + rect.getWidth() + " dY: " + rect.getHeight() + ".");
               index++;
            }
            break;

         case "reactors":
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "Current reactor states on map " + player.getMapId() + ":");

            for (MapleMapObject mmo : player.getMap().getReactors()) {
               MapleReactor mr = (MapleReactor) mmo;
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "Id: " + mr.getId() + " Oid: " + mr.objectId() + " name: '" + mr.getName() + "' -> Type: " + mr.getReactorType() + " State: " + mr.getState() + " Event State: " + mr.getEventState() + " Position: x " + mr.position().getX() + " y " + mr.position().getY() + ".");
            }
            break;

         case "servercoupons":
         case "coupons":
            StringBuilder s = new StringBuilder("Currently active SERVER coupons: ");
            for (Integer i : Server.getInstance().getActiveCoupons()) {
               s.append(i).append(" ");
            }

            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, s.toString());
            break;

         case "playercoupons":
            StringBuilder st = new StringBuilder("Currently active PLAYER coupons: ");
            for (Integer i : player.getActiveCoupons()) {
               st.append(i).append(" ");
            }

            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, st.toString());
            break;

         case "timer":
            TimerManager tMan = TimerManager.getInstance();
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "Total Task: " + tMan.getTaskCount() + " Current Task: " + tMan.getQueuedTasks() + " Active Task: " + tMan.getActiveCount() + " Completed Task: " + tMan.getCompletedTaskCount());
            break;

         case "marriage":
            c.getChannelServer().debugMarriageStatus();
            break;

         case "buff":
            c.getPlayer().debugListAllBuffs();
            break;
      }
   }
}
