package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;

public class GiveRpCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient client, String[] params) {
      MapleCharacter player = client.getPlayer();
      if (params.length < 2) {
         player.yellowMessage("Syntax: !giverp <playername> <gainrewardpoint>");
         return;
      }

      client.getWorldServer().getPlayerStorage().getCharacterByName(params[0]).ifPresentOrElse(victim -> {
         victim.setRewardPoints(victim.getRewardPoints() + Integer.parseInt(params[1]));
         player.message("RP given. Player " + params[0] + " now has " + victim.getRewardPoints()
               + " reward points.");
      }, () -> player.message("Player '" + params[0] + "' could not be found."));
   }
}
