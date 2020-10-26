package client.command.commands.gm6;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import rest.RestService;
import rest.UriBuilder;
import tools.I18nMessage;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class ClearQuestCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance()
               .sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("CLEAR_QUEST_COMMAND_LENGTH"));
         return;
      }
      UriBuilder.service(RestService.QUEST).path("quests").path(params[0]).getRestClient().delete();
      MessageBroadcaster.getInstance()
            .sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("CLEAR_QUEST_COMMAND_SUCCESS").with(params[0]));
   }
}
