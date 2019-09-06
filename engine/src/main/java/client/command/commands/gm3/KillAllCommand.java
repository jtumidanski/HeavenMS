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
package client.command.commands.gm3;

import java.util.Collections;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MapleMonster;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class KillAllCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      MapleMap map = player.getMap();
      List<MapleMapObject> monsters = map.getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.MONSTER));
      int count = 0;
      for (MapleMapObject monstermo : monsters) {
         MapleMonster monster = (MapleMonster) monstermo;
         if (!monster.getStats().isFriendly() && !(monster.getId() >= 8810010 && monster.getId() <= 8810018)) {
            map.damageMonster(player, monster, Integer.MAX_VALUE);
            count++;
         }
      }
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Killed " + count + " monsters.");
   }
}