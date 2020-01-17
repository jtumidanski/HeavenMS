package client.command.commands.gm5;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import net.server.Server;
import server.TimerManager;
import server.life.MapleMonster;
import server.life.SpawnPoint;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MaplePortal;
import server.maps.MapleReactor;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;
import tools.I18nMessage;

public class DebugCommand extends Command {
   private final static String[] debugTypes = {"monster", "packet", "portal", "spawnpoint", "pos", "map", "mobsp", "event", "areas", "reactors", "servercoupons", "playercoupons", "timer", "marriage", "buff", ""};

   {
      setDescription("");
   }

   @Override
   public void execute(MapleClient c, String[] params) {
      MapleCharacter player = c.getPlayer();

      if (params.length < 1) {
         MessageBroadcaster.getInstance().yellowMessage(player, I18nMessage.from("DEBUG_COMMAND_SYNTAX"));
         return;
      }

      switch (params[0]) {
         case "type":
         case "help":
            StringBuilder msgTypes = new StringBuilder("Available #bdebug types#k:\r\n\r\n");
            for (int i = 0; i < debugTypes.length; i++) {
               msgTypes.append("#L").append(i).append("#").append(debugTypes[i]).append("#l\r\n");
            }

            c.getAbstractPlayerInteraction().npcTalk(9201143, msgTypes.toString());
            break;
         case "monster":
            List<MapleMapObject> monsters = player.getMap().getMapObjectsInRange(player.position(), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.MONSTER));
            for (MapleMapObject mapObject : monsters) {
               MapleMonster monster = (MapleMonster) mapObject;
               MapleCharacter controller = monster.getController();
               if (controller != null) {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("DEBUG_COMMAND_MONSTER_WITH_AGGRO").with(monster.id(), controller.getName(), monster.isControllerHasAggro(), monster.isControllerKnowsAboutAggro()));
               } else {
                  MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, I18nMessage.from("DEBUG_COMMAND_MONSTER_WITH_AGGRO").with(monster.id()));
               }
            }
            break;
         case "packet":
            //player.getMap().broadcastMessage(MaplePacketCreator.customPacket(joinStringFrom(params, 1)));
            break;
         case "portal":
            MaplePortal portal = player.getMap().findClosestPortal(player.position());
            if (portal != null) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("DEBUG_COMMAND_PORTAL").with(portal.getId(), portal.getName(), portal.getType(), portal.getTargetMapId(), portal.getScriptName(), (portal.getPortalState() ? 1 : 0)));
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("DEBUG_COMMAND_PORTAL_NONE"));
            }
            break;
         case "spawnpoint":
            SpawnPoint sp = player.getMap().findClosestSpawnPoint(player.position());
            if (sp != null) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("DEBUG_COMMAND_SPAWN_POINT").with(sp.getPosition().getX(), sp.getPosition().getY(), sp.getMonsterId(), !sp.getDenySpawn(), sp.shouldSpawn()));
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("DEBUG_COMMAND_NO_SPAWN_POINT"));
            }
            break;
         case "pos":
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("DEBUG_COMMAND_POS").with(player.position().getX(), player.position().getY()));
            break;
         case "map":
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("DEBUG_COMMAND_MAP").with(player.getMap().getId(), ((player.getMap().getEventInstance() != null) ? player.getMap().getEventInstance().getName() : "null"), player.getMap().getAllPlayers().size(), player.getMap().countMonsters(), player.getMap().countReactors(), player.getMap().countItems(), player.getMap().getMapObjects().size()));
            break;
         case "mobsp":
            player.getMap().reportMonsterSpawnPoints(player);
            break;
         case "event":
            if (player.getEventInstance() == null) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("DEBUG_COMMAND_EVENT_NONE"));
            } else {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("DEBUG_COMMAND_EVENT").with(player.getEventInstance().getName()));
            }
            break;
         case "areas":
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("DEBUG_COMMAND_AREAS").with(player.getMapId()));
            byte index = 0;
            for (Rectangle rect : player.getMap().getAreas()) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("DEBUG_COMMAND_AREAS_BODY").with(index, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));
               index++;
            }
            break;
         case "reactors":
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("DEBUG_COMMAND_REACTORS").with(player.getMapId()));
            for (MapleMapObject mmo : player.getMap().getReactors()) {
               MapleReactor mr = (MapleReactor) mmo;
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("DEBUG_COMMAND_REACTORS_BODY").with(mr.getId(), mr.objectId(), mr.getName(), mr.getReactorType(), mr.getState(), mr.getEventState(), mr.position().getX(), mr.position().getY()));
            }
            break;
         case "servercoupons":
         case "coupons":
            String serverCoupons = Server.getInstance().getActiveCoupons().stream()
                  .collect(StringBuilder::new,
                        (sb, s1) -> sb.append(" ").append(s1),
                        (sb1, sb2) -> sb1.append(sb2.toString()))
                  .toString();
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("DEBUG_COMMAND_SERVER_COUPONS").with(serverCoupons));
            break;
         case "playercoupons":
            String playerCoupons = player.getActiveCoupons().stream()
                  .collect(StringBuilder::new,
                        (sb, s1) -> sb.append(" ").append(s1),
                        (sb1, sb2) -> sb1.append(sb2.toString()))
                  .toString();
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("DEBUG_COMMAND_PLAYER_COUPONS").with(playerCoupons));
            break;
         case "timer":
            TimerManager tMan = TimerManager.getInstance();
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("DEBUG_COMMAND_TIMER").with(tMan.getTaskCount(), tMan.getQueuedTasks(), tMan.getActiveCount(), tMan.getCompletedTaskCount()));
            break;
         case "marriage":
            c.getChannelServer().debugMarriageStatus();
            break;
         case "buff":
            c.getPlayer().debugListAllBuffs();
            break;
      }
   }
}
