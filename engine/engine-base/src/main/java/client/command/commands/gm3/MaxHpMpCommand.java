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
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class MaxHpMpCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      Optional<MapleCharacter> victim = Optional.of(player);

      int statUpdate = 1;
      if (params.length >= 2) {
         victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
         statUpdate = Integer.parseInt(params[1]);
      } else if (params.length == 1) {
         statUpdate = Integer.parseInt(params[0]);
      } else {
         player.yellowMessage("Syntax: !maxhpmp [<playername>] <value>");
      }

      if (victim.isPresent()) {
         int extraHp = victim.get().getCurrentMaxHp() - victim.get().getClientMaxHp();
         int extraMp = victim.get().getCurrentMaxMp() - victim.get().getClientMaxMp();
         statUpdate = Math.max(1 + Math.max(extraHp, extraMp), statUpdate);

         int maxhpUpdate = statUpdate - extraHp;
         int maxmpUpdate = statUpdate - extraMp;
         victim.get().updateMaxHpMaxMp(maxhpUpdate, maxmpUpdate);
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Player '" + params[0] + "' could not be found on this world.");
      }
   }
}
