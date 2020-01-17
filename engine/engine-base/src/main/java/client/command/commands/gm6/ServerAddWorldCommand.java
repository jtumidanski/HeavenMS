package client.command.commands.gm6;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import server.ThreadManager;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

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
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SERVER_ADD_WORLD_COMMAND").with(wid));
            } else {
               if (wid == -2) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SERVER_ADD_WORLD_COMMAND_INI_ERROR"));
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SERVER_ADD_WORLD_COMMAND_ERROR_GENERIC"));
               }
            }
         }
      });
   }
}
