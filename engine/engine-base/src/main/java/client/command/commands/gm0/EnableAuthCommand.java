package client.command.commands.gm0;

import client.MapleClient;
import client.command.Command;
import net.server.coordinator.login.MapleLoginBypassCoordinator;

public class EnableAuthCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      if (c.tryAcquireClient()) {
         try {
            MapleLoginBypassCoordinator.getInstance().unregisterLoginBypassEntry(c.getNibbleHWID(), c.getAccID());
         } finally {
            c.releaseClient();
         }
      }
   }
}
