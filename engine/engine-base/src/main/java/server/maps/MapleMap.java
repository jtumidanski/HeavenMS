package server.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import client.AbstractMapleCharacterObject;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.autoban.AutoBanFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import config.YamlConfig;
import constants.game.GameConstants;
import constants.inventory.ItemConstants;
import net.server.Server;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReadLock;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.audit.locks.MonitoredWriteLock;
import net.server.audit.locks.factory.MonitoredReadLockFactory;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.audit.locks.factory.MonitoredWriteLockFactory;
import net.server.channel.Channel;
import net.server.coordinator.world.MapleMonsterAggroCoordinator;
import net.server.services.task.channel.FaceExpressionService;
import net.server.services.task.channel.MobMistService;
import net.server.services.task.channel.OverallService;
import net.server.services.type.ChannelServices;
import net.server.world.World;
import scripting.event.EventInstanceManager;
import scripting.map.MapScriptManager;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.TimerManager;
import server.events.gm.MapleCoconut;
import server.events.gm.MapleFitness;
import server.events.gm.MapleOla;
import server.events.gm.MapleOxQuiz;
import server.events.gm.MapleSnowball;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MapleNPC;
import server.life.MaplePlayerNPC;
import server.life.MonsterDropEntry;
import server.life.MonsterGlobalDropEntry;
import server.life.MonsterListener;
import server.life.SelfDestruction;
import server.life.SpawnPoint;
import server.maps.spawner.DoorObjectSpawnAndDestroyer;
import server.maps.spawner.KiteSpawnAndDestroyer;
import server.maps.spawner.MistSpawnAndDestroyer;
import server.maps.spawner.ReactorSpawnAndDestroyer;
import server.partyquest.GuardianSpawnPoint;
import server.partyquest.MapleCarnivalFactory;
import server.partyquest.MapleCarnivalFactory.MCSkill;
import server.partyquest.MonsterCarnival;
import server.processor.DropEntryProcessor;
import server.processor.MobSkillProcessor;
import server.processor.maps.MapleMapObjectProcessor;
import server.processor.maps.MapleMapObjectTypeProcessor;
import server.processor.maps.MapleMapProcessor;
import tools.FilePrinter;
import tools.I18nMessage;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.PointUtil;
import tools.Randomizer;
import tools.ServerNoticeType;
import tools.packet.PacketInput;
import tools.packet.buff.GiveForeignBuff;
import tools.packet.character.CharacterLook;
import tools.packet.character.box.UseChalkboard;
import tools.packet.event.CoconutScore;
import tools.packet.event.RollSnowBall;
import tools.packet.field.Boat;
import tools.packet.field.CrimsonBalrogBoat;
import tools.packet.field.effect.ChangeBackgroundEffect;
import tools.packet.field.effect.EnvironmentChange;
import tools.packet.field.effect.ForcedEquip;
import tools.packet.field.effect.ForcedStatReset;
import tools.packet.field.effect.ForcedStatSet;
import tools.packet.field.obstacle.EnvironmentMove;
import tools.packet.foreigneffect.ShowBuffEffect;
import tools.packet.item.drop.DropItemFromMapObject;
import tools.packet.item.drop.UpdateMapItemObject;
import tools.packet.monster.KillMonster;
import tools.packet.monster.carnival.MonsterCarnivalStart;
import tools.packet.reactor.TriggerReactor;
import tools.packet.remove.RemoveDragon;
import tools.packet.remove.RemoveItem;
import tools.packet.remove.RemoveNPC;
import tools.packet.remove.RemovePlayer;
import tools.packet.showitemgaininchat.ShowOwnBuffEffect;
import tools.packet.spawn.MakeMonsterReal;
import tools.packet.spawn.RemoveNPCController;
import tools.packet.spawn.ShowPet;
import tools.packet.spawn.SpawnDragon;
import tools.packet.spawn.SpawnFakeMonster;
import tools.packet.spawn.SpawnMonster;
import tools.packet.spawn.SpawnNPC;
import tools.packet.spawn.SpawnPlayer;
import tools.packet.spawn.SpawnSummon;
import tools.packet.ui.GMEffect;
import tools.packet.ui.GetClock;
import tools.packet.ui.GetClockTime;

public class MapleMap {

   private static final List<MapleMapObjectType> rangedMapObjectTypes = Arrays.asList(MapleMapObjectType.SHOP, MapleMapObjectType.ITEM, MapleMapObjectType.NPC, MapleMapObjectType.MONSTER, MapleMapObjectType.DOOR, MapleMapObjectType.SUMMON, MapleMapObjectType.REACTOR);
   private static final Map<Integer, Pair<Integer, Integer>> dropBoundsCache = new HashMap<>(100);
   // due to the nature of loadMapFromWz (synchronized), sole function that calls 'generateMapDropRangeCache', this lock remains optional.
   private static final Lock bndLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.MAP_BOUNDS, true);
   private final List<Point> takenSpawns = new LinkedList<>();
   private final List<GuardianSpawnPoint> guardianSpawns = new LinkedList<>();
   private final List<MCSkill> blueTeamBuffs = new ArrayList<>();
   private final List<MCSkill> redTeamBuffs = new ArrayList<>();
   private Map<Integer, MapleMapObject> mapObjects = new LinkedHashMap<>();
   private Set<Integer> selfDestructiveObjects = new LinkedHashSet<>();
   private Collection<SpawnPoint> monsterSpawn = Collections.synchronizedList(new LinkedList<>());
   private Collection<SpawnPoint> allMonsterSpawn = Collections.synchronizedList(new LinkedList<>());
   private AtomicInteger spawnedMonstersOnMap = new AtomicInteger(0);
   private AtomicInteger droppedItemCount = new AtomicInteger(0);
   private Collection<MapleCharacter> characters = new LinkedHashSet<>();
   private Map<Integer, Set<Integer>> mapParty = new LinkedHashMap<>();
   private Map<Integer, MaplePortal> portals = new HashMap<>();
   private Map<Integer, Integer> backgroundTypes = new HashMap<>();
   private Map<String, Integer> environment = new LinkedHashMap<>();
   private Map<MapleMapItem, Long> droppedItems = new LinkedHashMap<>();
   private LinkedList<WeakReference<MapleMapObject>> registeredDrops = new LinkedList<>();
   private Map<MobLootEntry, Long> mobLootEntries = new HashMap<>(20);
   private List<Runnable> statUpdateRunnables = new ArrayList<>(50);
   private List<Rectangle> areas = new ArrayList<>();
   private MapleFootholdTree footholds = null;
   private Pair<Integer, Integer> xLimits;  // caches the min and max x's with available footholds
   private Rectangle mapArea = new Rectangle();
   private int mapId;
   private AtomicInteger runningOid = new AtomicInteger(1000000001);
   private int returnMapId;
   private int channel, world;
   private int seats;
   private byte monsterRate;
   private boolean clock;
   private boolean boat;
   private boolean docked = false;
   private EventInstanceManager event = null;
   private String mapName;
   private String streetName;
   private MapleMapEffect mapEffect = null;
   private boolean everLast = false;
   private int forcedReturnMap = 999999999;
   private int timeLimit;
   private long mapTimer;
   private int decHP = 0;
   private float recovery = 1.0f;
   private int protectItem = 0;
   private boolean town;
   private MapleOxQuiz ox;
   private boolean isOxQuiz = false;
   private boolean dropsOn = true;
   private String onFirstUserEnter;
   private String onUserEnter;
   private int fieldType;
   private int fieldLimit = 0;
   private int mobCapacity = -1;
   private MapleMonsterAggroCoordinator aggroMonitor;   // aggroMonitor activity in sync with itemMonitor
   private ScheduledFuture<?> itemMonitor = null;
   private ScheduledFuture<?> expireItemsTask = null;
   private ScheduledFuture<?> mobSpawnLootTask = null;
   private ScheduledFuture<?> characterStatUpdateTask = null;
   private short itemMonitorTimeout;
   private Pair<Integer, String> timeMob = null;
   private short mobInterval = 5000;
   private boolean allowSummons = true; // All maps should have this true at the beginning
   private MapleCharacter mapOwner = null;
   private long mapOwnerLastActivityTime = Long.MAX_VALUE;
   // events
   private boolean eventStarted = false, isMuted = false;
   private MapleSnowball snowball0 = null;
   private MapleSnowball snowball1 = null;
   private MapleCoconut coconut;
   //CPQ
   private int maxMobs;
   private int maxReactors;
   private int deathCP;
   private int timeDefault;
   private int timeExpand;
   //locks
   private MonitoredReadLock chrRLock;
   private MonitoredWriteLock chrWLock;
   private MonitoredReadLock objectRLock;
   private MonitoredWriteLock objectWLock;
   private Lock lootLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.MAP_LOOT, true);
   private List<Integer> skillIds = new ArrayList<>();
   private List<Pair<Integer, Integer>> mobsToSpawn = new ArrayList<>();

   public MapleMap(int mapId, int world, int channel, int returnMapId, float monsterRate) {
      this.mapId = mapId;
      this.channel = channel;
      this.world = world;
      this.returnMapId = returnMapId;
      this.monsterRate = (byte) Math.ceil(monsterRate);
      if (this.monsterRate == 0) {
         this.monsterRate = 1;
      }
      final MonitoredReentrantReadWriteLock chrLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.MAP_CHARACTERS, true);
      chrRLock = MonitoredReadLockFactory.createLock(chrLock);
      chrWLock = MonitoredWriteLockFactory.createLock(chrLock);

      final MonitoredReentrantReadWriteLock objectLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.MAP_OBJS, true);
      objectRLock = MonitoredReadLockFactory.createLock(objectLock);
      objectWLock = MonitoredWriteLockFactory.createLock(objectLock);

      aggroMonitor = new MapleMonsterAggroCoordinator();
   }

   public EventInstanceManager getEventInstance() {
      return event;
   }

   public void setEventInstance(EventInstanceManager eim) {
      event = eim;
   }

   public Rectangle getMapArea() {
      return mapArea;
   }

   public int getWorld() {
      return world;
   }

   //TODO - JDT Move
   public void broadcastMessage(MapleCharacter source, PacketInput packet) {
      MasterBroadcaster.getInstance().sendToAllInMap(this, packet, false, source, chrRLock);
   }

   //TODO - JDT Move
   public void broadcastGMMessage(MapleCharacter source, PacketInput packet) {
      MasterBroadcaster.getInstance().sendToGMAndAboveInMap(this, source, packet, chrRLock);
   }

   public void toggleDrops() {
      this.dropsOn = !dropsOn;
   }

   public List<MapleMapObject> getMapObjectsInRect(Rectangle box, List<MapleMapObjectType> types) {
      objectRLock.lock();
      final List<MapleMapObject> ret = new LinkedList<>();
      try {
         mapObjects.values().stream()
               .filter(mapObject -> types.contains(mapObject.type()) && box.contains(mapObject.position()))
               .forEach(ret::add);
      } finally {
         objectRLock.unlock();
      }
      return ret;
   }

   public int getId() {
      return mapId;
   }

   public Channel getChannelServer() {
      return Server.getInstance().getWorld(world).getChannel(channel);
   }

   public World getWorldServer() {
      return Server.getInstance().getWorld(world);
   }

   public MapleMap getReturnMap() {
      if (returnMapId == 999999999) {
         return this;
      }
      return getChannelServer().getMapFactory().getMap(returnMapId);
   }

   public int getReturnMapId() {
      return returnMapId;
   }

   public MapleMap getForcedReturnMap() {
      return getChannelServer().getMapFactory().getMap(forcedReturnMap);
   }

   public void setForcedReturnMap(int map) {
      this.forcedReturnMap = map;
   }

   public int getForcedReturnId() {
      return forcedReturnMap;
   }

   public int getTimeLimit() {
      return timeLimit;
   }

   public void setTimeLimit(int timeLimit) {
      this.timeLimit = timeLimit;
   }

   public int getTimeLeft() {
      return (int) ((mapTimer - System.currentTimeMillis()) / 1000);
   }

   public void setReactorState() {
      for (MapleMapObject o : getMapObjects()) {
         if (o.type() == MapleMapObjectType.REACTOR) {
            if (((MapleReactor) o).getState() < 1) {
               MapleReactor mr = (MapleReactor) o;
               mr.lockReactor();
               try {
                  mr.resetReactorActions(1);
                  MasterBroadcaster.getInstance().sendToAllInMap(this, new TriggerReactor((MapleReactor) o, 1));
               } finally {
                  mr.unlockReactor();
               }
            }
         }
      }
   }

   public final void limitReactor(final int rid, final int num) {
      List<MapleReactor> toDestroy = new ArrayList<>();
      Map<Integer, Integer> contained = new LinkedHashMap<>();

      for (MapleMapObject obj : getReactors()) {
         MapleReactor mr = (MapleReactor) obj;
         if (contained.containsKey(mr.getId())) {
            if (contained.get(mr.getId()) >= num) {
               toDestroy.add(mr);
            } else {
               contained.put(mr.getId(), contained.get(mr.getId()) + 1);
            }
         } else {
            contained.put(mr.getId(), 1);
         }
      }

      toDestroy.forEach(reactor -> destroyReactor(reactor.objectId()));
   }

   public boolean isAllReactorState(final int reactorId, final int state) {
      return getReactors().stream()
            .map(mapObject -> (MapleReactor) mapObject)
            .noneMatch(reactor -> reactor.getId() == reactorId && reactor.getState() != state);
   }

   public int getCurrentPartyId() {
      return getCharacters().stream()
            .map(MapleCharacter::getPartyId)
            .filter(id -> id != -1)
            .findFirst()
            .orElse(-1);
   }

   public void addPlayerNPCMapObject(MaplePlayerNPC playerNpc) {
      objectWLock.lock();
      try {
         this.mapObjects.put(playerNpc.objectId(), playerNpc);
      } finally {
         objectWLock.unlock();
      }
   }

   public void addMapObject(MapleMapObject mapObject) {
      int curOID = getUsableOID();

      objectWLock.lock();
      try {
         mapObject.setObjectId(curOID);
         this.mapObjects.put(curOID, mapObject);
      } finally {
         objectWLock.unlock();
      }
   }

   public void addSelfDestructive(MapleMonster mob) {
      if (mob.getStats().selfDestruction() != null) {
         this.selfDestructiveObjects.add(mob.objectId());
      }
   }

   public boolean removeSelfDestructive(int mapObjectId) {
      return this.selfDestructiveObjects.remove(mapObjectId);
   }

   private void spawnAndAddRangedMapObject(MapleMapObject mapObject, DelayedPacketCreation packetCreator) {
      spawnAndAddRangedMapObject(mapObject, packetCreator, null);
   }

   private void spawnAndAddRangedMapObject(MapleMapObject mapObject, DelayedPacketCreation packetCreator, SpawnCondition condition) {
      List<MapleCharacter> inRangeCharacters = new LinkedList<>();
      int curOID = getUsableOID();

      chrRLock.lock();
      objectWLock.lock();
      try {
         mapObject.setObjectId(curOID);
         this.mapObjects.put(curOID, mapObject);
         characters.stream()
               .filter(character -> condition == null || condition.canSpawn(character))
               .filter(character -> character.position().distanceSq(mapObject.position()) <= MapleMapProcessor.getInstance().getRangedDistance())
               .forEach(character -> {
                  inRangeCharacters.add(character);
                  character.addVisibleMapObject(mapObject);
               });
      } finally {
         objectWLock.unlock();
         chrRLock.unlock();
      }

      for (MapleCharacter chr : inRangeCharacters) {
         packetCreator.sendPackets(chr.getClient());
      }
   }

   private void spawnRangedMapObject(MapleMapObject mapObject, DelayedPacketCreation packetCreator, SpawnCondition condition) {
      List<MapleCharacter> inRangeCharacters = new LinkedList<>();

      chrRLock.lock();
      try {
         int curOID = getUsableOID();
         mapObject.setObjectId(curOID);

         characters.stream()
               .filter(character -> condition == null || condition.canSpawn(character))
               .filter(character -> character.position().distanceSq(mapObject.position()) <= MapleMapProcessor.getInstance().getRangedDistance())
               .forEach(character -> {
                  inRangeCharacters.add(character);
                  character.addVisibleMapObject(mapObject);
               });
      } finally {
         chrRLock.unlock();
      }

      inRangeCharacters.forEach(character -> packetCreator.sendPackets(character.getClient()));
   }

   private int getUsableOID() {
      objectRLock.lock();
      try {
         int curOid;

         // clashes with player npc on curOid >= 2147000000, developer npc uses >= 2147483000
         do {
            if ((curOid = runningOid.incrementAndGet()) >= 2147000000) {
               runningOid.set(curOid = 1000000001);
            }
         } while (mapObjects.containsKey(curOid));

         return curOid;
      } finally {
         objectRLock.unlock();
      }
   }

   public void removeMapObject(int num) {
      objectWLock.lock();
      try {
         this.mapObjects.remove(num);
      } finally {
         objectWLock.unlock();
      }
   }

   public void removeMapObject(final MapleMapObject obj) {
      removeMapObject(obj.objectId());
   }

   private Point calcPointBelow(Point initial) {
      MapleFoothold fh = footholds.findBelow(initial);
      if (fh == null) {
         return null;
      }
      int dropY = fh.firstPoint().y;
      if (!fh.isWall() && fh.firstPoint().y != fh.secondPoint().y) {
         double s1 = Math.abs(fh.secondPoint().y - fh.firstPoint().y);
         double s2 = Math.abs(fh.secondPoint().x - fh.firstPoint().x);
         double s5 = Math.cos(Math.atan(s2 / s1)) * (Math.abs(initial.x - fh.firstPoint().x) / Math.cos(Math.atan(s1 / s2)));
         if (fh.secondPoint().y < fh.firstPoint().y) {
            dropY = fh.firstPoint().y - (int) s5;
         } else {
            dropY = fh.firstPoint().y + (int) s5;
         }
      }
      return new Point(initial.x, dropY);
   }

   public void generateMapDropRangeCache() {
      bndLock.lock();
      try {
         Pair<Integer, Integer> bounds = dropBoundsCache.get(mapId);

         if (bounds != null) {
            xLimits = bounds;
         } else {
            // assuming MINI MAP always have an equal-greater picture representation of the map area (players won't walk beyond the area known by the mini map).
            Point lp = new Point(mapArea.x, mapArea.y), rp = new Point(mapArea.x + mapArea.width, mapArea.y), fallback = new Point(mapArea.x + (mapArea.width / 2), mapArea.y);

            lp = bSearchDropPos(lp, fallback);  // approximated leftmost fh node position
            rp = bSearchDropPos(rp, fallback);  // approximated rightmost fh node position

            xLimits = new Pair<>(lp.x + 14, rp.x - 14);
            dropBoundsCache.put(mapId, xLimits);
         }
      } finally {
         bndLock.unlock();
      }
   }

   private Point bSearchDropPos(Point initial, Point fallback) {
      Point res, dropPos = null;

      int awayX = fallback.x;
      int homeX = initial.x;

      int y = initial.y - 85;

      do {
         int distanceX = awayX - homeX;
         int dx = distanceX / 2;

         int searchX = homeX + dx;
         if ((res = calcPointBelow(new Point(searchX, y))) != null) {
            awayX = searchX;
            dropPos = res;
         } else {
            homeX = searchX;
         }
      } while (Math.abs(homeX - awayX) > 5);

      return (dropPos != null) ? dropPos : fallback;
   }

   public Point calcDropPos(Point initial, Point fallback) {
      if (initial.x < xLimits.left) {
         initial.x = xLimits.left;
      } else if (initial.x > xLimits.right) {
         initial.x = xLimits.right;
      }

      Point ret = calcPointBelow(new Point(initial.x, initial.y - 85));
      if (ret == null) {
         ret = bSearchDropPos(initial, fallback);
      }

      if (!mapArea.contains(ret)) { // found drop pos outside the map :O
         return fallback;
      }

      return ret;
   }

   public boolean canDeployDoor(Point pos) {
      Point toStep = calcPointBelow(pos);
      return toStep != null && toStep.distance(pos) <= 42;
   }

   public Pair<String, Integer> getDoorPositionStatus(Point pos) {
      MaplePortal portal = findClosestPlayerSpawnPoint(pos);

      double angle = PointUtil.getAngle(portal.getPosition(), pos);
      double distn = pos.distanceSq(portal.getPosition());

      if (distn <= 777777.7) {
         return null;
      }

      distn = Math.sqrt(distn);
      return new Pair<>(PointUtil.getRoundedCoordinate(angle), (int) distn);
   }

   private byte dropItemsFromMonsterOnMap(List<MonsterDropEntry> dropEntry, Point pos, byte d, int chRate, byte dropType, int mobPosition, MapleCharacter chr, MapleMonster mob) {
      if (dropEntry.isEmpty()) {
         return d;
      }

      Collections.shuffle(dropEntry);

      Item itemDrop;
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      for (final MonsterDropEntry de : dropEntry) {
         float cardRate = chr.getCardRate(de.itemId());
         int dropChance = (int) Math.min((float) de.chance() * chRate * cardRate, Integer.MAX_VALUE);

         if (Randomizer.nextInt(999999) < dropChance) {
            if (dropType == 3) {
               pos.x = mobPosition + ((d % 2 == 0) ? (40 * ((d + 1) / 2)) : -(40 * (d / 2)));
            } else {
               pos.x = mobPosition + ((d % 2 == 0) ? (25 * ((d + 1) / 2)) : -(25 * (d / 2)));
            }
            if (de.itemId() == 0) { // meso
               int mesos = Randomizer.nextInt(de.maximum() - de.minimum()) + de.minimum();

               if (mesos > 0) {
                  if (chr.getBuffedValue(MapleBuffStat.MESOUP) != null) {
                     mesos = (int) (mesos * chr.getBuffedValue(MapleBuffStat.MESOUP).doubleValue() / 100.0);
                  }
                  mesos = mesos * chr.getMesoRate();
                  if (mesos <= 0) {
                     mesos = Integer.MAX_VALUE;
                  }

                  spawnMesoDrop(mesos, calcDropPos(pos, mob.position()), mob, chr, false, dropType);
               }
            } else {
               if (ItemConstants.getInventoryType(de.itemId()) == MapleInventoryType.EQUIP) {
                  itemDrop = Equip.newBuilder(ii.getEquipById(de.itemId())).randomizeStats().build();
               } else {
                  itemDrop = Item.newBuilder(de.itemId())
                        .setPosition((short) 0)
                        .setQuantity((short) (de.maximum() != 1 ? Randomizer.nextInt(de.maximum() - de.minimum()) + de.minimum() : 1))
                        .build();
               }
               spawnDrop(itemDrop, calcDropPos(pos, mob.position()), mob, chr, dropType, (short) de.questId());
            }
            d++;
         }
      }

      return d;
   }

   private byte dropGlobalItemsFromMonsterOnMap(List<MonsterGlobalDropEntry> globalEntry, Point pos, byte d, byte dropType, int mobPosition, MapleCharacter chr, MapleMonster mob) {
      Collections.shuffle(globalEntry);

      Item itemDrop;
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      for (final MonsterGlobalDropEntry de : globalEntry) {
         if (Randomizer.nextInt(999999) < de.chance()) {
            if (dropType == 3) {
               pos.x = mobPosition + (d % 2 == 0 ? (40 * (d + 1) / 2) : -(40 * (d / 2)));
            } else {
               pos.x = mobPosition + ((d % 2 == 0) ? (25 * (d + 1) / 2) : -(25 * (d / 2)));
            }
            if (de.itemId() != 0) {
               if (ItemConstants.getInventoryType(de.itemId()) == MapleInventoryType.EQUIP) {
                  itemDrop = Equip.newBuilder(ii.getEquipById(de.itemId())).randomizeStats().build();
               } else {
                  itemDrop = Item.newBuilder(de.itemId())
                        .setPosition((short) 0)
                        .setQuantity((short) (de.maximum() != 1 ? Randomizer.nextInt(de.maximum() - de.minimum()) + de.minimum() : 1))
                        .build();
               }
               spawnDrop(itemDrop, calcDropPos(pos, mob.position()), mob, chr, dropType, (short) de.questId());
               d++;
            }
         }
      }

      return d;
   }

   private void dropFromMonster(final MapleCharacter chr, final MapleMonster mob, final boolean useBaseRate) {
      if (mob.dropsDisabled() || !dropsOn) {
         return;
      }

      final byte dropType = (byte) (mob.getStats().isExplosiveReward() ? 3 : mob.getStats().isFFALoot() ? 2 : chr.getParty().isPresent() ? 1 : 0);
      final int mobPosition = mob.position().x;
      int chRate = !mob.isBoss() ? chr.getDropRate() : chr.getBossDropRate();
      byte d = 1;
      Point pos = new Point(0, mob.position().y);

      MonsterStatusEffect status = mob.getStatus(MonsterStatus.SHOWDOWN);
      if (status != null) {
         chRate *= (status.getStatuses().get(MonsterStatus.SHOWDOWN).doubleValue() / 100.0 + 1.0);
      }

      if (useBaseRate) {
         chRate = 1;
      }

      final MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();
      final List<MonsterGlobalDropEntry> globalEntry = mi.getRelevantGlobalDrops(this.getId());

      final List<MonsterDropEntry> dropEntry = new ArrayList<>();
      final List<MonsterDropEntry> visibleQuestEntry = new ArrayList<>();
      final List<MonsterDropEntry> otherQuestEntry = new ArrayList<>();

      List<MonsterDropEntry> lootEntry = YamlConfig.config.server.USE_SPAWN_RELEVANT_LOOT ? mob.retrieveRelevantDrops() : mi.retrieveEffectiveDrop(mob.id());
      DropEntryProcessor.getInstance().sortDropEntries(lootEntry, dropEntry, visibleQuestEntry, otherQuestEntry, chr);
      if (lootEntry.isEmpty()) {
         return;
      }

      registerMobItemDrops(dropType, mobPosition, chRate, pos, dropEntry, visibleQuestEntry, otherQuestEntry, globalEntry, chr, mob);
   }

   public void dropItemsFromMonster(List<MonsterDropEntry> list, final MapleCharacter chr, final MapleMonster mob) {
      if (mob.dropsDisabled() || !dropsOn) {
         return;
      }

      final byte dropType = (byte) (chr.getParty().isPresent() ? 1 : 0);
      final int mobPosition = mob.position().x;
      int chRate = 1000000;   // guaranteed item drop
      byte d = 1;
      Point pos = new Point(0, mob.position().y);

      dropItemsFromMonsterOnMap(list, pos, d, chRate, dropType, mobPosition, chr, mob);
   }

   public void dropFromFriendlyMonster(final MapleCharacter chr, final MapleMonster mob) {
      dropFromMonster(chr, mob, true);
   }

   public void dropFromReactor(final MapleCharacter chr, final MapleReactor reactor, Item drop, Point dropPos, short questId) {
      spawnDrop(drop, this.calcDropPos(dropPos, reactor.position()), reactor, chr, (byte) (chr.getParty().isPresent() ? 1 : 0), questId);
   }

   private void stopItemMonitor() {
      itemMonitor.cancel(false);
      itemMonitor = null;

      expireItemsTask.cancel(false);
      expireItemsTask = null;

      if (YamlConfig.config.server.USE_SPAWN_LOOT_ON_ANIMATION) {
         mobSpawnLootTask.cancel(false);
         mobSpawnLootTask = null;
      }

      characterStatUpdateTask.cancel(false);
      characterStatUpdateTask = null;
   }

   private void cleanItemMonitor() {
      objectWLock.lock();
      try {
         registeredDrops.removeAll(Collections.singleton(null));
      } finally {
         objectWLock.unlock();
      }
   }

   private void startItemMonitor() {
      chrWLock.lock();
      try {
         if (itemMonitor != null) {
            return;
         }

         itemMonitor = TimerManager.getInstance().register(() -> {
            chrWLock.lock();
            try {
               if (characters.isEmpty()) {
                  if (itemMonitorTimeout == 0) {
                     if (itemMonitor != null) {
                        stopItemMonitor();
                        aggroMonitor.stopAggroCoordinator();
                     }

                     return;
                  } else {
                     itemMonitorTimeout--;
                  }
               } else {
                  itemMonitorTimeout = 1;
               }
            } finally {
               chrWLock.unlock();
            }

            boolean tryClean;
            objectRLock.lock();
            try {
               tryClean = registeredDrops.size() > 70;
            } finally {
               objectRLock.unlock();
            }

            if (tryClean) {
               cleanItemMonitor();
            }
         }, YamlConfig.config.server.ITEM_MONITOR_TIME, YamlConfig.config.server.ITEM_MONITOR_TIME);

         expireItemsTask = TimerManager.getInstance().register(this::makeDisappearExpiredItemDrops, YamlConfig.config.server.ITEM_EXPIRE_CHECK, YamlConfig.config.server.ITEM_EXPIRE_CHECK);

         if (YamlConfig.config.server.USE_SPAWN_LOOT_ON_ANIMATION) {
            lootLock.lock();
            try {
               mobLootEntries.clear();
            } finally {
               lootLock.unlock();
            }

            mobSpawnLootTask = TimerManager.getInstance().register(this::spawnMobItemDrops, 200, 200);
         }

         characterStatUpdateTask = TimerManager.getInstance().register(this::runCharacterStatUpdate, 200, 200);

         itemMonitorTimeout = 1;
      } finally {
         chrWLock.unlock();
      }
   }

   private boolean hasItemMonitor() {
      chrRLock.lock();
      try {
         return itemMonitor != null;
      } finally {
         chrRLock.unlock();
      }
   }

   public int getDroppedItemCount() {
      return droppedItemCount.get();
   }

   private void instantiateItemDrop(MapleMapItem mapItem) {
      if (droppedItemCount.get() >= YamlConfig.config.server.ITEM_LIMIT_ON_MAP) {
         MapleMapObject mapObject;

         do {
            mapObject = null;

            objectWLock.lock();
            try {
               while (mapObject == null) {
                  if (registeredDrops.isEmpty()) {
                     break;
                  }
                  mapObject = registeredDrops.remove(0).get();
               }
            } finally {
               objectWLock.unlock();
            }
         } while (!makeDisappearItemFromMap(mapObject));
      }

      objectWLock.lock();
      try {
         registerItemDrop(mapItem);
         registeredDrops.add(new WeakReference<>(mapItem));
      } finally {
         objectWLock.unlock();
      }

      droppedItemCount.incrementAndGet();
   }

   private void registerItemDrop(MapleMapItem mapItem) {
      droppedItems.put(mapItem, !everLast ? Server.getInstance().getCurrentTime() + YamlConfig.config.server.ITEM_EXPIRE_TIME : Long.MAX_VALUE);
   }

   private void unregisterItemDrop(MapleMapItem mapItem) {
      objectWLock.lock();
      try {
         droppedItems.remove(mapItem);
      } finally {
         objectWLock.unlock();
      }
   }

   private void makeDisappearExpiredItemDrops() {
      List<MapleMapItem> toDisappear = new LinkedList<>();

      objectRLock.lock();
      try {
         long timeNow = Server.getInstance().getCurrentTime();

         droppedItems.entrySet().stream()
               .filter(entry -> entry.getValue() < timeNow)
               .forEach(entry -> toDisappear.add(entry.getKey()));
      } finally {
         objectRLock.unlock();
      }

      toDisappear.forEach(this::makeDisappearItemFromMap);

      objectWLock.lock();
      try {
         toDisappear.forEach(mapItem -> droppedItems.remove(mapItem));
      } finally {
         objectWLock.unlock();
      }
   }

   private void registerMobItemDrops(byte dropType, int mobPosition, int chRate, Point pos, List<MonsterDropEntry> dropEntry, List<MonsterDropEntry> visibleQuestEntry, List<MonsterDropEntry> otherQuestEntry, List<MonsterGlobalDropEntry> globalEntry, MapleCharacter chr, MapleMonster mob) {
      MobLootEntry mle = new MobLootEntry(dropType, mobPosition, chRate, pos, dropEntry, visibleQuestEntry, otherQuestEntry, globalEntry, chr, mob);

      if (YamlConfig.config.server.USE_SPAWN_LOOT_ON_ANIMATION) {
         int animationTime = mob.getAnimationTime("die1");

         lootLock.lock();
         try {
            long timeNow = Server.getInstance().getCurrentTime();
            mobLootEntries.put(mle, timeNow + ((long) (0.42 * animationTime)));
         } finally {
            lootLock.unlock();
         }
      } else {
         mle.run();
      }
   }

   private void spawnMobItemDrops() {
      Set<Entry<MobLootEntry, Long>> mleList;

      lootLock.lock();
      try {
         mleList = new HashSet<>(mobLootEntries.entrySet());
      } finally {
         lootLock.unlock();
      }

      long timeNow = Server.getInstance().getCurrentTime();
      List<MobLootEntry> toRemove = mleList.stream()
            .filter(entry -> entry.getValue() < timeNow)
            .map(Entry::getKey)
            .collect(Collectors.toList());

      if (!toRemove.isEmpty()) {
         List<MobLootEntry> toSpawnLoot = new LinkedList<>();

         lootLock.lock();
         try {
            for (MobLootEntry mle : toRemove) {
               Long mler = mobLootEntries.remove(mle);
               if (mler != null) {
                  toSpawnLoot.add(mle);
               }
            }
         } finally {
            lootLock.unlock();
         }

         toSpawnLoot.forEach(MobLootEntry::run);
      }
   }

   private List<MapleMapItem> getDroppedItems() {
      objectRLock.lock();
      try {
         return new LinkedList<>(droppedItems.keySet());
      } finally {
         objectRLock.unlock();
      }
   }

   public int getDroppedItemsCountById(int itemId) {
      return (int) getDroppedItems().stream()
            .filter(mapItem -> mapItem.getItemId() == itemId)
            .count();
   }

   public void pickItemDrop(PacketInput pickupPacket, MapleMapItem mapItem) { // map drop must be already locked and not-picked up checked by now
      MasterBroadcaster.getInstance().sendToAllInMapRange(this, pickupPacket, mapItem.position());

      droppedItemCount.decrementAndGet();
      this.removeMapObject(mapItem);
      mapItem.setPickedUp(true);
      unregisterItemDrop(mapItem);
   }

   public List<MapleMapItem> updatePlayerItemDropsToParty(int partyId, int characterId, List<MapleCharacter> partyMembers, MapleCharacter partyLeaver) {
      List<MapleMapItem> partyDrops = new LinkedList<>();

      for (MapleMapItem mapItem : getDroppedItems()) {
         if (mapItem.getOwnerId() == characterId) {
            mapItem.lockItem();
            try {
               if (mapItem.isPickedUp()) {
                  continue;
               }

               mapItem.setPartyOwnerId(partyId);

               byte[] removePacket = PacketCreator.create(new RemoveItem(mapItem.objectId()));
               byte[] updatePacket = PacketCreator.create(new UpdateMapItemObject(mapItem, partyLeaver == null));

               for (MapleCharacter mc : partyMembers) {
                  if (this.equals(mc.getMap())) {
                     mc.announce(removePacket);

                     if (mc.needQuestItem(mapItem.getQuest(), mapItem.getItemId())) {
                        mc.announce(updatePacket);
                     }
                  }
               }

               if (partyLeaver != null) {
                  if (this.equals(partyLeaver.getMap())) {
                     partyLeaver.announce(removePacket);
                     if (partyLeaver.needQuestItem(mapItem.getQuest(), mapItem.getItemId())) {
                        PacketCreator.announce(partyLeaver, new UpdateMapItemObject(mapItem, true));
                     }
                  }
               }
            } finally {
               mapItem.unlockItem();
            }
         } else if (partyId != -1 && mapItem.getPartyOwnerId() == partyId) {
            partyDrops.add(mapItem);
         }
      }

      return partyDrops;
   }

   public void updatePartyItemDropsToNewcomer(MapleCharacter newcomer, List<MapleMapItem> partyItems) {
      for (MapleMapItem mapItem : partyItems) {
         mapItem.lockItem();
         try {
            if (mapItem.isPickedUp()) {
               continue;
            }

            byte[] removePacket = PacketCreator.create(new RemoveItem(mapItem.objectId()));
            byte[] updatePacket = PacketCreator.create(new UpdateMapItemObject(mapItem, true));

            if (newcomer != null) {
               if (this.equals(newcomer.getMap())) {
                  newcomer.announce(removePacket);

                  if (newcomer.needQuestItem(mapItem.getQuest(), mapItem.getItemId())) {
                     newcomer.announce(updatePacket);
                  }
               }
            }
         } finally {
            mapItem.unlockItem();
         }
      }
   }

   private void spawnDrop(final Item itemDrop, final Point dropPos, final MapleMapObject dropper, final MapleCharacter chr, final byte dropType, final short questId) {
      final MapleMapItem mapItem = new MapleMapItem(itemDrop, dropPos, dropper, chr, chr.getClient(), dropType, false, questId);
      mapItem.setDropTime(Server.getInstance().getCurrentTime());
      spawnAndAddRangedMapObject(mapItem, c -> {
         MapleCharacter chr1 = c.getPlayer();

         if (chr1.needQuestItem(questId, itemDrop.id())) {
            mapItem.lockItem();
            try {
               PacketCreator.announce(c, new DropItemFromMapObject(chr1, mapItem, dropper.position(), dropPos, (byte) 1));
            } finally {
               mapItem.unlockItem();
            }
         }
      }, null);

      instantiateItemDrop(mapItem);
      activateItemReactors(mapItem, chr.getClient());
   }

   public final void spawnMesoDrop(final int meso, final Point position, final MapleMapObject dropper, final MapleCharacter owner, final boolean playerDrop, final byte dropType) {
      final Point dropPosition = calcDropPos(position, position);
      final MapleMapItem mapItem = new MapleMapItem(meso, dropPosition, dropper, owner, owner.getClient(), dropType, playerDrop);
      mapItem.setDropTime(Server.getInstance().getCurrentTime());

      spawnAndAddRangedMapObject(mapItem, c -> {
         mapItem.lockItem();
         try {
            PacketCreator.announce(c, new DropItemFromMapObject(c.getPlayer(), mapItem, dropper.position(), dropPosition, (byte) 1));
         } finally {
            mapItem.unlockItem();
         }
      }, null);

      instantiateItemDrop(mapItem);
   }

   public final void disappearingItemDrop(final MapleMapObject dropper, final MapleCharacter owner, final Item item, final Point pos) {
      final Point dropPosition = calcDropPos(pos, pos);
      final MapleMapItem mapItem = new MapleMapItem(item, dropPosition, dropper, owner, owner.getClient(), (byte) 1, false);

      mapItem.lockItem();
      try {
         broadcastItemDropMessage(mapItem, dropper.position(), dropPosition, (byte) 3, mapItem.position());
      } finally {
         mapItem.unlockItem();
      }
   }

   public final void disappearingMesoDrop(final int meso, final MapleMapObject dropper, final MapleCharacter owner, final Point pos) {
      final Point dropPosition = calcDropPos(pos, pos);
      final MapleMapItem mapItem = new MapleMapItem(meso, dropPosition, dropper, owner, owner.getClient(), (byte) 1, false);

      mapItem.lockItem();
      try {
         broadcastItemDropMessage(mapItem, dropper.position(), dropPosition, (byte) 3, mapItem.position());
      } finally {
         mapItem.unlockItem();
      }
   }

   public MapleMonster getMonsterById(int id) {
      objectRLock.lock();
      try {
         return mapObjects.values().stream()
               .filter(mapObject -> mapObject.type() == MapleMapObjectType.MONSTER)
               .map(mapObject -> (MapleMonster) mapObject)
               .filter(monster -> monster.id() == id)
               .findFirst()
               .orElse(null);
      } finally {
         objectRLock.unlock();
      }
   }

   public int countMonster(int id) {
      return countMonster(id, id);
   }

   public int countMonster(int minimumId, int maximumId) {
      List<MapleMapObject> mapObjectsInRange = getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.MONSTER));
      return (int) mapObjectsInRange.stream()
            .map(mapObject -> (MapleMonster) mapObject)
            .filter(monster -> monster.id() >= minimumId && monster.id() <= maximumId)
            .count();
   }

   public int countMonsters() {
      return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.MONSTER)).size();
   }

   public int countReactors() {
      return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.REACTOR)).size();
   }

   public final List<MapleMapObject> getReactors() {
      return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.REACTOR));
   }

   public final List<MapleMapObject> getMonsters() {
      return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.MONSTER));
   }

   public final List<MapleReactor> getAllReactors() {
      return getReactors().stream()
            .map(mapObject -> (MapleReactor) mapObject)
            .collect(Collectors.toList());
   }

   public final List<MapleMonster> getAllMonsters() {
      return getMonsters().stream()
            .map(mapObject -> (MapleMonster) mapObject)
            .collect(Collectors.toList());
   }

   public int countItems() {
      return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.ITEM)).size();
   }

   public final List<MapleMapObject> getItems() {
      return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.ITEM));
   }

   public int countPlayers() {
      return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.PLAYER)).size();
   }

   public List<MapleMapObject> getPlayers() {
      return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.PLAYER));
   }

   public List<MapleCharacter> getAllPlayers() {
      List<MapleCharacter> character;
      chrRLock.lock();
      try {
         character = new ArrayList<>(characters);
      } finally {
         chrRLock.unlock();
      }

      return character;
   }

   public Map<Integer, MapleCharacter> getMapAllPlayers() {
      return getAllPlayers().stream()
            .collect(Collectors.toMap(MapleCharacter::getId, character -> character));
   }

   public List<MapleCharacter> getPlayersInRange(Rectangle box) {
      List<MapleCharacter> result;
      chrRLock.lock();
      try {
         result = characters.stream()
               .filter(character -> box.contains(character.position()))
               .collect(Collectors.toList());
      } finally {
         chrRLock.unlock();
      }

      return result;
   }

   public int countAlivePlayers() {
      return (int) getAllPlayers().stream().filter(AbstractMapleCharacterObject::isAlive).count();
   }

   public int countBosses() {
      int count = 0;

      for (MapleMonster mob : getAllMonsters()) {
         if (mob.isBoss()) {
            count++;
         }
      }

      return count;
   }

   public boolean damageMonster(final MapleCharacter chr, final MapleMonster monster, final int damage) {
      if (monster.id() == 8800000) {
         for (MapleMapObject object : chr.getMap().getMapObjects()) {
            MapleMonster mons = chr.getMap().getMonsterByOid(object.objectId());
            if (mons != null) {
               if (mons.id() >= 8800003 && mons.id() <= 8800010) {
                  return true;
               }
            }
         }
      }
      if (monster.isAlive()) {
         boolean killed = monster.damage(chr, damage, false);

         SelfDestruction selfDestruction = monster.getStats().selfDestruction();
         if (selfDestruction != null && selfDestruction.hp() > -1) {// should work ;p
            if (monster.getHp() <= selfDestruction.hp()) {
               killMonster(monster, chr, true, selfDestruction.action());
               return true;
            }
         }
         if (killed) {
            killMonster(monster, chr, true);
         }
         return true;
      }
      return false;
   }

   public void broadcastBalrogVictory(String leaderName) {
      MessageBroadcaster.getInstance().sendWorldServerNotice(world, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("BALROG_VICTORY").with(leaderName, countAlivePlayers()));
   }

   public void broadcastHorntailVictory() {
      MessageBroadcaster.getInstance().sendWorldServerNotice(world, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("HORNTAIL_VICTORY"));
   }

   public void broadcastZakumVictory() {
      MessageBroadcaster.getInstance().sendWorldServerNotice(world, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("ZAKUM_VICTORY"));
   }

   public void broadcastPinkBeanVictory(int channel) {
      MessageBroadcaster.getInstance().sendWorldServerNotice(world, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("PINK_BEAN_VICTORY").with(channel));
   }

   private boolean removeKilledMonsterObject(MapleMonster monster) {
      monster.lockMonster();
      try {
         if (monster.getHp() < 0) {
            return false;
         }

         spawnedMonstersOnMap.decrementAndGet();
         removeMapObject(monster);
         monster.disposeMapObject();
         if (monster.hasBossHPBar()) {
            broadcastBossHpMessage(monster, monster.hashCode(), monster.makeBossHPBarPacket(), monster.position());
         }

         return true;
      } finally {
         monster.unlockMonster();
      }
   }

   public void killMonster(final MapleMonster monster, final MapleCharacter chr, final boolean withDrops) {
      killMonster(monster, chr, withDrops, 1);
   }

   public void killMonster(final MapleMonster monster, final MapleCharacter chr, final boolean withDrops, int animation) {
      if (monster == null) {
         return;
      }

      if (chr == null) {
         if (removeKilledMonsterObject(monster)) {
            monster.dispatchMonsterKilled(false);
            MasterBroadcaster.getInstance().sendToAllInMapRange(this, new KillMonster(monster.objectId(), animation), monster.position());
            monster.aggroSwitchController(null, false);
         }
      } else {
         if (removeKilledMonsterObject(monster)) {
            try {
               if (monster.getStats().level() >= chr.getLevel() + 30 && !chr.isGM()) {
                  AutoBanFactory.GENERAL.alert(chr, " for killing a " + monster.getName() + " which is over 30 levels higher.");
               }

               if (monster.getCP() > 0 && chr.getMap().isCPQMap()) {
                  chr.gainCP(monster.getCP());
               }

               int buff = monster.getBuffToGive();
               if (buff > -1) {
                  MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();
                  getPlayers().stream()
                        .map(mapObject -> (MapleCharacter) mapObject)
                        .filter(AbstractMapleCharacterObject::isAlive)
                        .forEach(character -> {
                           MapleStatEffect statEffect = mii.getItemEffect(buff);
                           PacketCreator.announce(character, new ShowOwnBuffEffect(buff, 1));
                           MasterBroadcaster.getInstance().sendToAllInMap(this, new ShowBuffEffect(character.getId(), buff, 1, (byte) 3), false, character);
                           statEffect.applyTo(character);
                        });
               }

               if (monster.id() >= 8800003 && monster.id() <= 8800010) {
                  boolean makeZakReal = true;
                  Collection<MapleMapObject> objects = getMapObjects();
                  for (MapleMapObject object : objects) {
                     MapleMonster mons = getMonsterByOid(object.objectId());
                     if (mons != null) {
                        if (mons.id() >= 8800003 && mons.id() <= 8800010) {
                           makeZakReal = false;
                           break;
                        }
                     }
                  }
                  if (makeZakReal) {
                     MapleMap map = chr.getMap();

                     for (MapleMapObject object : objects) {
                        MapleMonster mons = map.getMonsterByOid(object.objectId());
                        if (mons != null) {
                           if (mons.id() == 8800000) {
                              makeMonsterReal(mons);
                              break;
                           }
                        }
                     }
                  }
               }

               MapleCharacter dropOwner = monster.killBy(chr);
               if (withDrops && !monster.dropsDisabled()) {
                  if (dropOwner == null) {
                     dropOwner = chr;
                  }
                  dropFromMonster(dropOwner, monster, false);
               }

               if (monster.hasBossHPBar()) {
                  getAllPlayers().stream()
                        .filter(character -> character.getTargetHpBarHash() == monster.hashCode())
                        .forEach(MapleCharacter::resetPlayerAggro);
               }
            } catch (Exception e) {
               e.printStackTrace();
            } finally {
               monster.dispatchMonsterKilled(true);
               MasterBroadcaster.getInstance().sendToAllInMapRange(this, new KillMonster(monster.objectId(), animation), monster.position());
            }
         }
      }
   }

   public void killFriendlies(MapleMonster mob) {
      this.killMonster(mob, (MapleCharacter) getPlayers().get(0), false);
   }

   public void killMonster(int mobId) {
      MapleCharacter chr = (MapleCharacter) getPlayers().get(0);
      getAllMonsters().stream()
            .filter(monster -> monster.id() == mobId)
            .forEach(monster -> killMonster(monster, chr, false));
   }

   public void killMonsterWithDrops(int mobId) {
      Map<Integer, MapleCharacter> mapChars = this.getMapPlayers();

      if (!mapChars.isEmpty()) {
         MapleCharacter defaultChr = mapChars.entrySet().iterator().next().getValue();
         List<MapleMonster> mobList = getAllMonsters();

         for (MapleMonster mob : mobList) {
            if (mob.id() == mobId) {
               MapleCharacter chr = mapChars.get(mob.getCharacterIdWithHighestDamage());
               if (chr == null) {
                  chr = defaultChr;
               }

               this.killMonster(mob, chr, true);
            }
         }
      }
   }

   public void softKillAllMonsters() {
      closeMapSpawnPoints();

      List<MapleMapObject> inRangeObjects = getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.MONSTER));
      inRangeObjects.stream()
            .map(mapObject -> (MapleMonster) mapObject)
            .filter(monster -> !monster.getStats().isFriendly() && removeKilledMonsterObject(monster))
            .forEach(monster -> monster.dispatchMonsterKilled(false));
   }

   public void killAllMonstersNotFriendly() {
      closeMapSpawnPoints();

      List<MapleMapObject> inRangeObjects = getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.MONSTER));
      inRangeObjects.stream()
            .map(mapObject -> (MapleMonster) mapObject)
            .filter(monster -> !monster.getStats().isFriendly())
            .forEach(monster -> killMonster(monster, null, false, 1));
   }

   public void killAllMonsters() {
      closeMapSpawnPoints();

      List<MapleMapObject> inRangeObjects = getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.MONSTER));
      inRangeObjects.stream()
            .map(mapObject -> (MapleMonster) mapObject)
            .forEach(monster -> killMonster(monster, null, false, 1));
   }

   public final void destroyReactors(final int first, final int last) {
      getReactors().stream()
            .map(mapObject -> (MapleReactor) mapObject)
            .filter(reactor -> reactor.getId() >= first && reactor.getId() <= last)
            .forEach(reactor -> destroyReactor(reactor.objectId()));
   }

   public void destroyReactor(int oid) {
      final MapleReactor reactor = getReactorByOid(oid);

      if (reactor != null) {
         if (reactor.destroy()) {
            removeMapObject(reactor);
         }
      }
   }

   public void resetReactors() {
      List<MapleReactor> list = new ArrayList<>();

      objectRLock.lock();
      try {
         mapObjects.values().stream()
               .filter(mapObject -> mapObject.type() == MapleMapObjectType.REACTOR)
               .map(mapObject -> (MapleReactor) mapObject)
               .forEach(list::add);
      } finally {
         objectRLock.unlock();
      }

      resetReactors(list);
   }

   public final void resetReactors(List<MapleReactor> list) {
      for (MapleReactor r : list) {
         if (r.forceDelayedRespawn()) {
            continue;
         }

         r.lockReactor();
         try {
            r.resetReactorActions(0);
            r.setAlive(true);
            MasterBroadcaster.getInstance().sendToAllInMap(this, new TriggerReactor(r, 0));
         } finally {
            r.unlockReactor();
         }
      }
   }

   public void shuffleReactors() {
      List<Point> points = new ArrayList<>();
      objectRLock.lock();
      try {
         mapObjects.values().stream()
               .filter(mapObject -> mapObject.type() == MapleMapObjectType.REACTOR)
               .forEach(mapObject -> points.add(mapObject.position()));

         Collections.shuffle(points);

         mapObjects.values().stream()
               .filter(mapObject -> mapObject.type() == MapleMapObjectType.REACTOR)
               .forEach(mapObject -> mapObject.setPosition(points.remove(points.size() - 1)));
      } finally {
         objectRLock.unlock();
      }
   }

   public final void shuffleReactors(int first, int last) {
      List<Point> points = new ArrayList<>();
      //List<MapleMapObject> reactors = getReactors();
      List<MapleMapObject> targets = new LinkedList<>();

      getReactors().stream()
            .map(mapObject -> (MapleReactor) mapObject)
            .filter(reactor -> reactor.getId() >= first && reactor.getId() <= last)
            .forEach(reactor -> {
               points.add(reactor.position());
               targets.add(reactor);
            });

      Collections.shuffle(points);

      targets.stream()
            .map(mapObject -> (MapleReactor) mapObject)
            .forEach(reactor -> reactor.setPosition(points.remove(points.size() - 1)));
   }

   public final void shuffleReactors(List<Object> list) {
      List<Point> points = new ArrayList<>();
      List<MapleMapObject> listObjects;
      List<MapleMapObject> targets = new LinkedList<>();

      objectRLock.lock();
      try {
         listObjects = list.stream()
               .filter(object -> object instanceof MapleMapObject)
               .map(object -> (MapleMapObject) object)
               .filter(mapObject -> mapObjects.containsValue(mapObject) && mapObject.type() == MapleMapObjectType.REACTOR)
               .collect(Collectors.toList());
      } finally {
         objectRLock.unlock();
      }

      listObjects.stream()
            .map(mapObject -> (MapleReactor) mapObject)
            .forEach(reactor -> {
               points.add(reactor.position());
               targets.add(reactor);
            });

      Collections.shuffle(points);

      targets.stream()
            .map(mapObject -> (MapleReactor) mapObject)
            .forEach(reactor -> reactor.setPosition(points.remove(points.size() - 1)));
   }

   private Map<Integer, MapleMapObject> getCopyMapObjects() {
      objectRLock.lock();
      try {
         return new HashMap<>(mapObjects);
      } finally {
         objectRLock.unlock();
      }
   }

   public List<MapleMapObject> getMapObjects() {
      objectRLock.lock();
      try {
         return new LinkedList<>(mapObjects.values());
      } finally {
         objectRLock.unlock();
      }
   }

   public MapleNPC getNPCById(int id) {
      return getMapObjects().stream()
            .filter(mapObject -> mapObject.type() == MapleMapObjectType.NPC)
            .map(mapObject -> (MapleNPC) mapObject)
            .filter(npc -> npc.id() == id)
            .findFirst()
            .orElse(null);
   }

   public boolean containsNPC(int npcId) {
      objectRLock.lock();
      try {
         return mapObjects.values().stream()
               .filter(mapObject -> mapObject.type() == MapleMapObjectType.NPC)
               .map(mapObject -> (MapleNPC) mapObject)
               .anyMatch(npc -> npc.id() == npcId);
      } finally {
         objectRLock.unlock();
      }
   }

   public void destroyNPC(int npcId) {     // assumption: there's at most one of the same NPC in a map.
      List<MapleMapObject> mapObjects = getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.NPC));

      chrRLock.lock();
      objectWLock.lock();
      try {
         for (MapleMapObject obj : mapObjects) {
            if (((MapleNPC) obj).id() == npcId) {
               MasterBroadcaster.getInstance().sendToAllInMap(this, new RemoveNPCController(obj.objectId()));
               MasterBroadcaster.getInstance().sendToAllInMap(this, new RemoveNPC(obj.objectId()));

               this.mapObjects.remove(obj.objectId());
            }
         }
      } finally {
         objectWLock.unlock();
         chrRLock.unlock();
      }
   }

   public MapleMapObject getMapObject(int oid) {
      objectRLock.lock();
      try {
         return mapObjects.get(oid);
      } finally {
         objectRLock.unlock();
      }
   }

   /**
    * returns a monster with the given oid, if no such monster exists returns null
    *
    * @param oid the object id
    * @return the monster
    */
   public MapleMonster getMonsterByOid(int oid) {
      MapleMapObject mmo = getMapObject(oid);
      return (mmo != null && mmo.type() == MapleMapObjectType.MONSTER) ? (MapleMonster) mmo : null;
   }

   public MapleReactor getReactorByOid(int oid) {
      MapleMapObject mmo = getMapObject(oid);
      return (mmo != null && mmo.type() == MapleMapObjectType.REACTOR) ? (MapleReactor) mmo : null;
   }

   public MapleReactor getReactorById(int Id) {
      objectRLock.lock();
      try {
         return mapObjects.values().stream()
               .filter(mapObject -> mapObject.type() == MapleMapObjectType.REACTOR)
               .map(mapObject -> (MapleReactor) mapObject)
               .filter(reactor -> reactor.getId() == Id)
               .findFirst()
               .orElse(null);
      } finally {
         objectRLock.unlock();
      }
   }

   public List<MapleReactor> getReactorsByIdRange(final int first, final int last) {
      objectRLock.lock();
      try {
         return mapObjects.values().stream()
               .filter(mapObject -> mapObject.type() == MapleMapObjectType.REACTOR)
               .map(mapObject -> (MapleReactor) mapObject)
               .filter(reactor -> reactor.getId() >= first && reactor.getId() <= last)
               .collect(Collectors.toList());
      } finally {
         objectRLock.unlock();
      }
   }

   public MapleReactor getReactorByName(String name) {
      objectRLock.lock();
      try {
         return mapObjects.values().stream()
               .filter(mapObject -> mapObject.type() == MapleMapObjectType.REACTOR)
               .map(mapObject -> (MapleReactor) mapObject)
               .filter(reactor -> reactor.getName().equals(name))
               .findFirst()
               .orElse(null);
      } finally {
         objectRLock.unlock();
      }
   }

   public void spawnMonsterOnGroundBelow(int id, int x, int y) {
      MapleLifeFactory.getMonster(id).ifPresent(monster -> spawnMonsterOnGroundBelow(monster, new Point(x, y)));
   }

   public void spawnMonsterOnGroundBelow(MapleMonster mob, Point pos) {
      Point spos = new Point(pos.x, pos.y - 1);
      spos = calcPointBelow(spos);
      spos.y--;
      mob.setPosition(spos);
      spawnMonster(mob);
   }

   public void spawnCPQMonster(MapleMonster mob, Point pos, int team) {
      Point spos = new Point(pos.x, pos.y - 1);
      spos = calcPointBelow(spos);
      spos.y--;
      mob.setPosition(spos);
      mob.setTeam(team);
      spawnMonster(mob);
   }

   private void monsterItemDrop(final MapleMonster m, long delay) {
      m.dropFromFriendlyMonster(delay);
   }

   public void spawnFakeMonsterOnGroundBelow(MapleMonster mob, Point pos) {
      Point spos = getGroundBelow(pos);
      mob.setPosition(spos);
      spawnFakeMonster(mob);
   }

   public Point getGroundBelow(Point pos) {
      Point spos = new Point(pos.x, pos.y - 14); // Using -14 fixes spawning pets causing a lot of issues.
      spos = calcPointBelow(spos);
      spos.y--;//shouldn't be null!
      return spos;
   }

   public Point getPointBelow(Point pos) {
      return calcPointBelow(pos);
   }

   public void spawnRevives(final MapleMonster monster) {
      monster.setMap(this);
      if (getEventInstance() != null) {
         getEventInstance().registerMonster(monster);
      }

      spawnAndAddRangedMapObject(monster, c -> PacketCreator.announce(c, new SpawnMonster(monster, false)));

      monster.aggroUpdateController();

      updateBossSpawn(monster);

      spawnedMonstersOnMap.incrementAndGet();
      addSelfDestructive(monster);
      applyRemoveAfter(monster);
   }

   private void applyRemoveAfter(final MapleMonster monster) {
      final SelfDestruction selfDestruction = monster.getStats().selfDestruction();
      if (monster.getStats().removeAfter() > 0 || selfDestruction != null && selfDestruction.hp() < 0) {
         Runnable removeAfterAction;

         if (selfDestruction == null) {
            removeAfterAction = () -> killMonster(monster, null, false);
            registerMapSchedule(removeAfterAction, monster.getStats().removeAfter() * 1000);
         } else {
            removeAfterAction = () -> killMonster(monster, null, false, selfDestruction.action());
            registerMapSchedule(removeAfterAction, selfDestruction.removeAfter() * 1000);
         }

         monster.pushRemoveAfterAction(removeAfterAction);
      }
   }

   public void dismissRemoveAfter(final MapleMonster monster) {
      Runnable removeAfterAction = monster.popRemoveAfterAction();
      if (removeAfterAction != null) {
         OverallService service = (OverallService) this.getChannelServer().getServiceAccess(ChannelServices.OVERALL);
         service.forceRunOverallAction(mapId, removeAfterAction);
      }
   }

   private List<SpawnPoint> getMonsterSpawn() {
      synchronized (monsterSpawn) {
         return new ArrayList<>(monsterSpawn);
      }
   }

   private List<SpawnPoint> getAllMonsterSpawn() {
      synchronized (allMonsterSpawn) {
         return new ArrayList<>(allMonsterSpawn);
      }
   }

   public void spawnAllMonsterIdFromMapSpawnList(int id) {
      spawnAllMonsterIdFromMapSpawnList(id, 1, false);
   }

   public void spawnAllMonsterIdFromMapSpawnList(int id, int difficulty, boolean isPq) {
      getAllMonsterSpawn().stream()
            .filter(spawnPoint -> spawnPoint.getMonsterId() == id && spawnPoint.shouldSpawn())
            .forEach(spawnPoint -> spawnMonster(spawnPoint.getMonster(), difficulty, isPq));
   }

   public void spawnAllMonstersFromMapSpawnList() {
      spawnAllMonstersFromMapSpawnList(1, false);
   }

   public void spawnAllMonstersFromMapSpawnList(int difficulty, boolean isPq) {
      getAllMonsterSpawn().forEach(spawnPoint -> spawnMonster(spawnPoint.getMonster(), difficulty, isPq));
   }

   public void spawnMonster(final MapleMonster monster) {
      spawnMonster(monster, 1, false);
   }

   public void spawnMonster(final MapleMonster monster, int difficulty, boolean isPq) {
      if (mobCapacity != -1 && mobCapacity == spawnedMonstersOnMap.get()) {
         return;//PyPQ
      }

      monster.changeDifficulty(difficulty, isPq);

      monster.setMap(this);
      if (getEventInstance() != null) {
         getEventInstance().registerMonster(monster);
      }

      spawnAndAddRangedMapObject(monster, c -> PacketCreator.announce(c, new SpawnMonster(monster, true)), null);

      monster.aggroUpdateController();
      updateBossSpawn(monster);

      if ((monster.getTeam() == 1 || monster.getTeam() == 0) && (isCPQMap() || isCPQMap2())) {
         List<MCSkill> teamS = null;
         if (monster.getTeam() == 0) {
            teamS = redTeamBuffs;
         } else if (monster.getTeam() == 1) {
            teamS = blueTeamBuffs;
         }
         if (teamS != null) {
            teamS.stream()
                  .filter(Objects::nonNull)
                  .forEach(skill -> MobSkillProcessor.getInstance().applyEffect(null, monster, skill.getSkill(), false, null));
         }
      }

      if (monster.getDropPeriodTime() > 0) { //9300102 - Watch hog, 9300061 - Moon Bunny (HPQ), 9300093 - Tylus
         if (monster.id() == 9300102) {
            monsterItemDrop(monster, monster.getDropPeriodTime());
         } else if (monster.id() == 9300061) {
            monsterItemDrop(monster, monster.getDropPeriodTime() / 3);
         } else if (monster.id() == 9300093) {
            monsterItemDrop(monster, monster.getDropPeriodTime());
         } else if (monster.id() == 9400326 || monster.id() == 9400331 || monster.id() == 9400336) {
            monsterItemDrop(monster, monster.getDropPeriodTime());
         } else {
            FilePrinter.printError(FilePrinter.UNHANDLED_EVENT, "UNCODED TIMED MOB DETECTED: " + monster.id());
         }
      }

      spawnedMonstersOnMap.incrementAndGet();
      addSelfDestructive(monster);
      applyRemoveAfter(monster);
   }

   public void spawnDojoMonster(final MapleMonster monster) {
      Point[] pts = {new Point(140, 0), new Point(190, 7), new Point(187, 7)};
      spawnMonsterWithEffect(monster, 15, pts[Randomizer.nextInt(3)]);
   }

   public void spawnMonsterWithEffect(final MapleMonster monster, final int effect, Point pos) {
      monster.setMap(this);
      Point spos = new Point(pos.x, pos.y - 1);
      spos = calcPointBelow(spos);
      if (spos == null) {
         return;
      }

      if (getEventInstance() != null) {
         getEventInstance().registerMonster(monster);
      }

      spos.y--;
      monster.setPosition(spos);
      monster.setSpawnEffect(effect);
      spawnAndAddRangedMapObject(monster, c -> PacketCreator.announce(c, new SpawnMonster(monster, true, effect)));

      monster.aggroUpdateController();

      updateBossSpawn(monster);

      spawnedMonstersOnMap.incrementAndGet();
      addSelfDestructive(monster);
      applyRemoveAfter(monster);
   }

   public void spawnFakeMonster(final MapleMonster monster) {
      monster.setMap(this);
      monster.setFake(true);
      spawnAndAddRangedMapObject(monster, c -> PacketCreator.announce(c, new SpawnFakeMonster(monster, 0)));

      spawnedMonstersOnMap.incrementAndGet();
      addSelfDestructive(monster);
   }

   public void makeMonsterReal(final MapleMonster monster) {
      monster.setFake(false);
      MasterBroadcaster.getInstance().sendToAllInMap(this, new MakeMonsterReal(monster));
      monster.broadcastMonsterStatus();
      monster.aggroUpdateController();
      updateBossSpawn(monster);
   }

   public void spawnReactor(final MapleReactor reactor) {
      reactor.setMap(this);
      spawnAndAddRangedMapObject(reactor, c -> c.announce(ReactorSpawnAndDestroyer.getInstance().makeSpawnData(reactor)));
   }

   public void spawnDoor(final MapleDoorObject door) {
      spawnAndAddRangedMapObject(door, c -> {
         MapleCharacter chr = c.getPlayer();
         if (chr != null) {
            DoorObjectSpawnAndDestroyer.getInstance().sendSpawnData(door, c, false);
            chr.addVisibleMapObject(door);
         }
      }, chr -> chr.getMapId() == door.getFrom());
   }

   public MaplePortal getDoorPortal(int doorId) {
      MaplePortal doorPortal = portals.get(0x80 + doorId);
      if (doorPortal == null) {
         FilePrinter.printError(FilePrinter.EXCEPTION, "[Door] " + mapName + "(" + mapId + ") does not contain door portal id " + doorId);
         return portals.get(0x80);
      }

      return doorPortal;
   }

   public void spawnSummon(final MapleSummon summon) {
      spawnAndAddRangedMapObject(summon, c -> {
         if (summon != null) {
            PacketCreator.announce(c, new SpawnSummon(summon.getOwner().getId(), summon.objectId(),
                  summon.getSkill(), summon.getSkillLevel(), summon.position(), summon.stance(),
                  summon.getMovementType().getValue(), summon.isPuppet(), true));
         }
      }, null);
   }

   public void spawnMist(final MapleMist mist, final int duration, boolean poison, boolean fake, boolean recovery) {
      addMapObject(mist);
      MasterBroadcaster.getInstance().sendToAllInMap(this, character -> {
         if (fake) {
            return MistSpawnAndDestroyer.getInstance().makeFakeSpawnData(mist, 30);
         } else {
            return MistSpawnAndDestroyer.getInstance().makeSpawnData(mist);
         }
      });
      TimerManager tMan = TimerManager.getInstance();
      final ScheduledFuture<?> poisonSchedule;
      if (poison) {
         Runnable poisonTask = () -> {
            List<MapleMapObject> affectedMonsters = getMapObjectsInBox(mist.getBox(), Collections.singletonList(MapleMapObjectType.MONSTER));
            for (MapleMapObject mo : affectedMonsters) {
               if (mist.makeChanceResult()) {
                  mist.getSourceSkill().ifPresent(skill -> {
                     MonsterStatusEffect poisonEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.POISON, 1), skill, null, false);
                     ((MapleMonster) mo).applyStatus(mist.getOwner(), poisonEffect, true, duration);
                  });
               }
            }
         };
         poisonSchedule = tMan.register(poisonTask, 2000, 2500);
      } else if (recovery) {
         Runnable poisonTask = () -> {
            List<MapleMapObject> players = getMapObjectsInBox(mist.getBox(), Collections.singletonList(MapleMapObjectType.PLAYER));
            for (MapleMapObject mo : players) {
               if (mist.makeChanceResult()) {
                  MapleCharacter chr = (MapleCharacter) mo;
                  if (mist.getOwner().getId() == chr.getId() || mist.getOwner().getParty().map(party -> party.containsMembers(chr.getMPC())).orElse(false)) {
                     mist.getSourceSkill()
                           .map(skill -> skill.getEffect(chr.getSkillLevel(skill.getId())))
                           .ifPresent(effect -> chr.addMP(effect.getX() * chr.getMp() / 100));
                  }
               }
            }
         };
         poisonSchedule = tMan.register(poisonTask, 2000, 2500);
      } else {
         poisonSchedule = null;
      }

      Runnable mistSchedule = () -> {
         removeMapObject(mist);
         if (poisonSchedule != null) {
            poisonSchedule.cancel(false);
         }
         MasterBroadcaster.getInstance().sendToAllInMap(MapleMap.this, character -> MistSpawnAndDestroyer.getInstance().makeDestroyData(mist));
      };

      MobMistService service = (MobMistService) this.getChannelServer().getServiceAccess(ChannelServices.MOB_MIST);
      service.registerMobMistCancelAction(mapId, mistSchedule, duration);
   }

   public void spawnKite(final MapleKite kite) {
      addMapObject(kite);
      MasterBroadcaster.getInstance().sendToAllInMap(this, character -> KiteSpawnAndDestroyer.getInstance().makeSpawnData(kite));

      Runnable expireKite = () -> {
         removeMapObject(kite);
         MasterBroadcaster.getInstance().sendToAllInMap(MapleMap.this, character -> KiteSpawnAndDestroyer.getInstance().makeDestroyData(kite));
      };

      getWorldServer().registerTimedMapObject(expireKite, YamlConfig.config.server.KITE_EXPIRE_TIME);
   }

   public final void spawnItemDrop(final MapleMapObject dropper, final MapleCharacter owner, final Item item, Point pos, final boolean ffaDrop, final boolean playerDrop) {
      spawnItemDrop(dropper, owner, item, pos, (byte) (ffaDrop ? 2 : 0), playerDrop);
   }

   public final void spawnItemDrop(final MapleMapObject dropper, final MapleCharacter owner, final Item item, Point pos, final byte dropType, final boolean playerDrop) {
      if (FieldLimit.DROP_LIMIT.check(this.getFieldLimit())) {
         this.disappearingItemDrop(dropper, owner, item, pos);
         return;
      }

      final Point dropPosition = calcDropPos(pos, pos);
      final MapleMapItem mapItem = new MapleMapItem(item, dropPosition, dropper, owner, owner.getClient(), dropType, playerDrop);
      mapItem.setDropTime(Server.getInstance().getCurrentTime());

      spawnAndAddRangedMapObject(mapItem, c -> {
         mapItem.lockItem();
         try {
            PacketCreator.announce(c, new DropItemFromMapObject(c.getPlayer(), mapItem, dropper.position(), dropPosition, (byte) 1));
         } finally {
            mapItem.unlockItem();
         }
      }, null);

      mapItem.lockItem();
      try {
         broadcastItemDropMessage(mapItem, dropper.position(), dropPosition, (byte) 0);
      } finally {
         mapItem.unlockItem();
      }

      instantiateItemDrop(mapItem);
      activateItemReactors(mapItem, owner.getClient());
   }

   public final void spawnItemDropList(List<Integer> list, final MapleMapObject dropper, final MapleCharacter owner, Point pos) {
      spawnItemDropList(list, 1, 1, dropper, owner, pos, true, false);
   }

   public final void spawnItemDropList(List<Integer> list, int minCopies, int maxCopies, final MapleMapObject dropper, final MapleCharacter owner, Point pos) {
      spawnItemDropList(list, minCopies, maxCopies, dropper, owner, pos, true, false);
   }

   // spawns item instances of all defined item ids on a list
   public final void spawnItemDropList(List<Integer> list, int minCopies, int maxCopies, final MapleMapObject dropper, final MapleCharacter owner, Point pos, final boolean ffaDrop, final boolean playerDrop) {
      int copies = (maxCopies - minCopies) + 1;
      if (copies < 1) {
         return;
      }

      Collections.shuffle(list);

      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      Random rnd = new Random();

      final Point dropPos = new Point(pos);
      dropPos.x -= (12 * list.size());

      for (Integer integer : list) {
         if (integer == 0) {
            spawnMesoDrop(owner != null ? 10 * owner.getMesoRate() : 10, calcDropPos(dropPos, pos), dropper, owner, playerDrop, (byte) (ffaDrop ? 2 : 0));
         } else {
            final Item drop;
            int randomId = integer;

            if (ItemConstants.getInventoryType(randomId) != MapleInventoryType.EQUIP) {
               drop = Item.newBuilder(randomId).setPosition((short) 0).setQuantity((short) (rnd.nextInt(copies) + minCopies)).build();
            } else {
               drop = Equip.newBuilder(ii.getEquipById(randomId)).randomizeStats().build();
            }

            spawnItemDrop(dropper, owner, drop, calcDropPos(dropPos, pos), ffaDrop, playerDrop);
         }

         dropPos.x += 25;
      }
   }

   private void registerMapSchedule(Runnable r, long delay) {
      OverallService service = (OverallService) this.getChannelServer().getServiceAccess(ChannelServices.OVERALL);
      service.registerOverallAction(mapId, r, delay);
   }

   private void activateItemReactors(final MapleMapItem drop, final MapleClient c) {
      final Item item = drop.getItem();

      for (final MapleMapObject o : getReactors()) {
         final MapleReactor react = (MapleReactor) o;

         if (react.getReactorType() == 100) {
            if (react.getReactItem(react.getEventState()).getLeft() == item.id() && react.getReactItem(react.getEventState()).getRight() == item.quantity()) {

               if (react.getArea().contains(drop.position())) {
                  registerMapSchedule(new ActivateItemReactor(drop, react, c), 5000);
                  break;
               }
            }
         }
      }
   }

   public void searchItemReactors(final MapleReactor react) {
      if (react.getReactorType() == 100) {
         Pair<Integer, Integer> reactProp = react.getReactItem(react.getEventState());
         int reactItem = reactProp.getLeft(), reactQty = reactProp.getRight();
         Rectangle reactArea = react.getArea();

         List<MapleMapItem> list;
         objectRLock.lock();
         try {
            list = new ArrayList<>(droppedItems.keySet());
         } finally {
            objectRLock.unlock();
         }

         for (final MapleMapItem drop : list) {
            drop.lockItem();
            try {
               if (!drop.isPickedUp()) {
                  final Item item = drop.getItem();

                  if (item != null && reactItem == item.id() && reactQty == item.quantity()) {
                     if (reactArea.contains(drop.position())) {
                        MapleClient owner = drop.getOwnerClient();
                        if (owner != null) {
                           registerMapSchedule(new ActivateItemReactor(drop, react, owner), 5000);
                        }
                     }
                  }
               }
            } finally {
               drop.unlockItem();
            }
         }
      }
   }

   public void changeEnvironment(String mapObj, int newState) {
      MasterBroadcaster.getInstance().sendToAllInMap(this, new EnvironmentChange(mapObj, newState));
   }

   public void startMapEffect(String msg, int itemId) {
      startMapEffect(msg, itemId, 30000);
   }

   public void startMapEffect(String msg, int itemId, long time) {
      if (mapEffect != null) {
         return;
      }
      mapEffect = new MapleMapEffect(msg, itemId);
      MasterBroadcaster.getInstance().sendToAllInMap(this, character -> mapEffect.makeStartData());

      Runnable r = () -> {
         MasterBroadcaster.getInstance().sendToAllInMap(MapleMap.this, character -> mapEffect.makeDestroyData());
         mapEffect = null;
      };

      registerMapSchedule(r, time);
   }

   public MapleCharacter getAnyCharacterFromParty(int partyId) {
      return getAllPlayers().stream()
            .filter(character -> character.getPartyId() == partyId)
            .findFirst()
            .orElse(null);
   }

   private void addPartyMemberInternal(MapleCharacter chr, int partyId) {
      int charactersPartyId = chr.getPartyId();
      if (charactersPartyId == -1) {
         return;
      }

      Set<Integer> partyEntry = mapParty.get(charactersPartyId);
      if (partyEntry == null) {
         partyEntry = new LinkedHashSet<>();
         partyEntry.add(chr.getId());

         mapParty.put(charactersPartyId, partyEntry);
      } else {
         partyEntry.add(chr.getId());
      }
   }

   private void removePartyMemberInternal(MapleCharacter chr, int partyId) {
      int charactersPartyId = chr.getPartyId();
      if (charactersPartyId == -1) {
         return;
      }

      Set<Integer> partyEntry = mapParty.get(charactersPartyId);
      if (partyEntry != null) {
         if (partyEntry.size() > 1) {
            partyEntry.remove(chr.getId());
         } else {
            mapParty.remove(charactersPartyId);
         }
      }
   }

   public void addPartyMember(MapleCharacter chr, int partyId) {
      chrWLock.lock();
      try {
         addPartyMemberInternal(chr, partyId);
      } finally {
         chrWLock.unlock();
      }
   }

   public void removePartyMember(MapleCharacter chr, int partyId) {
      chrWLock.lock();
      try {
         removePartyMemberInternal(chr, partyId);
      } finally {
         chrWLock.unlock();
      }
   }

   public void removeParty(int partyId) {
      chrWLock.lock();
      try {
         mapParty.remove(partyId);
      } finally {
         chrWLock.unlock();
      }
   }

   public void addPlayer(final MapleCharacter chr) {
      int chrSize;
      chrWLock.lock();
      try {
         characters.add(chr);
         chrSize = characters.size();

         if (chr.getParty().isPresent() && chr.getParty().get().getMemberById(chr.getId()).isPresent()) {
            addPartyMemberInternal(chr, chr.getPartyId());
         }

         itemMonitorTimeout = 1;
      } finally {
         chrWLock.unlock();
      }

      chr.setMapId(mapId);
      chr.updateActiveEffects();

      MapScriptManager mapScriptManager = MapScriptManager.getInstance();
      if (chrSize == 1) {
         if (!hasItemMonitor()) {
            startItemMonitor();
            aggroMonitor.startAggroCoordinator();
         }

         if (onFirstUserEnter.length() != 0) {
            mapScriptManager.runMapScript(chr.getClient(), "onFirstUserEnter/" + onFirstUserEnter, true);
         }
      }
      if (onUserEnter.length() != 0) {
         if (onUserEnter.equals("cygnusTest") && (mapId < 913040000 || mapId > 913040006)) {
            chr.saveLocation("INTRO");
         }
         mapScriptManager.runMapScript(chr.getClient(), "onUserEnter/" + onUserEnter, false);
      }
      if (FieldLimit.CANNOT_USE_MOUNTS.check(fieldLimit) && chr.getBuffedValue(MapleBuffStat.MONSTER_RIDING) != null) {
         chr.cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
         chr.cancelBuffStats(MapleBuffStat.MONSTER_RIDING);
      }

      if (mapId == 200090060) { // To Rien
         int travelTime = getWorldServer().getTransportationTime(1 * 60 * 1000);
         PacketCreator.announce(chr, new GetClock(travelTime / 1000));
         TimerManager.getInstance().schedule(() -> {
            if (chr.getMapId() == 200090060) {
               chr.changeMap(140020300, 0);
            }
         }, travelTime);
      } else if (mapId == 200090070) { // To Lith Harbor
         int travelTime = getWorldServer().getTransportationTime(1 * 60 * 1000);
         PacketCreator.announce(chr, new GetClock(travelTime / 1000));
         TimerManager.getInstance().schedule(() -> {
            if (chr.getMapId() == 200090070) {
               chr.changeMap(104000000, 3);
            }
         }, travelTime);
      } else if (mapId == 200090030) { // To Ereve (SkyFerry)
         int travelTime = getWorldServer().getTransportationTime(2 * 60 * 1000);
         PacketCreator.announce(chr, new GetClock(travelTime / 1000));
         TimerManager.getInstance().schedule(() -> {
            if (chr.getMapId() == 200090030) {
               chr.changeMap(130000210, 0);
            }
         }, travelTime);
      } else if (mapId == 200090031) { // To Victoria Island (SkyFerry)
         int travelTime = getWorldServer().getTransportationTime(2 * 60 * 1000);
         PacketCreator.announce(chr, new GetClock(travelTime / 1000));
         TimerManager.getInstance().schedule(() -> {
            if (chr.getMapId() == 200090031) {
               chr.changeMap(101000400, 0);
            }
         }, travelTime);
      } else if (mapId == 200090021) { // To Orbis (SkyFerry)
         int travelTime = getWorldServer().getTransportationTime(8 * 60 * 1000);
         PacketCreator.announce(chr, new GetClock(travelTime / 1000));
         TimerManager.getInstance().schedule(() -> {
            if (chr.getMapId() == 200090021) {
               chr.changeMap(200000161, 0);
            }
         }, travelTime);
      } else if (mapId == 200090020) { // To Ereve From Orbis (SkyFerry)
         int travelTime = getWorldServer().getTransportationTime(8 * 60 * 1000);
         PacketCreator.announce(chr, new GetClock(travelTime / 1000));
         TimerManager.getInstance().schedule(() -> {
            if (chr.getMapId() == 200090020) {
               chr.changeMap(130000210, 0);
            }
         }, travelTime);
      } else if (MapleMiniDungeonInfo.isDungeonMap(mapId)) {
         MapleMiniDungeon mmd = chr.getClient().getChannelServer().getMiniDungeon(mapId);
         if (mmd != null) {
            mmd.registerPlayer(chr);
         }
      } else if (GameConstants.isAriantColiseumArena(mapId)) {
         int pqTimer = (10 * 60 * 1000);
         PacketCreator.announce(chr, new GetClock(pqTimer / 1000));
      }

      MaplePet[] pets = chr.updateAndGetPets(pet -> pet.updatePos(getGroundBelow(chr.position())));
      Arrays.stream(pets).forEach(pet -> PacketCreator.announce(chr, new ShowPet(chr, pet, false, false)));
      chr.commitExcludedItems();

      if (chr.getMonsterCarnival() != null) {
         PacketCreator.announce(chr, new GetClock(chr.getMonsterCarnival().getTimeLeftSeconds()));
         if (isCPQMap()) {
            int team = -1;
            int opposition = -1;
            if (chr.getTeam() == 0) {
               team = 0;
               opposition = 1;
            }
            if (chr.getTeam() == 1) {
               team = 1;
               opposition = 0;
            }

            MonsterCarnival monsterCarnival = chr.getMonsterCarnival();
            PacketCreator.announce(chr, new MonsterCarnivalStart(team, chr.getCP(), chr.getTotalCP(),
                  monsterCarnival.getCP(team), monsterCarnival.getTotalCP(team), monsterCarnival.getCP(opposition),
                  monsterCarnival.getTotalCP(opposition)));
         }
      }

      chr.removeSandboxItems();

      if (chr.getChalkboard() != null) {
         if (!GameConstants.isFreeMarketRoom(mapId)) {
            PacketCreator.announce(chr, new UseChalkboard(chr.getId(), false, chr.getChalkboard()));
         } else {
            chr.setChalkboard(null);
         }
      }

      if (chr.isHidden()) {
         broadcastGMSpawnPlayerMapObjectMessage(chr, chr, true);
         PacketCreator.announce(chr, new GMEffect(0x10, (byte) 1));

         List<Pair<MapleBuffStat, Integer>> darkSightStat = Collections.singletonList(new Pair<>(MapleBuffStat.DARK_SIGHT, 0));
         broadcastGMMessage(chr, new GiveForeignBuff(chr.getId(), darkSightStat), false);
      } else {
         broadcastSpawnPlayerMapObjectMessage(chr, chr, true);
      }

      sendObjectPlacement(chr.getClient());

      if (isStartingEventMap() && !eventStarted()) {
         chr.getMap().getPortal("join00").setPortalStatus(false);
      }
      if (hasForcedEquip()) {
         PacketCreator.announce(chr, new ForcedEquip(-1));
      }
      if (specialEquip()) {
         PacketCreator.announce(chr, new CoconutScore(0, 0));
         PacketCreator.announce(chr, new ForcedEquip(chr.getTeam()));
      }
      objectWLock.lock();
      try {
         this.mapObjects.put(chr.objectId(), chr);
      } finally {
         objectWLock.unlock();
      }

      if (chr.getPlayerShop() != null) {
         addMapObject(chr.getPlayerShop());
      }

      final MapleDragon dragon = chr.getDragon();
      if (dragon != null) {
         dragon.setPosition(chr.position());
         this.addMapObject(dragon);
         if (chr.isHidden()) {
            this.broadcastGMMessage(chr, new SpawnDragon(dragon));
         } else {
            this.broadcastMessage(chr, new SpawnDragon(dragon));
         }
      }

      MapleStatEffect summonStat = chr.getStatForBuff(MapleBuffStat.SUMMON);
      if (summonStat != null) {
         MapleSummon summon = chr.getSummonByKey(summonStat.getSourceId());
         summon.setPosition(chr.position());
         chr.getMap().spawnSummon(summon);
         MapleMapObjectProcessor.getInstance().updateMapObjectVisibility(chr, summon);
      }
      if (mapEffect != null) {
         mapEffect.sendStartData(chr.getClient());
      }
      PacketCreator.announce(chr, new ForcedStatReset());
      if (mapId == 914000200 || mapId == 914000210 || mapId == 914000220) {
         PacketCreator.announce(chr, new ForcedStatSet());
      }
      if (chr.getEventInstance() != null && chr.getEventInstance().isTimerStarted()) {
         PacketCreator.announce(chr, new GetClock((int) (chr.getEventInstance().getTimeLeft() / 1000)));
      }
      if (chr.getFitness() != null && chr.getFitness().isTimerStarted()) {
         PacketCreator.announce(chr, new GetClock((int) (chr.getFitness().getTimeLeft() / 1000)));
      }

      if (chr.getOla() != null && chr.getOla().isTimerStarted()) {
         PacketCreator.announce(chr, new GetClock((int) (chr.getOla().getTimeLeft() / 1000)));
      }

      if (mapId == 109060000) {
         PacketCreator.announce(chr, new RollSnowBall(true, 0));
      }

      if (hasClock()) {
         Calendar cal = Calendar.getInstance();
         PacketCreator.announce(chr, new GetClockTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND)));
      }
      if (hasBoat() > 0) {
         if (hasBoat() == 1) {
            PacketCreator.announce(chr, new Boat(true));
         } else {
            PacketCreator.announce(chr, new Boat(false));
         }
      }

      chr.receivePartyMemberHP();
      MapleMapProcessor.getInstance().announcePlayerDiseases(chr.getClient());
   }

   public MaplePortal getRandomPlayerSpawnPoint() {
      List<MaplePortal> spawnPoints = portals.values().stream()
            .filter(portal -> portal.getType() >= 0 && portal.getType() <= 1 && portal.getTargetMapId() == 999999999)
            .collect(Collectors.toList());

      int randomIndex = new Random().nextInt(spawnPoints.size());
      MaplePortal portal = spawnPoints.get(randomIndex);
      return portal != null ? portal : getPortal(0);
   }

   public MaplePortal findClosestTeleportPortal(Point from) {
      MaplePortal closest = null;
      double shortestDistance = Double.POSITIVE_INFINITY;
      for (MaplePortal portal : portals.values()) {
         double distance = portal.getPosition().distanceSq(from);
         if (portal.getType() == MaplePortal.TELEPORT_PORTAL && distance < shortestDistance && portal.getTargetMapId() == 999999999) {
            closest = portal;
            shortestDistance = distance;
         }
      }
      return closest;
   }

   public MaplePortal findClosestPlayerSpawnPoint(Point from) {
      MaplePortal closest = null;
      double shortestDistance = Double.POSITIVE_INFINITY;
      for (MaplePortal portal : portals.values()) {
         double distance = portal.getPosition().distanceSq(from);
         if (portal.getType() >= 0 && portal.getType() <= 1 && distance < shortestDistance && portal.getTargetMapId() == 999999999) {
            closest = portal;
            shortestDistance = distance;
         }
      }
      return closest;
   }

   public MaplePortal findClosestPortal(Point from) {
      MaplePortal closest = null;
      double shortestDistance = Double.POSITIVE_INFINITY;
      for (MaplePortal portal : portals.values()) {
         double distance = portal.getPosition().distanceSq(from);
         if (distance < shortestDistance) {
            closest = portal;
            shortestDistance = distance;
         }
      }
      return closest;
   }

   public MaplePortal findMarketPortal() {
      for (MaplePortal portal : portals.values()) {
         String ptScript = portal.getScriptName();
         if (ptScript != null && ptScript.contains("market")) {
            return portal;
         }
      }
      return null;
   }

   public void addPlayerPuppet(MapleCharacter player) {
      getAllMonsters().forEach(monster -> monster.aggroAddPuppet(player));
   }

   public void removePlayerPuppet(MapleCharacter player) {
      getAllMonsters().forEach(monster -> monster.aggroRemovePuppet(player));
   }

   public void removePlayer(MapleCharacter chr) {
      Channel channel = chr.getClient().getChannelServer();

      FaceExpressionService service = (FaceExpressionService) this.getChannelServer().getServiceAccess(ChannelServices.FACE_EXPRESSION);
      service.unregisterFaceExpression(mapId, chr);
      chr.unregisterChairBuff();

      chrWLock.lock();
      try {
         if (chr.getParty().isPresent() && chr.getParty().get().getMemberById(chr.getId()).isPresent()) {
            removePartyMemberInternal(chr, chr.getPartyId());
         }
         characters.remove(chr);
      } finally {
         chrWLock.unlock();
      }

      if (MapleMiniDungeonInfo.isDungeonMap(mapId)) {
         MapleMiniDungeon mmd = channel.getMiniDungeon(mapId);
         if (mmd != null) {
            if (!mmd.unregisterPlayer(chr)) {
               channel.removeMiniDungeon(mapId);
            }
         }
      }

      removeMapObject(chr.objectId());
      if (!chr.isHidden()) {
         MasterBroadcaster.getInstance().sendToAllInMap(this, new RemovePlayer(chr.getId()));
      } else {
         MasterBroadcaster.getInstance().sendToAllGMInMap(this, new RemovePlayer(chr.getId()));
      }

      chr.leaveMap();

      for (MapleSummon summon : new ArrayList<>(chr.getSummonsValues())) {
         if (summon.isStationary()) {
            chr.cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
         } else {
            removeMapObject(summon);
         }
      }

      if (chr.getDragon() != null) {
         removeMapObject(chr.getDragon());
         if (chr.isHidden()) {
            this.broadcastGMMessage(chr, new RemoveDragon(chr.getId()));
         } else {
            this.broadcastMessage(chr, new RemoveDragon(chr.getId()));
         }
      }
   }

   private void updateBossSpawn(MapleMonster monster) {
      if (monster.hasBossHPBar()) {
         broadcastBossHpMessage(monster, monster.hashCode(), monster.makeBossHPBarPacket(), monster.position());
      }
      if (monster.isBoss()) {
         if (relinquishOwnership() != null) {
            String mobName = MapleMonsterInformationProvider.getInstance().getMobNameFromId(monster.id());
            if (mobName != null) {
               mobName = mobName.trim();
               MessageBroadcaster.getInstance().sendMapServerNotice(this, ServerNoticeType.PINK_TEXT, I18nMessage.from("LAWN_SIEGE").with(mobName));
            }
         }
      }
   }

   public void broadcastBossHpMessage(MapleMonster mm, int bossHash, final byte[] packet) {
      broadcastBossHpMessage(mm, bossHash, null, packet, Double.POSITIVE_INFINITY, null);
   }

   public void broadcastBossHpMessage(MapleMonster mm, int bossHash, final byte[] packet, Point rangedFrom) {
      broadcastBossHpMessage(mm, bossHash, null, packet, MapleMapProcessor.getInstance().getRangedDistance(), rangedFrom);
   }

   private void broadcastBossHpMessage(MapleMonster mm, int bossHash, MapleCharacter source, final byte[] packet, double rangeSq, Point rangedFrom) {
      chrRLock.lock();
      try {
         for (MapleCharacter chr : characters) {
            if (chr != source) {
               if (rangeSq < Double.POSITIVE_INFINITY) {
                  if (rangedFrom.distanceSq(chr.position()) <= rangeSq) {
                     chr.getClient().announceBossHpBar(mm, bossHash, packet);
                  }
               } else {
                  chr.getClient().announceBossHpBar(mm, bossHash, packet);
               }
            }
         }
      } finally {
         chrRLock.unlock();
      }
   }

   //TODO JDT
   private void broadcastItemDropMessage(MapleMapItem mapItem, Point dropperPos, Point dropPos, byte mod, Point rangedFrom) {
      MasterBroadcaster.getInstance().sendToAllInMapRange(this, character -> PacketCreator.create(new DropItemFromMapObject(character, mapItem, dropperPos, dropPos, mod)), rangedFrom, chrRLock);
   }

   //TODO JDT
   private void broadcastItemDropMessage(MapleMapItem mapItem, Point dropperPos, Point dropPos, byte mod) {
      MasterBroadcaster.getInstance().sendToAllInMap(this, character -> PacketCreator.create(new DropItemFromMapObject(character, mapItem, dropperPos, dropPos, mod)));
   }

   //TODO JDT
   public void broadcastSpawnPlayerMapObjectMessage(MapleCharacter source, MapleCharacter player, boolean enteringField) {
      MasterBroadcaster.getInstance().sendToAllInMap(this, character -> PacketCreator.create(new SpawnPlayer(character.getClient(), player, enteringField)), false, source, chrRLock);
   }

   //TODO JDT
   public void broadcastGMSpawnPlayerMapObjectMessage(MapleCharacter source, MapleCharacter player, boolean enteringField) {
      MasterBroadcaster.getInstance().sendToAllGMInMap(this, character -> PacketCreator.create(new SpawnPlayer(character.getClient(), player, enteringField)), false, source, chrRLock);
   }

   //TODO JDT
   public void broadcastUpdateCharLookMessage(MapleCharacter source, MapleCharacter player) {
      MasterBroadcaster.getInstance().sendToAllInMap(this, character -> PacketCreator.create(new CharacterLook(character.getClient(), player)), false, source, chrRLock);
   }

   private void sendObjectPlacement(MapleClient mapleClient) {
      MapleCharacter chr = mapleClient.getPlayer();
      Collection<MapleMapObject> objects;

      objectRLock.lock();
      try {
         objects = new ArrayList<>(mapObjects.values());
      } finally {
         objectRLock.unlock();
      }

      for (MapleMapObject o : objects) {
         if (MapleMapObjectTypeProcessor.getInstance().isNonRangedType(o.type())) {
            MapleMapObjectProcessor.getInstance().sendSpawnData(o, mapleClient);
         } else if (o.type() == MapleMapObjectType.MONSTER) {
            ((MapleMonster) o).aggroUpdateController();
         } else if (o.type() == MapleMapObjectType.SUMMON) {
            MapleSummon summon = (MapleSummon) o;
            if (summon.getOwner() == chr) {
               if (chr.isSummonsEmpty() || !chr.containsSummon(summon)) {
                  objectWLock.lock();
                  try {
                     mapObjects.remove(o.objectId());
                  } finally {
                     objectWLock.unlock();
                  }

                  //continue;
               }
            }
         }
      }

      if (chr != null) {
         for (MapleMapObject o : getMapObjectsInRange(chr.position(), MapleMapProcessor.getInstance().getRangedDistance(), rangedMapObjectTypes)) {
            if (o.type() == MapleMapObjectType.REACTOR) {
               if (((MapleReactor) o).isAlive()) {
                  MapleMapObjectProcessor.getInstance().sendSpawnData(o, chr.getClient());
                  chr.addVisibleMapObject(o);
               }
            } else {
               MapleMapObjectProcessor.getInstance().sendSpawnData(o, chr.getClient());
               chr.addVisibleMapObject(o);
            }
         }
      }
   }

   public List<MapleMapObject> getMapObjectsInRange(Point from, double rangeSq, List<MapleMapObjectType> types) {
      objectRLock.lock();
      try {
         return mapObjects.values().stream()
               .filter(mapObject -> types.contains(mapObject.type()) && from.distanceSq(mapObject.position()) <= rangeSq)
               .collect(Collectors.toList());
      } finally {
         objectRLock.unlock();
      }
   }

   public List<MapleMapObject> getMapObjectsInBox(Rectangle box, List<MapleMapObjectType> types) {
      objectRLock.lock();
      try {
         return mapObjects.values().stream()
               .filter(mapObject -> types.contains(mapObject.type()) && box.contains(mapObject.position()))
               .collect(Collectors.toList());
      } finally {
         objectRLock.unlock();
      }
   }

   public void addPortal(MaplePortal myPortal) {
      portals.put(myPortal.getId(), myPortal);
   }

   public MaplePortal getPortal(String portalName) {
      return portals.values().stream()
            .filter(portal -> portal.getName().equals(portalName))
            .findFirst()
            .orElse(null);
   }

   public MaplePortal getPortal(int portalId) {
      return portals.get(portalId);
   }

   public void addMapleArea(Rectangle rec) {
      areas.add(rec);
   }

   public List<Rectangle> getAreas() {
      return new ArrayList<>(areas);
   }

   public Rectangle getArea(int index) {
      return areas.get(index);
   }

   public MapleFootholdTree getFootholds() {
      return footholds;
   }

   public void setFootholds(MapleFootholdTree footholds) {
      this.footholds = footholds;
   }

   public void setMapPointBounds(int px, int py, int h, int w) {
      mapArea.setBounds(px, py, w, h);
   }

   public void setMapLineBounds(int vrTop, int vrBottom, int vrLeft, int vrRight) {
      mapArea.setBounds(vrLeft, vrTop, vrRight - vrLeft, vrBottom - vrTop);
   }

   public MapleMonsterAggroCoordinator getAggroCoordinator() {
      return aggroMonitor;
   }

   public void addMonsterSpawn(MapleMonster monster, int mobTime, int team) {
      Point newPosition = calcPointBelow(monster.position());
      newPosition.y -= 1;
      SpawnPoint sp = new SpawnPoint(monster, newPosition, !monster.isMobile(), mobTime, mobInterval, team);
      monsterSpawn.add(sp);
      if (sp.shouldSpawn() || mobTime == -1) {// -1 does not respawn and should not either but force ONE spawn
         spawnMonster(sp.getMonster());
      }
   }

   public void addAllMonsterSpawn(MapleMonster monster, int mobTime, int team) {
      Point newPosition = calcPointBelow(monster.position());
      newPosition.y -= 1;
      SpawnPoint sp = new SpawnPoint(monster, newPosition, !monster.isMobile(), mobTime, mobInterval, team);
      allMonsterSpawn.add(sp);
   }

   public void removeMonsterSpawn(int mobId, int x, int y) {
      // assumption: spawn points are identified by tuple (life id, x, y)

      Point checkPosition = calcPointBelow(new Point(x, y));
      checkPosition.y -= 1;

      List<SpawnPoint> toRemove = getMonsterSpawn().stream()
            .filter(spawnPoint -> spawnPoint.getMonsterId() == mobId && checkPosition.equals(spawnPoint.getPosition()))
            .collect(Collectors.toList());

      if (!toRemove.isEmpty()) {
         synchronized (monsterSpawn) {
            toRemove.forEach(spawnPoint -> monsterSpawn.remove(spawnPoint));
         }
      }
   }

   public void removeAllMonsterSpawn(int mobId, int x, int y) {
      // assumption: spawn points are identified by tuple (life id, x, y)

      Point checkPosition = calcPointBelow(new Point(x, y));
      checkPosition.y -= 1;

      List<SpawnPoint> toRemove = getAllMonsterSpawn().stream()
            .filter(spawnPoint -> spawnPoint.getMonsterId() == mobId && checkPosition.equals(spawnPoint.getPosition()))
            .collect(Collectors.toList());

      if (!toRemove.isEmpty()) {
         synchronized (allMonsterSpawn) {
            toRemove.forEach(spawnPoint -> allMonsterSpawn.remove(spawnPoint));
         }
      }
   }

   private I18nMessage getSpawnPointMessage(SpawnPoint spawnPoint) {
      return I18nMessage.from("DEBUG_COMMAND_MOB_SPAWN_POINTS_BODY").with(spawnPoint.getMonsterId(),
            !spawnPoint.getDenySpawn(), spawnPoint.getSpawned(), spawnPoint.getPosition().getX(),
            spawnPoint.getPosition().getY(), new Date(spawnPoint.getMobTime()), spawnPoint.getTeam());
   }

   public void reportMonsterSpawnPoints(MapleCharacter chr) {
      MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("DEBUG_COMMAND_MOB_SPAWN_POINTS_TITLE").with(getId(), monsterSpawn.size(), spawnedMonstersOnMap.get()));
      getAllMonsterSpawn().stream()
            .map(this::getSpawnPointMessage)
            .forEach(message -> MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.LIGHT_BLUE, message));
   }

   public Map<Integer, MapleCharacter> getMapPlayers() {
      chrRLock.lock();
      try {
         return characters.stream().collect(Collectors.toMap(MapleCharacter::getId, character -> character));
      } finally {
         chrRLock.unlock();
      }
   }

   public Collection<MapleCharacter> getCharacters() {
      chrRLock.lock();
      try {
         return Collections.unmodifiableCollection(this.characters);
      } finally {
         chrRLock.unlock();
      }
   }

   public MapleCharacter getCharacterById(int id) {
      chrRLock.lock();
      try {
         return characters.stream().filter(character -> character.getId() == id).findFirst().orElse(null);
      } finally {
         chrRLock.unlock();
      }
   }

   public void moveMonster(MapleMonster monster, Point reportedPos) {
      monster.setPosition(reportedPos);
      getAllPlayers().forEach(character -> MapleMapObjectProcessor.getInstance().updateMapObjectVisibility(character, monster));
   }

   public void movePlayer(MapleCharacter player, Point newPosition) {
      player.setPosition(newPosition);

      try {
         MapleMapObject[] visibleObjects = player.getVisibleMapObjects();

         Map<Integer, MapleMapObject> mapObjects = getCopyMapObjects();
         for (MapleMapObject mo : visibleObjects) {
            if (mo != null) {
               if (mapObjects.get(mo.objectId()) == mo) {
                  MapleMapObjectProcessor.getInstance().updateMapObjectVisibility(player, mo);
               } else {
                  player.removeVisibleMapObject(mo);
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      for (MapleMapObject mo : getMapObjectsInRange(player.position(), MapleMapProcessor.getInstance().getRangedDistance(), rangedMapObjectTypes)) {
         if (!player.isMapObjectVisible(mo)) {
            MapleMapObjectProcessor.getInstance().sendSpawnData(mo, player.getClient());
            player.addVisibleMapObject(mo);
         }
      }
   }

   public final void toggleEnvironment(final String ms) {
      Map<String, Integer> env = getEnvironment();

      if (env.containsKey(ms)) {
         moveEnvironment(ms, env.get(ms) == 1 ? 2 : 1);
      } else {
         moveEnvironment(ms, 1);
      }
   }

   public final void moveEnvironment(final String ms, final int type) {
      MasterBroadcaster.getInstance().sendToAllInMap(this, new EnvironmentMove(ms, type));

      objectWLock.lock();
      try {
         environment.put(ms, type);
      } finally {
         objectWLock.unlock();
      }
   }

   public final Map<String, Integer> getEnvironment() {
      objectRLock.lock();
      try {
         return Collections.unmodifiableMap(environment);
      } finally {
         objectRLock.unlock();
      }
   }

   public String getMapName() {
      return mapName;
   }

   public void setMapName(String mapName) {
      this.mapName = mapName;
   }

   public String getStreetName() {
      return streetName;
   }

   public void setStreetName(String streetName) {
      this.streetName = streetName;
   }

   public void setClock(boolean hasClock) {
      this.clock = hasClock;
   }

   public boolean hasClock() {
      return clock;
   }

   public boolean isTown() {
      return town;
   }

   public void setTown(boolean isTown) {
      this.town = isTown;
   }

   public boolean isMuted() {
      return isMuted;
   }

   public void setMuted(boolean mute) {
      isMuted = mute;
   }

   public boolean getEverLast() {
      return everLast;
   }

   public void setEverLast(boolean everLast) {
      this.everLast = everLast;
   }

   public int getSpawnedMonstersOnMap() {
      return spawnedMonstersOnMap.get();
   }

   public void setMobCapacity(int capacity) {
      this.mobCapacity = capacity;
   }

   public void setBackgroundTypes(HashMap<Integer, Integer> backTypes) {
      backgroundTypes.putAll(backTypes);
   }

   // not really costly to keep generating imo
   public void sendNightEffect(MapleCharacter mc) {
      backgroundTypes.entrySet().stream()
            .filter(entry -> entry.getValue() >= 3)
            .forEach(entry -> PacketCreator.announce(mc, new ChangeBackgroundEffect(true, entry.getKey(), 0)));
   }

   public void broadcastNightEffect() {
      chrRLock.lock();
      try {
         characters.forEach(this::sendNightEffect);
      } finally {
         chrRLock.unlock();
      }
   }

   public MapleCharacter getCharacterByName(String name) {
      chrRLock.lock();
      try {
         return characters.stream()
               .filter(character -> character.getName().equalsIgnoreCase(name))
               .findFirst()
               .orElse(null);
      } finally {
         chrRLock.unlock();
      }
   }

   public boolean makeDisappearItemFromMap(MapleMapObject mapObject) {
      if (mapObject instanceof MapleMapItem) {
         return makeDisappearItemFromMap((MapleMapItem) mapObject);
      } else {
         return mapObject == null;  // no drop to make disappear...
      }
   }

   public boolean makeDisappearItemFromMap(MapleMapItem mapItem) {
      if (mapItem != null && mapItem == getMapObject(mapItem.objectId())) {
         mapItem.lockItem();
         try {
            if (mapItem.isPickedUp()) {
               return true;
            }

            MapleMap.this.pickItemDrop(new RemoveItem(mapItem.objectId(), 0, 0), mapItem);
            return true;
         } finally {
            mapItem.unlockItem();
         }
      }

      return false;
   }

   public void instanceMapFirstSpawn(int difficulty, boolean isPq) {
      getAllMonsterSpawn().stream()
            .filter(spawnPoint -> spawnPoint.getMobTime() == -1)
            .forEach(spawnPoint -> spawnMonster(spawnPoint.getMonster()));
   }

   public void instanceMapRespawn() {
      if (!allowSummons) {
         return;
      }

      final int numShouldSpawn = (short) ((monsterSpawn.size() - spawnedMonstersOnMap.get()));
      if (numShouldSpawn > 0) {
         List<SpawnPoint> randomSpawn = getMonsterSpawn();
         Collections.shuffle(randomSpawn);
         int spawned = 0;
         for (SpawnPoint spawnPoint : randomSpawn) {
            if (spawnPoint.shouldSpawn()) {
               spawnMonster(spawnPoint.getMonster());
               spawned++;
               if (spawned >= numShouldSpawn) {
                  break;
               }
            }
         }
      }
   }

   public void instanceMapForceRespawn() {
      if (!allowSummons) {
         return;
      }

      final int numShouldSpawn = (short) ((monsterSpawn.size() - spawnedMonstersOnMap.get()));
      if (numShouldSpawn > 0) {
         List<SpawnPoint> randomSpawn = getMonsterSpawn();
         Collections.shuffle(randomSpawn);
         int spawned = 0;
         for (SpawnPoint spawnPoint : randomSpawn) {
            if (spawnPoint.shouldForceSpawn()) {
               spawnMonster(spawnPoint.getMonster());
               spawned++;
               if (spawned >= numShouldSpawn) {
                  break;
               }
            }
         }
      }
   }

   public void closeMapSpawnPoints() {
      getMonsterSpawn().forEach(spawnPoint -> spawnPoint.setDenySpawn(true));
   }

   public void restoreMapSpawnPoints() {
      getMonsterSpawn().forEach(spawnPoint -> spawnPoint.setDenySpawn(false));
   }

   public void setAllowSpawnPointInBox(boolean allow, Rectangle box) {
      getMonsterSpawn().stream()
            .filter(spawnPoint -> box.contains(spawnPoint.getPosition()))
            .forEach(spawnPoint -> spawnPoint.setDenySpawn(!allow));
   }

   public void setAllowSpawnPointInRange(boolean allow, Point from, double rangeSq) {
      getMonsterSpawn().stream()
            .filter(spawnPoint -> from.distanceSq(spawnPoint.getPosition()) <= rangeSq)
            .forEach(spawnPoint -> spawnPoint.setDenySpawn(!allow));
   }

   public SpawnPoint findClosestSpawnPoint(Point from) {
      SpawnPoint closest = null;
      double shortestDistance = Double.POSITIVE_INFINITY;
      for (SpawnPoint sp : getMonsterSpawn()) {
         double distance = sp.getPosition().distanceSq(from);
         if (distance < shortestDistance) {
            closest = sp;
            shortestDistance = distance;
         }
      }
      return closest;
   }

   private int getNumShouldSpawn(int numPlayers) {
        /*
        System.out.println("----------------------------------");
        for (SpawnPoint spawnPoint : getMonsterSpawn()) {
            System.out.println("sp " + spawnPoint.getPosition().getX() + ", " + spawnPoint.getPosition().getY() + ": " + spawnPoint.getDenySpawn());
        }
        System.out.println("try " + monsterSpawn.size() + " - " + spawnedMonstersOnMap.get());
        System.out.println("----------------------------------");
        */

      if (YamlConfig.config.server.USE_ENABLE_FULL_RESPAWN) {
         return (monsterSpawn.size() - spawnedMonstersOnMap.get());
      }

      int maxNumShouldSpawn = (int) Math.ceil(MapleMapProcessor.getInstance().getCurrentSpawnRate(numPlayers) * monsterSpawn.size());
      return maxNumShouldSpawn - spawnedMonstersOnMap.get();
   }

   public void respawn() {
      if (!allowSummons) {
         return;
      }

      int numPlayers;
      chrRLock.lock();
      try {
         numPlayers = characters.size();

         if (numPlayers == 0) {
            return;
         }
      } finally {
         chrRLock.unlock();
      }

      int numShouldSpawn = getNumShouldSpawn(numPlayers);
      if (numShouldSpawn > 0) {
         List<SpawnPoint> randomSpawn = new ArrayList<>(getMonsterSpawn());
         Collections.shuffle(randomSpawn);
         short spawned = 0;
         for (SpawnPoint spawnPoint : randomSpawn) {
            if (spawnPoint.shouldSpawn()) {
               spawnMonster(spawnPoint.getMonster());
               spawned++;

               if (spawned >= numShouldSpawn) {
                  break;
               }
            }
         }
      }
   }

   public void mobMpRecovery() {
      getAllMonsters().stream()
            .filter(MapleMonster::isAlive)
            .forEach(monster -> monster.heal(0, monster.getLevel()));
   }

   public final int getNumPlayersInArea(final int index) {
      return getNumPlayersInRect(getArea(index));
   }

   public final int getNumPlayersInRect(final Rectangle rect) {
      chrRLock.lock();
      try {
         return (int) characters.stream()
               .filter(character -> rect.contains(character.position()))
               .count();
      } finally {
         chrRLock.unlock();
      }
   }

   public final int getNumPlayersItemsInArea(final int index) {
      return getNumPlayersItemsInRect(getArea(index));
   }

   public final int getNumPlayersItemsInRect(final Rectangle rect) {
      int retP = getNumPlayersInRect(rect);
      int retI = getMapObjectsInBox(rect, Collections.singletonList(MapleMapObjectType.ITEM)).size();

      return retP + retI;
   }

   public int getHPDec() {
      return decHP;
   }

   public void setHPDec(int delta) {
      decHP = delta;
   }

   public int getHPDecProtect() {
      return protectItem;
   }

   public void setHPDecProtect(int delta) {
      this.protectItem = delta;
   }

   public float getRecovery() {
      return recovery;
   }

   public void setRecovery(float recRate) {
      recovery = recRate;
   }

   private int hasBoat() {
      return !boat ? 0 : (docked ? 1 : 2);
   }

   public void setBoat(boolean hasBoat) {
      this.boat = hasBoat;
   }

   public boolean getDocked() {
      return this.docked;
   }

   public void setDocked(boolean isDocked) {
      this.docked = isDocked;
   }

   public int getSeats() {
      return seats;
   }

   public void setSeats(int seats) {
      this.seats = seats;
   }

   //TODO JDT
   public void broadcastGMMessage(MapleCharacter source, PacketInput packet, boolean repeatToSource) {
      MasterBroadcaster.getInstance().sendToAllGMInMap(this, packet, false, source, chrRLock);
   }

   //TODO JDT
   public void broadcastNONGMMessage(MapleCharacter source, PacketInput packet, boolean repeatToSource) {
      MasterBroadcaster.getInstance().sendToAllNonGMInMap(this, packet, false, source, chrRLock);
   }

   public MapleOxQuiz getOx() {
      return ox;
   }

   public void setOx(MapleOxQuiz set) {
      this.ox = set;
   }

   public boolean isOxQuiz() {
      return isOxQuiz;
   }

   public void setOxQuiz(boolean b) {
      this.isOxQuiz = b;
   }

   public String getOnUserEnter() {
      return onUserEnter;
   }

   public void setOnUserEnter(String onUserEnter) {
      this.onUserEnter = onUserEnter;
   }

   public String getOnFirstUserEnter() {
      return onFirstUserEnter;
   }

   public void setOnFirstUserEnter(String onFirstUserEnter) {
      this.onFirstUserEnter = onFirstUserEnter;
   }

   private boolean hasForcedEquip() {
      return fieldType == 81 || fieldType == 82;
   }

   public void setFieldType(int fieldType) {
      this.fieldType = fieldType;
   }

   public void clearDrops(MapleCharacter player) {
      List<MapleMapObject> inRangeObjects = getMapObjectsInRange(player.position(), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.ITEM));
      inRangeObjects.forEach(item -> clearDrop(item, player.getId()));
   }

   public void clearDrops() {
      List<MapleMapObject> inRangeObjects = getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.ITEM));
      inRangeObjects.forEach(item -> clearDrop(item, 0));
   }

   private void clearDrop(MapleMapObject i, int characterId) {
      droppedItemCount.decrementAndGet();
      removeMapObject(i);
      MasterBroadcaster.getInstance().sendToAllInMap(this, new RemoveItem(i.objectId(), 0, characterId));
   }

   public int getFieldLimit() {
      return fieldLimit;
   }

   public void setFieldLimit(int fieldLimit) {
      this.fieldLimit = fieldLimit;
   }

   public void allowSummonState(boolean b) {
      MapleMap.this.allowSummons = b;
   }

   public boolean getSummonState() {
      return MapleMap.this.allowSummons;
   }

   public void warpEveryone(int to) {
      List<MapleCharacter> players = new ArrayList<>(getCharacters());
      players.forEach(character -> character.changeMap(to));
   }

   public void warpEveryone(int to, int pto) {
      List<MapleCharacter> players = new ArrayList<>(getCharacters());
      players.forEach(character -> character.changeMap(to, pto));
   }

   // BEGIN EVENTS
   public void setSnowball(int team, MapleSnowball ball) {
      switch (team) {
         case 0:
            this.snowball0 = ball;
            break;
         case 1:
            this.snowball1 = ball;
            break;
         default:
            break;
      }
   }

   public MapleSnowball getSnowball(int team) {
      return switch (team) {
         case 0 -> snowball0;
         case 1 -> snowball1;
         default -> null;
      };
   }

   private boolean specialEquip() {//Maybe I shouldn't use fieldType :\
      return fieldType == 4 || fieldType == 19;
   }

   public MapleCoconut getCoconut() {
      return coconut;
   }

   public void setCoconut(MapleCoconut nut) {
      this.coconut = nut;
   }

   public void warpOutByTeam(int team, int mapId) {
      List<MapleCharacter> chars = new ArrayList<>(getCharacters());
      chars.stream()
            .filter(character -> character != null && character.getTeam() == team)
            .forEach(character -> character.changeMap(mapId));
   }

   public void startEvent(final MapleCharacter chr) {
      if (this.mapId == 109080000 && getCoconut() == null) {
         setCoconut(new MapleCoconut(this));
         coconut.startEvent();
      } else if (this.mapId == 109040000) {
         chr.setFitness(new MapleFitness(chr));
         chr.getFitness().startFitness();
      } else if (this.mapId == 109030101 || this.mapId == 109030201 || this.mapId == 109030301 || this.mapId == 109030401) {
         chr.setOla(new MapleOla(chr));
         chr.getOla().startOla();
      } else if (this.mapId == 109020001 && getOx() == null) {
         setOx(new MapleOxQuiz(this));
         getOx().sendQuestion();
         setOxQuiz(true);
      } else if (this.mapId == 109060000 && getSnowball(chr.getTeam()) == null) {
         setSnowball(0, new MapleSnowball(0, this));
         setSnowball(1, new MapleSnowball(1, this));
         getSnowball(chr.getTeam()).startEvent();
      }
   }

   public boolean eventStarted() {
      return eventStarted;
   }

   public void startEvent() {
      this.eventStarted = true;
   }

   public void setEventStarted(boolean event) {
      this.eventStarted = event;
   }

   public String getEventNPC() {
      StringBuilder sb = new StringBuilder();
      sb.append("Talk to ");
      if (mapId == 60000) {
         sb.append("Paul!");
      } else if (mapId == 104000000) {
         sb.append("Jean!");
      } else if (mapId == 200000000) {
         sb.append("Martin!");
      } else if (mapId == 220000000) {
         sb.append("Tony!");
      } else {
         return null;
      }
      return sb.toString();
   }

   public boolean hasEventNPC() {
      return this.mapId == 60000 || this.mapId == 104000000 || this.mapId == 200000000 || this.mapId == 220000000;
   }

   public boolean isStartingEventMap() {
      return this.mapId == 109040000 || this.mapId == 109020001 || this.mapId == 109010000 || this.mapId == 109030001 || this.mapId == 109030101;
   }

   public boolean isEventMap() {
      return this.mapId >= 109010000 && this.mapId < 109050000 || this.mapId > 109050001 && this.mapId <= 109090000;
   }

   public void setTimeMob(int id, String msg) {
      timeMob = new Pair<>(id, msg);
   }

   public Pair<Integer, String> getTimeMob() {
      return timeMob;
   }

   public void toggleHiddenNPC(int id) {
      chrRLock.lock();
      objectRLock.lock();
      try {
         for (MapleMapObject obj : mapObjects.values()) {
            if (obj.type() == MapleMapObjectType.NPC) {
               MapleNPC npc = (MapleNPC) obj;
               if (npc.id() == id) {
                  npc.setHide(!npc.hide());
                  if (!npc.hide()) //Should only be hidden upon changing maps
                  {
                     MasterBroadcaster.getInstance().sendToAllInMap(this, new SpawnNPC(npc));
                  }
               }
            }
         }
      } finally {
         objectRLock.unlock();
         chrRLock.unlock();
      }
   }

   public short getMobInterval() {
      return mobInterval;
   }

   public void setMobInterval(short interval) {
      this.mobInterval = interval;
   }

   public void clearMapObjects() {
      clearDrops();
      killAllMonsters();
      resetReactors();
   }

   public final void resetFully() {
      resetMapObjects();
   }

   public void resetMapObjects() {
      resetMapObjects(1, false);
   }

   public void resetPQ() {
      resetPQ(1);
   }

   public void resetPQ(int difficulty) {
      resetMapObjects(difficulty, true);
   }

   public void resetMapObjects(int difficulty, boolean isPq) {
      clearMapObjects();

      restoreMapSpawnPoints();
      instanceMapFirstSpawn(difficulty, isPq);
   }

   public void broadcastShip(final boolean state) {
      MasterBroadcaster.getInstance().sendToAllInMap(this, new Boat(state));
      this.setDocked(state);
   }

   public void broadcastEnemyShip(final boolean state) {
      MasterBroadcaster.getInstance().sendToAllInMap(this, new CrimsonBalrogBoat(state));
      this.setDocked(state);
   }

   public boolean isHorntailDefeated() {   // all parts of dead horntail can be found here?
      for (int i = 8810010; i <= 8810017; i++) {
         if (getMonsterById(i) == null) {
            return false;
         }
      }

      return true;
   }

   public void spawnHorntailOnGroundBelow(final Point targetPoint) {
      MapleLifeFactory.getMonster(8810026).ifPresent(htIntro -> {
         spawnMonsterOnGroundBelow(htIntro, targetPoint);

         MapleLifeFactory.getMonster(8810018).ifPresent(ht -> {
            ht.setParentMobOid(htIntro.objectId());
            ht.addListener(new MonsterListener() {
               @Override
               public void monsterKilled(int aniTime) {
               }

               @Override
               public void monsterDamaged(MapleCharacter from, int trueDmg) {
                  ht.addHp(trueDmg);
               }

               @Override
               public void monsterHealed(int trueHeal) {
                  ht.addHp(-trueHeal);
               }
            });
            spawnMonsterOnGroundBelow(ht, targetPoint);

            IntStream.rangeClosed(8810002, 8810009)
                  .mapToObj(MapleLifeFactory::getMonster)
                  .flatMap(Optional::stream)
                  .filter(Objects::nonNull)
                  .forEach(monster -> {
                     monster.setParentMobOid(htIntro.objectId());
                     monster.addListener(new MonsterListener() {
                        @Override
                        public void monsterKilled(int aniTime) {
                        }

                        @Override
                        public void monsterDamaged(MapleCharacter from, int trueDmg) {
                           ht.applyFakeDamage(from, trueDmg, true);
                        }

                        @Override
                        public void monsterHealed(int trueHeal) {
                           ht.addHp(trueHeal);
                        }
                     });

                     spawnMonsterOnGroundBelow(monster, targetPoint);
                  });
         });
      });
   }

   public boolean claimOwnership(MapleCharacter chr) {
      if (mapOwner == null) {
         this.mapOwner = chr;
         chr.setOwnedMap(this);

         mapOwnerLastActivityTime = Server.getInstance().getCurrentTime();

         getChannelServer().registerOwnedMap(this);
         return true;
      } else {
         return chr == mapOwner;
      }
   }

   public MapleCharacter relinquishOwnership() {
      MapleCharacter lastOwner = this.mapOwner;
      return relinquishOwnership(lastOwner) ? lastOwner : null;
   }

   public boolean relinquishOwnership(MapleCharacter chr) {
      if (chr != null && mapOwner == chr) {
         this.mapOwner = null;
         chr.setOwnedMap(null);

         mapOwnerLastActivityTime = Long.MAX_VALUE;

         getChannelServer().unregisterOwnedMap(this);
         return true;
      } else {
         return false;
      }
   }

   private void refreshOwnership() {
      mapOwnerLastActivityTime = Server.getInstance().getCurrentTime();
   }

   public boolean isOwnershipRestricted(MapleCharacter chr) {
      MapleCharacter owner = mapOwner;

      if (owner != null) {
         if (owner != chr && !owner.isPartyMember(chr)) {
            chr.showMapOwnershipInfo(owner);
            return true;
         } else {
            this.refreshOwnership();
         }
      }

      return false;
   }

   public void checkMapOwnerActivity() {
      long timeNow = Server.getInstance().getCurrentTime();
      if (timeNow - mapOwnerLastActivityTime > 60000) {
         if (relinquishOwnership() != null) {
            MessageBroadcaster.getInstance().sendMapServerNotice(this, ServerNoticeType.PINK_TEXT, I18nMessage.from("LAWN_IS_FREE"));
         }
      }
   }

   public List<MCSkill> getBlueTeamBuffs() {
      return blueTeamBuffs;
   }

   public List<MCSkill> getRedTeamBuffs() {
      return redTeamBuffs;
   }

   public void clearBuffList() {
      redTeamBuffs.clear();
      blueTeamBuffs.clear();
   }

   public List<MapleMapObject> getAllPlayer() {
      return getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Collections.singletonList(MapleMapObjectType.PLAYER));
   }

   public boolean isCPQMap() {
      return switch (this.getId()) {
         case 980000101, 980000201, 980000301, 980000401, 980000501, 980000601, 980031100, 980032100, 980033100 -> true;
         default -> false;
      };
   }

   public boolean isCPQMap2() {
      return switch (this.getId()) {
         case 980031100, 980032100, 980033100 -> true;
         default -> false;
      };
   }

   public boolean isCPQLobby() {
      return switch (this.getId()) {
         case 980000100, 980000200, 980000300, 980000400, 980000500, 980000600 -> true;
         default -> false;
      };
   }

   public boolean isBlueCPQMap() {
      return switch (this.getId()) {
         case 980000501, 980000601, 980031200, 980032200, 980033200 -> true;
         default -> false;
      };
   }

   public boolean isPurpleCPQMap() {
      return switch (this.getId()) {
         case 980000301, 980000401, 980031200, 980032200, 980033200 -> true;
         default -> false;
      };
   }

   public Point getRandomSP(int team) {
      if (takenSpawns.size() > 0) {
         for (SpawnPoint sp : monsterSpawn) {
            for (Point pt : takenSpawns) {
               if ((sp.getPosition().x == pt.x && sp.getPosition().y == pt.y) || (sp.getTeam() != team && !this.isBlueCPQMap())) {
               } else {
                  takenSpawns.add(pt);
                  return sp.getPosition();
               }
            }
         }
      } else {
         for (SpawnPoint sp : monsterSpawn) {
            if (sp.getTeam() == team || this.isBlueCPQMap()) {
               takenSpawns.add(sp.getPosition());
               return sp.getPosition();
            }
         }
      }
      return null;
   }

   public GuardianSpawnPoint getRandomGuardianSpawn(int team) {
      boolean allTaken = false;
      for (GuardianSpawnPoint a : this.guardianSpawns) {
         if (!a.taken()) {
            allTaken = false;
            break;
         }
      }
      if (allTaken) {
         return null;
      }
      if (this.guardianSpawns.size() > 0) {
         while (true) {
            for (GuardianSpawnPoint gsp : this.guardianSpawns) {
               if (!gsp.taken() && Math.random() < 0.3 && (gsp.team() == -1 || gsp.team() == team)) {
                  return gsp;
               }
            }
         }
      }
      return null;
   }

   public void addGuardianSpawnPoint(GuardianSpawnPoint a) {
      this.guardianSpawns.add(a);
   }

   public int spawnGuardian(int team, int num) {
      try {
         if (team == 0 && redTeamBuffs.size() >= 4 || team == 1 && blueTeamBuffs.size() >= 4) {
            return 2;
         }
         final MCSkill skill = MapleCarnivalFactory.getInstance().getGuardian(num);
         if (team == 0 && redTeamBuffs.contains(skill)) {
            return 0;
         } else if (team == 1 && blueTeamBuffs.contains(skill)) {
            return 0;
         }
         GuardianSpawnPoint pt = this.getRandomGuardianSpawn(team);
         if (pt == null) {
            return -1;
         }
         int reactorID = 9980000 + team;
         MapleReactor reactor = new MapleReactor(MapleReactorFactory.getReactorS(reactorID), reactorID);
         pt.setTaken(true);
         reactor.setPosition(pt.position());
         reactor.setName(team + "" + num); //lol
         reactor.resetReactorActions(0);
         this.spawnReactor(reactor);
         reactor.setGuardian(pt);
         this.buffMonsters(team, skill);
         getReactorByOid(reactor.objectId()).hitReactor(((MapleCharacter) this.getAllPlayer().get(0)).getClient());
      } catch (Exception e) {
         e.printStackTrace();
      }
      return 1;
   }

   public void buffMonsters(int team, MCSkill skill) {
      if (skill == null) {
         return;
      }

      if (team == 0) {
         redTeamBuffs.add(skill);
      } else if (team == 1) {
         blueTeamBuffs.add(skill);
      }

      mapObjects.values().stream()
            .filter(mapObject -> mapObject.type() == MapleMapObjectType.MONSTER)
            .map(mapObject -> (MapleMonster) mapObject)
            .filter(monster -> monster.getTeam() == team)
            .forEach(monster -> MobSkillProcessor.getInstance().applyEffect(null, monster, skill.getSkill(), false, null));
   }

   public final List<Integer> getSkillIds() {
      return skillIds;
   }

   public final void addSkillId(int z) {
      this.skillIds.add(z);
   }

   public final void addMobSpawn(int mobId, int spendCP) {
      this.mobsToSpawn.add(new Pair<>(mobId, spendCP));
   }

   public final List<Pair<Integer, Integer>> getMobsToSpawn() {
      return mobsToSpawn;
   }

   public boolean isCPQWinnerMap() {
      return switch (this.getId()) {
         case 980000103, 980000203, 980000303, 980000403, 980000503, 980000603, 980031300, 980032300, 980033300 -> true;
         default -> false;
      };
   }

   public boolean isCPQLoserMap() {
      return switch (this.getId()) {
         case 980000104, 980000204, 980000304, 980000404, 980000504, 980000604, 980031400, 980032400, 980033400 -> true;
         default -> false;
      };
   }

   public void runCharacterStatUpdate() {
      if (!statUpdateRunnables.isEmpty()) {
         List<Runnable> toRun = new ArrayList<>(statUpdateRunnables);
         statUpdateRunnables.clear();
         toRun.forEach(Runnable::run);
      }
   }

   public void registerCharacterStatUpdate(Runnable r) {
      statUpdateRunnables.add(r);
   }

   public void dispose() {
      getAllMonsters().forEach(MapleMonster::dispose);

      clearMapObjects();

      event = null;
      footholds = null;
      portals.clear();
      mapEffect = null;

      chrWLock.lock();
      try {
         aggroMonitor.dispose();
         aggroMonitor = null;

         if (itemMonitor != null) {
            itemMonitor.cancel(false);
            itemMonitor = null;
         }

         if (expireItemsTask != null) {
            expireItemsTask.cancel(false);
            expireItemsTask = null;
         }

         if (mobSpawnLootTask != null) {
            mobSpawnLootTask.cancel(false);
            mobSpawnLootTask = null;
         }

         if (characterStatUpdateTask != null) {
            characterStatUpdateTask.cancel(false);
            characterStatUpdateTask = null;
         }
      } finally {
         chrWLock.unlock();
      }
   }

   public int getMaxMobs() {
      return maxMobs;
   }

   public void setMaxMobs(int maxMobs) {
      this.maxMobs = maxMobs;
   }

   public int getMaxReactors() {
      return maxReactors;
   }

   public void setMaxReactors(int maxReactors) {
      this.maxReactors = maxReactors;
   }

   public int getDeathCP() {
      return deathCP;
   }

   public void setDeathCP(int deathCP) {
      this.deathCP = deathCP;
   }

   public int getTimeDefault() {
      return timeDefault;
   }

   public void setTimeDefault(int timeDefault) {
      this.timeDefault = timeDefault;
   }

   public int getTimeExpand() {
      return timeExpand;
   }

   public void setTimeExpand(int timeExpand) {
      this.timeExpand = timeExpand;
   }

   private interface DelayedPacketCreation {

      void sendPackets(MapleClient c);
   }

   private interface SpawnCondition {

      boolean canSpawn(MapleCharacter chr);
   }

   private class MobLootEntry implements Runnable {

      private byte dropType;
      private int mobPosition;
      private int chRate;
      private Point pos;
      private List<MonsterDropEntry> dropEntry;
      private List<MonsterDropEntry> visibleQuestEntry;
      private List<MonsterDropEntry> otherQuestEntry;
      private List<MonsterGlobalDropEntry> globalEntry;
      private MapleCharacter chr;
      private MapleMonster mob;

      protected MobLootEntry(byte dropType, int mobPosition, int chRate, Point pos, List<MonsterDropEntry> dropEntry, List<MonsterDropEntry> visibleQuestEntry, List<MonsterDropEntry> otherQuestEntry, List<MonsterGlobalDropEntry> globalEntry, MapleCharacter chr, MapleMonster mob) {
         this.dropType = dropType;
         this.mobPosition = mobPosition;
         this.chRate = chRate;
         this.pos = pos;
         this.dropEntry = dropEntry;
         this.visibleQuestEntry = visibleQuestEntry;
         this.otherQuestEntry = otherQuestEntry;
         this.globalEntry = globalEntry;
         this.chr = chr;
         this.mob = mob;
      }

      @Override
      public void run() {
         byte d = 1;

         // Normal Drops
         d = dropItemsFromMonsterOnMap(dropEntry, pos, d, chRate, dropType, mobPosition, chr, mob);

         // Global Drops
         d = dropGlobalItemsFromMonsterOnMap(globalEntry, pos, d, dropType, mobPosition, chr, mob);

         // Quest Drops
         d = dropItemsFromMonsterOnMap(visibleQuestEntry, pos, d, chRate, dropType, mobPosition, chr, mob);
         dropItemsFromMonsterOnMap(otherQuestEntry, pos, d, chRate, dropType, mobPosition, chr, mob);
      }
   }

   private class ActivateItemReactor implements Runnable {

      private MapleMapItem mapItem;
      private MapleReactor reactor;
      private MapleClient c;

      public ActivateItemReactor(MapleMapItem mapItem, MapleReactor reactor, MapleClient c) {
         this.mapItem = mapItem;
         this.reactor = reactor;
         this.c = c;
      }

      @Override
      public void run() {
         reactor.hitLockReactor();
         try {
            if (reactor.getReactorType() == 100) {
               if (reactor.getShouldCollect() && mapItem != null && mapItem == getMapObject(mapItem.objectId())) {
                  mapItem.lockItem();
                  try {
                     if (mapItem.isPickedUp()) {
                        return;
                     }
                     mapItem.setPickedUp(true);
                     unregisterItemDrop(mapItem);

                     reactor.setShouldCollect(false);
                     MasterBroadcaster.getInstance().sendToAllInMapRange(MapleMap.this, new RemoveItem(mapItem.objectId(), 0, 0), mapItem.position());

                     droppedItemCount.decrementAndGet();
                     MapleMap.this.removeMapObject(mapItem);

                     reactor.hitReactor(c);

                     if (reactor.getDelay() > 0) {
                        MapleMap reactorMap = reactor.getMap();

                        OverallService service = (OverallService) reactorMap.getChannelServer().getServiceAccess(ChannelServices.OVERALL);
                        service.registerOverallAction(reactorMap.getId(), () -> {
                           reactor.lockReactor();
                           try {
                              reactor.resetReactorActions(0);
                              reactor.setAlive(true);
                              MasterBroadcaster.getInstance().sendToAllInMap(MapleMap.this, new TriggerReactor(reactor, 0));
                           } finally {
                              reactor.unlockReactor();
                           }
                        }, reactor.getDelay());
                     }
                  } finally {
                     mapItem.unlockItem();
                  }
               }
            }
         } finally {
            reactor.hitUnlockReactor();
         }
      }
   }

}
