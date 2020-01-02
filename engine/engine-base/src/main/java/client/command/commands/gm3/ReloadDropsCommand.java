package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MapleMonsterInformationProvider;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class ReloadDropsCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      MapleMonsterInformationProvider.getInstance().clearDrops();
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Reloaded Drops");
   }
}
