package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;

public class HealMapCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      for (MapleCharacter mch : player.getMap().getCharacters()) {
         if (mch != null) {
            mch.healHpMp();
         }
      }
   }
}
