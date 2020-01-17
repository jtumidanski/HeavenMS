package client.command.commands.gm0;

import client.MapleClient;
import client.command.Command;
import client.processor.action.BuybackProcessor;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class BuyBackCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(c.getPlayer(), I18nMessage.from("BUY_BACK_COMMAND_SYNTAX"));
         return;
      }

      if (params[0].contentEquals("now")) {
         BuybackProcessor.processBuyback(c);
      } else {
         c.getPlayer().showBuybackInfo();
      }
   }
}
