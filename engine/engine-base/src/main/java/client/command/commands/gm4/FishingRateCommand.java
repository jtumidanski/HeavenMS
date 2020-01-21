package client.command.commands.gm4;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class FishingRateCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("FISH_RATE_COMMAND_SYNTAX"));
         return;
      }

      int fishRate = Math.max(Integer.parseInt(params[0]), 1);
      c.getWorldServer().setFishingRate(fishRate);
      MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("FISH_RATE_COMMAND_SUCCESS").with(fishRate));
   }
}
