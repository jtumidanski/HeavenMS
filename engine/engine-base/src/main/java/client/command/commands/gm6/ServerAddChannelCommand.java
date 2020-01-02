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
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Syntax: @addchannel <world id>");
         return;
      }

      final int worldId = Integer.parseInt(params[0]);

      ThreadManager.getInstance().newTask(() -> {
         int channelId = Server.getInstance().addChannel(worldId);
         if (player.isLoggedInWorld()) {
            if (channelId >= 0) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "NEW Channel " + channelId + " successfully deployed on world " + worldId + ".");
            } else {
               if (channelId == -3) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Invalid worldId detected. Channel creation aborted.");
               } else if (channelId == -2) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Reached channel limit on worldId " + worldId + ". Channel creation aborted.");
               } else if (channelId == -1) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Error detected when loading the 'world.ini' file. Channel creation aborted.");
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "NEW Channel failed to be deployed. Check if the needed port is already in use or other limitations are taking place.");
               }
            }
         }
      });
   }
}
