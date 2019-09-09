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

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.maps.SavedLocationType;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class ClearSavedLocationsCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      Optional<MapleCharacter> player = Optional.of(c.getPlayer()), victim;

      if (params.length > 0) {
         victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
         if (victim.isEmpty()) {
            MessageBroadcaster.getInstance().sendServerNotice(player.get(), ServerNoticeType.PINK_TEXT, "Player '" + params[0] + "' could not be found.");
            return;
         }
      } else {
         victim = Optional.of(c.getPlayer());
      }

      for (SavedLocationType type : SavedLocationType.values()) {
         victim.get().clearSavedLocation(type);
      }

      MessageBroadcaster.getInstance().sendServerNotice(player.get(), ServerNoticeType.PINK_TEXT, "Cleared " + params[0] + "'s saved locations.");
   }
}
