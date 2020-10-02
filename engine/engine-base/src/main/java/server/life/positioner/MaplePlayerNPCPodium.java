package server.life.positioner;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import config.YamlConfig;
import net.server.Server;
import net.server.channel.Channel;
import server.life.MaplePlayerNPC;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.MasterBroadcaster;
import tools.packet.character.npc.GetPlayerNPC;
import tools.packet.character.npc.RemovePlayerNPC;
import tools.packet.spawn.RemoveNPCController;
import tools.packet.spawn.SpawnPlayerNPC;

public class MaplePlayerNPCPodium {
   private static int getPlatformPosX(int platform) {
      return switch (platform) {
         case 0 -> -50;
         case 1 -> -170;
         default -> 70;
      };
   }

   private static int getPlatformPosY(int platform) {
      return switch (platform) {
         case 0 -> -47;
         default -> 40;
      };
   }

   private static Point calcNextPos(int rank, int step) {
      int podiumPlatform = rank / step;
      int relativePos = (rank % step) + 1;

      return new Point(getPlatformPosX(podiumPlatform) + ((100 * relativePos) / (step + 1)), getPlatformPosY(podiumPlatform));
   }

   private static Point rearrangePlayerNpc(MapleMap map, int newStep, List<MaplePlayerNPC> playerNpcList) {
      int i = 0;
      for (MaplePlayerNPC pn : playerNpcList) {
         pn.updatePlayerNPCPosition(map, calcNextPos(i, newStep));
         i++;
      }

      return calcNextPos(i, newStep);
   }

   private static Point reorganizePlayerNpc(MapleMap map, int newStep, List<MapleMapObject> mmoList) {
      if (!mmoList.isEmpty()) {
         if (YamlConfig.config.server.USE_DEBUG) {
            LoggerUtil.printInfo(LoggerOriginator.NPC, "Reorganizing player npc map, step " + newStep);
         }

         List<MaplePlayerNPC> playerNpcList = new ArrayList<>(mmoList.size());
         for (MapleMapObject mmo : mmoList) {
            playerNpcList.add((MaplePlayerNPC) mmo);
         }

         playerNpcList.sort(Comparator.comparingInt(MaplePlayerNPC::getScriptId));

         for (Channel ch : Server.getInstance().getChannelsFromWorld(map.getWorld())) {
            MapleMap m = ch.getMapFactory().getMap(map.getId());

            for (MaplePlayerNPC pn : playerNpcList) {
               m.removeMapObject(pn);
               MasterBroadcaster.getInstance().sendToAllInMap(m, new RemoveNPCController(pn.objectId()));
               MasterBroadcaster.getInstance().sendToAllInMap(m, new RemovePlayerNPC(pn.objectId()));
            }
         }

         Point ret = rearrangePlayerNpc(map, newStep, playerNpcList);

         for (Channel ch : Server.getInstance().getChannelsFromWorld(map.getWorld())) {
            MapleMap m = ch.getMapFactory().getMap(map.getId());

            for (MaplePlayerNPC pn : playerNpcList) {
               m.addPlayerNPCMapObject(pn);
               MasterBroadcaster.getInstance().sendToAllInMap(m, new SpawnPlayerNPC(pn));
               MasterBroadcaster.getInstance().sendToAllInMap(m, new GetPlayerNPC(pn));
            }
         }

         return ret;
      }

      return null;
   }

   private static int encodePodiumData(int podiumStep, int podiumCount) {
      return (podiumCount * (1 << 5)) + podiumStep;
   }

   private static Point getNextPlayerNpcPosition(MapleMap map, int podiumData) {
      int podiumStep = podiumData % (1 << 5), podiumCount = (podiumData / (1 << 5));

      if (podiumCount >= 3 * podiumStep) {
         if (podiumStep >= YamlConfig.config.server.PLAYERNPC_AREA_STEPS) {
            return null;
         }

         List<MapleMapObject> mmoList = map.getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.PLAYER_NPC));
         map.getWorldServer().setPlayerNpcMapPodiumData(map.getId(), encodePodiumData(podiumStep + 1, podiumCount + 1));
         return reorganizePlayerNpc(map, podiumStep + 1, mmoList);
      } else {
         map.getWorldServer().setPlayerNpcMapPodiumData(map.getId(), encodePodiumData(podiumStep, podiumCount + 1));
         return calcNextPos(podiumCount, podiumStep);
      }
   }

   public static Point getNextPlayerNpcPosition(MapleMap map) {
      Point pos = getNextPlayerNpcPosition(map, map.getWorldServer().getPlayerNpcMapPodiumData(map.getId()));
      if (pos == null) {
         return null;
      }

      return map.getGroundBelow(pos);
   }
}
