package client.command.commands.gm4;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.life.MaplePlayerNPC;

public class PlayerNpcRemoveCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !playernpcremove <player name>");
         return;
      }
      c.getChannelServer().getPlayerStorage().getCharacterByName(params[0]).ifPresent(MaplePlayerNPC::removePlayerNPC);
   }
}
