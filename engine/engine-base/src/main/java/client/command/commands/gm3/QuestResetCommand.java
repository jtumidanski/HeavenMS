package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.processor.QuestProcessor;
import server.quest.MapleQuest;
import tools.I18nMessage;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

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
         MapleQuest quest = QuestProcessor.getInstance().getQuest(questId);
         if (quest != null) {
            QuestProcessor.getInstance().reset(player, quest);
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT,
                  I18nMessage.from("QUEST_RESET_COMMAND_SUCCESS").with(questId));
         } else {    // should not occur
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT,
                  I18nMessage.from("QUEST_RESET_COMMAND_FAILURE").with(questId));
         }
      }
   }
}
