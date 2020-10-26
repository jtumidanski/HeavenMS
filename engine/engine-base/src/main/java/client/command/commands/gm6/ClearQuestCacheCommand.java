package client.command.commands.gm6;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import rest.RestService;
import rest.UriBuilder;
import tools.I18nMessage;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class ClearQuestCacheCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      UriBuilder.service(RestService.QUEST).path("quests").getRestClient()
            .delete();
      MessageBroadcaster.getInstance()
            .sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("CLEAR_QUEST_CACHE_COMMAND_SUCCESS"));
   }
}
