package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;

public class HealCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      player.healHpMp();
   }
}
