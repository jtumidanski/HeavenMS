package client.command.commands.gm6;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import config.YamlConfig;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class SupplyRateCouponCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SUPPLY_RATE_COUPON_COMMAND_SYNTAX"));
         return;
      }

      YamlConfig.config.server.USE_SUPPLY_RATE_COUPONS = params[0].compareToIgnoreCase("no") != 0;
      if (YamlConfig.config.server.USE_SUPPLY_RATE_COUPONS) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SUPPLY_RATE_COUPON_COMMAND_ENABLED"));
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("SUPPLY_RATE_COUPON_COMMAND_DISABLED"));
      }
   }
}
