package client.command.commands.gm2;

import java.io.File;
import java.util.List;

import com.ms.qos.rest.QuestAttributes;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import rest.DataBody;
import server.MapleItemInformationProvider;
import server.processor.QuestProcessor;
import tools.I18nMessage;
import tools.MessageBroadcaster;
import tools.Pair;

public class SearchCommand extends Command {
   private static MapleData npcStringData;
   private static MapleData mobStringData;
   private static MapleData skillStringData;
   private static MapleData mapStringData;

   {
      setDescription("");

      MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File("wz/String.wz"));
      npcStringData = dataProvider.getData("Npc.img");
      mobStringData = dataProvider.getData("Mob.img");
      skillStringData = dataProvider.getData("Skill.img");
      mapStringData = dataProvider.getData("Map.img");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 2) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("SEARCH_COMMAND_SYNTAX"));
         return;
      }
      StringBuilder sb = new StringBuilder();

      String search = joinStringFrom(params, 1);
      long start = System.currentTimeMillis();
      MapleData data = null;
      if (!params[0].equalsIgnoreCase("ITEM")) {
         int searchType = 0;

         if (params[0].equalsIgnoreCase("NPC")) {
            data = npcStringData;
         } else if (params[0].equalsIgnoreCase("MOB") || params[0].equalsIgnoreCase("MONSTER")) {
            data = mobStringData;
         } else if (params[0].equalsIgnoreCase("SKILL")) {
            data = skillStringData;
         } else if (params[0].equalsIgnoreCase("MAP")) {
            data = mapStringData;
            searchType = 1;
         } else if (params[0].equalsIgnoreCase("QUEST")) {
            data = mapStringData;
            searchType = 2;
         } else {
            sb.append(
                  "#bInvalid search.\r\nSyntax: '!search [type] [name]', where [type] is MAP, QUEST, NPC, ITEM, MOB, or SKILL.");
         }
         if (data != null) {
            String name;

            if (searchType == 0) {
               for (MapleData searchData : data.getChildren()) {
                  name = MapleDataTool.getString(searchData.getChildByPath("name"), "NO-NAME");
                  if (name.toLowerCase().contains(search.toLowerCase())) {
                     sb.append("#b").append(Integer.parseInt(searchData.getName())).append("#k - #r").append(name).append("\r\n");
                  }
               }
            } else if (searchType == 1) {
               String mapName, streetName;

               for (MapleData searchDataDir : data.getChildren()) {
                  for (MapleData searchData : searchDataDir.getChildren()) {
                     mapName = MapleDataTool.getString(searchData.getChildByPath("mapName"), "NO-NAME");
                     streetName = MapleDataTool.getString(searchData.getChildByPath("streetName"), "NO-NAME");

                     if (mapName.toLowerCase().contains(search.toLowerCase()) || streetName.toLowerCase()
                           .contains(search.toLowerCase())) {
                        sb.append("#b").append(Integer.parseInt(searchData.getName())).append("#k - #r").append(streetName)
                              .append(" - ").append(mapName).append("\r\n");
                     }
                  }
               }
            } else {
               List<DataBody<QuestAttributes>> quests = QuestProcessor.getInstance().getMatchedQuests(search);
               for (DataBody<QuestAttributes> mq : quests) {
                  sb.append("#b").append(mq.getId()).append("#k - #r");

                  String parentName = mq.getAttributes().getParentName();
                  if (!parentName.isEmpty()) {
                     sb.append(parentName).append(" - ");
                  }
                  sb.append(mq.getAttributes().getName()).append("\r\n");
               }
            }
         }
      } else {
         for (Pair<Integer, String> itemPair : MapleItemInformationProvider.getInstance().getAllItems()) {
            if (sb.length() < 32654) {
               if (itemPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                  sb.append("#b").append(itemPair.getLeft()).append("#k - #r").append(itemPair.getRight()).append("\r\n");
               }
            } else {
               sb.append("#bCouldn't load all items, there are too many results.\r\n");
               break;
            }
         }
      }
      if (sb.length() == 0) {
         sb.append("#bNo ").append(params[0].toLowerCase()).append("s found.\r\n");
      }
      sb.append("\r\n#kLoaded within ").append((double) (System.currentTimeMillis() - start) / 1000)
            .append(" seconds.");//because I can, and it's free

      c.getAbstractPlayerInteraction().npcTalk(9010000, sb.toString());
   }
}
