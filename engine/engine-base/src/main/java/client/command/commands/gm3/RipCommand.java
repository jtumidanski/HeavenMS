package client.command.commands.gm3;

import client.MapleClient;
import client.command.Command;
import tools.I18nMessage;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class RipCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("BANNED_WITH_MESSAGE").with(joinStringFrom(params, 1)));
   }
}
