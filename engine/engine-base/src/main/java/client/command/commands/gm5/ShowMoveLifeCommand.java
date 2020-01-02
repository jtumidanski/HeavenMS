package client.command.commands.gm5;

import client.MapleClient;
import client.command.Command;
import config.YamlConfig;

public class ShowMoveLifeCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      YamlConfig.config.server.USE_DEBUG_SHOW_RCVD_MVLIFE = !YamlConfig.config.server.USE_DEBUG_SHOW_RCVD_MVLIFE;
   }
}
