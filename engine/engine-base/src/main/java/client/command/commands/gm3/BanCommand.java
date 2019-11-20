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

import client.processor.BanProcessor;
import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import client.database.administrator.IpBanAdministrator;
import server.TimerManager;
import tools.DatabaseConnection;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.StringUtil;
import tools.packet.ui.GMEffect;

public class BanCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 2) {
         player.yellowMessage("Syntax: !ban <IGN> <Reason> (Please be descriptive)");
         return;
      }
      String ign = params[0];
      String reason = joinStringFrom(params, 1);
      MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(ign).orElse(null);
      if (target != null) {
         String readableTargetName = StringUtil.makeMapleReadable(target.getName());
         String ip = target.getClient().getSession().getRemoteAddress().toString().split(":")[0];
         //Ban ip
         if (ip.matches("/[0-9]{1,3}\\..*")) {
            DatabaseConnection.getInstance().withConnection(connection -> IpBanAdministrator.getInstance().banIp(connection, ip, target.getClient().getAccID()));
         }
         target.getClient().banMacs();
         reason = c.getPlayer().getName() + " banned " + readableTargetName + " for " + reason + " (IP: " + ip + ") " + "(MAC: " + c.getMacs() + ")";
         target.ban(reason);
         target.yellowMessage("You have been banned by #b" + c.getPlayer().getName() + " #k.");
         target.yellowMessage("Reason: " + reason);
         PacketCreator.announce(c, new GMEffect(4, (byte) 0));
         final MapleCharacter rip = target;
         TimerManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
               rip.getClient().disconnect(false, false);
            }
         }, 5000); //5 Seconds
         MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.LIGHT_BLUE, "[RIP]: " + ign + " has been banned.");
      } else if (BanProcessor.getInstance().ban(ign, reason, false)) {
         PacketCreator.announce(c, new GMEffect(4, (byte) 0));
         MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.LIGHT_BLUE, "[RIP]: " + ign + " has been banned.");
      } else {
         PacketCreator.announce(c, new GMEffect(6, (byte) 1));
      }
   }
}
