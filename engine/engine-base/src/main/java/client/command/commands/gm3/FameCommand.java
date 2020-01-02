package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class FameCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 2) {
         player.yellowMessage("Syntax: !fame <player name> <gain fame>");
         return;
      }

      c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]).ifPresentOrElse(victim -> {
         victim.setFame(Integer.parseInt(params[1]));
         victim.updateSingleStat(MapleStat.FAME, victim.getFame());
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "FAME given.");
      }, () -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Player '" + params[0] + "' could not be found."));
   }
}
