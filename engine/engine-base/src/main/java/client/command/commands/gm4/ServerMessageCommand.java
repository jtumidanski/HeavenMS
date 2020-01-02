package client.command.commands.gm4;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;

public class ServerMessageCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      c.getWorldServer().setServerMessage(player.getLastCommandMessage());
   }
}
