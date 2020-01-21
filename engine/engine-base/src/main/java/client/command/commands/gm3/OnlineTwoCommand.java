package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import net.server.channel.Channel;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.SimpleMessage;
import tools.StringUtil;

public class OnlineTwoCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      int total = 0;
      for (Channel ch : Server.getInstance().getChannelsFromWorld(player.getWorld())) {
         int size = ch.getPlayerStorage().getAllCharacters().size();
         total += size;
         StringBuilder s = new StringBuilder("(Channel " + ch.getId() + " Online: " + size + ") : ");
         if (ch.getPlayerStorage().getAllCharacters().size() < 50) {
            for (MapleCharacter chr : ch.getPlayerStorage().getAllCharacters()) {
               s.append(StringUtil.makeMapleReadable(chr.getName())).append(", ");
            }
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, SimpleMessage.from(s.substring(0, s.length() - 2)));
         }
      }

      //player.dropMessage(6, "There are a total of " + total + " players online.");
      player.showHint("Players online: #e#r" + total + "#k#n.", 300);
   }
}
