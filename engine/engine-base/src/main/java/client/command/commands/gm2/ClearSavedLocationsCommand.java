package client.command.commands.gm2;

import java.util.Arrays;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.maps.SavedLocationType;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class ClearSavedLocationsCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      Optional<MapleCharacter> player = Optional.of(c.getPlayer()), victim;

      if (params.length > 0) {
         victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
         if (victim.isEmpty()) {
            MessageBroadcaster.getInstance().sendServerNotice(player.get(), ServerNoticeType.PINK_TEXT, I18nMessage.from("PLAYER_NOT_FOUND").with(params[0]));
            return;
         }
      } else {
         victim = Optional.of(c.getPlayer());
      }

      Arrays.stream(SavedLocationType.values()).forEach(type -> victim.get().clearSavedLocation(type));
      MessageBroadcaster.getInstance().sendServerNotice(player.get(), ServerNoticeType.PINK_TEXT, I18nMessage.from("CLEAR_SAVED_LOCATIONS_COMMAND_SUCCESS").with(params[0]));
   }
}
