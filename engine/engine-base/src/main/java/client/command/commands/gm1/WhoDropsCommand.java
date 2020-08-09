package client.command.commands.gm1;

import java.util.List;
import java.util.Objects;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import database.DatabaseConnection;
import database.provider.DropDataProvider;
import server.MapleItemInformationProvider;
import tools.MessageBroadcaster;
import tools.Pair;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class WhoDropsCommand extends Command {
   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("WHO_DROPS_COMMAND_SYNTAX"));
         return;
      }

      if (c.tryAcquireClient()) {
         try {
            String searchString = player.getLastCommandMessage();
            StringBuilder output = new StringBuilder();

            List<Pair<Integer, String>> itemData = MapleItemInformationProvider.getInstance().getItemDataByName(searchString);
            if (itemData.size() == 0) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("ITEM_SEARCHED_DOES_NOT_EXIST"));
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
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("COMMAND_PATIENCE"));
      }
   }
}
