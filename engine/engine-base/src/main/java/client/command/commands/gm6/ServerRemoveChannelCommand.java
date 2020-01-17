package client.command.commands.gm6;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import server.ThreadManager;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class ServerRemoveChannelCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      final MapleCharacter player = c.getPlayer();

      if (params.length < 1) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SERVER_REMOVE_CHANNEL_COMMAND_SYNTAX"));
         return;
      }

      final int worldId = Integer.parseInt(params[0]);
      ThreadManager.getInstance().newTask(() -> {
         if (Server.getInstance().removeChannel(worldId)) {
            if (player.isLoggedInWorld()) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SERVER_REMOVE_CHANNEL_COMMAND_SUCCESS").with(worldId, Server.getInstance().getWorld(worldId).getChannelsSize()));
            }
         } else {
            if (player.isLoggedInWorld()) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SERVER_REMOVE_CHANNEL_COMMAND_ERROR").with(worldId));
            }
         }
      });
   }
}
