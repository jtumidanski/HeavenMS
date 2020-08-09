package server.life;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.EntityManager;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.MapleInventoryType;
import config.YamlConfig;
import constants.game.GameConstants;
import database.DatabaseConnection;
import database.administrator.PlayerNpcAdministrator;
import database.provider.PlayerNpcProvider;
import net.server.Server;
import net.server.channel.Channel;
import net.server.world.World;
import server.life.positioner.MaplePlayerNPCPodium;
import server.life.positioner.MaplePlayerNPCPositioner;
import server.maps.AbstractMapleMapObject;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.MasterBroadcaster;
import tools.Pair;
import tools.packet.character.npc.GetPlayerNPC;
import tools.packet.character.npc.RemovePlayerNPC;
import tools.packet.spawn.RemoveNPCController;
import tools.packet.spawn.SpawnPlayerNPC;

public class MaplePlayerNPC extends AbstractMapleMapObject {
   private static final Map<Byte, List<Integer>> availablePlayerNpcScriptIds = new HashMap<>();
   private static final AtomicInteger runningOverallRank = new AtomicInteger();
   private static final List<AtomicInteger> runningWorldRank = new ArrayList<>();
   private static final Map<Pair<Integer, Integer>, AtomicInteger> runningWorldJobRank = new HashMap<>();

   static {
      getRunningMetadata();
   }

   private Map<Short, Integer> equips = new HashMap<>();
   private int scriptId, face, hair, gender, job;
   private byte skin;
   private String name;
   private int dir, FH, RX0, RX1, CY;
   private int worldRank, overallRank, worldJobRank, overallJobRank;

   public MaplePlayerNPC(String name, int scriptId, int face, int hair, int gender, byte skin, Map<Short, Integer> equips, int dir, int FH, int RX0, int RX1, int CX, int CY, int oid) {
      this.equips = equips;
      this.scriptId = scriptId;
      this.face = face;
      this.hair = hair;
      this.gender = gender;
      this.skin = skin;
      this.name = name;
      this.dir = dir;
      this.FH = FH;
      this.RX0 = RX0;
      this.RX1 = RX1;
      this.CY = CY;
      this.job = 7777;    // supposed to be developer

      setPosition(new Point(CX, CY));
      setObjectId(oid);
   }

   public MaplePlayerNPC(int id, int x, int cy, String name, int hair, int face, int skin, int gender, int dir, int fh, int rx0,
                         int rx1, int scriptId, int worldRank, int overallRank, int worldJobRank, int overallJobRank, int job) {
      setObjectId(id);
      this.CY = cy;
      this.name = name;
      this.hair = hair;
      this.face = face;
      this.skin = (byte) skin;
      this.gender = gender;
      this.dir = dir;
      this.FH = fh;
      this.RX0 = rx0;
      this.RX1 = rx1;
      this.scriptId = scriptId;
      this.worldRank = worldRank;
      this.overallRank = overallRank;
      this.worldJobRank = worldJobRank;
      this.overallJobRank = overallJobRank;
      this.job = job;
      setPosition(new Point(x, cy));
   }

   private static void getRunningMetadata() {
      DatabaseConnection.getInstance().withConnection(connection -> {
         getRunningOverallRanks(connection);
         getRunningWorldRanks(connection);
         getRunningWorldJobRanks(connection);
      });
   }

   private static void getRunningOverallRanks(EntityManager entityManager) {
      runningOverallRank.set(PlayerNpcProvider.getInstance().getMaxRank(entityManager) + 1);
   }

   private static void getRunningWorldRanks(EntityManager entityManager) {
      int numWorlds = Server.getInstance().getWorldsSize();
      for (int i = 0; i < numWorlds; i++) {
         runningWorldRank.add(new AtomicInteger(1));
      }


      PlayerNpcProvider.getInstance().getMaxRankByWorld(entityManager).stream()
            .filter(result -> result.getLeft() < numWorlds)
            .forEach(result -> runningWorldRank.get(result.getLeft()).set(result.getRight() + 1));
   }

   private static void getRunningWorldJobRanks(EntityManager entityManager) {
      PlayerNpcProvider.getInstance().getMaxRankByJobAndWorld(entityManager).forEach(result -> runningWorldJobRank.put(result.getLeft(), result.getRight()));
   }

   private static int getAndIncrementRunningWorldJobRanks(int world, int job) {
      AtomicInteger wjr = runningWorldJobRank.get(new Pair<>(world, job));
      if (wjr == null) {
         wjr = new AtomicInteger(1);
         runningWorldJobRank.put(new Pair<>(world, job), wjr);
      }

      return wjr.getAndIncrement();
   }

   public static boolean canSpawnPlayerNpc(String name, int mapId) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> PlayerNpcProvider.getInstance().getLikeNameAndMap(connection, name, mapId).stream().findFirst().isPresent()).orElse(false);
   }

   private static void fetchAvailableScriptIdsFromDb(byte branch, List<Integer> list) {
      int branchLen = (branch < 26) ? 100 : 400;
      int branchSid = 9900000 + (branch * 100);
      int nextBranchSid = branchSid + branchLen;

      Set<Integer> usedScriptIds = DatabaseConnection.getInstance().withConnectionResult(connection ->
            new HashSet<>(PlayerNpcProvider.getInstance().getAvailableScripts(connection, branchSid, nextBranchSid)))
            .orElseThrow();

      List<Integer> availables = new ArrayList<>(20);
      int j = 0;
      for (int i = branchSid; i < nextBranchSid; i++) {
         if (!usedScriptIds.contains(i)) {
            if (MaplePlayerNPCFactory.isExistentScriptId(i)) {
               availables.add(i);
               j++;

               if (j == 20) {
                  break;
               }
            } else {
               break;  // after this point no more script ids expected...
            }
         }
      }

      for (int i = availables.size() - 1; i >= 0; i--) {
         list.add(availables.get(i));
      }
   }

   private static int getNextScriptId(byte branch) {
      List<Integer> availablesBranch = availablePlayerNpcScriptIds.computeIfAbsent(branch, k -> new ArrayList<>(20));
      if (availablesBranch.isEmpty()) {
         fetchAvailableScriptIdsFromDb(branch, availablesBranch);

         if (availablesBranch.isEmpty()) {
            return -1;
         }
      }

      return availablesBranch.remove(availablesBranch.size() - 1);
   }

   private static MaplePlayerNPC createPlayerNPCInternal(MapleMap map, Point pos, MapleCharacter chr) {
      int mapId = map.getId();

      if (!canSpawnPlayerNpc(chr.getName(), mapId)) {
         return null;
      }

      byte branch = GameConstants.getHallOfFameBranch(chr.getJob(), mapId);

      int scriptId = getNextScriptId(branch);
      if (scriptId == -1) {
         return null;
      }

      if (pos == null) {
         if (GameConstants.isPodiumHallOfFameMap(map.getId())) {
            pos = MaplePlayerNPCPodium.getNextPlayerNpcPosition(map);
         } else {
            pos = MaplePlayerNPCPositioner.getNextPlayerNpcPosition(map);
         }

         if (pos == null) {
            return null;
         }
      }

      if (YamlConfig.config.server.USE_DEBUG) {
         System.out.println("GOT SID " + scriptId + " POS " + pos);
      }

      int jobId = (chr.getJob().getId() / 100) * 100;

      final Point actualPosition = pos;
      return DatabaseConnection.getInstance().withConnectionResult(connection -> {
         Optional<MaplePlayerNPC> playerNPC = PlayerNpcProvider.getInstance().getByScriptId(connection, scriptId);
         if (playerNPC.isPresent()) {
            return playerNPC.get();
         } else {   // creates new player npc if script id doesn't exist
            int worldRank = runningWorldRank.get(chr.getWorld()).getAndIncrement();
            int overallRank = runningOverallRank.getAndIncrement();
            int worldJobRank = getAndIncrementRunningWorldJobRanks(chr.getWorld(), jobId);
            int npcId = PlayerNpcAdministrator.getInstance().create(connection, chr, map, actualPosition, scriptId, worldRank, overallRank, worldJobRank);
            PlayerNpcAdministrator.getInstance().createEquips(connection, npcId, chr.getInventory(MapleInventoryType.EQUIPPED));
            return PlayerNpcProvider.getInstance().getById(connection, npcId).orElseThrow();
         }
      }).orElseThrow();
   }

   private static List<Integer> removePlayerNPCInternal(MapleMap map, MapleCharacter chr) {
      List<Integer> mapIds = new LinkedList<>();
      mapIds.add(chr.getWorld());
      Set<Integer> mapIdsToUpdate = new HashSet<>();

      DatabaseConnection.getInstance().withConnection(connection ->
            PlayerNpcProvider.getInstance().getLikeNameAndMap(connection, chr.getName(), map.getId())
                  .forEach(pair -> {
                     mapIdsToUpdate.add(pair.getRight());
                     PlayerNpcAdministrator.getInstance().deleteById(connection, pair.getLeft());
                     PlayerNpcAdministrator.getInstance().deleteEquipById(connection, pair.getLeft());
                  }));

      mapIds.addAll(mapIdsToUpdate);
      return mapIds;
   }

   private static synchronized Pair<MaplePlayerNPC, List<Integer>> processPlayerNPCInternal(MapleMap map, Point pos, MapleCharacter chr, boolean create) {
      if (create) {
         return new Pair<>(createPlayerNPCInternal(map, pos, chr), null);
      } else {
         return new Pair<>(null, removePlayerNPCInternal(map, chr));
      }
   }

   public static boolean spawnPlayerNPC(int mapId, MapleCharacter chr) {
      return spawnPlayerNPC(mapId, null, chr);
   }

   public static boolean spawnPlayerNPC(int mapId, Point pos, MapleCharacter chr) {
      if (chr == null) {
         return false;
      }

      MaplePlayerNPC pn = processPlayerNPCInternal(chr.getClient().getChannelServer().getMapFactory().getMap(mapId), pos, chr, true).getLeft();
      if (pn != null) {
         for (Channel channel : Server.getInstance().getChannelsFromWorld(chr.getWorld())) {
            MapleMap m = channel.getMapFactory().getMap(mapId);

            m.addPlayerNPCMapObject(pn);
            MasterBroadcaster.getInstance().sendToAllInMap(m, new SpawnPlayerNPC(pn));
            MasterBroadcaster.getInstance().sendToAllInMap(m, new GetPlayerNPC(pn));
         }

         return true;
      } else {
         return false;
      }
   }

   private static MaplePlayerNPC getPlayerNPCFromWorldMap(String name, int worldId, int map) {
      World world = Server.getInstance().getWorld(worldId);
      for (MapleMapObject mapObject : world.getChannel(1).getMapFactory().getMap(map).getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.PLAYER_NPC))) {
         MaplePlayerNPC pn = (MaplePlayerNPC) mapObject;

         if (name.contentEquals(pn.getName()) && pn.getScriptId() < 9977777) {
            return pn;
         }
      }

      return null;
   }

   public static void removePlayerNPC(MapleCharacter chr) {
      if (chr == null) {
         return;
      }

      List<Integer> updateMapIds = processPlayerNPCInternal(null, null, chr, false).getRight();
      int worldId = updateMapIds.remove(0);

      for (Integer mapId : updateMapIds) {
         MaplePlayerNPC pn = getPlayerNPCFromWorldMap(chr.getName(), worldId, mapId);

         if (pn != null) {
            for (Channel channel : Server.getInstance().getChannelsFromWorld(worldId)) {
               MapleMap m = channel.getMapFactory().getMap(mapId);
               m.removeMapObject(pn);

               MasterBroadcaster.getInstance().sendToAllInMap(m, new RemoveNPCController(pn.objectId()));
               MasterBroadcaster.getInstance().sendToAllInMap(m, new RemovePlayerNPC(pn.objectId()));
            }
         }
      }
   }

   public static void multicastSpawnPlayerNPC(int mapId, int worldId) {
      World world = Server.getInstance().getWorld(worldId);
      if (world == null) {
         return;
      }

      MapleClient c = new MapleClient(null, null, null);  // mock client
      c.setWorld(worldId);
      c.setChannel(1);

      for (MapleCharacter mc : world.loadAndGetAllCharactersView()) {
         mc.setClient(c);
         spawnPlayerNPC(mapId, mc);
      }
   }

   public static void removeAllPlayerNPC() {
      int worldSize = Server.getInstance().getWorldsSize();
      DatabaseConnection.getInstance().withConnection(connection -> {
         PlayerNpcProvider.getInstance().getWorldMapsWithPlayerNpc(connection).stream()
               .filter(result -> result.getLeft() >= worldSize)
               .forEach(result -> {
                  for (Channel channel : Server.getInstance().getChannelsFromWorld(result.getLeft())) {
                     MapleMap m = channel.getMapFactory().getMap(result.getRight());

                     for (MapleMapObject mapObject : m.getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.PLAYER_NPC))) {
                        MaplePlayerNPC pn = (MaplePlayerNPC) mapObject;
                        m.removeMapObject(mapObject);
                        MasterBroadcaster.getInstance().sendToAllInMap(m, new RemoveNPCController(pn.objectId()));
                        MasterBroadcaster.getInstance().sendToAllInMap(m, new RemovePlayerNPC(pn.objectId()));
                     }
                  }
               });
         PlayerNpcAdministrator.getInstance().deleteAllPlayerNpc(connection);
      });
      Server.getInstance().getWorlds().parallelStream().forEach(World::resetPlayerNpcMapData);
   }

   public Map<Short, Integer> getEquips() {
      return equips;
   }

   public int getScriptId() {
      return scriptId;
   }

   public int getJob() {
      return job;
   }

   public int getDirection() {
      return dir;
   }

   public int getFH() {
      return FH;
   }

   public int getRX0() {
      return RX0;
   }

   public int getRX1() {
      return RX1;
   }

   public int getCY() {
      return CY;
   }

   public byte getSkin() {
      return skin;
   }

   public String getName() {
      return name;
   }

   public int getFace() {
      return face;
   }

   public int getHair() {
      return hair;
   }

   public int getGender() {
      return gender;
   }

   public int getWorldRank() {
      return worldRank;
   }

   public int getOverallRank() {
      return overallRank;
   }

   public int getWorldJobRank() {
      return worldJobRank;
   }

   public int getOverallJobRank() {
      return overallJobRank;
   }

   @Override
   public MapleMapObjectType type() {
      return MapleMapObjectType.PLAYER_NPC;
   }

   public void updatePlayerNPCPosition(MapleMap map, Point newPos) {
      setPosition(newPos);
      RX0 = newPos.x + 50;
      RX1 = newPos.x - 50;
      CY = newPos.y;
      FH = map.getFootholds().findBelow(newPos).id();

      DatabaseConnection.getInstance().withConnection(connection -> PlayerNpcAdministrator.getInstance().updatePosition(connection, newPos.x, CY, FH, RX0, RX1, this.objectId()));
   }
}