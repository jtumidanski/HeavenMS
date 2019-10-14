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
   @Author: Ronan
*/
package client.command.commands.gm4;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import client.database.provider.PlayerLifeProvider;
import tools.DatabaseConnection;
import tools.Pair;

public class PmobRemoveCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();

      int mapId = player.getMapId();
      int mobId = params.length > 0 ? Integer.parseInt(params[0]) : -1;

      Point pos = player.position();
      int xpos = pos.x;
      int ypos = pos.y;

      List<Pair<Integer, Pair<Integer, Integer>>> toRemove = DatabaseConnection.getInstance().withConnectionResult(connection -> {
         if (mobId > -1) {
            return PlayerLifeProvider.getInstance().get(connection, player.getWorld(), mapId, "n", mobId);
         } else {
            return PlayerLifeProvider.getInstance().get(connection, player.getWorld(), mapId, "n", xpos - 50, xpos + 50, ypos - 50, ypos + 50);
         }
      }).orElse(new ArrayList<>());

      if (!toRemove.isEmpty()) {
         player.getWorldServer().getChannels().stream()
               .map(channel -> channel.getMapFactory().getMap(mapId))
               .forEach(map -> toRemove.forEach(pair -> {
                  map.removeMonsterSpawn(pair.getLeft(), pair.getRight().getLeft(), pair.getRight().getRight());
                  map.removeAllMonsterSpawn(pair.getLeft(), pair.getRight().getLeft(), pair.getRight().getRight());
               }));
      }

      player.yellowMessage("Cleared " + toRemove.size() + " pmob placements.");
   }
}