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
package client.command.commands.gm0;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import constants.ServerConstants;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class StatLukCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      int remainingAp = player.getRemainingAp();

      int amount;
      if (params.length > 0) {
         try {
            amount = Math.min(Integer.parseInt(params[0]), remainingAp);
         } catch (NumberFormatException e) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.NOTICE, "That is not a valid number!");
            return;
         }
      } else {
         amount = Math.min(remainingAp, ServerConstants.MAX_AP - player.getLuk());
      }
      if (!player.assignLuk(Math.max(amount, 0))) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.NOTICE, "Please make sure your AP is not over " + ServerConstants.MAX_AP + " and you have enough to distribute.");
      }
   }
}
