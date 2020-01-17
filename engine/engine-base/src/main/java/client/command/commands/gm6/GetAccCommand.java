package client.command.commands.gm6;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class GetAccCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("GET_ACCOUNT_NAME_COMMAND_SYNTAX"));
         return;
      }
      c.getWorldServer().getPlayerStorage().getCharacterByName(params[0])
            .ifPresentOrElse(victim -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("GET_ACCOUNT_NAME_COMMAND_SUCCESS").with(victim.getName(), victim.getClient().getAccountName())),
                  () -> MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("PLAYER_NOT_FOUND").with(params[0])));
   }
}
