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
package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.maps.MapleMap;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class ReachCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !reach <playername>");
         return;
      }

      c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]).ifPresentOrElse(victim -> {
         if (player.getClient().getChannel() != victim.getClient().getChannel()) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Player '" + victim.getName() + "' is at channel " + victim.getClient().getChannel() + ".");
         } else {
            MapleMap map = victim.getMap();
            player.saveLocationOnWarp();
            player.forceChangeMap(map, map.findClosestPortal(victim.getPosition()));
         }
      }, () -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, "Unknown player."));
   }
}