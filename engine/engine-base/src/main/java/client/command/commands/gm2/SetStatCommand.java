package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;

public class SetStatCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !setstat <new stat>");
         return;
      }

      try {
         int x = Integer.parseInt(params[0]);

         if (x > Short.MAX_VALUE) x = Short.MAX_VALUE;
         else if (x < 4) x = 4;

         player.updateStrDexIntLuk(x);
      } catch (NumberFormatException ignored) {
      }
   }
}
