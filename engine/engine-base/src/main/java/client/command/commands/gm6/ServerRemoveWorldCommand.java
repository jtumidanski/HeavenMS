package client.command.commands.gm6;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import server.ThreadManager;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class ServerRemoveWorldCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      final MapleCharacter player = c.getPlayer();

      final int worldId = Server.getInstance().getWorldsSize() - 1;
      if (worldId <= 0) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Unable to remove world 0.");
         return;
      }

      ThreadManager.getInstance().newTask(() -> {
         if (Server.getInstance().removeWorld()) {
            if (player.isLoggedInWorld()) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Successfully removed a world. Current world count: " + Server.getInstance().getWorldsSize() + ".");
            }
         } else {
            if (player.isLoggedInWorld()) {
               if (worldId < 0) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "No registered worlds to remove.");
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Failed to remove world " + worldId + ". Check if there are people currently playing there.");
               }
            }
         }
      });
   }
}
