package client.command.commands.gm6;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import server.ThreadManager;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class ServerRemoveChannelCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      final MapleCharacter player = c.getPlayer();

      if (params.length < 1) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Syntax: @removechannel <world id>");
         return;
      }

      final int worldId = Integer.parseInt(params[0]);
      ThreadManager.getInstance().newTask(() -> {
         if (Server.getInstance().removeChannel(worldId)) {
            if (player.isLoggedInWorld()) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Successfully removed a channel on World " + worldId + ". Current channel count: " + Server.getInstance().getWorld(worldId).getChannelsSize() + ".");
            }
         } else {
            if (player.isLoggedInWorld()) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Failed to remove last Channel on world " + worldId + ". Check if either that world exists or there are people currently playing there.");
            }
         }
      });
   }
}
