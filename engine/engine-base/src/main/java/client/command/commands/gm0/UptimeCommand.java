package client.command.commands.gm0;

import client.MapleClient;
import client.command.Command;
import net.server.Server;

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
      c.getPlayer().yellowMessage("Server has been online for " + days + " days " + hours + " hours " + minutes + " minutes and " + seconds + " seconds.");
   }
}
