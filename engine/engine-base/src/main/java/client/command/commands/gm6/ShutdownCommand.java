package client.command.commands.gm6;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import net.server.world.World;
import server.TimerManager;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class ShutdownCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !shutdown [<time>|NOW]");
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

         String strTime = "";
         if (days > 0) strTime += days + " days, ";
         if (hours > 0) strTime += hours + " hours, ";
         strTime += minutes + " minutes, ";
         strTime += seconds + " seconds";

         for (World w : Server.getInstance().getWorlds()) {
            for (MapleCharacter chr : w.getPlayerStorage().getAllCharacters()) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, "Server is undergoing maintenance process, and will be shutdown in " + strTime + ". Prepare yourself to quit safely in the mean time.");
            }
         }
      }

      TimerManager.getInstance().schedule(Server.getInstance().shutdown(false), time);
   }
}
