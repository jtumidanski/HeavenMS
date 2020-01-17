package client.command.commands.gm0;

import client.MapleClient;
import client.command.Command;
import net.server.Server;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class UptimeCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      long milliseconds = System.currentTimeMillis() - Server.uptime;
      int seconds = (int) (milliseconds / 1000) % 60;
      int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
      int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
      int days = (int) ((milliseconds / (1000 * 60 * 60 * 24)));
      MessageBroadcaster.getInstance().yellowMessage(c.getPlayer(), I18nMessage.from("UPTIME_COMMAND_MESSAGE").with(days, hours, minutes, seconds));
   }
}
