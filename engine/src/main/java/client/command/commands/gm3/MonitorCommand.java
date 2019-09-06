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

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MapleLogger;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class MonitorCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !monitor <ign>");
         return;
      }
      Optional<MapleCharacter> victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
      if (victim.isEmpty()) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Player '" + params[0] + "' could not be found on this world.");
         return;
      }
      boolean monitored = MapleLogger.monitored.contains(victim.get().getId());
      if (monitored) {
         MapleLogger.monitored.remove(victim.get().getId());
      } else {
         MapleLogger.monitored.add(victim.get().getId());
      }
      player.yellowMessage(victim.get().getId() + " is " + (!monitored ? "now being monitored." : "no longer being monitored."));
      String message = player.getName() + (!monitored ? " has started monitoring " : " has stopped monitoring ") + victim.get().getId() + ".";
      MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.PINK_TEXT, MapleCharacter::isGM, message);

   }
}