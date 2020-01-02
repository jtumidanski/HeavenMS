package client.command.commands.gm0;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import net.server.channel.Channel;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.StringUtil;

public class OnlineCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      for (Channel ch : Server.getInstance().getChannelsFromWorld(player.getWorld())) {
         player.yellowMessage("Players in Channel " + ch.getId() + ":");
         for (MapleCharacter chr : ch.getPlayerStorage().getAllCharacters()) {
            if (!chr.isGM()) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, " >> " + StringUtil.makeMapleReadable(chr.getName()) + " is at " + chr.getMap().getMapName() + ".");
            }
         }
      }
   }
}
