package client.command.commands.gm0;

import client.MapleClient;
import client.command.Command;

public class ToggleExpCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      if (c.tryAcquireClient()) {
         try {
            c.getPlayer().toggleExpGain();
         } finally {
            c.releaseClient();
         }
      }
   }
}
