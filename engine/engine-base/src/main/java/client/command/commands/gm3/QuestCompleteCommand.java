package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.quest.MapleQuest;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class QuestCompleteCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();

      if (params.length < 1) {
         player.yellowMessage("Syntax: !completequest <quest id>");
         return;
      }

      int questId = Integer.parseInt(params[0]);

      if (player.getQuestStatus(questId) == 1) {
         MapleQuest quest = MapleQuest.getInstance(questId);
         if (quest != null && quest.getNpcRequirement(true) != -1) {
            c.getAbstractPlayerInteraction().forceCompleteQuest(questId, quest.getNpcRequirement(true));
         } else {
            c.getAbstractPlayerInteraction().forceCompleteQuest(questId);
         }

         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "QUEST " + questId + " completed.");
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "QUEST " + questId + " not started or already completed.");
      }
   }
}
