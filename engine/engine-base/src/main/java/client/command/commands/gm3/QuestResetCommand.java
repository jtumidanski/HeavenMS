package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.quest.MapleQuest;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class QuestResetCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();

      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("QUEST_RESET_COMMAND_SYNTAX"));
         return;
      }

      int questId = Integer.parseInt(params[0]);

      if (player.getQuestStatus(questId) != 0) {
         MapleQuest quest = MapleQuest.getInstance(questId);
         if (quest != null) {
            quest.reset(player);
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("QUEST_RESET_COMMAND_SUCCESS").with(questId));
         } else {    // should not occur
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("QUEST_RESET_COMMAND_FAILURE").with(questId));
         }
      }
   }
}
