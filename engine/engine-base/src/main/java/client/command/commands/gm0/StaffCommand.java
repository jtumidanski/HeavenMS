package client.command.commands.gm0;

import client.MapleClient;
import client.command.Command;

public class StaffCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      c.getAbstractPlayerInteraction().openNpc(2010007, "credits");
   }
}
