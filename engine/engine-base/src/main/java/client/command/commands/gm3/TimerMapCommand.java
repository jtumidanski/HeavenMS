package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.PacketCreator;
import tools.packet.ui.GetClock;
import tools.packet.ui.StopClock;

public class TimerMapCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !timermap <seconds>|remove");
         return;
      }

      if (params[0].equalsIgnoreCase("remove")) {
         for (MapleCharacter victim : player.getMap().getCharacters()) {
            PacketCreator.announce(victim, new StopClock());
         }
      } else {
         try {
            int seconds = Integer.parseInt(params[0]);
            for (MapleCharacter victim : player.getMap().getCharacters()) {
               PacketCreator.announce(victim, new GetClock(seconds));
            }
         } catch (NumberFormatException e) {
            player.yellowMessage("Syntax: !timermap <seconds>|remove");
         }
      }
   }
}
