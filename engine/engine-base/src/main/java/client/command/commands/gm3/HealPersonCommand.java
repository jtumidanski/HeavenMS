package client.command.commands.gm3;

import client.AbstractMapleCharacterObject;
import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class HealPersonCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      c.getWorldServer().getPlayerStorage().getCharacterByName(params[0])
            .ifPresentOrElse(AbstractMapleCharacterObject::healHpMp, () -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Player '" + params[0] + "' could not be found."));
   }
}
