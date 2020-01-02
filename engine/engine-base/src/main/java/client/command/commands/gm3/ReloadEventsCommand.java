package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import net.server.channel.Channel;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class ReloadEventsCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      for (Channel ch : Server.getInstance().getAllChannels()) {
         ch.reloadEventScriptManager();
      }
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Reloaded Events");
   }
}
