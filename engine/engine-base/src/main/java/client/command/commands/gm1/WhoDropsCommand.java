package client.command.commands.gm1;

import java.util.ArrayList;
import java.util.Objects;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import database.provider.DropDataProvider;
import server.MapleItemInformationProvider;
import database.DatabaseConnection;
import tools.MessageBroadcaster;
import tools.Pair;
import tools.ServerNoticeType;

public class WhoDropsCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Please do @whodrops <item name>");
         return;
      }

      if (c.tryAcquireClient()) {
         try {
            String searchString = player.getLastCommandMessage();
            StringBuilder output = new StringBuilder();

            ArrayList<Pair<Integer, String>> itemData = MapleItemInformationProvider.getInstance().getItemDataByName(searchString);
            if (itemData.size() == 0) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "The item you searched for doesn't exist.");
               return;
            }

            itemData.stream().limit(10).forEach(pair -> {
               DatabaseConnection.getInstance().withConnectionResult(connection -> DropDataProvider.getInstance().getMonstersWhoDrop(connection, pair.getLeft()))
                     .ifPresent(monsterIds -> {
                        output.append("#v").append(pair.getLeft()).append("##k is dropped by:\r\n");
                        monsterIds.stream()
                              .filter(Objects::nonNull)
                              .forEach(name -> output.append("#o").append(name).append("#, "));
                     });
               output.append("\r\n\r\n");
            });

            c.getAbstractPlayerInteraction().npcTalk(9010000, output.toString());
         } finally {
            c.releaseClient();
         }
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Please wait a while for your request to be processed.");
      }
   }
}
