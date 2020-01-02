package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import config.YamlConfig;

public class LevelCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !level <new level>");
         return;
      }

      player.loseExp(player.getExp(), false, false);
      player.setLevel(Math.min(Integer.parseInt(params[0]), player.getMaxClassLevel()) - 1);

      player.resetPlayerRates();
      if (YamlConfig.config.server.USE_ADD_RATES_BY_LEVEL) player.setPlayerRates();
      player.setWorldRates();

      player.levelUp(false);
   }
}
