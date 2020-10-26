package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.processor.QuestProcessor;
import tools.I18nMessage;
import tools.MessageBroadcaster;

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

      if (!QuestProcessor.getInstance().isNotStarted(player, questId)) {
         QuestProcessor.getInstance().reset(player, questId);
      }
   }
}
