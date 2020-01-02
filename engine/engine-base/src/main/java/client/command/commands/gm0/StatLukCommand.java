package client.command.commands.gm0;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import config.YamlConfig;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class StatLukCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      int remainingAp = player.getRemainingAp();

      int amount;
      if (params.length > 0) {
         try {
            amount = Math.min(Integer.parseInt(params[0]), remainingAp);
         } catch (NumberFormatException e) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.NOTICE, "That is not a valid number!");
            return;
         }
      } else {
         amount = Math.min(remainingAp, YamlConfig.config.server.MAX_AP - player.getLuk());
      }
      if (!player.assignLuk(Math.max(amount, 0))) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.NOTICE, "Please make sure your AP is not over " + YamlConfig.config.server.MAX_AP + " and you have enough to distribute.");
      }
   }
}
