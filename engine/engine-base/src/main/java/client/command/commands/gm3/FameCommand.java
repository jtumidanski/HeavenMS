package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class FameCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 2) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("FAME_COMMAND_SYNTAX"));
         return;
      }

      c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]).ifPresentOrElse(victim -> {
         victim.setFame(Integer.parseInt(params[1]));
         victim.updateSingleStat(MapleStat.FAME, victim.getFame());
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("FAME_COMMAND_SUCCESS"));
      }, () -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PLAYER_NOT_FOUND").with(params[0])));
   }
}
