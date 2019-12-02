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

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import database.administrator.PlayerLifeAdministrator;
import net.server.channel.Channel;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.MapleMap;
import database.DatabaseConnection;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class PmobCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !pmob <mobid> [<mobtime>]");
         return;
      }

      // command suggestion thanks to HighKey21, none, bibiko94 (TAYAMO), asafgb
      int mapId = player.getMapId();
      int mobId = Integer.parseInt(params[0]);
      int mobTime = (params.length > 1) ? Integer.parseInt(params[1]) : -1;

      Point checkpos = player.getMap().getGroundBelow(player.position());
      int xpos = checkpos.x;
      int ypos = checkpos.y;
      int fh = player.getMap().getFootholds().findBelow(checkpos).id();

      MapleMonster mob = MapleLifeFactory.getMonster(mobId);
      if (mob != null && !mob.getName().equals("MISSINGNO")) {
         mob.position_$eq(checkpos);
         mob.cy_$eq(ypos);
         mob.rx0_$eq(xpos + 50);
         mob.rx1_$eq(xpos - 50);
         mob.fh_$eq(fh);

         DatabaseConnection.getInstance().withConnection(connection ->
               PlayerLifeAdministrator.getInstance().create(connection, mobId, 0, fh, ypos, xpos + 50,
                     xpos - 50, "m", xpos, ypos, player.getWorld(), mapId, mobTime, 0));

         for (Channel ch : player.getWorldServer().getChannels()) {
            MapleMap map = ch.getMapFactory().getMap(mapId);
            map.addMonsterSpawn(mob, mobTime, -1);
            map.addAllMonsterSpawn(mob, mobTime, -1);
         }

         player.yellowMessage("Pmob created.");
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "You have entered an invalid mob id.");
      }
   }
}