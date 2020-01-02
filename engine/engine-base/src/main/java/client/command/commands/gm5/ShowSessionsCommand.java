package client.command.commands.gm5;

import client.MapleClient;
import client.command.Command;
import net.server.coordinator.session.MapleSessionCoordinator;

public class ShowSessionsCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleSessionCoordinator.getInstance().printSessionTrace(c);
   }
}
