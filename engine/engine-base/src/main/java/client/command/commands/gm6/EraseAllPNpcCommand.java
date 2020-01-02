package client.command.commands.gm6;

import client.MapleClient;
import client.command.Command;
import server.life.MaplePlayerNPC;

public class EraseAllPNpcCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MaplePlayerNPC.removeAllPlayerNPC();
   }
}
