package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class UnJailCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("UN_JAIL_COMMAND_SYNTAX"));
         return;
      }

      c.getWorldServer().getPlayerStorage().getCharacterByName(params[0]).ifPresentOrElse(victim -> {
         if (victim.getJailExpirationTimeLeft() <= 0) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("UN_JAIL_COMMAND_ALREADY_FREE"));
            return;
         }
         victim.removeJailExpirationTime();
         MessageBroadcaster.getInstance().sendServerNotice(victim, ServerNoticeType.PINK_TEXT, I18nMessage.from("UN_JAIL_COMMAND_SUCCESS_VICTIM"));
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("UN_JAIL_COMMAND_SUCCESS").with(victim.getName()));
      }, () -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PLAYER_NOT_FOUND").with(params[0])));
   }
}
