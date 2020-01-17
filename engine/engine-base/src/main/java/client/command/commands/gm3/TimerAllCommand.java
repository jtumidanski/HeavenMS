package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.I18nMessage;
import tools.packet.ui.GetClock;
import tools.packet.ui.StopClock;

public class TimerAllCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("TIMER_ALL_COMMAND_SYNTAX"));
         return;
      }

      if (params[0].equalsIgnoreCase("remove")) {
         for (MapleCharacter victim : player.getWorldServer().getPlayerStorage().getAllCharacters()) {
            PacketCreator.announce(victim, new StopClock());
         }
      } else {
         try {
            int seconds = Integer.parseInt(params[0]);
            for (MapleCharacter victim : player.getWorldServer().getPlayerStorage().getAllCharacters()) {
               PacketCreator.announce(victim, new GetClock(seconds));
            }
         } catch (NumberFormatException e) {
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("TIMER_ALL_COMMAND_SYNTAX"));
         }
      }
   }
}
