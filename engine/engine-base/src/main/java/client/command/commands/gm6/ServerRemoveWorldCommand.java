package client.command.commands.gm6;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import server.ThreadManager;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class ServerRemoveWorldCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      final MapleCharacter player = c.getPlayer();

      final int worldId = Server.getInstance().getWorldsSize() - 1;
      if (worldId <= 0) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SERVER_REMOVE_WORLD_COMMAND_WORLD_0"));
         return;
      }

      ThreadManager.getInstance().newTask(() -> {
         if (Server.getInstance().removeWorld()) {
            if (player.isLoggedInWorld()) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SERVER_REMOVE_WORLD_COMMAND_SUCCESS").with(Server.getInstance().getWorldsSize()));
            }
         } else {
            if (player.isLoggedInWorld()) {
               if (worldId < 0) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SERVER_REMOVE_WORLD_COMMAND_NO_WORLDS_TO_REMOVE"));
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SERVER_REMOVE_WORLD_COMMAND_ERROR").with(worldId));
               }
            }
         }
      });
   }
}
