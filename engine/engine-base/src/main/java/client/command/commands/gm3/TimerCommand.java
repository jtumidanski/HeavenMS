package client.command.commands.gm3;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
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
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("TIMER_COMMAND_SYNTAX"));
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
               MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("TIMER_COMMAND_SYNTAX"));
            }
         }
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PLAYER_NOT_FOUND").with(params[0]));
      }
   }
}
