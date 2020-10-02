package server.life.positioner;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
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

public class MaplePlayerNPCPositioner {

   private static boolean isPlayerNpcNearby(List<Point> otherPos, Point searchPos, int xLimit, int yLimit) {
      int xLimit2 = xLimit / 2, yLimit2 = yLimit / 2;

      Rectangle searchRect = new Rectangle(searchPos.x - xLimit2, searchPos.y - yLimit2, xLimit, yLimit);
      for (Point pos : otherPos) {
         Rectangle otherRect = new Rectangle(pos.x - xLimit2, pos.y - yLimit2, xLimit, yLimit);

         if (otherRect.intersects(searchRect)) {
            return true;
         }
      }

      return false;
   }

   private static int calcDx(int newStep) {
      return YamlConfig.config.server.PLAYERNPC_AREA_X / (newStep + 1);
   }

   private static int calcDy(int newStep) {
      return (YamlConfig.config.server.PLAYERNPC_AREA_Y / 2) + (YamlConfig.config.server.PLAYERNPC_AREA_Y / (1 << (newStep + 1)));
   }

   private static List<Point> rearrangePlayerNpcPositions(MapleMap map, int newStep, int playerNpcSize) {
      Rectangle mapArea = map.getMapArea();

      int leftPx = mapArea.x + YamlConfig.config.server.PLAYERNPC_INITIAL_X, px, py = mapArea.y + YamlConfig.config.server.PLAYERNPC_INITIAL_Y;
      int outX = mapArea.x + mapArea.width - YamlConfig.config.server.PLAYERNPC_INITIAL_X, outY = mapArea.y + mapArea.height;
      int cx = calcDx(newStep), cy = calcDy(newStep);

      List<Point> otherPlayerNpc = new LinkedList<>();
      while (py < outY) {
         px = leftPx;

         while (px < outX) {
            Point searchPos = map.getPointBelow(new Point(px, py));
            if (searchPos != null) {
               if (!isPlayerNpcNearby(otherPlayerNpc, searchPos, cx, cy)) {
                  otherPlayerNpc.add(searchPos);

                  if (otherPlayerNpc.size() == playerNpcSize) {
                     return otherPlayerNpc;
                  }
               }
            }

            px += cx;
         }

         py += cy;
      }

      return null;
   }

   private static Point rearrangePlayerNpc(MapleMap map, int newStep, List<MaplePlayerNPC> playerNpcList) {
      Rectangle mapArea = map.getMapArea();

      int leftPx = mapArea.x + YamlConfig.config.server.PLAYERNPC_INITIAL_X, px, py = mapArea.y + YamlConfig.config.server.PLAYERNPC_INITIAL_Y;
      int outX = mapArea.x + mapArea.width - YamlConfig.config.server.PLAYERNPC_INITIAL_X, outY = mapArea.y + mapArea.height;
      int cx = calcDx(newStep), cy = calcDy(newStep);

      List<Point> otherPlayerNpcList = new LinkedList<>();
      int i = 0;

      while (py < outY) {
         px = leftPx;

         while (px < outX) {
            Point searchPos = map.getPointBelow(new Point(px, py));
            if (searchPos != null) {
               if (!isPlayerNpcNearby(otherPlayerNpcList, searchPos, cx, cy)) {
                  if (i == playerNpcList.size()) {
                     return searchPos;
                  }

                  MaplePlayerNPC pn = playerNpcList.get(i);
                  i++;

                  pn.updatePlayerNPCPosition(map, searchPos);
                  otherPlayerNpcList.add(searchPos);
               }
            }

            px += cx;
         }

         py += cy;
      }

      return null;    // this area should not be reached under any scenario
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

   private static Point getNextPlayerNpcPosition(MapleMap map, int initStep) {
      List<MapleMapObject> mmoList = map.getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.PLAYER_NPC));
      List<Point> otherPlayerNpc = new LinkedList<>();
      for (MapleMapObject mmo : mmoList) {
         otherPlayerNpc.add(mmo.position());
      }

      int cx = calcDx(initStep), cy = calcDy(initStep);
      Rectangle mapArea = map.getMapArea();
      int outX = mapArea.x + mapArea.width - YamlConfig.config.server.PLAYERNPC_INITIAL_X, outY = mapArea.y + mapArea.height;
      boolean reorganize = false;

      int i = initStep;
      while (i < YamlConfig.config.server.PLAYERNPC_AREA_STEPS) {
         int leftPx = mapArea.x + YamlConfig.config.server.PLAYERNPC_INITIAL_X, px, py = mapArea.y + YamlConfig.config.server.PLAYERNPC_INITIAL_Y;

         while (py < outY) {
            px = leftPx;

            while (px < outX) {
               Point searchPos = map.getPointBelow(new Point(px, py));
               if (searchPos != null) {
                  if (!isPlayerNpcNearby(otherPlayerNpc, searchPos, cx, cy)) {
                     if (i > initStep) {
                        map.getWorldServer().setPlayerNpcMapStep(map.getId(), i);
                     }

                     if (reorganize && YamlConfig.config.server.PLAYERNPC_ORGANIZE_AREA) {
                        return reorganizePlayerNpc(map, i, mmoList);
                     }

                     return searchPos;
                  }
               }

               px += cx;
            }

            py += cy;
         }

         reorganize = true;
         i++;

         cx = calcDx(i);
         cy = calcDy(i);
         if (YamlConfig.config.server.PLAYERNPC_ORGANIZE_AREA) {
            otherPlayerNpc = rearrangePlayerNpcPositions(map, i, mmoList.size());
         }
      }

      if (i > initStep) {
         map.getWorldServer().setPlayerNpcMapStep(map.getId(), YamlConfig.config.server.PLAYERNPC_AREA_STEPS - 1);
      }
      return null;
   }

   public static Point getNextPlayerNpcPosition(MapleMap map) {
      return getNextPlayerNpcPosition(map, map.getWorldServer().getPlayerNpcMapStep(map.getId()));
   }
}
