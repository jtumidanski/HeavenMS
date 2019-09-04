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
import client.MapleStat;
import client.command.Command;
import constants.ItemConstants;
import server.MapleItemInformationProvider;

public class HairCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !hair [<playername>] <hairid>");
         return;
      }

      try {
         if (params.length == 1) {
            int itemId = Integer.parseInt(params[0]);
            if (!ItemConstants.isHair(itemId) || MapleItemInformationProvider.getInstance().getName(itemId) == null) {
               player.yellowMessage("Hair id '" + params[0] + "' does not exist.");
               return;
            }

            player.setHair(itemId);
            player.updateSingleStat(MapleStat.HAIR, itemId);
            player.equipChanged();
         } else {
            int itemId = Integer.parseInt(params[1]);
            if (!ItemConstants.isHair(itemId) || MapleItemInformationProvider.getInstance().getName(itemId) == null) {
               player.yellowMessage("Hair id '" + params[1] + "' does not exist.");
               return;
            }

            Optional<MapleCharacter> victim = c.getChannelServer().getPlayerStorage().getCharacterByName(params[0]);
            if (victim.isPresent()) {
               victim.get().setHair(itemId);
               victim.get().updateSingleStat(MapleStat.HAIR, itemId);
               victim.get().equipChanged();
            } else {
               player.yellowMessage("Player '" + params[0] + "' has not been found on this channel.");
            }
         }
      } catch (Exception ignored) {
      }
   }
}
