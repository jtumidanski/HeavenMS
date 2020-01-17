package client.command.commands.gm6;

import java.util.Collection;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

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

      String message = player.getName() + " used !saveall.";
      MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.PINK_TEXT, MapleCharacter::isGM, message);
      MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SAVE_ALL_COMMAND_SUCCESS"));
   }
}
