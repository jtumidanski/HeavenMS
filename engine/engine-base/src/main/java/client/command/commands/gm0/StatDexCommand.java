package client.command.commands.gm0;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import config.YamlConfig;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class StatDexCommand extends Command {
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
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.NOTICE, I18nMessage.from("NUMBER_IS_INVALID"));
            return;
         }
      } else {
         amount = Math.min(remainingAp, YamlConfig.config.server.MAX_AP - player.getDex());
      }
      if (!player.assignDex(Math.max(amount, 0))) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.NOTICE, I18nMessage.from("AP_OVER_MAX_ERROR").with(YamlConfig.config.server.MAX_AP));
      }
   }
}
