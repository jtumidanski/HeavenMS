package client.command.commands.gm0;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class LeaveEventCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      int returnMap = player.getSavedLocation("EVENT");
      if (returnMap != -1) {
         if (player.getOla() != null) {
            player.getOla().resetTimes();
            player.setOla(null);
         }
         if (player.getFitness() != null) {
            player.getFitness().resetTimes();
            player.setFitness(null);
         }

         player.saveLocationOnWarp();
         player.changeMap(returnMap);
         if (c.getChannelServer().getEvent() != null) {
            c.getChannelServer().getEvent().addLimit();
         }
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("NOT_IN_EVENT"));
      }

   }
}
