package client.command.commands.gm3;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.ui.GetClock;
import tools.packet.ui.StopClock;

public class TimerCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 2) {
         player.yellowMessage("Syntax: !timer <player name> <seconds>|remove");
         return;
      }

      Optional<MapleCharacter> victim = c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]);
      if (victim.isPresent()) {
         if (params[1].equalsIgnoreCase("remove")) {
            PacketCreator.announce(victim.get(), new StopClock());
         } else {
            try {
               PacketCreator.announce(victim.get(), new GetClock(Integer.parseInt(params[1])));
            } catch (NumberFormatException e) {
               player.yellowMessage("Syntax: !timer <player name> <seconds>|remove");
            }
         }
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Player '" + params[0] + "' could not be found.");
      }
   }
}
