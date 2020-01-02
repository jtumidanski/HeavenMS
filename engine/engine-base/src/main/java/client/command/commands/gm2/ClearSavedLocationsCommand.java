package client.command.commands.gm2;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.maps.SavedLocationType;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

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
            MessageBroadcaster.getInstance().sendServerNotice(player.get(), ServerNoticeType.PINK_TEXT, "Player '" + params[0] + "' could not be found.");
            return;
         }
      } else {
         victim = Optional.of(c.getPlayer());
      }

      for (SavedLocationType type : SavedLocationType.values()) {
         victim.get().clearSavedLocation(type);
      }

      MessageBroadcaster.getInstance().sendServerNotice(player.get(), ServerNoticeType.PINK_TEXT, "Cleared " + params[0] + "'s saved locations.");
   }
}
