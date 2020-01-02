package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.quest.MapleQuest;
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
         player.yellowMessage("Syntax: !resetquest <quest id>");
         return;
      }

      int questId = Integer.parseInt(params[0]);

      if (player.getQuestStatus(questId) != 0) {
         MapleQuest quest = MapleQuest.getInstance(questId);
         if (quest != null) {
            quest.reset(player);
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "QUEST " + questId + " reset.");
         } else {    // should not occur
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "QUEST " + questId + " is invalid.");
         }
      }
   }
}
