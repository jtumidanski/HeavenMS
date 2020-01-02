package client.command.commands.gm6;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import net.server.world.World;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class DCAllCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      for (World world : Server.getInstance().getWorlds()) {
         for (MapleCharacter chr : world.getPlayerStorage().getAllCharacters()) {
            if (!chr.isGM()) {
               chr.getClient().disconnect(false, false);
            }
         }
      }
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "All players successfully disconnected.");
   }
}
