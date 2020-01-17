package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class PosCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      float xPosition = player.position().x;
      float yPosition = player.position().y;
      float fh = player.getMap().getFootholds().findBelow(player.position()).id();
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("POSITION_COMMAND_TITLE").with(xPosition, yPosition));
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("POSITION_COMMAND_BODY").with(fh));
   }
}
