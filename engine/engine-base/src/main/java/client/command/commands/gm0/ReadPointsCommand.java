package client.command.commands.gm0;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.I18nMessage;

public class ReadPointsCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient client, String[] params) {

      MapleCharacter player = client.getPlayer();
      if (params.length > 2) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("READ_POINTS_COMMAND_SYNTAX"));
         return;
      } else if (params.length == 0) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("READ_POINTS_COMMAND_ALL").with(player.getRewardPoints(), player.getClient().getVotePoints()));
         return;
      }

      switch (params[0]) {
         case "rp":
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("READ_POINTS_COMMAND_RP").with(player.getRewardPoints()));
            break;
         case "vp":
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("READ_POINTS_COMMAND_VP").with(player.getClient().getVotePoints()));
            break;
         default:
            MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("READ_POINTS_COMMAND_ALL").with(player.getRewardPoints(), player.getClient().getVotePoints()));
            break;
      }
   }
}