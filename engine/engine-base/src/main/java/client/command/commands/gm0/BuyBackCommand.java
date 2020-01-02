package client.command.commands.gm0;

import client.MapleClient;
import client.command.Command;
import client.processor.action.BuybackProcessor;

public class BuyBackCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      if (params.length < 1) {
         c.getPlayer().yellowMessage("Syntax: @buyback <info|now>");
         return;
      }

      if (params[0].contentEquals("now")) {
         BuybackProcessor.processBuyback(c);
      } else {
         c.getPlayer().showBuybackInfo();
      }
   }
}
