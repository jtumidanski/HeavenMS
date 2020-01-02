package client.command.commands.gm0;

import client.MapleClient;
import client.command.Command;
import config.YamlConfig;

public class DropLimitCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      int dropCount = c.getPlayer().getMap().getDroppedItemCount();
      if (((float) dropCount) / YamlConfig.config.server.ITEM_LIMIT_ON_MAP < 0.75f) {
         c.getPlayer().showHint("Current drop count: #b" + dropCount + "#k / #e" + YamlConfig.config.server.ITEM_LIMIT_ON_MAP + "#n", 300);
      } else {
         c.getPlayer().showHint("Current drop count: #r" + dropCount + "#k / #e" + YamlConfig.config.server.ITEM_LIMIT_ON_MAP + "#n", 300);
      }

   }
}
