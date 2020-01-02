package server.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import database.provider.PlayerLifeProvider;
import database.provider.PlayerNpcProvider;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import scripting.event.EventInstanceManager;
import server.life.AbstractLoadedMapleLife;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MaplePlayerNPC;
import server.life.MaplePlayerNPCFactory;
import server.partyquest.GuardianSpawnPoint;
import database.DatabaseConnection;
import tools.StringUtil;

public class MapleMapFactory {

   private static MapleData nameData;
   private static MapleDataProvider mapSource;

   static {
      nameData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/String.wz")).getData("Map.img");
      mapSource = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/Map.wz"));
   }

   private static void loadLifeFromWz(MapleMap map, MapleData mapData) {
      for (MapleData life : mapData.getChildByPath("life")) {
         life.getName();
         String id = MapleDataTool.getString(life.getChildByPath("id"));
         String type = MapleDataTool.getString(life.getChildByPath("type"));
         int team = MapleDataTool.getInt("team", life, -1);
         if (map.isCPQMap2() && type.equals("m")) {
            if ((Integer.parseInt(life.getName()) % 2) == 0) {
               team = 0;
            } else {
               team = 1;
            }
         }
         int cy = MapleDataTool.getInt(life.getChildByPath("cy"));
         MapleData dF = life.getChildByPath("f");
         int f = (dF != null) ? MapleDataTool.getInt(dF) : 0;
         int fh = MapleDataTool.getInt(life.getChildByPath("fh"));
         int rx0 = MapleDataTool.getInt(life.getChildByPath("rx0"));
         int rx1 = MapleDataTool.getInt(life.getChildByPath("rx1"));
         int x = MapleDataTool.getInt(life.getChildByPath("x"));
         int y = MapleDataTool.getInt(life.getChildByPath("y"));
         int hide = MapleDataTool.getInt("hide", life, 0);
         int mobTime = MapleDataTool.getInt("mobTime", life, 0);

         loadLifeRaw(map, Integer.parseInt(id), type, cy, f, fh, rx0, rx1, x, y, hide, mobTime, team);
      }
   }

   private static void loadLifeFromDb(MapleMap map) {
      DatabaseConnection.getInstance().withConnection(connection ->
            PlayerLifeProvider.getInstance().getForMapAndWorld(connection, map.getId(), map.getWorld())
                  .forEach(playerLifeData -> loadLifeRaw(map, playerLifeData.lifeId(), playerLifeData.theType(),
                        playerLifeData.cy(),
                        playerLifeData.f(), playerLifeData.fh(), playerLifeData.rx0(), playerLifeData.rx1(),
                        playerLifeData.x(), playerLifeData.y(), playerLifeData.hide(),
                        playerLifeData.mobTime(), playerLifeData.team())
                  ));
   }

   private static void loadLifeRaw(MapleMap map, int id, String type, int cy, int f, int fh, int rx0, int rx1, int x, int y, int hide, int mobTime, int team) {
      AbstractLoadedMapleLife myLife = loadLife(id, type, cy, f, fh, rx0, rx1, x, y, hide);
      if (myLife instanceof MapleMonster) {
         MapleMonster monster = (MapleMonster) myLife;

         if (mobTime == -1) { //does not respawn, force spawn once
            map.spawnMonster(monster);
         } else {
            map.addMonsterSpawn(monster, mobTime, team);
         }

         //should the map be reset, use allMonsterSpawn list of monsters to spawn them again
         map.addAllMonsterSpawn(monster, mobTime, team);
      } else {
         map.addMapObject(myLife);
      }
   }

   public static MapleMap loadMapFromWz(int mapId, int world, int channel, EventInstanceManager event) {
      MapleMap map;

      String mapName = getMapName(mapId);
      MapleData mapData = mapSource.getData(mapName);
      MapleData infoData = mapData.getChildByPath("info");

      String link = MapleDataTool.getString(infoData.getChildByPath("link"), "");
      if (!link.equals("")) { //nexon made hundreds of dojo maps so to reduce the size they added links.
         mapName = getMapName(Integer.parseInt(link));
         mapData = mapSource.getData(mapName);
      }
      float monsterRate = 0;
      MapleData mobRate = infoData.getChildByPath("mobRate");
      if (mobRate != null) {
         monsterRate = (Float) mobRate.getData();
      }
      map = new MapleMap(mapId, world, channel, MapleDataTool.getInt("returnMap", infoData), monsterRate);
      map.setEventInstance(event);

      String onFirstEnter = MapleDataTool.getString(infoData.getChildByPath("onFirstUserEnter"), String.valueOf(mapId));
      map.setOnFirstUserEnter(onFirstEnter.equals("") ? String.valueOf(mapId) : onFirstEnter);

      String onEnter = MapleDataTool.getString(infoData.getChildByPath("onUserEnter"), String.valueOf(mapId));
      map.setOnUserEnter(onEnter.equals("") ? String.valueOf(mapId) : onEnter);

      map.setFieldLimit(MapleDataTool.getInt(infoData.getChildByPath("fieldLimit"), 0));
      map.setMobInterval((short) MapleDataTool.getInt(infoData.getChildByPath("createMobInterval"), 5000));
      MaplePortalFactory portalFactory = new MaplePortalFactory();
      for (MapleData portal : mapData.getChildByPath("portal")) {
         map.addPortal(portalFactory.makePortal(MapleDataTool.getInt(portal.getChildByPath("pt")), portal));
      }
      MapleData timeMob = infoData.getChildByPath("timeMob");
      if (timeMob != null) {
         map.setTimeMob(MapleDataTool.getInt(timeMob.getChildByPath("id")), MapleDataTool.getString(timeMob.getChildByPath("message")));
      }

      int[] bounds = new int[4];
      bounds[0] = MapleDataTool.getInt(infoData.getChildByPath("VRTop"));
      bounds[1] = MapleDataTool.getInt(infoData.getChildByPath("VRBottom"));

      if (bounds[0] == bounds[1]) {    // old-style baked map
         MapleData miniMap = mapData.getChildByPath("miniMap");
         if (miniMap != null) {
            bounds[0] = MapleDataTool.getInt(miniMap.getChildByPath("centerX")) * -1;
            bounds[1] = MapleDataTool.getInt(miniMap.getChildByPath("centerY")) * -1;
            bounds[2] = MapleDataTool.getInt(miniMap.getChildByPath("height"));
            bounds[3] = MapleDataTool.getInt(miniMap.getChildByPath("width"));

            map.setMapPointBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
         } else {
            int dist = (1 << 18);
            map.setMapPointBounds(-dist / 2, -dist / 2, dist, dist);
         }
      } else {
         bounds[2] = MapleDataTool.getInt(infoData.getChildByPath("VRLeft"));
         bounds[3] = MapleDataTool.getInt(infoData.getChildByPath("VRRight"));

         map.setMapLineBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
      }

      List<MapleFoothold> allFootholds = new LinkedList<>();
      Point lBound = new Point();
      Point uBound = new Point();
      for (MapleData footRoot : mapData.getChildByPath("foothold")) {
         for (MapleData footCat : footRoot) {
            for (MapleData footHold : footCat) {
               int x1 = MapleDataTool.getInt(footHold.getChildByPath("x1"));
               int y1 = MapleDataTool.getInt(footHold.getChildByPath("y1"));
               int x2 = MapleDataTool.getInt(footHold.getChildByPath("x2"));
               int y2 = MapleDataTool.getInt(footHold.getChildByPath("y2"));
               MapleFoothold fh = new MapleFoothold(new Point(x1, y1), new Point(x2, y2), Integer.parseInt(footHold.getName()));
               if (fh.firstX() < lBound.x) {
                  lBound.x = fh.firstX();
               }
               if (fh.secondX() > uBound.x) {
                  uBound.x = fh.secondX();
               }
               if (fh.firstY() < lBound.y) {
                  lBound.y = fh.firstY();
               }
               if (fh.secondY() > uBound.y) {
                  uBound.y = fh.secondY();
               }
               allFootholds.add(fh);
            }
         }
      }
      MapleFootholdTree fTree = new MapleFootholdTree(lBound, uBound);
      for (MapleFoothold fh : allFootholds) {
         fTree.insert(fh);
      }
      map.setFootholds(fTree);
      if (mapData.getChildByPath("area") != null) {
         for (MapleData area : mapData.getChildByPath("area")) {
            int x1 = MapleDataTool.getInt(area.getChildByPath("x1"));
            int y1 = MapleDataTool.getInt(area.getChildByPath("y1"));
            int x2 = MapleDataTool.getInt(area.getChildByPath("x2"));
            int y2 = MapleDataTool.getInt(area.getChildByPath("y2"));
            map.addMapleArea(new Rectangle(x1, y1, (x2 - x1), (y2 - y1)));
         }
      }
      if (mapData.getChildByPath("seat") != null) {
         int seats = mapData.getChildByPath("seat").getChildren().size();
         map.setSeats(seats);
      }
      if (event == null) {
         DatabaseConnection.getInstance().withConnection(connection -> PlayerNpcProvider.getInstance().getForMapAndWorld(connection, mapId, world).forEach(map::addPlayerNPCMapObject));
         List<MaplePlayerNPC> developerNpcList = MaplePlayerNPCFactory.getDeveloperNpcListFromMapId(mapId);
         if (developerNpcList != null) {
            for (MaplePlayerNPC developerNpc : developerNpcList) {
               map.addPlayerNPCMapObject(developerNpc);
            }
         }
      }

      loadLifeFromWz(map, mapData);
      loadLifeFromDb(map);

      if (map.isCPQMap()) {
         MapleData mcData = mapData.getChildByPath("monsterCarnival");
         if (mcData != null) {
            map.setDeathCP(MapleDataTool.getIntConvert("deathCP", mcData, 0));
            map.setMaxMobs(MapleDataTool.getIntConvert("mobGenMax", mcData, 20));
            map.setTimeDefault(MapleDataTool.getIntConvert("timeDefault", mcData, 0));
            map.setTimeExpand(MapleDataTool.getIntConvert("timeExpand", mcData, 0));
            map.setMaxReactors(MapleDataTool.getIntConvert("guardianGenMax", mcData, 16));
            MapleData guardianGenData = mcData.getChildByPath("guardianGenPos");
            for (MapleData node : guardianGenData.getChildren()) {
               GuardianSpawnPoint pt = new GuardianSpawnPoint(new Point(MapleDataTool.getIntConvert("x", node), MapleDataTool.getIntConvert("y", node)));
               pt.team_$eq(MapleDataTool.getIntConvert("team", node, -1));
               pt.taken_$eq(false);
               map.addGuardianSpawnPoint(pt);
            }
            if (mcData.getChildByPath("skill") != null) {
               for (MapleData area : mcData.getChildByPath("skill")) {
                  map.addSkillId(MapleDataTool.getInt(area));
               }
            }

            if (mcData.getChildByPath("mob") != null) {
               for (MapleData area : mcData.getChildByPath("mob")) {
                  map.addMobSpawn(MapleDataTool.getInt(area.getChildByPath("id")), MapleDataTool.getInt(area.getChildByPath("spendCP")));
               }
            }
         }

      }

      if (mapData.getChildByPath("reactor") != null) {
         for (MapleData reactor : mapData.getChildByPath("reactor")) {
            String id = MapleDataTool.getString(reactor.getChildByPath("id"));
            if (id != null) {
               MapleReactor newReactor = loadReactor(reactor, id, (byte) MapleDataTool.getInt(reactor.getChildByPath("f"), 0));
               map.spawnReactor(newReactor);
            }
         }
      }
      try {
         map.setMapName(loadPlaceName(mapId));
         map.setStreetName(loadStreetName(mapId));
      } catch (Exception e) {
         if (mapId / 1000 != 1020) {     // explorer job introduction scenes
            e.printStackTrace();
            System.err.println("Not found map " + mapId);
         }

         map.setMapName("");
         map.setStreetName("");
      }

      map.setClock(mapData.getChildByPath("clock") != null);
      map.setEverLast(MapleDataTool.getIntConvert("everlast", infoData, 0) != 0);
      map.setTown(MapleDataTool.getIntConvert("town", infoData, 0) != 0);
      map.setHPDec(MapleDataTool.getIntConvert("decHP", infoData, 0));
      map.setHPDecProtect(MapleDataTool.getIntConvert("protectItem", infoData, 0));
      map.setForcedReturnMap(MapleDataTool.getInt(infoData.getChildByPath("forcedReturn"), 999999999));
      map.setBoat(mapData.getChildByPath("shipObj") != null);
      map.setTimeLimit(MapleDataTool.getIntConvert("timeLimit", infoData, -1));
      map.setFieldType(MapleDataTool.getIntConvert("fieldType", infoData, 0));
      map.setMobCapacity(MapleDataTool.getIntConvert("fixedMobCapacity", infoData, 500));//Is there a map that contains more than 500 mobs?

      MapleData recData = infoData.getChildByPath("recovery");
      if (recData != null) {
         map.setRecovery(MapleDataTool.getFloat(recData));
      }

      HashMap<Integer, Integer> backTypes = new HashMap<>();
      try {
         for (MapleData layer : mapData.getChildByPath("back")) {
            int layerNum = Integer.parseInt(layer.getName());
            int btype = MapleDataTool.getInt(layer.getChildByPath("type"), 0);

            backTypes.put(layerNum, btype);
         }
      } catch (Exception e) {
         e.printStackTrace();
         // swallow cause I'm cool
      }

      map.setBackgroundTypes(backTypes);
      map.generateMapDropRangeCache();

      return map;
   }

   private static AbstractLoadedMapleLife loadLife(int id, String type, int cy, int f, int fh, int rx0, int rx1, int x, int y, int hide) {
      AbstractLoadedMapleLife myLife = MapleLifeFactory.getLife(id, type);
      myLife.cy_$eq(cy);
      myLife.f_$eq(f);
      myLife.fh_$eq(fh);
      myLife.rx0_$eq(rx0);
      myLife.rx1_$eq(rx1);
      myLife.position_$eq(new Point(x, y));
      if (hide == 1) {
         myLife.hide_$eq(true);
      }
      return myLife;
   }

   private static MapleReactor loadReactor(MapleData reactor, String id, final byte FacingDirection) {
      MapleReactor myReactor = new MapleReactor(MapleReactorFactory.getReactor(Integer.parseInt(id)), Integer.parseInt(id));
      int x = MapleDataTool.getInt(reactor.getChildByPath("x"));
      int y = MapleDataTool.getInt(reactor.getChildByPath("y"));
      myReactor.setFacingDirection(FacingDirection);
      myReactor.position_$eq(new Point(x, y));
      myReactor.setDelay(MapleDataTool.getInt(reactor.getChildByPath("reactorTime")) * 1000);
      myReactor.setName(MapleDataTool.getString(reactor.getChildByPath("name"), ""));
      myReactor.resetReactorActions(0);
      return myReactor;
   }

   private static String getMapName(int mapId) {
      String mapName = StringUtil.getLeftPaddedStr(Integer.toString(mapId), '0', 9);
      int area = mapId / 100000000;
      mapName = "Map/Map" + area + "/" + mapName + ".img";
      return mapName;
   }

   private static String getMapStringName(int mapId) {
      StringBuilder builder = new StringBuilder();
      if (mapId < 100000000) {
         builder.append("maple");
      } else if (mapId >= 100000000 && mapId < 200000000) {
         builder.append("victoria");
      } else if (mapId >= 200000000 && mapId < 300000000) {
         builder.append("ossyria");
      } else if (mapId >= 300000000 && mapId < 400000000) {
         builder.append("elin");
      } else if (mapId >= 540000000 && mapId < 560000000) {
         builder.append("singapore");
      } else if (mapId >= 600000000 && mapId < 620000000) {
         builder.append("MasteriaGL");
      } else if (mapId >= 677000000 && mapId < 677100000) {
         builder.append("Episode1GL");
      } else if (mapId >= 670000000 && mapId < 682000000) {
         if ((mapId >= 674030000 && mapId < 674040000) || (mapId >= 680100000 && mapId < 680200000)) {
            builder.append("etc");
         } else {
            builder.append("weddingGL");
         }
      } else if (mapId >= 682000000 && mapId < 683000000) {
         builder.append("HalloweenGL");
      } else if (mapId >= 683000000 && mapId < 684000000) {
         builder.append("event");
      } else if (mapId >= 800000000 && mapId < 900000000) {
         if ((mapId >= 889100000 && mapId < 889200000)) {
            builder.append("etc");
         } else {
            builder.append("jp");
         }
      } else {
         builder.append("etc");
      }
      builder.append("/").append(mapId);
      return builder.toString();
   }

   public static String loadPlaceName(int mapId) {
      try {
         return MapleDataTool.getString("mapName", nameData.getChildByPath(getMapStringName(mapId)), "");
      } catch (Exception e) {
         return "";
      }
   }

   public static String loadStreetName(int mapId) {
      try {
         return MapleDataTool.getString("streetName", nameData.getChildByPath(getMapStringName(mapId)), "");
      } catch (Exception e) {
         return "";
      }
   }
}
