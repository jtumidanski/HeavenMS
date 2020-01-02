package client.command.commands.gm4;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class BossDropRateCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         player.yellowMessage("Syntax: !bossdroprate <new rate>");
         return;
      }

      int bossDropRate = Math.max(Integer.parseInt(params[0]), 1);
      c.getWorldServer().setBossDropRate(bossDropRate);
      MessageBroadcaster.getInstance().sendWorldServerNotice(c.getWorld(), ServerNoticeType.LIGHT_BLUE, "[Rate] Boss Drop Rate has been changed to " + bossDropRate + "x.");
   }
}
