package client.command.commands.gm4;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class TravelRateCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("TRAVEL_RATE_COMMAND_SYNTAX"));
         return;
      }

      int travelRate = Math.max(Integer.parseInt(params[0]), 1);
      c.getWorldServer().setTravelRate(travelRate);
      MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.LIGHT_BLUE, "[Rate] Travel Rate has been changed to " + travelRate + "x.");
   }
}
