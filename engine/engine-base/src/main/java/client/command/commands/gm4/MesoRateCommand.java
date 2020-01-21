package client.command.commands.gm4;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class MesoRateCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("MESO_RATE_COMMAND_SYNTAX"));
         return;
      }

      int mesoRate = Math.max(Integer.parseInt(params[0]), 1);
      c.getWorldServer().setMesoRate(mesoRate);
      MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("MESO_RATE_COMMAND_SUCCESS").with(mesoRate));
   }
}
