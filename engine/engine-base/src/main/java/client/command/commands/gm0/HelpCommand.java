package client.command.commands.gm0;

import client.MapleClient;
import client.command.Command;

public class HelpCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient client, String[] params) {
      client.getAbstractPlayerInteraction().openNpc(9201143, "commands");
   }
}
