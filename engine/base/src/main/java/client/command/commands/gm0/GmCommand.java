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
import net.server.Server;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.Randomizer;
import tools.ServerNoticeType;
import tools.StringUtil;

public class GmCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      String[] tips = {
            "Please only use @gm in emergencies or to report somebody.",
            "To report a bug or make a suggestion, use the forum.",
            "Please do not use @gm to ask if a GM is online.",
            "Do not ask if you can receive help, just state your issue.",
            "Do not say 'I have a bug to report', just state it.",
      };
      MapleCharacter player = c.getPlayer();
      if (params.length < 1 || params[0].length() < 3) { // #goodbye 'hi'
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Your message was too short. Please provide as much detail as possible.");
         return;
      }
      String message = player.getLastCommandMessage();
      Server.getInstance().broadcastGMMessage(c.getWorld(), MaplePacketCreator.sendYellowTip("[GM Message]:" + StringUtil.makeMapleReadable(player.getName()) + ": " + message));
      MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.POP_UP, MapleCharacter::isGM, message);
      FilePrinter.printError(FilePrinter.COMMAND_GM, StringUtil.makeMapleReadable(player.getName()) + ": " + message);
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Your message '" + message + "' was sent to GMs.");
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, tips[Randomizer.nextInt(tips.length)]);
   }
}
