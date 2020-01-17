package client.command.commands.gm1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import constants.game.GameConstants;
import server.maps.FieldLimit;
import server.maps.MapleMap;
import server.maps.MapleMapFactory;
import server.maps.MapleMiniDungeonInfo;
import server.maps.MaplePortal;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class GotoCommand extends Command {

   public static StringBuilder GOTO_TOWNS_INFO = new StringBuilder();
   public static StringBuilder GOTO_AREAS_INFO = new StringBuilder();

   {
      setDescription("");

      List<Entry<String, Integer>> towns = new ArrayList<>(GameConstants.GOTO_TOWNS.entrySet());
      sortGotoEntries(towns);

      try {
         for (Map.Entry<String, Integer> e : towns) {
            GOTO_TOWNS_INFO
                  .append("'")
                  .append(e.getKey())
                  .append("' - #b")
                  .append(MapleMapFactory.loadPlaceName(e.getValue()))
                  .append("#k\r\n");
         }

         List<Entry<String, Integer>> areas = new ArrayList<>(GameConstants.GOTO_AREAS.entrySet());
         sortGotoEntries(areas);
         for (Map.Entry<String, Integer> e : areas) {
            GOTO_AREAS_INFO
                  .append("'")
                  .append(e.getKey())
                  .append("' - #b")
                  .append(MapleMapFactory.loadPlaceName(e.getValue()))
                  .append("#k\r\n");
         }
      } catch (Exception e) {
         e.printStackTrace();
         GOTO_TOWNS_INFO = new StringBuilder("(none)");
         GOTO_AREAS_INFO = new StringBuilder("(none)");
      }
   }

   private static void sortGotoEntries(List<Entry<String, Integer>> listEntries) {
      listEntries.sort(Entry.comparingByValue());
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();
      if (params.length < 1) {
         String sendStr = "Syntax: #b@goto <map name>#k. Available areas:\r\n\r\n#rTowns:#k\r\n" + GOTO_TOWNS_INFO;
         if (player.isGM()) {
            sendStr += ("\r\n#rAreas:#k\r\n" + GOTO_AREAS_INFO);
         }

         player.getAbstractPlayerInteraction().npcTalk(9000020, sendStr);
         return;
      }

      if (!player.isAlive()) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, I18nMessage.from("COMMAND_CANNOT_BE_USED_WHEN_DEAD"));
         return;
      }

      if (!player.isGM()) {
         if (player.getEventInstance() != null || MapleMiniDungeonInfo.isDungeonMap(player.getMapId()) || FieldLimit.CANNOT_MIGRATE.check(player.getMap().getFieldLimit())) {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, I18nMessage.from("COMMAND_CANNOT_BE_USED_IN_MAP"));
            return;
         }
      }

      HashMap<String, Integer> gotoMaps;
      if (player.isGM()) {
         gotoMaps = new HashMap<>(GameConstants.GOTO_AREAS);
         gotoMaps.putAll(GameConstants.GOTO_TOWNS);
      } else {
         gotoMaps = GameConstants.GOTO_TOWNS;
      }

      if (gotoMaps.containsKey(params[0])) {
         MapleMap target = c.getChannelServer().getMapFactory().getMap(gotoMaps.get(params[0]));
         MaplePortal targetPortal = target.getRandomPlayerSpawnPoint();
         player.saveLocationOnWarp();
         player.changeMap(target, targetPortal);
      } else {
         String sendStr = "Area '#r" + params[0] + "#k' is not available. Available areas:\r\n\r\n#rTowns:#k" + GOTO_TOWNS_INFO;
         if (player.isGM()) {
            sendStr += ("\r\n#rAreas:#k\r\n" + GOTO_AREAS_INFO);
         }

         player.getAbstractPlayerInteraction().npcTalk(9000020, sendStr);
      }
   }
}
