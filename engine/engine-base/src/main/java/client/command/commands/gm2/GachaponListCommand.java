package client.command.commands.gm2;

import client.MapleClient;
import client.command.Command;

public class GachaponListCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      c.getAbstractPlayerInteraction().openNpc(10000, "gachaponInfo");
   }
}
