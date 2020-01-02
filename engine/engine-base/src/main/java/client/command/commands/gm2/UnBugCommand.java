package client.command.commands.gm2;

import client.MapleClient;
import client.command.Command;
import tools.MasterBroadcaster;
import tools.packet.stat.EnableActions;

public class UnBugCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MasterBroadcaster.getInstance().sendToAllInMap(c.getPlayer().getMap(), new EnableActions());
   }
}
