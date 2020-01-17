package client.command.commands.gm6;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import server.ThreadManager;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class ServerAddChannelCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      final MapleCharacter player = c.getPlayer();

      if (params.length < 1) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SERVER_ADD_CHANNEL_COMMAND_SYNTAX"));
         return;
      }

      final int worldId = Integer.parseInt(params[0]);

      ThreadManager.getInstance().newTask(() -> {
         int channelId = Server.getInstance().addChannel(worldId);
         if (player.isLoggedInWorld()) {
            if (channelId >= 0) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SERVER_ADD_CHANNEL_COMMAND_SUCCESS").with(channelId, worldId));
            } else {
               if (channelId == -3) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SERVER_ADD_CHANNEL_COMMAND_INVALID_WORLD"));
               } else if (channelId == -2) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SERVER_ADD_CHANNEL_COMMAND_CHANNEL_LIMIT").with(worldId));
               } else if (channelId == -1) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SERVER_ADD_CHANNEL_COMMAND_INI_ERROR"));
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SERVER_ADD_CHANNEL_COMMAND_ERROR_GENERIC"));
               }
            }
         }
      });
   }
}
