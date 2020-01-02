package client.command.commands.gm6;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import server.ThreadManager;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class ServerAddWorldCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      final MapleCharacter player = c.getPlayer();

      ThreadManager.getInstance().newTask(() -> {
         int wid = Server.getInstance().addWorld();

         if (player.isLoggedInWorld()) {
            if (wid >= 0) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "NEW World " + wid + " successfully deployed.");
            } else {
               if (wid == -2) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Error detected when loading the 'world.ini' file. World creation aborted.");
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "NEW World failed to be deployed. Check if needed ports are already in use or maximum world count has been reached.");
               }
            }
         }
      });
   }
}
