package client.command.commands.gm5;

import client.MapleClient;
import client.command.Command;
import constants.ServerConstants;

public class SetCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      for (int i = 0; i < params.length; i++) {
         ServerConstants.DEBUG_VALUES[i] = Integer.parseInt(params[i]);
      }
   }
}
