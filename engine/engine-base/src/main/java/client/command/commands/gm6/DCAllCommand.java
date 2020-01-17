package client.command.commands.gm6;

import java.util.Collection;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class DCAllCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      Server.getInstance().getWorlds().stream()
            .map(world -> world.getPlayerStorage().getAllCharacters())
            .flatMap(Collection::stream)
            .filter(character -> !character.isGM())
            .forEach(character -> character.getClient().disconnect(false, false));
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("DC_ALL_COMMAND_SUCCESS"));
   }
}
