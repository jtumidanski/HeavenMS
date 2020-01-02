package client.command.commands.gm6;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class SetGmLevelCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 2) {
         player.yellowMessage("Syntax: !setgmlevel <player name> <new level>");
         return;
      }

      int newLevel = Integer.parseInt(params[1]);
      Optional<MapleCharacter> target = c.getChannelServer().getPlayerStorage().getCharacterByName(params[0]);
      if (target.isPresent()) {
         target.get().setGMLevel(newLevel);
         target.get().getClient().setGMLevel(newLevel);

         MessageBroadcaster.getInstance().sendServerNotice(target.get(), ServerNoticeType.NOTICE, "You are now a level " + newLevel + " GM. See @commands for a list of available commands.");
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.NOTICE, target + " is now a level " + newLevel + " GM.");
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.NOTICE, "Player '" + params[0] + "' was not found on this channel.");
      }
   }
}
