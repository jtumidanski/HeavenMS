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
import client.database.administrator.PlayerLifeAdministrator;
import net.server.channel.Channel;
import server.life.MapleLifeFactory;
import server.life.MapleNPC;
import server.maps.MapleMap;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class PnpcCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !pnpc <npcid>");
         return;
      }

      // command suggestion thanks to HighKey21, none, bibiko94 (TAYAMO), asafgb
      int mapId = player.getMapId();
      int npcId = Integer.parseInt(params[0]);
      if (player.getMap().containsNPC(npcId)) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "This map already contains the specified NPC.");
         return;
      }

      MapleNPC npc = MapleLifeFactory.getNPC(npcId);

      Point checkpos = player.getMap().getGroundBelow(player.getPosition());
      int xpos = checkpos.x;
      int ypos = checkpos.y;
      int fh = player.getMap().getFootholds().findBelow(checkpos).id();

      if (npc != null && !npc.getName().equals("MISSINGNO")) {

         DatabaseConnection.getInstance().withConnection(connection ->
               PlayerLifeAdministrator.getInstance().create(connection, npcId, 0, fh, ypos, xpos + 50,
                     xpos - 50, "n", xpos, ypos, player.getWorld(), mapId, -1, 0));

         for (Channel ch : player.getWorldServer().getChannels()) {
            npc = MapleLifeFactory.getNPC(npcId);
            npc.setPosition(checkpos);
            npc.setCy(ypos);
            npc.setRx0(xpos + 50);
            npc.setRx1(xpos - 50);
            npc.setFh(fh);

            MapleMap map = ch.getMapFactory().getMap(mapId);
            map.addMapObject(npc);
            MapleNPC finalNpc = npc;
            MasterBroadcaster.getInstance().sendToAllInMap(map, character -> MaplePacketCreator.spawnNPC(finalNpc));
         }

         player.yellowMessage("Pnpc created.");
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "You have entered an invalid NPC id.");
      }
   }
}