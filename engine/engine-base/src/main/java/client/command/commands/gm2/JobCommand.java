package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleJob;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class JobCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length == 1) {
         int jobId = Integer.parseInt(params[0]);
         if (jobId < 0 || jobId >= 2200) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "job id " + jobId + " is not available.");
            return;
         }

         player.changeJob(MapleJob.getById(jobId));
         player.equipChanged();
      } else if (params.length == 2) {
         c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]).ifPresentOrElse(victim -> {
            int jobId = Integer.parseInt(params[1]);
            if (jobId < 0 || jobId >= 2200) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "job id " + jobId + " is not available.");
               return;
            }
            victim.changeJob(MapleJob.getById(jobId));
            player.equipChanged();
         }, () -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Player '" + params[0] + "' could not be found."));
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Syntax: !job <job id> <opt: IGN of another person>");
      }
   }
}
