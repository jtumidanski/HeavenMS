package client.command.commands.gm5;

import client.MapleClient;
import client.command.Command;
import config.YamlConfig;

public class ShowPacketsCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      YamlConfig.config.server.USE_DEBUG_SHOW_RCVD_PACKET = !YamlConfig.config.server.USE_DEBUG_SHOW_RCVD_PACKET;
   }
}
