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
package client.command.commands.gm6;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class SetGmLevelCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 2) {
         player.yellowMessage("Syntax: !setgmlevel <playername> <newlevel>");
         return;
      }

      int newLevel = Integer.parseInt(params[1]);
      Optional<MapleCharacter> target = c.getChannelServer().getPlayerStorage().getCharacterByName(params[0]);
      if (target.isPresent()) {
         target.get().setGMLevel(newLevel);
         target.get().getClient().setGMLevel(newLevel);

         MessageBroadcaster.getInstance().sendServerNotice(target.get(), ServerNoticeType.NOTICE, "You are now a level " + newLevel + " GM. See @commands for a list of available commands.");
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.NOTICE, target + " is now a level " + newLevel + " GM.");
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.NOTICE, "Player '" + params[0] + "' was not found on this channel.");
      }
   }
}