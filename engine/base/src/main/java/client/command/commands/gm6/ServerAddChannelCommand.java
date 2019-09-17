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

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import server.ThreadManager;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class ServerAddChannelCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      final MapleCharacter player = c.getPlayer();

      if (params.length < 1) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Syntax: @addchannel <worldid>");
         return;
      }

      final int worldid = Integer.parseInt(params[0]);

      ThreadManager.getInstance().newTask(new Runnable() {
         @Override
         public void run() {
            int chid = Server.getInstance().addChannel(worldid);
            if (player.isLoggedinWorld()) {
               if (chid >= 0) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "NEW Channel " + chid + " successfully deployed on world " + worldid + ".");
               } else {
                  if (chid == -3) {
                     MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Invalid worldid detected. Channel creation aborted.");
                  } else if (chid == -2) {
                     MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Reached channel limit on worldid " + worldid + ". Channel creation aborted.");
                  } else if (chid == -1) {
                     MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Error detected when loading the 'world.ini' file. Channel creation aborted.");
                  } else {
                     MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "NEW Channel failed to be deployed. Check if the needed port is already in use or other limitations are taking place.");
                  }
               }
            }
         }
      });
   }
}