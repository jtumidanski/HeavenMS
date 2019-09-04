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
import server.maps.MaplePortal;
import server.maps.MapleMap;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class JailCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !jail <playername> [<minutes>]");
         return;
      }

      int minutesJailed = params.length >= 2 ? Integer.valueOf(params[1]) : 5;

      if (minutesJailed <= 0) {
         player.yellowMessage("Syntax: !jail <playername> [<minutes>]");
         return;
      }

      c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]).ifPresentOrElse(victim -> {
         victim.addJailExpirationTime(minutesJailed * 60 * 1000);
         int mapid = 300000012;
         if (victim.getMapId() != mapid) {    // those gone to jail won't be changing map anyway
            MapleMap target = c.getChannelServer().getMapFactory().getMap(mapid);
            MaplePortal targetPortal = target.getPortal(0);
            victim.saveLocationOnWarp();
            victim.changeMap(target, targetPortal);
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, victim.getName() + " was jailed for " + minutesJailed + " minutes.");
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, victim.getName() + "'s time in jail has been extended for " + minutesJailed + " minutes.");
         }
      }, () -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Player '" + params[0] + "' could not be found."));
   }
}
