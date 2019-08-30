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
import server.life.MapleLifeFactory;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class BombCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length > 0) {
         Optional<MapleCharacter> victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
         if (victim.isPresent()) {
            victim.get().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300166), victim.get().getPosition());
            MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.PINK_TEXT, MapleCharacter::isGM, player.getName() + " used !bomb on " + victim.get().getName());
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Player '" + params[0] + "' could not be found on this world.");
         }
      } else {
         player.getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300166), player.getPosition());
      }
   }
}
