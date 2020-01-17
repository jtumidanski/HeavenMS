package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import config.YamlConfig;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class LevelCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("LEVEL_COMMAND_SYNTAX"));
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
