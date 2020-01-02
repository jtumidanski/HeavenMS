package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.quest.MapleQuest;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class QuestStartCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();

      if (params.length < 1) {
         player.yellowMessage("Syntax: !startquest <quest id>");
         return;
      }

      int questId = Integer.parseInt(params[0]);

      if (player.getQuestStatus(questId) == 0) {
         MapleQuest quest = MapleQuest.getInstance(questId);
         if (quest != null && quest.getNpcRequirement(false) != -1) {
            c.getAbstractPlayerInteraction().forceStartQuest(questId, quest.getNpcRequirement(false));
         } else {
            c.getAbstractPlayerInteraction().forceStartQuest(questId);
         }

         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "QUEST " + questId + " started.");
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "QUEST " + questId + " already started/completed.");
      }
   }
}
