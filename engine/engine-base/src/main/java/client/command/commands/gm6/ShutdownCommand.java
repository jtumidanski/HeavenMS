package client.command.commands.gm6;

import java.util.Collection;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import server.TimerManager;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class ShutdownCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("SHUT_DOWN_COMMAND_SYNTAX"));
         return;
      }

      int time = 60000;
      if (params[0].equalsIgnoreCase("now")) {
         time = 1;
      } else {
         time *= Integer.parseInt(params[0]);
      }

      if (time > 1) {
         int seconds = (time / 1000) % 60;
         int minutes = (time / (1000 * 60)) % 60;
         int hours = (time / (1000 * 60 * 60)) % 24;
         int days = (time / (1000 * 60 * 60 * 24));

         String strTime = (days > 0 ? days + "days, " : "") + (hours > 0 ? hours + " hours, " : "") + minutes + " minutes, " + seconds + " seconds";
         Server.getInstance().getWorlds().parallelStream()
               .map(world -> world.getPlayerStorage().getAllCharacters())
               .flatMap(Collection::stream)
               .forEach(character -> MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.NOTICE, I18nMessage.from("SHUT_DOWN_COMMAND_MESSAGE").with(strTime)));
      }
      TimerManager.getInstance().schedule(Server.getInstance().shutdown(false), time);
   }
}
