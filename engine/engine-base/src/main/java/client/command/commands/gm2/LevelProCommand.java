package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;

public class LevelProCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !levelpro <new level>");
         return;
      }
      while (player.getLevel() < Math.min(player.getMaxClassLevel(), Integer.parseInt(params[0]))) {
         player.levelUp(false);
      }
   }
}
