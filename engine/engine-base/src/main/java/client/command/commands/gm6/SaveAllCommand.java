package client.command.commands.gm6;

import java.util.Collection;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import tools.I18nMessage;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class SaveAllCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      Server.getInstance().getWorlds().stream()
            .map(world -> world.getPlayerStorage().getAllCharacters())
            .flatMap(Collection::stream)
            .forEach(MapleCharacter::saveCharToDB);

      MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.PINK_TEXT, MapleCharacter::isGM, I18nMessage.from("SAVE_ALL_COMMAND_GM_SUCCESS").with(player.getName()));
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SAVE_ALL_COMMAND_SUCCESS"));
   }
}
