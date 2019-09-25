/* 
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation version 3 as published by
 the Free Software Foundation. You may not use, modify or distribute
 this program under any otheer version of the GNU Affero General Public
 License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; witout even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.


 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package client;

import static client.ServerNoticeConstants.LEVEL_200;

import java.awt.Point;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.apache.mina.util.ConcurrentHashSet;

import client.autoban.AutobanManager;
import client.database.administrator.AccountAdministrator;
import client.database.administrator.AreaInfoAdministrator;
import client.database.administrator.BuddyAdministrator;
import client.database.administrator.CharacterAdministrator;
import client.database.administrator.CoolDownAdministrator;
import client.database.administrator.EventStatAdministrator;
import client.database.administrator.FameLogAdministrator;
import client.database.administrator.GuildAdministrator;
import client.database.administrator.KeyMapAdministrator;
import client.database.administrator.MedalMapAdministrator;
import client.database.administrator.NameChangeAdministrator;
import client.database.administrator.PetIgnoreAdministrator;
import client.database.administrator.PlayerDiseaseAdministrator;
import client.database.administrator.QuestProgressAdministrator;
import client.database.administrator.QuestStatusAdministrator;
import client.database.administrator.SavedLocationAdministrator;
import client.database.administrator.SkillAdministrator;
import client.database.administrator.SkillMacroAdministrator;
import client.database.administrator.TeleportRockLocationAdministrator;
import client.database.administrator.WorldTransferAdministrator;
import client.database.data.GameData;
import client.database.provider.AccountProvider;
import client.database.provider.CharacterProvider;
import client.database.provider.FredStorageProvider;
import client.database.provider.NameChangeProvider;
import client.database.provider.NoteProvider;
import client.database.provider.WorldTransferProvider;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryProof;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.MapleWeaponType;
import client.inventory.ModifyInventory;
import client.inventory.PetDataFactory;
import client.inventory.StatUpgrade;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.newyear.NewYearCardRecord;
import client.processor.BuffStatProcessor;
import client.processor.ChairProcessor;
import client.processor.CharacterProcessor;
import client.processor.ItemProcessor;
import client.processor.MapleFamilyProcessor;
import client.processor.MapleJobProcessor;
import client.processor.PartyProcessor;
import client.processor.PetAutopotProcessor;
import client.processor.PetProcessor;
import client.processor.SkillProcessor;
import constants.ExpTable;
import constants.GameConstants;
import constants.ItemConstants;
import constants.ServerConstants;
import constants.skills.Aran;
import constants.skills.Bishop;
import constants.skills.BlazeWizard;
import constants.skills.Bowmaster;
import constants.skills.Brawler;
import constants.skills.Buccaneer;
import constants.skills.Corsair;
import constants.skills.Crusader;
import constants.skills.DarkKnight;
import constants.skills.DawnWarrior;
import constants.skills.Evan;
import constants.skills.FPArchMage;
import constants.skills.Hero;
import constants.skills.ILArchMage;
import constants.skills.Magician;
import constants.skills.Marauder;
import constants.skills.Marksman;
import constants.skills.NightLord;
import constants.skills.Paladin;
import constants.skills.Priest;
import constants.skills.Shadower;
import constants.skills.ThunderBreaker;
import constants.skills.Warrior;
import net.server.PlayerBuffValueHolder;
import net.server.PlayerCoolDownValueHolder;
import net.server.Server;
import net.server.SkillMacro;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.coordinator.MapleInviteCoordinator;
import net.server.guild.MapleAlliance;
import net.server.guild.MapleGuild;
import net.server.guild.MapleGuildCharacter;
import net.server.processor.MapleGuildProcessor;
import net.server.processor.MaplePartyProcessor;
import net.server.world.MapleMessenger;
import net.server.world.MapleMessengerCharacter;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import net.server.world.PartyOperation;
import net.server.world.World;
import scripting.AbstractPlayerInteraction;
import scripting.event.EventInstanceManager;
import scripting.item.ItemScriptManager;
import server.CashShop;
import server.MapleItemInformationProvider;
import server.MapleItemInformationProvider.ScriptedItem;
import server.MapleMarriage;
import server.MapleShop;
import server.MapleStatEffect;
import server.MapleStorage;
import server.MapleTrade;
import server.ThreadManager;
import server.TimerManager;
import server.events.MapleEvents;
import server.events.gm.MapleFitness;
import server.events.gm.MapleOla;
import server.life.MapleMonster;
import server.life.MaplePlayerNPC;
import server.life.MobSkill;
import server.maps.FieldLimit;
import server.maps.MapleDoor;
import server.maps.MapleDragon;
import server.maps.MapleHiredMerchant;
import server.maps.MapleMap;
import server.maps.MapleMapEffect;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleMiniGame;
import server.maps.MapleMiniGame.MiniGameResult;
import server.maps.MaplePlayerShop;
import server.maps.MaplePlayerShopItem;
import server.maps.MaplePortal;
import server.maps.MapleSummon;
import server.maps.SavedLocation;
import server.maps.SavedLocationType;
import server.minigame.MapleRockPaperScissor;
import server.partyquest.AriantColiseum;
import server.partyquest.MonsterCarnival;
import server.partyquest.MonsterCarnivalParty;
import server.partyquest.PartyQuest;
import server.processor.maps.MapleDoorProcessor;
import server.quest.MapleQuest;
import tools.DatabaseConnection;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.MapleStringUtil;
import tools.MessageBroadcaster;
import tools.Pair;
import tools.Randomizer;
import tools.ServerNoticeType;
import tools.StringUtil;
import tools.exceptions.NotEnabledException;
import tools.packets.Wedding;

public class MapleCharacter extends AbstractMapleCharacterObject {
   private static final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
   private final Map<Short, MapleQuestStatus> quests;
   private AriantColiseum ariantColiseum;
   private short totalCP, availableCP;
   private int world;
   private int accountid, id, level;
   private int rank, rankMove, jobRank, jobRankMove;
   private int gender, hair, face;
   private int fame, questFame;
   private int initialSpawnPoint;
   private int mapid;
   private int currentPage, currentType = 0, currentTab = 1;
   private int itemEffect;
   private int guildid, guildRank, allianceRank;
   private int messengerposition = 4;
   private int slots = 0;
   private int energybar;
   private int gmLevel;
   private int ci = 0;
   private MapleFamilyEntry familyEntry;
   private int familyId;
   private int bookCover;
   private int battleShipHp = 0;
   private int mesosTraded = 0;
   private int possibleReports = 10;
   private int ariantPoints, dojoPoints, vanquisherStage, dojoStage, dojoEnergy, vanquisherKills;
   private int expRate = 1, mesoRate = 1, dropRate = 1, expCoupon = 1, mesoCoupon = 1, dropCoupon = 1;
   private GameData omok;
   private GameData matchCard;
   private int owlSearch;
   private long lastfametime, lastUsedCashItem, lastExpression = 0, lastHealed, lastBuyback = 0, lastDeathtime, jailExpiration = -1;
   private transient int localstr, localdex, localluk, localint_, localmagic, localwatk;
   private transient int equipmaxhp, equipmaxmp, equipstr, equipdex, equipluk, equipint_, equipmagic, equipwatk, localchairhp, localchairmp;
   private int localchairrate;
   private boolean hidden, equipchanged = true, berserk, hasMerchant, hasSandboxItem = false, whiteChat = false, canRecvPartySearchInvite = true;
   private boolean equippedMesoMagnet = false, equippedItemPouch = false, equippedPetItemIgnore = false;
   private int linkedLevel = 0;
   private String linkedName = null;
   private boolean finishedDojoTutorial;
   private boolean usedStorage = false;
   private String name;
   private String chalktext;
   private String commandtext;
   private String dataString;
   private String search = null;
   private AtomicBoolean mapTransitioning = new AtomicBoolean(true);  // player client is currently trying to change maps or log in the game map
   private AtomicBoolean awayFromWorld = new AtomicBoolean(true);  // player is online, but on cash shop or mts
   private AtomicInteger exp = new AtomicInteger();
   private AtomicInteger gachaexp = new AtomicInteger();
   private AtomicInteger meso = new AtomicInteger();
   private AtomicInteger chair = new AtomicInteger(-1);
   private int merchantmeso;
   private BuddyList buddylist;
   private EventInstanceManager eventInstance = null;
   private MapleHiredMerchant hiredMerchant = null;
   private MapleClient client;
   private MapleGuildCharacter mgc = null;
   private MaplePartyCharacter mpc = null;
   private MapleInventory[] inventory;
   private MapleJob job = MapleJob.BEGINNER;
   private MapleMessenger messenger = null;
   private MapleMiniGame miniGame;
   private MapleRockPaperScissor rps;
   private MapleMount maplemount;
   private MapleParty party;
   private MaplePet[] pets = new MaplePet[3];
   private MaplePlayerShop playerShop = null;
   private MapleShop shop = null;
   private MapleSkinColor skinColor = MapleSkinColor.NORMAL;
   private MapleStorage storage = null;
   private MapleTrade trade = null;
   private MonsterBook monsterbook;
   private CashShop cashshop;
   private Set<NewYearCardRecord> newyears = new LinkedHashSet<>();
   private SavedLocation[] savedLocations;
   private SkillMacro[] skillMacros = new SkillMacro[5];
   private List<Integer> lastmonthfameids;
   private List<WeakReference<MapleMap>> lastVisitedMaps = new LinkedList<>();
   private WeakReference<MapleMap> ownedMap = new WeakReference<>(null);
   private Set<MapleMonster> controlled = new LinkedHashSet<>();
   private Map<Integer, String> entered = new LinkedHashMap<>();
   private Set<MapleMapObject> visibleMapObjects = new ConcurrentHashSet<>();
   private Map<Skill, SkillEntry> skills = new LinkedHashMap<>();
   private Map<Integer, Integer> activeCoupons = new LinkedHashMap<>();
   private Map<Integer, Integer> activeCouponRates = new LinkedHashMap<>();
   private EnumMap<MapleBuffStat, MapleBuffStatValueHolder> effects = new EnumMap<>(MapleBuffStat.class);
   private Map<MapleBuffStat, Byte> buffEffectsCount = new LinkedHashMap<>();
   private Map<MapleDisease, Long> diseaseExpires = new LinkedHashMap<>();
   private Map<Integer, Map<MapleBuffStat, MapleBuffStatValueHolder>> buffEffects = new LinkedHashMap<>(); // non-overriding buffs thanks to Ronan
   private Map<Integer, Long> buffExpires = new LinkedHashMap<>();
   private Map<Integer, KeyBinding> keymap = new LinkedHashMap<>();
   private Map<Integer, MapleSummon> summons = new LinkedHashMap<>();
   private Map<Integer, CoolDownValueHolder> coolDowns = new LinkedHashMap<>();
   private EnumMap<MapleDisease, Pair<DiseaseValueHolder, MobSkill>> diseases = new EnumMap<>(MapleDisease.class);
   private MapleDoor pdoor = null;
   private Map<MapleQuest, Long> questExpirations = new LinkedHashMap<>();
   private MapleCharacterScheduler scheduler;
   private Lock chrLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CHARACTER_CHR, true);
   private Lock evtLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CHARACTER_EVT, true);
   private Lock petLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CHARACTER_PET, true);
   private Lock prtLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CHARACTER_PRT);
   private Lock cpnLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CHARACTER_CPN);
   private Map<Integer, Set<Integer>> excluded = new LinkedHashMap<>();
   private Set<Integer> excludedItems = new LinkedHashSet<>();
   private Set<Integer> disabledPartySearchInvites = new LinkedHashSet<>();
   private long portaldelay = 0, lastcombo = 0;
   private short combocounter = 0;
   private List<String> blockedPortals = new ArrayList<>();
   private Map<Short, String> area_info = new LinkedHashMap<>();
   private AutobanManager autoban;
   private boolean isbanned = false;
   private boolean blockCashShop = false;
   private boolean allowExpGain = true;
   private byte pendantExp = 0, lastmobcount = 0, doorSlot = -1;
   private List<Integer> trockmaps = new ArrayList<>();
   private List<Integer> viptrockmaps = new ArrayList<>();
   private Map<String, MapleEvents> events = new LinkedHashMap<>();
   private PartyQuest partyQuest = null;
   private List<Pair<DelayedQuestUpdate, Object[]>> npcUpdateQuests = new LinkedList<>();
   private MapleDragon dragon = null;
   private Ring marriageRing;
   private int marriageItemid = -1;
   private int partnerId = -1;
   private List<Ring> crushRings = new ArrayList<>();
   private List<Ring> friendshipRings = new ArrayList<>();
   private boolean loggedIn = false;
   private boolean useCS;  //chaos scroll upon crafting item.
   private long npcCd;
   private long petLootCd;
   private long lastHpDec = 0;
   private int newWarpMap = -1;
   private boolean canWarpMap = true;  //only one "warp" must be used per call, and this will define the right one.
   private int canWarpCounter = 0;     //counts how many times "inner warps" have been called.
   private byte extraHpRec = 0, extraMpRec = 0;
   private short extraRecInterval;
   private int targetHpBarHash = 0;
   private long targetHpBarTime = 0;
   private long nextWarningTime = 0;
   private int banishMap = -1;
   private int banishSp = -1;
   private long banishTime = 0;
   private long lastExpGainTime;
   //EVENTS
   private byte team = 0;
   private MapleFitness fitness;
   private MapleOla ola;
   private long snowballattack;
   private MonsterCarnival monsterCarnival;
   private MonsterCarnivalParty monsterCarnivalParty = null;
   private int cp = 0;
   private int totCP = 0;
   private int FestivalPoints;
   private boolean challenged = false;
   private boolean pendingNameChange; //only used to change name on logout, not to be relied upon elsewhere
   private long loginTime;

   public MapleCharacter(int id, int accountId, int str, int dex, int int_, int luk, int hp, int mp, int meso) {
      this();
      this.id = id;
      this.accountid = accountId;
      init(str, dex, int_, luk, hp, mp, meso);
   }

   public void init(int str, int dex, int int_, int luk, int hp, int mp, int meso) {
      this.str = str;
      this.dex = dex;
      this.int_ = int_;
      this.luk = luk;
      this.hp = hp;
      this.mp = mp;
      this.meso.set(meso);
   }

   private MapleCharacter() {
      scheduler = new MapleCharacterScheduler();
      super.setListener(new AbstractCharacterListener() {
         @Override
         public void onHpChanged(int oldHp) {
            hpChangeAction(oldHp);
         }

         @Override
         public void onHpmpPoolUpdate() {
            List<Pair<MapleStat, Integer>> hpmpupdate = recalcLocalStats();
            for (Pair<MapleStat, Integer> p : hpmpupdate) {
               statUpdates.put(p.getLeft(), p.getRight());
            }

            if (hp > localmaxhp) {
               setHp(localmaxhp);
               statUpdates.put(MapleStat.HP, hp);
            }

            if (mp > localmaxmp) {
               setMp(localmaxmp);
               statUpdates.put(MapleStat.MP, mp);
            }
         }

         @Override
         public void onStatUpdate() {
            recalcLocalStats();
         }

         @Override
         public void onAnnounceStatPoolUpdate() {
            List<Pair<MapleStat, Integer>> statup = new ArrayList<>(8);
            for (Map.Entry<MapleStat, Integer> s : statUpdates.entrySet()) {
               statup.add(new Pair<>(s.getKey(), s.getValue()));
            }

            announce(MaplePacketCreator.updatePlayerStats(statup, true, MapleCharacter.this));
         }
      });

      useCS = false;

      setStance(0);
      inventory = new MapleInventory[MapleInventoryType.values().length];
      savedLocations = new SavedLocation[SavedLocationType.values().length];

      for (MapleInventoryType type : MapleInventoryType.values()) {
         byte b = 24;
         if (type == MapleInventoryType.CASH) {
            b = 96;
         }
         inventory[type.ordinal()] = new MapleInventory(this, type, b);
      }
      inventory[MapleInventoryType.CANHOLD.ordinal()] = new MapleInventoryProof(this);

      for (int i = 0; i < SavedLocationType.values().length; i++) {
         savedLocations[i] = null;
      }
      quests = new LinkedHashMap<>();
      setPosition(new Point(0, 0));

      petLootCd = Server.getInstance().getCurrentTime();
   }

   public void setMount(MapleMount mapleMount) {
      maplemount = mapleMount;
   }

   public void addSkill(Skill skill, SkillEntry skillEntry) {
      this.skills.put((skill), skillEntry);
   }

   public void addQuest(Short id, MapleQuestStatus questStatus) {
      this.quests.put(id, questStatus);
   }

   public void addVipTeleportRockMap(int id) {
      viptrockmaps.add(id);
   }

   public void addTeleportRockMap(int id) {
      trockmaps.add(id);
   }

   public void setMessenger(MapleMessenger messenger, int position) {
      this.messenger = messenger;
      this.messengerposition = position;
   }

   public void setInitialSpawnPoint(int spawnPoint) {
      this.initialSpawnPoint = spawnPoint;
   }

   public void initCashShop() {
      this.cashshop = new CashShop(accountid, id, getJobType());
   }

   public void initAutoBanManager() {
      autoban = new AutobanManager(this);
   }

   public void setLinkedCharacterInformation(String name, int level) {
      linkedName = name;
      linkedLevel = level;
   }

   public void giveFame(int fromId, long time) {
      lastfametime = (Math.max(lastfametime, time));
      lastmonthfameids.add(fromId);
   }

   public void setStorage(MapleStorage mapleStorage) {
      this.storage = mapleStorage;
   }

   public void updateSavedLocation(int index, SavedLocation savedLocation) {
      savedLocations[index] = savedLocation;
   }

   public void setLoggedIn() {
      loggedIn = true;
   }

   public void setQuestFame(int questFame) {
      this.questFame = questFame;
   }

   public void setMerchantMesoNoUpdate(int merchantMeso) {
      merchantmeso = merchantMeso;
   }

   public void setFinishedDojoTutorial(boolean finishedDojoTutorial) {
      this.finishedDojoTutorial = finishedDojoTutorial;
   }

   public void setJailExpiration(long jailExpiration) {
      this.jailExpiration = jailExpiration;
   }

   public void setRankAndMove(int rank, int move) {
      this.rank = rank;
      this.rankMove = move;
   }

   public void setJobRankAndMove(int rank, int move) {
      this.jobRank = rank;
      this.jobRankMove = move;
   }

   public void setDataString(String dataString) {
      this.dataString = dataString;
   }

   public void setLastExpGainTime(long lastExpGainTime) {
      this.lastExpGainTime = lastExpGainTime;
   }

   public void setCanRecvPartySearchInvite(boolean canRecvPartySearchInvite) {
      this.canRecvPartySearchInvite = canRecvPartySearchInvite;
   }

   public void initBuddyList(int capacity) {
      buddylist = new BuddyList(capacity);
   }

   public void initMonsterBook() {
      monsterbook = new MonsterBook();
      monsterbook.loadCards(id);
   }

   public void setOmok(GameData omok) {
      this.omok = omok;
   }

   public void setMatchCard(GameData matchCard) {
      this.matchCard = matchCard;
   }

   private static int calcTransientRatio(float transientpoint) {
      int ret = (int) transientpoint;
      return !(ret <= 0 && transientpoint > 0.0f) ? ret : 1;
   }

   private static List<Equip> getEquipsWithStat(List<Pair<Equip, Map<StatUpgrade, Short>>> equipped, StatUpgrade stat) {
      List<Equip> equippedWithStat = new LinkedList<>();

      for (Pair<Equip, Map<StatUpgrade, Short>> eq : equipped) {
         if (eq.getRight().containsKey(stat)) {
            equippedWithStat.add(eq.getLeft());
         }
      }

      return equippedWithStat;
   }

   public MapleJob getJobStyle(byte opt) {
      return MapleJobProcessor.getInstance().getJobStyleInternal(this.getJob().getId(), opt);
   }

   public MapleJob getJobStyle() {
      return getJobStyle((byte) ((this.getStr() > this.getDex()) ? 0x80 : 0x40));
   }

   public boolean isLoggedinWorld() {
      return this.isLoggedin() && !this.isAwayFromWorld();
   }

   public boolean isAwayFromWorld() {
      return awayFromWorld.get();
   }

   public void setEnteredChannelWorld() {
      awayFromWorld.set(false);
      client.getChannelServer().removePlayerAway(id);

      if (canRecvPartySearchInvite) {
         this.getWorldServer().getPartySearchCoordinator().attachPlayer(this);
      }
   }

   public void setAwayFromChannelWorld() {
      setAwayFromChannelWorld(false);
   }

   public void setDisconnectedFromChannelWorld() {
      setAwayFromChannelWorld(true);
   }

   private void setAwayFromChannelWorld(boolean disconnect) {
      awayFromWorld.set(true);

      if (!disconnect) {
         client.getChannelServer().insertPlayerAway(id);
      } else {
         client.getChannelServer().removePlayerAway(id);
      }
   }

   public void updatePartySearchAvailability(boolean psearchAvailable) {
      if (psearchAvailable) {
         if (canRecvPartySearchInvite && getParty() == null) {
            this.getWorldServer().getPartySearchCoordinator().attachPlayer(this);
         }
      } else {
         if (canRecvPartySearchInvite) {
            this.getWorldServer().getPartySearchCoordinator().detachPlayer(this);
         }
      }
   }

   public boolean toggleRecvPartySearchInvite() {
      canRecvPartySearchInvite = !canRecvPartySearchInvite;

      if (canRecvPartySearchInvite) {
         updatePartySearchAvailability(getParty() == null);
      } else {
         this.getWorldServer().getPartySearchCoordinator().detachPlayer(this);
      }

      return canRecvPartySearchInvite;
   }

   public boolean isRecvPartySearchInviteEnabled() {
      return canRecvPartySearchInvite;
   }

   public void resetPartySearchInvite(int fromLeaderid) {
      disabledPartySearchInvites.remove(fromLeaderid);
   }

   public void disablePartySearchInvite(int fromLeaderid) {
      disabledPartySearchInvites.add(fromLeaderid);
   }

   public boolean hasDisabledPartySearchInvite(int fromLeaderid) {
      return disabledPartySearchInvites.contains(fromLeaderid);
   }

   public void setSessionTransitionState() {
      client.getSession().setAttribute(MapleClient.CLIENT_TRANSITION);
   }

   public long getPetLootCd() {
      return petLootCd;
   }

   public boolean getCS() {
      return useCS;
   }

   public void setCS(boolean cs) {
      useCS = cs;
   }

   public long getNpcCooldown() {
      return npcCd;
   }

   public void setNpcCooldown(long d) {
      npcCd = d;
   }

   public int getOwlSearch() {
      return owlSearch;
   }

   public void setOwlSearch(int id) {
      owlSearch = id;
   }

   public void addCooldown(int skillId, long startTime, long length) {
      effLock.lock();
      chrLock.lock();
      try {
         this.coolDowns.put(skillId, new CoolDownValueHolder(skillId, startTime, length));
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   public void addCrushRing(Ring r) {
      crushRings.add(r);
   }

   public Ring getRingById(int id) {
      for (Ring ring : getCrushRings()) {
         if (ring.ringId() == id) {
            return ring;
         }
      }
      for (Ring ring : getFriendshipRings()) {
         if (ring.ringId() == id) {
            return ring;
         }
      }

      if (marriageRing != null) {
         if (marriageRing.ringId() == id) {
            return marriageRing;
         }
      }

      return null;
   }

   public int getMarriageItemId() {
      return marriageItemid;
   }

   public void setMarriageItemId(int itemid) {
      marriageItemid = itemid;
   }

   public int getPartnerId() {
      return partnerId;
   }

   public void setPartnerId(int partnerid) {
      partnerId = partnerid;
   }

   public int getRelationshipId() {
      return getWorldServer().getRelationshipId(id);
   }

   public boolean isMarried() {
      return marriageRing != null && partnerId > 0;
   }

   public boolean hasJustMarried() {
      EventInstanceManager eim = getEventInstance();
      if (eim != null) {
         String prop = eim.getProperty("groomId");

         if (prop != null) {
            return (Integer.parseInt(prop) == id || eim.getIntProperty("brideId") == id) && (mapid == 680000110 || mapid == 680000210);
         }
      }

      return false;
   }

   public int addDojoPointsByMap(int mapid) {
      int pts = 0;
      if (dojoPoints < 17000) {
         pts = 1 + ((mapid - 1) / 100 % 100) / 6;
         if (!getDojoParty()) {
            pts++;
         }
         this.dojoPoints += pts;
      }
      return pts;
   }

   public void addFriendshipRing(Ring r) {
      friendshipRings.add(r);
   }

   public void addMarriageRing(Ring r) {
      marriageRing = r;
   }

   public void addMesosTraded(int gain) {
      this.mesosTraded += gain;
   }

   public void addPet(MaplePet pet) {
      petLock.lock();
      try {
         for (int i = 0; i < 3; i++) {
            if (pets[i] == null) {
               pets[i] = pet;
               return;
            }
         }
      } finally {
         petLock.unlock();
      }
   }

   public void addSummon(int id, MapleSummon summon) {
      summons.put(id, summon);

      if (summon.isPuppet()) {
         map.addPlayerPuppet(this);
      }
   }

   public void addVisibleMapObject(MapleMapObject mo) {
      visibleMapObjects.add(mo);
   }

   public void ban(String reason) {
      DatabaseConnection.getInstance().withConnection(connection -> AccountAdministrator.getInstance().setPermaBan(connection, accountid, reason));
   }

   public int calculateMaxBaseDamage(int watk, MapleWeaponType weapon) {
      int mainstat, secondarystat;
      if (getJob().isA(MapleJob.THIEF) && weapon == MapleWeaponType.DAGGER_OTHER) {
         weapon = MapleWeaponType.DAGGER_THIEVES;
      }

      if (weapon == MapleWeaponType.BOW || weapon == MapleWeaponType.CROSSBOW || weapon == MapleWeaponType.GUN) {
         mainstat = localdex;
         secondarystat = localstr;
      } else if (weapon == MapleWeaponType.CLAW || weapon == MapleWeaponType.DAGGER_THIEVES) {
         mainstat = localluk;
         secondarystat = localdex + localstr;
      } else {
         mainstat = localstr;
         secondarystat = localdex;
      }
      return (int) (((weapon.getMaxDamageMultiplier() * mainstat + secondarystat) / 100.0) * watk);
   }

   public int calculateMaxBaseDamage(int watk) {
      int maxbasedamage;
      Item weapon_item = getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
      if (weapon_item != null) {
         maxbasedamage = calculateMaxBaseDamage(watk, ii.getWeaponType(weapon_item.id()));
      } else {
         if (job.isA(MapleJob.PIRATE) || job.isA(MapleJob.THUNDERBREAKER1)) {
            double weapMulti = 3;
            if (job.getId() % 100 != 0) {
               weapMulti = 4.2;
            }

            int attack = (int) Math.min(Math.floor((2 * getLevel() + 31) / 3), 31);
            maxbasedamage = (int) (localstr * weapMulti + localdex) * attack / 100;
         } else {
            maxbasedamage = 1;
         }
      }
      return maxbasedamage;
   }

   public int calculateMaxBaseMagicDamage(int matk) {
      int maxbasedamage = matk;
      int totalint = getTotalInt();

      if (totalint > 2000) {
         maxbasedamage -= 2000;
         maxbasedamage += (int) ((0.09033024267 * totalint) + 3823.8038);
      } else {
         maxbasedamage -= totalint;

         if (totalint > 1700) {
            maxbasedamage += (int) (0.1996049769 * Math.pow(totalint, 1.300631341));
         } else {
            maxbasedamage += (int) (0.1996049769 * Math.pow(totalint, 1.290631341));
         }
      }

      return (maxbasedamage * 107) / 100;
   }

   public short getCombo() {
      return combocounter;
   }

   public void setCombo(short count) {
      if (count < combocounter) {
         cancelEffectFromBuffStat(MapleBuffStat.ARAN_COMBO);
      }
      combocounter = (short) Math.min(30000, count);
      if (count > 0) {
         announce(MaplePacketCreator.showCombo(combocounter));
      }
   }

   public long getLastCombo() {
      return lastcombo;
   }

   public void setLastCombo(long time) {
      lastcombo = time;
   }

   public boolean cannotEnterCashShop() {
      return blockCashShop;
   }

   public void toggleBlockCashShop() {
      blockCashShop = !blockCashShop;
   }

   public void toggleExpGain() {
      allowExpGain = !allowExpGain;
   }

   public void newClient(MapleClient c) {
      this.loggedIn = true;
      c.setAccountName(this.client.getAccountName());//No null's for accountName
      this.setClient(c);
      this.map = c.getChannelServer().getMapFactory().getMap(getMapId());
      MaplePortal portal = map.findClosestPlayerSpawnpoint(getPosition());
      if (portal == null) {
         portal = map.getPortal(0);
      }
      this.setPosition(portal.getPosition());
      this.initialSpawnPoint = portal.getId();
   }

   private String getMedalText() {
      String medal = "";
      final Item medalItem = getInventory(MapleInventoryType.EQUIPPED).getItem((short) -49);
      if (medalItem != null) {
         medal = "<" + ii.getName(medalItem.id()) + "> ";
      }
      return medal;
   }

   private void hide(boolean hide, boolean login) {
      if (isGM() && hide != this.hidden) {
         if (!hide) {
            this.hidden = false;
            announce(MaplePacketCreator.getGMEffect(0x10, (byte) 0));
            List<MapleBuffStat> dsstat = Collections.singletonList(MapleBuffStat.DARKSIGHT);
            getMap().broadcastGMMessage(this, MaplePacketCreator.cancelForeignBuff(id, dsstat), false);
            getMap().broadcastSpawnPlayerMapObjectMessage(this, this, false);

            for (MapleSummon ms : this.getSummonsValues()) {
               getMap().broadcastNONGMMessage(this, MaplePacketCreator.spawnSummon(ms, false), false);
            }
         } else {
            this.hidden = true;
            announce(MaplePacketCreator.getGMEffect(0x10, (byte) 1));
            if (!login) {
               getMap().broadcastNONGMMessage(this, MaplePacketCreator.removePlayerFromMap(getId()), false);
            }
            List<Pair<MapleBuffStat, Integer>> ldsstat = Collections.singletonList(new Pair<>(MapleBuffStat.DARKSIGHT, 0));
            getMap().broadcastGMMessage(this, MaplePacketCreator.giveForeignBuff(id, ldsstat), false);
            this.releaseControlledMonsters();
         }
         announce(MaplePacketCreator.enableActions());
      }
   }

   public void hide(boolean hide) {
      hide(hide, false);
   }

   public void toggleHide(boolean login) {
      hide(!hidden);
   }

   public void cancelMagicDoor() {
      List<MapleBuffStatValueHolder> mbsvhList = getAllStatups();
      for (MapleBuffStatValueHolder mbsvh : mbsvhList) {
         if (mbsvh.effect.isMagicDoor()) {
            cancelEffect(mbsvh.effect, false, mbsvh.startTime);
            break;
         }
      }
   }

   private void cancelPlayerBuffs(List<MapleBuffStat> buffstats) {
      if (client.getChannelServer().getPlayerStorage().getCharacterById(getId()).isPresent()) {
         updateLocalStats();
         client.announce(MaplePacketCreator.cancelBuff(buffstats));
         if (buffstats.size() > 0) {
            getMap().broadcastMessage(this, MaplePacketCreator.cancelForeignBuff(getId(), buffstats), false);
         }
      }
   }

   public boolean canDoor() {
      MapleDoor door = getPlayerDoor();
      return door == null || (door.isActive() && door.getElapsedDeployTime() > 5000);
   }

   public void setHasSandboxItem() {
      hasSandboxItem = true;
   }

   public void removeSandboxItems() {  // sandbox idea thanks to Morty
      if (!hasSandboxItem) {
         return;
      }

      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      for (MapleInventoryType invType : MapleInventoryType.values()) {
         MapleInventory inv = this.getInventory(invType);

         inv.lockInventory();
         try {
            for (Item item : new ArrayList<>(inv.list())) {
               if (MapleInventoryManipulator.isSandboxItem(item)) {
                  MapleInventoryManipulator.removeFromSlot(client, invType, item.position(), item.quantity(), false);
                  MessageBroadcaster.getInstance().sendServerNotice(this, ServerNoticeType.PINK_TEXT, "[" + ii.getName(item.id()) + "] has passed its trial conditions and will be removed from your inventory.");
               }
            }
         } finally {
            inv.unlockInventory();
         }
      }

      hasSandboxItem = false;
   }

   public FameStatus canGiveFame(MapleCharacter from) {
      if (this.isGM()) {
         return FameStatus.OK;
      } else if (lastfametime >= System.currentTimeMillis() - 3600000 * 24) {
         return FameStatus.NOT_TODAY;
      } else if (lastmonthfameids.contains(from.getId())) {
         return FameStatus.NOT_THIS_MONTH;
      } else {
         return FameStatus.OK;
      }
   }

   public void changeCI(int type) {
      this.ci = type;
   }

   private void setMasteries(int jobId) {
      int[] skills = new int[4];
      for (int i = 0; i > skills.length; i++) {
         skills[i] = 0; //that initialization meng
      }
      if (jobId == 112) {
         skills[0] = Hero.ACHILLES;
         skills[1] = Hero.MONSTER_MAGNET;
         skills[2] = Hero.BRANDISH;
      } else if (jobId == 122) {
         skills[0] = Paladin.ACHILLES;
         skills[1] = Paladin.MONSTER_MAGNET;
         skills[2] = Paladin.BLAST;
      } else if (jobId == 132) {
         skills[0] = DarkKnight.BEHOLDER;
         skills[1] = DarkKnight.ACHILLES;
         skills[2] = DarkKnight.MONSTER_MAGNET;
      } else if (jobId == 212) {
         skills[0] = FPArchMage.BIG_BANG;
         skills[1] = FPArchMage.MANA_REFLECTION;
         skills[2] = FPArchMage.PARALYZE;
      } else if (jobId == 222) {
         skills[0] = ILArchMage.BIG_BANG;
         skills[1] = ILArchMage.MANA_REFLECTION;
         skills[2] = ILArchMage.CHAIN_LIGHTNING;
      } else if (jobId == 232) {
         skills[0] = Bishop.BIG_BANG;
         skills[1] = Bishop.MANA_REFLECTION;
         skills[2] = Bishop.HOLY_SHIELD;
      } else if (jobId == 312) {
         skills[0] = Bowmaster.BOW_EXPERT;
         skills[1] = Bowmaster.HAMSTRING;
         skills[2] = Bowmaster.SHARP_EYES;
      } else if (jobId == 322) {
         skills[0] = Marksman.MARKSMAN_BOOST;
         skills[1] = Marksman.BLIND;
         skills[2] = Marksman.SHARP_EYES;
      } else if (jobId == 412) {
         skills[0] = NightLord.SHADOW_STARS;
         skills[1] = NightLord.SHADOW_SHIFTER;
         skills[2] = NightLord.VENOMOUS_STAR;
      } else if (jobId == 422) {
         skills[0] = Shadower.SHADOW_SHIFTER;
         skills[1] = Shadower.VENOMOUS_STAB;
         skills[2] = Shadower.BOOMERANG_STEP;
      } else if (jobId == 512) {
         skills[0] = Buccaneer.BARRAGE;
         skills[1] = Buccaneer.ENERGY_ORB;
         skills[2] = Buccaneer.SPEED_INFUSION;
         skills[3] = Buccaneer.DRAGON_STRIKE;
      } else if (jobId == 522) {
         skills[0] = Corsair.ELEMENTAL_BOOST;
         skills[1] = Corsair.BULLSEYE;
         skills[2] = Corsair.WRATH_OF_THE_OCTOPI;
         skills[3] = Corsair.RAPID_FIRE;
      } else if (jobId == 2112) {
         skills[0] = Aran.OVER_SWING;
         skills[1] = Aran.HIGH_MASTERY;
         skills[2] = Aran.FREEZE_STANDING;
      } else if (jobId == 2217) {
         skills[0] = Evan.MAPLE_WARRIOR;
         skills[1] = Evan.ILLUSION;
      } else if (jobId == 2218) {
         skills[0] = Evan.BLESSING_OF_THE_ONYX;
         skills[1] = Evan.BLAZE;
      }

      Arrays.stream(skills)
            .filter(id -> id != 0)
            .mapToObj(SkillFactory::getSkill)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(skill -> {
               int skillLevel = getSkillLevel(skill);
               if (skillLevel > 0) {
                  changeSkillLevel(skill, (byte) 0, 10, -1);
               }
            });
   }

   private void broadcastChangeJob() {
      for (MapleCharacter chr : map.getAllPlayers()) {
         MapleClient chrC = chr.getClient();

         if (chrC != null) {     // propagate new job 3rd-person effects (FJ, Aran 1st strike, etc)
            this.sendDestroyData(chrC);
            this.sendSpawnData(chrC);
         }
      }

      // need to delay to ensure clientside has finished reloading character data
      TimerManager.getInstance().schedule(() -> {
         MapleCharacter thisChr = MapleCharacter.this;
         MapleMap map = thisChr.getMap();

         if (map != null) {
            map.broadcastMessage(thisChr, MaplePacketCreator.showForeignEffect(thisChr.getId(), 8), false);
         }
      }, 777);
   }

   public synchronized void changeJob(MapleJob newJob) {
      if (newJob == null) {
         return;//the fuck you doing idiot!
      }

      if (canRecvPartySearchInvite && getParty() == null) {
         this.updatePartySearchAvailability(false);
         this.job = newJob;
         this.updatePartySearchAvailability(true);
      } else {
         this.job = newJob;
      }

      int spGain = 1;
      if (GameConstants.hasSPTable(newJob)) {
         spGain += 2;
      } else {
         if (newJob.getId() % 10 == 2) {
            spGain += 2;
         }

         if (ServerConstants.USE_ENFORCE_JOB_SP_RANGE) {
            spGain = getChangedJobSp(newJob);
         }
      }

      if (spGain > 0) {
         gainSp(spGain, GameConstants.getSkillBook(newJob.getId()), true);
      }

      // thanks xinyifly for finding out missing AP awards (AP Reset can be used as a compass)
      if (newJob.getId() % 100 >= 1) {
         if (this.isCygnus()) {
            gainAp(7, true);
         } else {
            if (ServerConstants.USE_STARTING_AP_4 || newJob.getId() % 10 >= 1) {
               gainAp(5, true);
            }
         }
      } else {    // thanks Periwinks for noticing an AP shortage from lower levels
         if (ServerConstants.USE_STARTING_AP_4 && newJob.getId() % 1000 >= 1) {
            gainAp(4, true);
         }
      }

      if (!isGM()) {
         for (byte i = 1; i < 5; i++) {
            gainSlots(i, 4, true);
         }
      }

      int addhp = 0, addmp = 0;
      int job_ = job.getId() % 1000; // lame temp "fix"
      if (job_ == 100) {                      // 1st warrior
         addhp += Randomizer.rand(200, 250);
      } else if (job_ == 200) {               // 1st mage
         addmp += Randomizer.rand(100, 150);
      } else if (job_ % 100 == 0) {           // 1st others
         addhp += Randomizer.rand(100, 150);
         addhp += Randomizer.rand(25, 50);
      } else if (job_ > 0 && job_ < 200) {    // 2nd~4th warrior
         addhp += Randomizer.rand(300, 350);
      } else if (job_ < 300) {                // 2nd~4th mage
         addmp += Randomizer.rand(450, 500);
      } else if (job_ > 0) {                  // 2nd~4th others
         addhp += Randomizer.rand(300, 350);
         addmp += Randomizer.rand(150, 200);
      }

        /*
        //aran perks?
        int newJobId = newJob.getId();
        if(newJobId == 2100) {          // become aran1
            addhp += 275;
            addmp += 15;
        } else if(newJobId == 2110) {   // become aran2
            addmp += 275;
        } else if(newJobId == 2111) {   // become aran3
            addhp += 275;
            addmp += 275;
        }
        */

      effLock.lock();
      statWlock.lock();
      try {
         addMaxMPMaxHP(addhp, addmp, true);
         recalcLocalStats();

         List<Pair<MapleStat, Integer>> statup = new ArrayList<>(7);
         statup.add(new Pair<>(MapleStat.HP, hp));
         statup.add(new Pair<>(MapleStat.MP, mp));
         statup.add(new Pair<>(MapleStat.MAXHP, clientmaxhp));
         statup.add(new Pair<>(MapleStat.MAXMP, clientmaxmp));
         statup.add(new Pair<>(MapleStat.AVAILABLEAP, remainingAp));
         statup.add(new Pair<>(MapleStat.AVAILABLESP, remainingSp[GameConstants.getSkillBook(job.getId())]));
         statup.add(new Pair<>(MapleStat.JOB, job.getId()));
         client.announce(MaplePacketCreator.updatePlayerStats(statup, true, this));
      } finally {
         statWlock.unlock();
         effLock.unlock();
      }

      setMPC(new MaplePartyCharacter(this));
      silentPartyUpdate();

      if (dragon != null) {
         getMap().broadcastMessage(MaplePacketCreator.removeDragon(dragon.getObjectId()));
         dragon = null;
      }

      if (this.guildid > 0) {
         getGuild().ifPresent(guild -> guild.broadcast(MaplePacketCreator.jobMessage(0, job.getId(), name), this.getId()));
      }

      MapleFamily family = getFamily();
      if (family != null) {
         family.broadcast(MaplePacketCreator.jobMessage(1, job.getId(), name), this.getId());
      }

      setMasteries(this.job.getId());
      guildUpdate();

      broadcastChangeJob();

      if (GameConstants.hasSPTable(newJob) && newJob.getId() != 2001) {
         if (getBuffedValue(MapleBuffStat.MONSTER_RIDING) != null) {
            cancelBuffStats(MapleBuffStat.MONSTER_RIDING);
         }
         createDragon();
      }

      if (ServerConstants.USE_ANNOUNCE_CHANGEJOB) {
         if (!this.isGM()) {
            String message = "[" + GameConstants.ordinal(GameConstants.getJobBranch(newJob)) + " Job] " + name + " has just become a " + GameConstants.getJobName(this.job.getId()) + ".";
            MessageBroadcaster.getInstance().sendServerNoticeToAcquaintances(this, ServerNoticeType.LIGHT_BLUE, message);
         }
      }
   }

   public void changeKeybinding(int key, KeyBinding keybinding) {
      if (keybinding.theType() != 0) {
         keymap.put(key, keybinding);
      } else {
         keymap.remove(key);
      }
   }

   public void broadcastStance(int newStance) {
      setStance(newStance);
      broadcastStance();
   }

   private void broadcastStance() {
      map.broadcastMessage(this, MaplePacketCreator.movePlayer(id, this.getIdleMovement(), getIdleMovementDataLength()), false);
   }

   public MapleMap getWarpMap(int map) {
      MapleMap warpMap;
      EventInstanceManager eim = getEventInstance();
      if (eim != null) {
         warpMap = eim.getMapInstance(map);
      } else if (this.getMonsterCarnival() != null && this.getMonsterCarnival().getEventMap().getId() == map) {
         warpMap = this.getMonsterCarnival().getEventMap();
      } else {
         warpMap = client.getChannelServer().getMapFactory().getMap(map);
      }
      return warpMap;
   }

   // for use ONLY inside OnUserEnter map scripts that requires a player to change map while still moving between maps.
   public void warpAhead(int map) {
      newWarpMap = map;
   }

   private void eventChangedMap(int map) {
      EventInstanceManager eim = getEventInstance();
      if (eim != null) {
         eim.changedMap(this, map);
      }
   }

   private void eventAfterChangedMap(int map) {
      EventInstanceManager eim = getEventInstance();
      if (eim != null) {
         eim.afterChangedMap(this, map);
      }
   }

   public boolean canRecoverLastBanish() {
      return System.currentTimeMillis() - this.banishTime < 5 * 60 * 1000;
   }

   public Pair<Integer, Integer> getLastBanishData() {
      return new Pair<>(this.banishMap, this.banishSp);
   }

   public void clearBanishPlayerData() {
      this.banishMap = -1;
      this.banishSp = -1;
      this.banishTime = 0;
   }

   public void setBanishPlayerData(int banishMap, int banishSp, long banishTime) {
      this.banishMap = banishMap;
      this.banishSp = banishSp;
      this.banishTime = banishTime;
   }

   public void changeMapBanish(int mapid, String portal, String msg) {
      if (ServerConstants.USE_SPIKES_AVOID_BANISH) {
         for (Item it : this.getInventory(MapleInventoryType.EQUIPPED).list()) {
            if ((it.flag() & ItemConstants.SPIKES) == ItemConstants.SPIKES) {
               return;
            }
         }
      }

      int banMap = this.getMapId();
      int banSp = this.getMap().findClosestPlayerSpawnpoint(this.getPosition()).getId();
      long banTime = System.currentTimeMillis();

      if (msg != null) {
         MessageBroadcaster.getInstance().sendServerNotice(this, ServerNoticeType.PINK_TEXT, msg);
      }

      MapleMap map_ = getWarpMap(mapid);
      MaplePortal portal_ = map_.getPortal(portal);
      changeMap(map_, portal_ != null ? portal_ : map_.getRandomPlayerSpawnpoint());

      setBanishPlayerData(banMap, banSp, banTime);
   }

   public void changeMap(int map) {
      MapleMap warpMap = getWarpMap(map);
      changeMap(warpMap, warpMap.getRandomPlayerSpawnpoint());
   }

   public void changeMap(int map, int portal) {
      MapleMap warpMap = getWarpMap(map);
      changeMap(warpMap, warpMap.getPortal(portal));
   }

   public void changeMap(int map, String portal) {
      MapleMap warpMap = getWarpMap(map);
      changeMap(warpMap, warpMap.getPortal(portal));
   }

   public void changeMap(MapleMap to) {
      changeMap(to, 0);
   }

   public void changeMap(MapleMap to, int portal) {
      changeMap(to, to.getPortal(portal));
   }

   public void changeMap(final MapleMap target, final MaplePortal pto) {
      canWarpCounter++;

      eventChangedMap(target.getId());    // player can be dropped from an event here, hence the new warping target.
      MapleMap to = getWarpMap(target.getId());
      changeMapInternal(to, pto.getPosition(), MaplePacketCreator.getWarpToMap(to, pto.getId(), this));
      canWarpMap = false;

      canWarpCounter--;
      if (canWarpCounter == 0) {
         canWarpMap = true;
      }

      eventAfterChangedMap(this.getMapId());
   }

   public void changeMap(final MapleMap target, final Point pos) {
      canWarpCounter++;

      eventChangedMap(target.getId());
      MapleMap to = getWarpMap(target.getId());
      changeMapInternal(to, pos, MaplePacketCreator.getWarpToMap(to, 0x80, pos, this));
      canWarpMap = false;

      canWarpCounter--;
      if (canWarpCounter == 0) {
         canWarpMap = true;
      }

      eventAfterChangedMap(this.getMapId());
   }

   public void forceChangeMap(final MapleMap target, final MaplePortal pto) {
      // will actually enter the map given as parameter, regardless of being an eventmap or whatnot

      canWarpCounter++;
      eventChangedMap(999999999);

      EventInstanceManager mapEim = target.getEventInstance();
      if (mapEim != null) {
         EventInstanceManager playerEim = this.getEventInstance();
         if (playerEim != null) {
            playerEim.exitPlayer(this);
            if (playerEim.getPlayerCount() == 0) {
               playerEim.dispose();
            }
         }

         // thanks Thora for finding an issue with players not being actually warped into the target event map (rather sent to the event starting map)
         mapEim.registerPlayer(this, false);
      }

      changeMapInternal(target, pto.getPosition(), MaplePacketCreator.getWarpToMap(target, pto.getId(), this));
      canWarpMap = false;

      canWarpCounter--;
      if (canWarpCounter == 0) {
         canWarpMap = true;
      }

      eventAfterChangedMap(this.getMapId());
   }

   private boolean buffMapProtection() {
      int thisMapid = mapid;
      int returnMapid = client.getChannelServer().getMapFactory().getMap(thisMapid).getReturnMapId();

      effLock.lock();
      chrLock.lock();
      try {
         for (Entry<MapleBuffStat, MapleBuffStatValueHolder> mbs : effects.entrySet()) {
            if (mbs.getKey() == MapleBuffStat.MAP_PROTECTION) {
               byte value = (byte) mbs.getValue().value;

               if (value == 1 && ((returnMapid == 211000000 && thisMapid != 200082300) || returnMapid == 193000000)) {
                  return true;        //protection from cold
               } else {
                  return value == 2 && (returnMapid == 230000000 || thisMapid == 200082300);        //breathing underwater
               }
            }
         }
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }

      for (Item it : this.getInventory(MapleInventoryType.EQUIPPED).list()) {
         if ((it.flag() & ItemConstants.COLD) == ItemConstants.COLD && ((returnMapid == 211000000 && thisMapid != 200082300) || returnMapid == 193000000)) {
            return true;        //protection from cold
         }
      }

      return false;
   }

   public List<Integer> getLastVisitedMapids() {
      List<Integer> lastVisited = new ArrayList<>(5);

      petLock.lock();
      try {
         for (WeakReference<MapleMap> lv : lastVisitedMaps) {
            MapleMap lvm = lv.get();

            if (lvm != null) {
               lastVisited.add(lvm.getId());
            }
         }
      } finally {
         petLock.unlock();
      }

      return lastVisited;
   }

   public void partyOperationUpdate(MapleParty party, List<MapleCharacter> exPartyMembers) {
      List<WeakReference<MapleMap>> mapids;

      petLock.lock();
      try {
         mapids = new LinkedList<>(lastVisitedMaps);
      } finally {
         petLock.unlock();
      }

      List<MapleCharacter> partyMembers = new LinkedList<>();
      for (MapleCharacter mc : (exPartyMembers != null) ? exPartyMembers : this.getPartyMembersOnline()) {
         if (mc.isLoggedinWorld()) {
            partyMembers.add(mc);
         }
      }

      MapleCharacter partyLeaver = null;
      if (exPartyMembers != null) {
         partyMembers.remove(this);
         partyLeaver = this;
      }

      MapleMap map = this.getMap();
      List<MapleMapItem> partyItems = null;

      int partyId = exPartyMembers != null ? -1 : this.getPartyId();
      for (WeakReference<MapleMap> mapRef : mapids) {
         MapleMap mapObj = mapRef.get();

         if (mapObj != null) {
            List<MapleMapItem> partyMapItems = mapObj.updatePlayerItemDropsToParty(partyId, id, partyMembers, partyLeaver);
            if (map.hashCode() == mapObj.hashCode()) {
               partyItems = partyMapItems;
            }
         }
      }

      if (partyItems != null && exPartyMembers == null) {
         map.updatePartyItemDropsToNewcomer(this, partyItems);
      }

      PartyProcessor.getInstance().updatePartyTownDoors(party, this, partyLeaver, partyMembers);
   }

   public void collectDiseases() {
      for (MapleCharacter chr : map.getAllPlayers()) {
         int cid = chr.getId();

         for (Entry<MapleDisease, Pair<Long, MobSkill>> di : chr.getAllDiseases().entrySet()) {
            MapleDisease disease = di.getKey();
            MobSkill skill = di.getValue().getRight();
            final List<Pair<MapleDisease, Integer>> debuff = Collections.singletonList(new Pair<>(disease, skill.getX()));

            if (disease != MapleDisease.SLOW) {
               this.announce(MaplePacketCreator.giveForeignDebuff(cid, debuff, skill));
            } else {
               this.announce(MaplePacketCreator.giveForeignSlowDebuff(cid, debuff, skill));
            }
         }
      }
   }

   private Integer getVisitedMapIndex(MapleMap map) {
      int idx = 0;

      for (WeakReference<MapleMap> mapRef : lastVisitedMaps) {
         if (map.equals(mapRef.get())) {
            return idx;
         }

         idx++;
      }

      return -1;
   }

   public void visitMap(MapleMap map) {
      petLock.lock();
      try {
         int idx = getVisitedMapIndex(map);

         if (idx == -1) {
            if (lastVisitedMaps.size() == ServerConstants.MAP_VISITED_SIZE) {
               lastVisitedMaps.remove(0);
            }
         } else {
            WeakReference<MapleMap> mapRef = lastVisitedMaps.remove(idx);
            lastVisitedMaps.add(mapRef);
            return;
         }

         lastVisitedMaps.add(new WeakReference<>(map));
      } finally {
         petLock.unlock();
      }
   }

   public MapleMap getOwnedMap() {
      return ownedMap.get();
   }

   public void setOwnedMap(MapleMap map) {
      ownedMap = new WeakReference<>(map);
   }

   public void notifyMapTransferToPartner(int mapId) {
      if (partnerId > 0) {
         getWorldServer().getPlayerStorage().getCharacterById(partnerId)
               .filter(character -> !character.isAwayFromWorld())
               .ifPresent(character -> character.announce(Wedding.OnNotifyWeddingPartnerTransfer(id, mapId)));
      }
   }

   public void removeIncomingInvites() {
      MapleInviteCoordinator.removePlayerIncomingInvites(id);
   }

   private void changeMapInternal(final MapleMap to, final Point pos, final byte[] warpPacket) {
      if (!canWarpMap) {
         return;
      }

      this.mapTransitioning.set(true);

      this.unregisterChairBuff();
      this.clearBanishPlayerData();
      MapleTrade.cancelTrade(this, MapleTrade.TradeResult.UNSUCCESSFUL_ANOTHER_MAP);
      this.closePlayerInteractions();

      MapleParty e = null;
      if (this.getParty() != null && this.getParty().getEnemy() != null) {
         e = this.getParty().getEnemy();
      }
      final MapleParty k = e;

      client.announce(warpPacket);
      map.removePlayer(this);
      if (client.getChannelServer().getPlayerStorage().getCharacterById(getId()).isPresent()) {
         map = to;
         setPosition(pos);
         map.addPlayer(this);
         visitMap(map);

         prtLock.lock();
         try {
            if (party != null) {
               mpc.setMapId(to.getId());
               client.announce(MaplePacketCreator.updateParty(client.getChannel(), party, PartyOperation.SILENT_UPDATE, null));
               updatePartyMemberHPInternal();
            }
         } finally {
            prtLock.unlock();
         }
         if (MapleCharacter.this.getParty() != null) {
            MapleCharacter.this.getParty().setEnemy(k);
         }
         silentPartyUpdateInternal(getParty());  // EIM script calls inside

         if (getMap().getHPDec() > 0) {
            resetHpDecreaseTask();
         }
      } else {
         FilePrinter.printError(FilePrinter.MAPLE_MAP, "Character " + this.getName() + " got stuck when moving to map " + map.getId() + ".");
      }

      notifyMapTransferToPartner(map.getId());

      //alas, new map has been specified when a warping was being processed...
      if (newWarpMap != -1) {
         canWarpMap = true;

         int temp = newWarpMap;
         newWarpMap = -1;
         changeMap(temp);
      } else {
         // if this event map has a gate already opened, render it
         EventInstanceManager eim = getEventInstance();
         if (eim != null) {
            eim.recoverOpenedGate(this, map.getId());
         }

         // if this map has obstacle components moving, make it do so for this client
         announce(MaplePacketCreator.environmentMoveList(map.getEnvironment().entrySet()));
      }
   }

   public boolean isChangingMaps() {
      return this.mapTransitioning.get();
   }

   public void setMapTransitionComplete() {
      this.mapTransitioning.set(false);
   }

   public void changePage(int page) {
      this.currentPage = page;
   }

   public void changeSkillLevel(Skill skill, byte newLevel, int newMasterlevel, long expiration) {
      if (newLevel > -1) {
         skills.put(skill, new SkillEntry(newLevel, newMasterlevel, expiration));
         if (!GameConstants.isHiddenSkills(skill.getId())) {
            this.client.announce(MaplePacketCreator.updateSkill(skill.getId(), newLevel, newMasterlevel, expiration));
         }
      } else {
         skills.remove(skill);
         this.client.announce(MaplePacketCreator.updateSkill(skill.getId(), newLevel, newMasterlevel, -1)); //Shouldn't use expiration anymore :)
         DatabaseConnection.getInstance().withConnection(connection -> SkillAdministrator.getInstance().deleteForSkillCharacter(connection, skill.getId(), id));
      }
   }

   public void changeTab(int tab) {
      this.currentTab = tab;
   }

   public void changeType(int type) {
      this.currentType = type;
   }

   /**
    * Executes a function given a skill
    *
    * @param skillId  the skill identifier
    * @param function the function to apply
    */
   private void executeForSkill(int skillId, BiConsumer<Skill, Integer> function) {
      SkillFactory.executeForSkill(this, skillId, function);
   }

   /**
    * Executes a function if the user has a skill above level 0.
    *
    * @param skillId  the skill identifier
    * @param function the function to execute
    */
   private void executeIfHasSkill(int skillId, BiConsumer<Skill, Integer> function) {
      SkillFactory.executeIfHasSkill(this, skillId, function);
   }

   /**
    * Executes a function if the user has a skill above level 0.
    *
    * @param skillId  the skill identifier
    * @param function the function to execute
    */
   private boolean applyIfHasSkill(int skillId, BiFunction<Skill, Integer, Boolean> function) {
      return SkillFactory.applyIfHasSkill(this, skillId, function, false);
   }

   public void checkBerserk(final boolean isHidden) {
      scheduler.cancel(MapleCharacterScheduler.Type.BERSERK);
      final MapleCharacter chr = this;
      if (job.equals(MapleJob.DARKKNIGHT)) {
         executeIfHasSkill(DarkKnight.BERSERK, (skill, skillLevel) -> scheduleBerserk(isHidden, chr, skill, skillLevel));
      }
   }

   /**
    * Schedules a runnable for enabling the users berserk skill.
    *
    * @param isHidden   true if the source is hidden
    * @param character  the character
    * @param skill      the berserk skill
    * @param skillLevel the level of berserk
    */
   private void scheduleBerserk(boolean isHidden, MapleCharacter character, Skill skill, Integer skillLevel) {
      this.berserk = character.getHp() * 100 / character.getCurrentMaxHp() < skill.getEffect(skillLevel).getX();
      scheduler.add(MapleCharacterScheduler.Type.BERSERK, () -> {
         if (awayFromWorld.get()) {
            return;
         }

         client.announce(MaplePacketCreator.showOwnBerserk(skillLevel, MapleCharacter.this.berserk));
         if (!isHidden) {
            getMap().broadcastMessage(MapleCharacter.this, MaplePacketCreator.showBerserk(getId(), skillLevel, MapleCharacter.this.berserk), false);
         } else {
            getMap().broadcastGMMessage(MapleCharacter.this, MaplePacketCreator.showBerserk(getId(), skillLevel, MapleCharacter.this.berserk), false);
         }
      }, 5000, 3000);
   }

   public void checkMessenger() {
      getMessenger().ifPresent(messenger -> {
         if (messengerposition > -1 && messengerposition < 4) {
            World worldz = getWorldServer();
            worldz.silentJoinMessenger(messenger.getId(), new MapleMessengerCharacter(this, messengerposition), messengerposition);
            worldz.updateMessenger(messenger.getId(), name, client.getChannel());
         }
      });
   }

   public void controlMonster(MapleMonster monster) {
      if (cpnLock.tryLock()) {
         try {
            controlled.add(monster);
         } finally {
            cpnLock.unlock();
         }
      }
   }

   public void stopControllingMonster(MapleMonster monster) {
      if (cpnLock.tryLock()) {
         try {
            controlled.remove(monster);
         } finally {
            cpnLock.unlock();
         }
      }
   }

   public int getNumControlledMonsters() {
      cpnLock.lock();
      try {
         return controlled.size();
      } finally {
         cpnLock.unlock();
      }
   }

   public Collection<MapleMonster> getControlledMonsters() {
      cpnLock.lock();
      try {
         return new ArrayList<>(controlled);
      } finally {
         cpnLock.unlock();
      }
   }

   private void releaseControlledMonsters() {
      Collection<MapleMonster> controlledMonsters;

      cpnLock.lock();
      try {
         controlledMonsters = new ArrayList<>(controlled);
         controlled.clear();
      } finally {
         cpnLock.unlock();
      }

      for (MapleMonster monster : controlledMonsters) {
         monster.aggroRedirectController();
      }
   }

   public boolean applyConsumeOnPickup(final int itemid) {
      if (itemid / 1000000 == 2) {
         if (ii.isConsumeOnPickup(itemid)) {
            if (ItemConstants.isPartyItem(itemid)) {
               List<MapleCharacter> pchr = this.getPartyMembersOnSameMap();

               if (!ItemConstants.isPartyAllcure(itemid)) {
                  MapleStatEffect mse = ii.getItemEffect(itemid);

                  if (!pchr.isEmpty()) {
                     for (MapleCharacter mc : pchr) {
                        mse.applyTo(mc);
                     }
                  } else {
                     mse.applyTo(this);
                  }
               } else {
                  if (!pchr.isEmpty()) {
                     for (MapleCharacter mc : pchr) {
                        mc.dispelDebuffs();
                     }
                  } else {
                     this.dispelDebuffs();
                  }
               }
            } else {
               ii.getItemEffect(itemid).applyTo(this);
            }

            if (itemid / 10000 == 238) {
               this.getMonsterBook().addCard(client, itemid);
            }
            return true;
         }
      }
      return false;
   }

   public final void pickupItem(MapleMapObject ob) {
      pickupItem(ob, -1);
   }

   public final void pickupItem(MapleMapObject ob, int petIndex) {     // yes, one picks the MapleMapObject, not the MapleMapItem
      if (ob == null) {                                               // pet index refers to the one picking up the item
         return;
      }

      if (ob instanceof MapleMapItem) {
         MapleMapItem mapitem = (MapleMapItem) ob;
         if (System.currentTimeMillis() - mapitem.getDropTime() < 400 || !mapitem.canBePickedBy(this)) {
            client.announce(MaplePacketCreator.enableActions());
            return;
         }

         List<MapleCharacter> mpcs = new LinkedList<>();
         if (mapitem.getMeso() > 0 && !mapitem.isPickedUp()) {
            mpcs = getPartyMembersOnSameMap();
         }

         ScriptedItem itemScript = null;
         mapitem.lockItem();
         try {
            if (mapitem.isPickedUp()) {
               client.announce(MaplePacketCreator.showItemUnavailable());
               client.announce(MaplePacketCreator.enableActions());
               return;
            }

            boolean isPet = petIndex > -1;
            final byte[] pickupPacket = MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), (isPet) ? 5 : 2, this.getId(), isPet, petIndex);

            Item mItem = mapitem.getItem();
            boolean hasSpaceInventory;
            if (mapitem.getItemId() == 4031865 || mapitem.getItemId() == 4031866 || mapitem.getMeso() > 0 || ii.isConsumeOnPickup(mapitem.getItemId()) || (hasSpaceInventory = MapleInventoryManipulator.checkSpace(client, mapitem.getItemId(), mItem.quantity(), mItem.owner()))) {
               int mapId = this.getMapId();

               if ((mapId > 209000000 && mapId < 209000016) || (mapId >= 990000500 && mapId <= 990000502)) {//happyville trees and guild PQ
                  if (!mapitem.isPlayerDrop() || mapitem.getDropper().getObjectId() == client.getPlayer().getObjectId()) {
                     if (mapitem.getMeso() > 0) {
                        if (!mpcs.isEmpty()) {
                           int mesosamm = mapitem.getMeso() / mpcs.size();
                           for (MapleCharacter partymem : mpcs) {
                              if (partymem.isLoggedinWorld()) {
                                 partymem.gainMeso(mesosamm, true, true, false);
                              }
                           }
                        } else {
                           this.gainMeso(mapitem.getMeso(), true, true, false);
                        }

                        this.getMap().pickItemDrop(pickupPacket, mapitem);
                     } else if (mapitem.getItemId() == 4031865 || mapitem.getItemId() == 4031866) {
                        // Add NX to account, show effect and make item disappear
                        int nxGain = mapitem.getItemId() == 4031865 ? 100 : 250;
                        this.getCashShop().gainCash(1, nxGain);

                        showHint("You have earned #e#b" + nxGain + " NX#k#n. (" + this.getCashShop().getCash(1) + " NX)", 300);

                        this.getMap().pickItemDrop(pickupPacket, mapitem);
                     } else if (MapleInventoryManipulator.addFromDrop(client, mItem, true)) {
                        this.getMap().pickItemDrop(pickupPacket, mapitem);
                     } else {
                        client.announce(MaplePacketCreator.enableActions());
                        return;
                     }
                  } else {
                     client.announce(MaplePacketCreator.showItemUnavailable());
                     client.announce(MaplePacketCreator.enableActions());
                     return;
                  }
                  client.announce(MaplePacketCreator.enableActions());
                  return;
               }

               if (!this.needQuestItem(mapitem.getQuest(), mapitem.getItemId())) {
                  client.announce(MaplePacketCreator.showItemUnavailable());
                  client.announce(MaplePacketCreator.enableActions());
                  return;
               }

               if (mapitem.getMeso() > 0) {
                  if (!mpcs.isEmpty()) {
                     int mesosamm = mapitem.getMeso() / mpcs.size();
                     for (MapleCharacter partymem : mpcs) {
                        if (partymem.isLoggedinWorld()) {
                           partymem.gainMeso(mesosamm, true, true, false);
                        }
                     }
                  } else {
                     this.gainMeso(mapitem.getMeso(), true, true, false);
                  }
               } else if (mItem.id() / 10000 == 243) {
                  ScriptedItem info = ii.getScriptedItemInfo(mItem.id());
                  if (info != null && info.runOnPickup()) {
                     itemScript = info;
                  } else {
                     if (!MapleInventoryManipulator.addFromDrop(client, mItem, true)) {
                        client.announce(MaplePacketCreator.enableActions());
                        return;
                     }
                  }
               } else if (mapitem.getItemId() == 4031865 || mapitem.getItemId() == 4031866) {
                  // Add NX to account, show effect and make item disappear
                  int nxGain = mapitem.getItemId() == 4031865 ? 100 : 250;
                  this.getCashShop().gainCash(1, nxGain);

                  showHint("You have earned #e#b" + nxGain + " NX#k#n. (" + this.getCashShop().getCash(1) + " NX)", 300);
               } else if (applyConsumeOnPickup(mItem.id())) {
               } else if (MapleInventoryManipulator.addFromDrop(client, mItem, true)) {
                  if (mItem.id() == 4031868) {
                     updateAriantScore();
                  }
               } else {
                  client.announce(MaplePacketCreator.enableActions());
                  return;
               }

               this.getMap().pickItemDrop(pickupPacket, mapitem);
            } else if (!hasSpaceInventory) {
               client.announce(MaplePacketCreator.getInventoryFull());
               client.announce(MaplePacketCreator.getShowInventoryFull());
            }
         } finally {
            mapitem.unlockItem();
         }

         if (itemScript != null) {
            ItemScriptManager ism = ItemScriptManager.getInstance();
            ism.runItemScript(client, itemScript);
         }
      }
      client.announce(MaplePacketCreator.enableActions());
   }

   public int countItem(int itemid) {
      return inventory[ItemConstants.getInventoryType(itemid).ordinal()].countById(itemid);
   }

   public boolean canHold(int itemid) {
      return canHold(itemid, 1);
   }

   public boolean canHold(int itemid, int quantity) {
      return client.getAbstractPlayerInteraction().canHold(itemid, quantity);
   }

   public boolean canHoldUniques(List<Integer> itemids) {
      for (Integer itemid : itemids) {
         if (ii.isPickupRestricted(itemid) && this.haveItem(itemid)) {
            return false;
         }
      }

      return true;
   }

   public boolean isRidingBattleship() {
      Integer bv = getBuffedValue(MapleBuffStat.MONSTER_RIDING);
      return bv != null && bv.equals(Corsair.BATTLE_SHIP);
   }

   public void announceBattleshipHp() {
      announce(MaplePacketCreator.skillCooldown(5221999, battleShipHp));
   }

   public void decreaseBattleshipHp(int decrease) {
      this.battleShipHp -= decrease;
      if (battleShipHp <= 0) {
         SkillFactory.getSkill(Corsair.BATTLE_SHIP).ifPresent(skill -> {
                  int coolDown = skill.getEffect(getSkillLevel(skill)).getCooldown();
                  announce(MaplePacketCreator.skillCooldown(Corsair.BATTLE_SHIP, coolDown));
                  addCooldown(Corsair.BATTLE_SHIP, Server.getInstance().getCurrentTime(), coolDown * 1000);
                  removeCooldown(5221999);
                  cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
               }
         );
      } else {
         announceBattleshipHp();
         addCooldown(5221999, 0, Long.MAX_VALUE);
      }
   }

   public void decreaseReports() {
      this.possibleReports--;
   }

   public void deleteGuild(int guildId) {
      DatabaseConnection.getInstance().withConnection(connection -> {
         CharacterAdministrator.getInstance().removeAllCharactersFromGuild(connection, guildId);
         GuildAdministrator.getInstance().deleteGuild(connection, guildId);
      });
   }

   private void nextPendingRequest(MapleClient c) {
      CharacterNameAndId pendingBuddyRequest = c.getPlayer().getBuddylist().pollPendingRequest();
      if (pendingBuddyRequest != null) {
         c.announce(MaplePacketCreator.requestBuddylistAdd(pendingBuddyRequest.id(), c.getPlayer().getId(), pendingBuddyRequest.name()));
      }
   }

   private void notifyRemoteChannel(MapleClient c, int remoteChannel, int otherCid, BuddyListOperation operation) {
      MapleCharacter player = c.getPlayer();
      if (remoteChannel != -1) {
         c.getWorldServer().buddyChanged(otherCid, player.getId(), player.getName(), c.getChannel(), operation);
      }
   }

   public void deleteBuddy(int otherCid) {
      BuddyList bl = getBuddylist();

      if (bl.containsVisible(otherCid)) {
         notifyRemoteChannel(client, getWorldServer().find(otherCid), otherCid, BuddyListOperation.DELETED);
      }
      bl.remove(otherCid);
      client.announce(MaplePacketCreator.updateBuddylist(getBuddylist().getBuddies()));
      nextPendingRequest(client);
   }

   private void stopChairTask() {
      chrLock.lock();
      try {
         scheduler.cancel(MapleCharacterScheduler.Type.CHAIR_RECOVERY);
      } finally {
         chrLock.unlock();
      }
   }

   private void updateChairHealStats() {
      statRlock.lock();
      try {
         if (localchairrate != -1) {
            return;
         }
      } finally {
         statRlock.unlock();
      }

      effLock.lock();
      statWlock.lock();
      try {
         Pair<Integer, Pair<Integer, Integer>> p = ChairProcessor.getInstance().getChairTaskIntervalRate(localmaxhp, localmaxmp);

         localchairrate = p.getLeft();
         localchairhp = p.getRight().getLeft();
         localchairmp = p.getRight().getRight();
      } finally {
         statWlock.unlock();
         effLock.unlock();
      }
   }

   private void startChairTask() {
      if (chair.get() < 0) {
         return;
      }

      int healInterval;
      effLock.lock();
      try {
         updateChairHealStats();
         healInterval = localchairrate;
      } finally {
         effLock.unlock();
      }

      chrLock.lock();
      try {
         stopChairTask();
         scheduler.add(MapleCharacterScheduler.Type.CHAIR_RECOVERY, () -> {
            updateChairHealStats();
            final int healHP = localchairhp;
            final int healMP = localchairmp;

            if (MapleCharacter.this.getHp() < localmaxhp) {
               byte recHP = (byte) (healHP / ServerConstants.CHAIR_EXTRA_HEAL_MULTIPLIER);

               client.announce(MaplePacketCreator.showOwnRecovery(recHP));
               getMap().broadcastMessage(MapleCharacter.this, MaplePacketCreator.showRecovery(id, recHP), false);
            } else if (MapleCharacter.this.getMp() >= localmaxmp) {
               stopChairTask();    // optimizing schedule management when player is already with full pool.
            }

            addMPHP(healHP, healMP);
         }, healInterval, healInterval);
      } finally {
         chrLock.unlock();
      }
   }

   private void stopExtraTask() {
      chrLock.lock();
      try {
         scheduler.cancel(MapleCharacterScheduler.Type.EXTRA_RECOVERY);
      } finally {
         chrLock.unlock();
      }
   }

   private void startExtraTask(final byte healHP, final byte healMP, final short healInterval) {
      chrLock.lock();
      try {
         startExtraTaskInternal(healHP, healMP, healInterval);
      } finally {
         chrLock.unlock();
      }
   }

   private void startExtraTaskInternal(final byte healHP, final byte healMP, final short healInterval) {
      extraRecInterval = healInterval;

      scheduler.add(MapleCharacterScheduler.Type.EXTRA_RECOVERY, () -> {
         if (getBuffSource(MapleBuffStat.HPREC) == -1 && getBuffSource(MapleBuffStat.MPREC) == -1) {
            stopExtraTask();
            return;
         }

         if (MapleCharacter.this.getHp() < localmaxhp) {
            if (healHP > 0) {
               client.announce(MaplePacketCreator.showOwnRecovery(healHP));
               getMap().broadcastMessage(MapleCharacter.this, MaplePacketCreator.showRecovery(id, healHP), false);
            }
         }

         addMPHP(healHP, healMP);
      }, healInterval, healInterval);
   }

   public void disbandGuild() {
      if (guildid < 1 || guildRank != 1) {
         return;
      }
      try {
         Server.getInstance().disbandGuild(guildid);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void dispel() {
      if (!(ServerConstants.USE_UNDISPEL_HOLY_SHIELD && this.hasActiveBuff(Bishop.HOLY_SHIELD))) {
         List<MapleBuffStatValueHolder> mbsvhList = getAllStatups();
         for (MapleBuffStatValueHolder mbsvh : mbsvhList) {
            if (mbsvh.effect.isSkill()) {
               if (mbsvh.effect.getBuffSourceId() != Aran.COMBO_ABILITY) { // check discovered thanks to Croosade dev team
                  cancelEffect(mbsvh.effect, false, mbsvh.startTime);
               }
            }
         }
      }
   }

   public final boolean hasDisease(final MapleDisease dis) {
      chrLock.lock();
      try {
         return diseases.containsKey(dis);
      } finally {
         chrLock.unlock();
      }
   }

   private int getDiseasesSize() {
      chrLock.lock();
      try {
         return diseases.size();
      } finally {
         chrLock.unlock();
      }
   }

   public Map<MapleDisease, Pair<Long, MobSkill>> getAllDiseases() {
      chrLock.lock();
      try {
         long curtime = Server.getInstance().getCurrentTime();
         Map<MapleDisease, Pair<Long, MobSkill>> ret = new LinkedHashMap<>();

         for (Entry<MapleDisease, Long> de : diseaseExpires.entrySet()) {
            Pair<DiseaseValueHolder, MobSkill> dee = diseases.get(de.getKey());
            DiseaseValueHolder mdvh = dee.getLeft();

            ret.put(de.getKey(), new Pair<>(mdvh.length() - (curtime - mdvh.startTime()), dee.getRight()));
         }

         return ret;
      } finally {
         chrLock.unlock();
      }
   }

   public void silentApplyDiseases(Map<MapleDisease, Pair<Long, MobSkill>> diseaseMap) {
      chrLock.lock();
      try {
         long curTime = Server.getInstance().getCurrentTime();

         for (Entry<MapleDisease, Pair<Long, MobSkill>> di : diseaseMap.entrySet()) {
            long expTime = curTime + di.getValue().getLeft();

            diseaseExpires.put(di.getKey(), expTime);
            diseases.put(di.getKey(), new Pair<>(new DiseaseValueHolder(curTime, di.getValue().getLeft()), di.getValue().getRight()));
         }
      } finally {
         chrLock.unlock();
      }
   }

   public void announceDiseases() {
      Set<Entry<MapleDisease, Pair<DiseaseValueHolder, MobSkill>>> chrDiseases;

      chrLock.lock();
      try {
         // Poison damage visibility and diseases status visibility, extended through map transitions thanks to Ronan
         if (!this.isLoggedinWorld()) {
            return;
         }

         chrDiseases = new LinkedHashSet<>(diseases.entrySet());
      } finally {
         chrLock.unlock();
      }

      for (Entry<MapleDisease, Pair<DiseaseValueHolder, MobSkill>> di : chrDiseases) {
         MapleDisease disease = di.getKey();
         MobSkill skill = di.getValue().getRight();
         final List<Pair<MapleDisease, Integer>> debuff = Collections.singletonList(new Pair<>(disease, skill.getX()));

         if (disease != MapleDisease.SLOW) {
            map.broadcastMessage(MaplePacketCreator.giveForeignDebuff(id, debuff, skill));
         } else {
            map.broadcastMessage(MaplePacketCreator.giveForeignSlowDebuff(id, debuff, skill));
         }
      }
   }

   public void giveDebuff(final MapleDisease disease, MobSkill skill) {
      if (!hasDisease(disease) && getDiseasesSize() < 2) {
         if (!(disease == MapleDisease.SEDUCE || disease == MapleDisease.STUN)) {
            if (hasActiveBuff(Bishop.HOLY_SHIELD)) {
               return;
            }
         }

         chrLock.lock();
         try {
            long curTime = Server.getInstance().getCurrentTime();
            diseaseExpires.put(disease, curTime + skill.getDuration());
            diseases.put(disease, new Pair<>(new DiseaseValueHolder(curTime, skill.getDuration()), skill));
         } finally {
            chrLock.unlock();
         }

         if (disease == MapleDisease.SEDUCE && chair.get() < 0) {
            sitChair(-1);
         }

         final List<Pair<MapleDisease, Integer>> debuff = Collections.singletonList(new Pair<>(disease, skill.getX()));
         client.announce(MaplePacketCreator.giveDebuff(debuff, skill));

         if (disease != MapleDisease.SLOW) {
            map.broadcastMessage(this, MaplePacketCreator.giveForeignDebuff(id, debuff, skill), false);
         } else {
            map.broadcastMessage(this, MaplePacketCreator.giveForeignSlowDebuff(id, debuff, skill), false);
         }
      }
   }

   public void dispelDebuff(MapleDisease debuff) {
      if (hasDisease(debuff)) {
         long mask = debuff.getValue();
         announce(MaplePacketCreator.cancelDebuff(mask));

         if (debuff != MapleDisease.SLOW) {
            map.broadcastMessage(this, MaplePacketCreator.cancelForeignDebuff(id, mask), false);
         } else {
            map.broadcastMessage(this, MaplePacketCreator.cancelForeignSlowDebuff(id), false);
         }

         chrLock.lock();
         try {
            diseases.remove(debuff);
            diseaseExpires.remove(debuff);
         } finally {
            chrLock.unlock();
         }
      }
   }

   public void dispelDebuffs() {
      dispelDebuff(MapleDisease.CURSE);
      dispelDebuff(MapleDisease.DARKNESS);
      dispelDebuff(MapleDisease.POISON);
      dispelDebuff(MapleDisease.SEAL);
      dispelDebuff(MapleDisease.WEAKEN);
      dispelDebuff(MapleDisease.SLOW);
   }

   public void cancelAllDebuffs() {
      chrLock.lock();
      try {
         diseases.clear();
         diseaseExpires.clear();
      } finally {
         chrLock.unlock();
      }
   }

   private void dispelSkill(int skillid) {
      List<MapleBuffStatValueHolder> allBuffs = getAllStatups();
      for (MapleBuffStatValueHolder mbsvh : allBuffs) {
         if (skillid == 0) {
            if (mbsvh.effect.isSkill() && (mbsvh.effect.getSourceId() % 10000000 == 1004 || SkillProcessor.getInstance().dispelSkills(mbsvh.effect.getSourceId()))) {
               cancelEffect(mbsvh.effect, false, mbsvh.startTime);
            }
         } else if (mbsvh.effect.isSkill() && mbsvh.effect.getSourceId() == skillid) {
            cancelEffect(mbsvh.effect, false, mbsvh.startTime);
         }
      }
   }

   public void changeFaceExpression(int emote) {
      long timeNow = Server.getInstance().getCurrentTime();
      if (timeNow - lastExpression > 2000) {
         lastExpression = timeNow;
         client.getChannelServer().registerFaceExpression(map, this, emote);
      }
   }

   private void doHurtHp() {
      if (!(this.getInventory(MapleInventoryType.EQUIPPED).findById(getMap().getHPDecProtect()) != null || buffMapProtection())) {
         addHP(-getMap().getHPDec());
         lastHpDec = Server.getInstance().getCurrentTime();
      }
   }

   private void startHpDecreaseTask(long lastHpTask) {
      scheduler.add(MapleCharacterScheduler.Type.HP_DECREASE, this::doHurtHp, ServerConstants.MAP_DAMAGE_OVERTIME_INTERVAL, ServerConstants.MAP_DAMAGE_OVERTIME_INTERVAL - lastHpTask);
   }

   public void resetHpDecreaseTask() {
      scheduler.cancel(MapleCharacterScheduler.Type.HP_DECREASE);
      long lastHpTask = Server.getInstance().getCurrentTime() - lastHpDec;
      startHpDecreaseTask((lastHpTask > ServerConstants.MAP_DAMAGE_OVERTIME_INTERVAL) ? ServerConstants.MAP_DAMAGE_OVERTIME_INTERVAL : lastHpTask);
   }

   public void enteredScript(String script, int mapid) {
      if (!entered.containsKey(mapid)) {
         entered.put(mapid, script);
      }
   }

   public void equipChanged() {
      getMap().broadcastUpdateCharLookMessage(this, this);
      equipchanged = true;
      updateLocalStats();
      getMessenger().ifPresent(messenger -> getWorldServer().updateMessenger(messenger, getName(), getWorld(), client.getChannel()));
   }

   public void cancelDiseaseExpireTask() {
      scheduler.cancel(MapleCharacterScheduler.Type.DISEASE_EXPIRE);
   }

   public void diseaseExpireTask() {
      scheduler.addIfNotExists(MapleCharacterScheduler.Type.DISEASE_EXPIRE, () -> {
         Set<MapleDisease> toExpire = new LinkedHashSet<>();

         chrLock.lock();
         try {
            long curTime = Server.getInstance().getCurrentTime();

            for (Entry<MapleDisease, Long> de : diseaseExpires.entrySet()) {
               if (de.getValue() < curTime) {
                  toExpire.add(de.getKey());
               }
            }
         } finally {
            chrLock.unlock();
         }

         for (MapleDisease d : toExpire) {
            dispelDebuff(d);
         }
      }, 1500);
   }

   public void cancelBuffExpireTask() {
      scheduler.cancel(MapleCharacterScheduler.Type.BUFF_EXPIRE);
   }

   public void buffExpireTask() {
      scheduler.addIfNotExists(MapleCharacterScheduler.Type.BUFF_EXPIRE, () -> {
         Set<Entry<Integer, Long>> es;
         List<MapleBuffStatValueHolder> toCancel = new ArrayList<>();

         effLock.lock();
         chrLock.lock();
         try {
            es = new LinkedHashSet<>(buffExpires.entrySet());

            long curTime = Server.getInstance().getCurrentTime();
            for (Entry<Integer, Long> bel : es) {
               if (curTime >= bel.getValue()) {
                  toCancel.add(buffEffects.get(bel.getKey()).entrySet().iterator().next().getValue());    //rofl
               }
            }
         } finally {
            chrLock.unlock();
            effLock.unlock();
         }

         for (MapleBuffStatValueHolder mbsvh : toCancel) {
            cancelEffect(mbsvh.effect, false, mbsvh.startTime);
         }
      }, 1500);
   }

   public void cancelSkillCooldownTask() {
      scheduler.cancel(MapleCharacterScheduler.Type.SKILL_COOLDOWN);
   }

   public void skillCooldownTask() {
      scheduler.addIfNotExists(MapleCharacterScheduler.Type.SKILL_COOLDOWN, () -> {
         Set<Entry<Integer, CoolDownValueHolder>> es;

         effLock.lock();
         chrLock.lock();
         try {
            es = new LinkedHashSet<>(coolDowns.entrySet());
         } finally {
            chrLock.unlock();
            effLock.unlock();
         }

         long curTime = Server.getInstance().getCurrentTime();
         for (Entry<Integer, CoolDownValueHolder> bel : es) {
            CoolDownValueHolder mcdvh = bel.getValue();
            if (curTime >= mcdvh.startTime() + mcdvh.length()) {
               removeCooldown(mcdvh.skillId());
               client.announce(MaplePacketCreator.skillCooldown(mcdvh.skillId(), 0));
            }
         }
      }, 1500);
   }

   public void cancelExpirationTask() {
      scheduler.cancel(MapleCharacterScheduler.Type.ITEM_EXPIRE);
   }

   public void expirationTask() {
      scheduler.addIfNotExists(MapleCharacterScheduler.Type.ITEM_EXPIRE, () -> {
         boolean deletedCoupon = false;

         long expiration, currenttime = System.currentTimeMillis();
         Set<Skill> keys = getSkills().keySet();
         for (Skill key : keys) {
            SkillEntry skill = getSkills().get(key);
            if (skill.expiration() != -1 && skill.expiration() < currenttime) {
               changeSkillLevel(key, (byte) -1, 0, -1);
            }
         }

         List<Item> toberemove = new ArrayList<>();
         for (MapleInventory inv : inventory) {
            for (Item item : inv.list()) {
               expiration = item.expiration();

               if (expiration != -1 && (expiration < currenttime) && ((item.flag() & ItemConstants.LOCK) == ItemConstants.LOCK)) {
                  short lock = item.flag();
                  lock &= ~(ItemConstants.LOCK);
                  ItemProcessor.getInstance().setFlag(item, lock); //Probably need a check, else people can make expiring items into permanent items...
                  item.expiration_(-1);
                  forceUpdateItem(item);   //TEST :3
               } else if (expiration != -1 && expiration < currenttime) {
                  if (!ItemConstants.isPet(item.id())) {
                     client.announce(MaplePacketCreator.itemExpired(item.id()));
                     toberemove.add(item);
                     if (ItemConstants.isRateCoupon(item.id())) {
                        deletedCoupon = true;
                     }
                  } else {
                     if (ItemConstants.isExpirablePet(item.id())) {
                        client.announce(MaplePacketCreator.itemExpired(item.id()));
                        toberemove.add(item);
                     } else {
                        item.expiration_(-1);
                        forceUpdateItem(item);
                     }
                  }
               }
            }

            if (!toberemove.isEmpty()) {
               for (Item item : toberemove) {
                  MapleInventoryManipulator.removeFromSlot(client, inv.getType(), item.position(), item.quantity(), true);
               }

               MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
               for (Item item : toberemove) {
                  List<Integer> toadd = new ArrayList<>();
                  Pair<Integer, String> replace = ii.getReplaceOnExpire(item.id());
                  if (replace.left > 0) {
                     toadd.add(replace.left);
                     if (!replace.right.isEmpty()) {
                        MessageBroadcaster.getInstance().sendServerNotice(MapleCharacter.this, ServerNoticeType.NOTICE, replace.right);
                     }
                  }
                  for (Integer itemid : toadd) {
                     MapleInventoryManipulator.addById(client, itemid, (short) 1);
                  }
               }

               toberemove.clear();
            }

            if (deletedCoupon) {
               updateCouponRates();
            }
         }
      }, 60000);
   }

   public void forceUpdateItem(Item item) {
      final List<ModifyInventory> mods = new LinkedList<>();
      mods.add(new ModifyInventory(3, item));
      mods.add(new ModifyInventory(0, item));
      client.announce(MaplePacketCreator.modifyInventory(true, mods));
   }

   public void gainGachaExp() {
      int expgain = 0;
      long currentgexp = gachaexp.get();
      if ((currentgexp + exp.get()) >= ExpTable.getExpNeededForLevel(level)) {
         expgain += ExpTable.getExpNeededForLevel(level) - exp.get();

         int nextneed = ExpTable.getExpNeededForLevel(level + 1);
         if (currentgexp - expgain >= nextneed) {
            expgain += nextneed;
         }

         this.gachaexp.set((int) (currentgexp - expgain));
      } else {
         expgain = this.gachaexp.getAndSet(0);
      }
      gainExp(expgain, false, true);
      updateSingleStat(MapleStat.GACHAEXP, this.gachaexp.get());
   }

   public void addGachaExp(int gain) {
      updateSingleStat(MapleStat.GACHAEXP, gachaexp.addAndGet(gain));
   }

   public void gainExp(int gain) {
      gainExp(gain, true, true);
   }

   public void gainExp(int gain, boolean show, boolean inChat) {
      gainExp(gain, show, inChat, true);
   }

   public void gainExp(int gain, boolean show, boolean inChat, boolean white) {
      gainExp(gain, 0, show, inChat, white);
   }

   public void gainExp(int gain, int party, boolean show, boolean inChat, boolean white) {
      if (hasDisease(MapleDisease.CURSE)) {
         gain *= 0.5;
         party *= 0.5;
      }

      if (gain < 0) {
         gain = Integer.MAX_VALUE;   // integer overflow, heh.
      }

      if (party < 0) {
         party = Integer.MAX_VALUE;  // integer overflow, heh.
      }

      int equip = (int) Math.min((long) (gain / 10) * pendantExp, Integer.MAX_VALUE);

      gainExpInternal(gain, equip, party, show, inChat, white);
   }

   public void loseExp(int loss, boolean show, boolean inChat) {
      loseExp(loss, show, inChat, true);
   }

   private void loseExp(int loss, boolean show, boolean inChat, boolean white) {
      gainExpInternal(-loss, 0, 0, show, inChat, white);
   }

   private void announceExpGain(long gain, int equip, int party, boolean inChat, boolean white) {
      gain = Math.min(gain, Integer.MAX_VALUE);
      if (gain == 0) {
         if (party == 0) {
            return;
         }

         gain = party;
         party = 0;
         white = false;
      }

      client.announce(MaplePacketCreator.getShowExpGain((int) gain, equip, party, inChat, white));
   }

   private synchronized void gainExpInternal(long gain, int equip, int party, boolean show, boolean inChat, boolean white) {   // need of method synchonization here detected thanks to MedicOP
      long total = Math.max(gain + equip + party, -exp.get());

      if (level < getMaxLevel() && (allowExpGain || this.getEventInstance() != null)) {
         long leftover = 0;
         long nextExp = exp.get() + total;

         if (nextExp > (long) Integer.MAX_VALUE) {
            total = Integer.MAX_VALUE - exp.get();
            leftover = nextExp - Integer.MAX_VALUE;
         }
         updateSingleStat(MapleStat.EXP, exp.addAndGet((int) total));
         if (show) {
            announceExpGain(gain, equip, party, inChat, white);
         }
         while (exp.get() >= ExpTable.getExpNeededForLevel(level)) {
            levelUp(true);
            if (level == getMaxLevel()) {
               setExp(0);
               updateSingleStat(MapleStat.EXP, 0);
               break;
            }
         }

         if (leftover > 0) {
            gainExpInternal(leftover, equip, party, false, inChat, white);
         } else {
            lastExpGainTime = System.currentTimeMillis();
         }
      }
   }

   private Pair<Integer, Integer> applyFame(int delta) {
      petLock.lock();
      try {
         int newFame = fame + delta;
         if (newFame < -30000) {
            delta = -(30000 + fame);
         } else if (newFame > 30000) {
            delta = 30000 - fame;
         }

         fame += delta;
         return new Pair<>(fame, delta);
      } finally {
         petLock.unlock();
      }
   }

   public void gainFame(int delta) {
      gainFame(delta, null, 0);
   }

   public boolean gainFame(int delta, MapleCharacter fromPlayer, int mode) {
      Pair<Integer, Integer> fameRes = applyFame(delta);
      delta = fameRes.getRight();
      if (delta != 0) {
         int thisFame = fameRes.getLeft();
         updateSingleStat(MapleStat.FAME, thisFame);

         if (fromPlayer != null) {
            fromPlayer.announce(MaplePacketCreator.giveFameResponse(mode, getName(), thisFame));
            announce(MaplePacketCreator.receiveFame(mode, fromPlayer.getName()));
         } else {
            announce(MaplePacketCreator.getShowFameGain(delta));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean canHoldMeso(int gain) {  // thanks lucasziron found pointing out a need to check space availability for mesos on player transactions
      long nextMeso = (long) meso.get() + gain;
      return nextMeso <= Integer.MAX_VALUE;
   }

   public void gainMeso(int gain) {
      gainMeso(gain, true, false, true);
   }

   public void gainMeso(int gain, boolean show) {
      gainMeso(gain, show, false, false);
   }

   public void gainMeso(int gain, boolean show, boolean enableActions, boolean inChat) {
      long nextMeso;
      petLock.lock();
      try {
         nextMeso = (long) meso.get() + gain;  // thanks Thora for pointing integer overflow here
         if (nextMeso > Integer.MAX_VALUE) {
            gain -= (nextMeso - Integer.MAX_VALUE);
         } else if (nextMeso < 0) {
            gain = -meso.get();
         }
         nextMeso = meso.addAndGet(gain);
      } finally {
         petLock.unlock();
      }

      if (gain != 0) {
         updateSingleStat(MapleStat.MESO, (int) nextMeso, enableActions);
         if (show) {
            client.announce(MaplePacketCreator.getShowMesoGain(gain, inChat));
         }
      } else {
         client.announce(MaplePacketCreator.enableActions());
      }
   }

   public void genericGuildMessage(int code) {
      this.client.announce(MaplePacketCreator.genericGuildMessage((byte) code));
   }

   public int getAccountID() {
      return accountid;
   }

   public List<PlayerCoolDownValueHolder> getAllCooldowns() {
      List<PlayerCoolDownValueHolder> ret = new ArrayList<>();

      effLock.lock();
      chrLock.lock();
      try {
         for (CoolDownValueHolder mcdvh : coolDowns.values()) {
            ret.add(new PlayerCoolDownValueHolder(mcdvh.skillId(), mcdvh.startTime(), mcdvh.length()));
         }
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }

      return ret;
   }

   public int getAllianceRank() {
      return allianceRank;
   }

   public void setAllianceRank(int _rank) {
      allianceRank = _rank;
   }

   public void updateAriantScore() {
      updateAriantScore(0);
   }

   public void updateAriantScore(int dropQty) {
      AriantColiseum arena = this.getAriantColiseum();
      if (arena != null) {
         arena.updateAriantScore(this, countItem(4031868));

         if (dropQty > 0) {
            arena.addLostShards(dropQty);
         }
      }
   }

   public int getBattleshipHp() {
      return battleShipHp;
   }

   public BuddyList getBuddylist() {
      return buddylist;
   }

   public Long getBuffedStarttime(MapleBuffStat effect) {
      effLock.lock();
      chrLock.lock();
      try {
         MapleBuffStatValueHolder mbsvh = effects.get(effect);
         if (mbsvh == null) {
            return null;
         }
         return mbsvh.startTime;
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   public Integer getBuffedValue(MapleBuffStat effect) {
      effLock.lock();
      chrLock.lock();
      try {
         MapleBuffStatValueHolder mbsvh = effects.get(effect);
         if (mbsvh == null) {
            return null;
         }
         return mbsvh.value;
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   public int getBuffSource(MapleBuffStat stat) {
      effLock.lock();
      chrLock.lock();
      try {
         MapleBuffStatValueHolder mbsvh = effects.get(stat);
         if (mbsvh == null) {
            return -1;
         }
         return mbsvh.effect.getSourceId();
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   public MapleStatEffect getBuffEffect(MapleBuffStat stat) {
      effLock.lock();
      chrLock.lock();
      try {
         MapleBuffStatValueHolder mbsvh = effects.get(stat);
         if (mbsvh == null) {
            return null;
         } else {
            return mbsvh.effect;
         }
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   private List<MapleBuffStatValueHolder> getAllStatups() {
      effLock.lock();
      chrLock.lock();
      try {
         List<MapleBuffStatValueHolder> ret = new ArrayList<>();
         for (Map<MapleBuffStat, MapleBuffStatValueHolder> bel : buffEffects.values()) {
            ret.addAll(bel.values());
         }
         return ret;
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   public List<PlayerBuffValueHolder> getAllBuffs() {  // buff values will be stored in an arbitrary order
      effLock.lock();
      chrLock.lock();
      try {
         long curtime = Server.getInstance().getCurrentTime();

         Map<Integer, PlayerBuffValueHolder> ret = new LinkedHashMap<>();
         for (Map<MapleBuffStat, MapleBuffStatValueHolder> bel : buffEffects.values()) {
            for (MapleBuffStatValueHolder mbsvh : bel.values()) {
               int srcid = mbsvh.effect.getBuffSourceId();
               if (!ret.containsKey(srcid)) {
                  ret.put(srcid, new PlayerBuffValueHolder((int) (curtime - mbsvh.startTime), mbsvh.effect));
               }
            }
         }
         return new ArrayList<>(ret.values());
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   public boolean hasBuffFromSourceid(int sourceid) {
      effLock.lock();
      chrLock.lock();
      try {
         return buffEffects.containsKey(sourceid);
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   public boolean hasActiveBuff(int sourceid) {
      LinkedList<MapleBuffStatValueHolder> allBuffs;

      effLock.lock();
      chrLock.lock();
      try {
         allBuffs = new LinkedList<>(effects.values());
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }

      for (MapleBuffStatValueHolder mbsvh : allBuffs) {
         if (mbsvh.effect.getBuffSourceId() == sourceid) {
            return true;
         }
      }
      return false;
   }

   private List<Pair<MapleBuffStat, Integer>> getActiveStatupsFromSourceid(int sourceid) { // already under effLock & chrLock
      List<Pair<MapleBuffStat, Integer>> ret = new ArrayList<>();
      List<Pair<MapleBuffStat, Integer>> singletonStatups = new ArrayList<>();

      for (Entry<MapleBuffStat, MapleBuffStatValueHolder> bel : buffEffects.get(sourceid).entrySet()) {
         MapleBuffStat mbs = bel.getKey();
         MapleBuffStatValueHolder mbsvh = effects.get(bel.getKey());

         Pair<MapleBuffStat, Integer> p;
         if (mbsvh != null) {
            p = new Pair<>(mbs, mbsvh.value);
         } else {
            p = new Pair<>(mbs, 0);
         }

         if (!BuffStatProcessor.getInstance().isSingletonStatup(mbs)) {   // thanks resinate, Egg Daddy for pointing out morph issues when updating it along with other statups
            ret.add(p);
         } else {
            singletonStatups.add(p);
         }
      }

      ret.sort((p1, p2) -> p1.getLeft().compareTo(p2.getLeft()));

      if (!singletonStatups.isEmpty()) {
         singletonStatups.sort((p1, p2) -> p1.getLeft().compareTo(p2.getLeft()));

         ret.addAll(singletonStatups);
      }

      return ret;
   }

   private void addItemEffectHolder(Integer sourceid, long expirationtime, Map<MapleBuffStat, MapleBuffStatValueHolder> statups) {
      buffEffects.put(sourceid, statups);
      buffExpires.put(sourceid, expirationtime);
   }

   private boolean removeEffectFromItemEffectHolder(Integer sourceid, MapleBuffStat buffStat) {
      Map<MapleBuffStat, MapleBuffStatValueHolder> lbe = buffEffects.get(sourceid);

      if (lbe.remove(buffStat) != null) {
         buffEffectsCount.put(buffStat, (byte) (buffEffectsCount.get(buffStat) - 1));

         if (lbe.isEmpty()) {
            buffEffects.remove(sourceid);
            buffExpires.remove(sourceid);
         }

         return true;
      }

      return false;
   }

   private void removeItemEffectHolder(Integer sourceid) {
      Map<MapleBuffStat, MapleBuffStatValueHolder> be = buffEffects.remove(sourceid);
      if (be != null) {
         for (Entry<MapleBuffStat, MapleBuffStatValueHolder> bei : be.entrySet()) {
            buffEffectsCount.put(bei.getKey(), (byte) (buffEffectsCount.get(bei.getKey()) - 1));
         }
      }

      buffExpires.remove(sourceid);
   }

   private MapleBuffStatValueHolder fetchBestEffectFromItemEffectHolder(MapleBuffStat mbs) {
      Pair<Integer, Integer> max = new Pair<>(Integer.MIN_VALUE, 0);
      MapleBuffStatValueHolder mbsvh = null;
      for (Entry<Integer, Map<MapleBuffStat, MapleBuffStatValueHolder>> bpl : buffEffects.entrySet()) {
         MapleBuffStatValueHolder mbsvhi = bpl.getValue().get(mbs);
         if (mbsvhi != null) {
            if (!mbsvhi.effect.isActive(this)) {
               continue;
            }

            if (mbsvhi.value > max.left) {
               max = new Pair<>(mbsvhi.value, mbsvhi.effect.getStatups().size());
               mbsvh = mbsvhi;
            } else if (mbsvhi.value == max.left && mbsvhi.effect.getStatups().size() > max.right) {
               max = new Pair<>(mbsvhi.value, mbsvhi.effect.getStatups().size());
               mbsvh = mbsvhi;
            }
         }
      }

      if (mbsvh != null) {
         effects.put(mbs, mbsvh);
      }
      return mbsvh;
   }

   private void extractBuffValue(int sourceid, MapleBuffStat stat) {
      chrLock.lock();
      try {
         removeEffectFromItemEffectHolder(sourceid, stat);
      } finally {
         chrLock.unlock();
      }
   }

   public void debugListAllBuffs() {
      effLock.lock();
      chrLock.lock();
      try {
         System.out.println("-------------------");
         System.out.println("CACHED BUFF COUNT: ");
         for (Entry<MapleBuffStat, Byte> bpl : buffEffectsCount.entrySet()) {
            System.out.println(bpl.getKey() + ": " + bpl.getValue());
         }
         System.out.println("-------------------");
         System.out.println("CACHED BUFFS: ");
         for (Entry<Integer, Map<MapleBuffStat, MapleBuffStatValueHolder>> bpl : buffEffects.entrySet()) {
            System.out.print(bpl.getKey() + ": ");
            for (Entry<MapleBuffStat, MapleBuffStatValueHolder> pble : bpl.getValue().entrySet()) {
               System.out.print(pble.getKey().name() + pble.getValue().value + ", ");
            }
            System.out.println();
         }
         System.out.println("-------------------");

         System.out.println("IN ACTION:");
         for (Entry<MapleBuffStat, MapleBuffStatValueHolder> bpl : effects.entrySet()) {
            System.out.println(bpl.getKey().name() + " -> " + MapleItemInformationProvider.getInstance().getName(bpl.getValue().effect.getSourceId()));
         }
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   public void cancelAllBuffs(boolean softcancel) {
      if (softcancel) {
         effLock.lock();
         chrLock.lock();
         try {
            cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
            cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
            cancelEffectFromBuffStat(MapleBuffStat.COMBO);

            effects.clear();

            for (Integer srcid : new ArrayList<>(buffEffects.keySet())) {
               removeItemEffectHolder(srcid);
            }
         } finally {
            chrLock.unlock();
            effLock.unlock();
         }
      } else {
         Map<MapleStatEffect, Long> mseBuffs = new LinkedHashMap<>();

         effLock.lock();
         chrLock.lock();
         try {
            for (Entry<Integer, Map<MapleBuffStat, MapleBuffStatValueHolder>> bpl : buffEffects.entrySet()) {
               for (Entry<MapleBuffStat, MapleBuffStatValueHolder> mbse : bpl.getValue().entrySet()) {
                  mseBuffs.put(mbse.getValue().effect, mbse.getValue().startTime);
               }
            }
         } finally {
            chrLock.unlock();
            effLock.unlock();
         }

         for (Entry<MapleStatEffect, Long> mse : mseBuffs.entrySet()) {
            cancelEffect(mse.getKey(), false, mse.getValue());
         }
      }
   }

   private void dropBuffStats(List<Pair<MapleBuffStat, MapleBuffStatValueHolder>> effectsToCancel) {
      for (Pair<MapleBuffStat, MapleBuffStatValueHolder> cancelEffectCancelTasks : effectsToCancel) {
         //boolean nestedCancel = false;

         chrLock.lock();
         try {
                /*
                if (buffExpires.get(cancelEffectCancelTasks.getRight().effect.getBuffSourceId()) != null) {
                    nestedCancel = true;
                }*/

            if (cancelEffectCancelTasks.getRight().bestApplied) {
               fetchBestEffectFromItemEffectHolder(cancelEffectCancelTasks.getLeft());
            }
         } finally {
            chrLock.unlock();
         }

            /*
            if (nestedCancel) {
                this.cancelEffect(cancelEffectCancelTasks.getRight().effect, false, -1, false);
            }*/
      }
   }

   private List<Pair<MapleBuffStat, MapleBuffStatValueHolder>> deregisterBuffStats(Map<MapleBuffStat, MapleBuffStatValueHolder> stats) {
      chrLock.lock();
      try {
         List<Pair<MapleBuffStat, MapleBuffStatValueHolder>> effectsToCancel = new ArrayList<>(stats.size());
         for (Entry<MapleBuffStat, MapleBuffStatValueHolder> stat : stats.entrySet()) {
            int sourceid = stat.getValue().effect.getBuffSourceId();

            if (!buffEffects.containsKey(sourceid)) {
               buffExpires.remove(sourceid);
            }

            MapleBuffStat mbs = stat.getKey();
            effectsToCancel.add(new Pair<>(mbs, stat.getValue()));

            MapleBuffStatValueHolder mbsvh = effects.get(mbs);
            if (mbsvh != null && mbsvh.effect.getBuffSourceId() == sourceid) {
               mbsvh.bestApplied = true;
               effects.remove(mbs);

               if (mbs == MapleBuffStat.RECOVERY) {
                  scheduler.cancel(MapleCharacterScheduler.Type.RECOVERY);
               } else if (mbs == MapleBuffStat.SUMMON || mbs == MapleBuffStat.PUPPET) {
                  int summonId = mbsvh.effect.getSourceId();

                  MapleSummon summon = summons.get(summonId);
                  if (summon != null) {
                     getMap().broadcastMessage(MaplePacketCreator.removeSummon(summon, true), summon.getPosition());
                     getMap().removeMapObject(summon);
                     removeVisibleMapObject(summon);

                     summons.remove(summonId);
                     if (summon.isPuppet()) {
                        map.removePlayerPuppet(this);
                     } else if (summon.getSkill() == DarkKnight.BEHOLDER) {
                        scheduler.cancel(MapleCharacterScheduler.Type.BEHODLER_HEAL);
                        scheduler.cancel(MapleCharacterScheduler.Type.BEHOLDER_BUFF);
                     }
                  }
               } else if (mbs == MapleBuffStat.DRAGONBLOOD) {
                  scheduler.cancel(MapleCharacterScheduler.Type.DRAGON_BLOOD);
               } else if (mbs == MapleBuffStat.HPREC || mbs == MapleBuffStat.MPREC) {
                  if (mbs == MapleBuffStat.HPREC) {
                     extraHpRec = 0;
                  } else {
                     extraMpRec = 0;
                  }
                  scheduler.cancel(MapleCharacterScheduler.Type.EXTRA_RECOVERY);

                  if (extraHpRec != 0 || extraMpRec != 0) {
                     startExtraTaskInternal(extraHpRec, extraMpRec, extraRecInterval);
                  }
               }
            }
         }

         return effectsToCancel;
      } finally {
         chrLock.unlock();
      }
   }

   public void cancelEffect(int itemId) {
      cancelEffect(ii.getItemEffect(itemId), false, -1);
   }

   public boolean cancelEffect(MapleStatEffect effect, boolean overwrite, long startTime) {
      boolean ret;

      effLock.lock();
      try {
         ret = cancelEffect(effect, overwrite, startTime, true);
      } finally {
         effLock.unlock();
      }

      if (effect.isMagicDoor() && ret) {
         prtLock.lock();
         effLock.lock();
         try {
            if (!hasBuffFromSourceid(Priest.MYSTIC_DOOR)) {
               MapleDoorProcessor.getInstance().attemptRemoveDoor(this);
            }
         } finally {
            effLock.unlock();
            prtLock.unlock();
         }
      }

      return ret;
   }

   private boolean isUpdatingEffect(Set<MapleStatEffect> activeEffects, MapleStatEffect mse) {
      if (mse == null) {
         return false;
      }

      // thanks xinyifly for noticing "Speed Infusion" crashing game when updating buffs during map transition
      boolean active = mse.isActive(this);
      if (active) {
         return !activeEffects.contains(mse);
      } else {
         return activeEffects.contains(mse);
      }
   }

   public void updateActiveEffects() {
      effLock.lock();     // thanks davidlafriniere, maple006, RedHat for pointing a deadlock occurring here
      try {
         Set<MapleBuffStat> updatedBuffs = new LinkedHashSet<>();
         Set<MapleStatEffect> activeEffects = new LinkedHashSet<>();

         for (MapleBuffStatValueHolder mse : effects.values()) {
            activeEffects.add(mse.effect);
         }

         for (Map<MapleBuffStat, MapleBuffStatValueHolder> buff : buffEffects.values()) {
            MapleStatEffect mse = BuffStatProcessor.getInstance().getEffectFromBuffSource(buff);
            if (isUpdatingEffect(activeEffects, mse)) {
               for (Pair<MapleBuffStat, Integer> p : mse.getStatups()) {
                  updatedBuffs.add(p.getLeft());
               }
            }
         }

         for (MapleBuffStat mbs : updatedBuffs) {
            effects.remove(mbs);
         }

         updateEffects(updatedBuffs);
      } finally {
         effLock.unlock();
      }
   }

   private void updateEffects(Set<MapleBuffStat> removedStats) {
      effLock.lock();
      chrLock.lock();
      try {
         Set<MapleBuffStat> retrievedStats = new LinkedHashSet<>();

         for (MapleBuffStat mbs : removedStats) {
            fetchBestEffectFromItemEffectHolder(mbs);

            MapleBuffStatValueHolder mbsvh = effects.get(mbs);
            if (mbsvh != null) {
               for (Pair<MapleBuffStat, Integer> statup : mbsvh.effect.getStatups()) {
                  retrievedStats.add(statup.getLeft());
               }
            }
         }

         propagateBuffEffectUpdates(new LinkedHashMap<>(), retrievedStats, removedStats);
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   private boolean cancelEffect(MapleStatEffect effect, boolean overwrite, long startTime, boolean firstCancel) {
      Set<MapleBuffStat> removedStats = new LinkedHashSet<>();
      dropBuffStats(cancelEffectInternal(effect, overwrite, startTime, removedStats));
      updateLocalStats();
      updateEffects(removedStats);

      return !removedStats.isEmpty();
   }

   private List<Pair<MapleBuffStat, MapleBuffStatValueHolder>> cancelEffectInternal(MapleStatEffect effect, boolean overwrite, long startTime, Set<MapleBuffStat> removedStats) {
      Map<MapleBuffStat, MapleBuffStatValueHolder> buffstats = null;
      MapleBuffStat ombs;
      if (!overwrite) {   // is removing the source effect, meaning every effect from this srcid is being purged
         buffstats = extractCurrentBuffStats(effect);
      } else if ((ombs = BuffStatProcessor.getInstance().getSingletonStatupFromEffect(effect)) != null) {   // removing all effects of a buff having non-shareable buff stat.
         MapleBuffStatValueHolder mbsvh = effects.get(ombs);
         if (mbsvh != null) {
            buffstats = extractCurrentBuffStats(mbsvh.effect);
         }
      }

      if (buffstats == null) {            // all else, is dropping ALL current statups that uses same stats as the given effect
         buffstats = extractLeastRelevantStatEffectsIfFull(effect);
      }

      if (effect.isMapChair()) {
         stopChairTask();
      }

      List<Pair<MapleBuffStat, MapleBuffStatValueHolder>> toCancel = deregisterBuffStats(buffstats);
      if (effect.isMonsterRiding()) {
         this.getClient().getWorldServer().unregisterMountHunger(this);
         this.getMount().setActive(false);
      }

      if (!overwrite) {
         removedStats.addAll(buffstats.keySet());
      }

      return toCancel;
   }

   public void cancelEffectFromBuffStat(MapleBuffStat stat) {
      MapleBuffStatValueHolder effect;

      effLock.lock();
      chrLock.lock();
      try {
         effect = effects.get(stat);
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
      if (effect != null) {
         cancelEffect(effect.effect, false, -1);
      }
   }

   public void cancelBuffStats(MapleBuffStat stat) {
      effLock.lock();
      try {
         List<Pair<Integer, MapleBuffStatValueHolder>> cancelList = new LinkedList<>();

         chrLock.lock();
         try {
            for (Entry<Integer, Map<MapleBuffStat, MapleBuffStatValueHolder>> bel : this.buffEffects.entrySet()) {
               MapleBuffStatValueHolder beli = bel.getValue().get(stat);
               if (beli != null) {
                  cancelList.add(new Pair<>(bel.getKey(), beli));
               }
            }
         } finally {
            chrLock.unlock();
         }

         Map<MapleBuffStat, MapleBuffStatValueHolder> buffStatList = new LinkedHashMap<>();
         for (Pair<Integer, MapleBuffStatValueHolder> p : cancelList) {
            buffStatList.put(stat, p.getRight());
            extractBuffValue(p.getLeft(), stat);
            dropBuffStats(deregisterBuffStats(buffStatList));
         }
      } finally {
         effLock.unlock();
      }

      cancelPlayerBuffs(Collections.singletonList(stat));
   }

   private Map<MapleBuffStat, MapleBuffStatValueHolder> extractCurrentBuffStats(MapleStatEffect effect) {
      chrLock.lock();
      try {
         Map<MapleBuffStat, MapleBuffStatValueHolder> stats = new LinkedHashMap<>();
         Map<MapleBuffStat, MapleBuffStatValueHolder> buffList = buffEffects.remove(effect.getBuffSourceId());

         if (buffList != null) {
            for (Entry<MapleBuffStat, MapleBuffStatValueHolder> stateffect : buffList.entrySet()) {
               stats.put(stateffect.getKey(), stateffect.getValue());
               buffEffectsCount.put(stateffect.getKey(), (byte) (buffEffectsCount.get(stateffect.getKey()) - 1));
            }
         }

         return stats;
      } finally {
         chrLock.unlock();
      }
   }

   private Map<MapleBuffStat, MapleBuffStatValueHolder> extractLeastRelevantStatEffectsIfFull(MapleStatEffect effect) {
      Map<MapleBuffStat, MapleBuffStatValueHolder> extractedStatBuffs = new LinkedHashMap<>();

      chrLock.lock();
      try {
         Map<MapleBuffStat, Byte> stats = new LinkedHashMap<>();
         Map<MapleBuffStat, MapleBuffStatValueHolder> minStatBuffs = new LinkedHashMap<>();

         for (Entry<Integer, Map<MapleBuffStat, MapleBuffStatValueHolder>> mbsvhi : buffEffects.entrySet()) {
            for (Entry<MapleBuffStat, MapleBuffStatValueHolder> mbsvhe : mbsvhi.getValue().entrySet()) {
               MapleBuffStat mbs = mbsvhe.getKey();
               Byte b = stats.get(mbs);

               if (b != null) {
                  stats.put(mbs, (byte) (b + 1));
                  if (mbsvhe.getValue().value < minStatBuffs.get(mbs).value) {
                     minStatBuffs.put(mbs, mbsvhe.getValue());
                  }
               } else {
                  stats.put(mbs, (byte) 1);
                  minStatBuffs.put(mbs, mbsvhe.getValue());
               }
            }
         }

         Set<MapleBuffStat> effectStatups = new LinkedHashSet<>();
         for (Pair<MapleBuffStat, Integer> efstat : effect.getStatups()) {
            effectStatups.add(efstat.getLeft());
         }

         for (Entry<MapleBuffStat, Byte> it : stats.entrySet()) {
            boolean uniqueBuff = BuffStatProcessor.getInstance().isSingletonStatup(it.getKey());

            if (it.getValue() >= (!uniqueBuff ? ServerConstants.MAX_MONITORED_BUFFSTATS : 1) && effectStatups.contains(it.getKey())) {
               MapleBuffStatValueHolder mbsvh = minStatBuffs.get(it.getKey());

               Map<MapleBuffStat, MapleBuffStatValueHolder> lpbe = buffEffects.get(mbsvh.effect.getBuffSourceId());
               lpbe.remove(it.getKey());
               buffEffectsCount.put(it.getKey(), (byte) (buffEffectsCount.get(it.getKey()) - 1));

               if (lpbe.isEmpty()) {
                  buffEffects.remove(mbsvh.effect.getBuffSourceId());
               }
               extractedStatBuffs.put(it.getKey(), mbsvh);
            }
         }
      } finally {
         chrLock.unlock();
      }

      return extractedStatBuffs;
   }

   private void cancelInactiveBuffStats(Set<MapleBuffStat> retrievedStats, Set<MapleBuffStat> removedStats) {
      List<MapleBuffStat> inactiveStats = new LinkedList<>();
      for (MapleBuffStat mbs : removedStats) {
         if (!retrievedStats.contains(mbs)) {
            inactiveStats.add(mbs);
         }
      }

      if (!inactiveStats.isEmpty()) {
         client.announce(MaplePacketCreator.cancelBuff(inactiveStats));
         getMap().broadcastMessage(this, MaplePacketCreator.cancelForeignBuff(getId(), inactiveStats), false);
      }
   }

   private List<Pair<Integer, Pair<MapleStatEffect, Long>>> propagatePriorityBuffEffectUpdates(Set<MapleBuffStat> retrievedStats) {
      List<Pair<Integer, Pair<MapleStatEffect, Long>>> priorityUpdateEffects = new LinkedList<>();
      Map<MapleBuffStatValueHolder, MapleStatEffect> yokeStats = new LinkedHashMap<>();

      // priority buffsources: override buffstats for the client to perceive those as "currently buffed"
      Set<MapleBuffStatValueHolder> mbsvhList = new LinkedHashSet<>(getAllStatups());

      for (MapleBuffStatValueHolder mbsvh : mbsvhList) {
         MapleStatEffect mse = mbsvh.effect;
         int buffSourceId = mse.getBuffSourceId();
         if (BuffStatProcessor.getInstance().isPriorityBuffSourceid(buffSourceId) && !hasActiveBuff(buffSourceId)) {
            for (Pair<MapleBuffStat, Integer> ps : mse.getStatups()) {
               MapleBuffStat mbs = ps.getLeft();
               if (retrievedStats.contains(mbs)) {
                  MapleBuffStatValueHolder mbsvhe = effects.get(mbs);

                  // this shouldn't even be null...
                  //if (mbsvh != null) {
                  yokeStats.put(mbsvh, mbsvhe.effect);
                  //}
               }
            }
         }
      }

      for (Entry<MapleBuffStatValueHolder, MapleStatEffect> e : yokeStats.entrySet()) {
         MapleBuffStatValueHolder mbsvhPriority = e.getKey();
         MapleStatEffect mseActive = e.getValue();

         priorityUpdateEffects.add(new Pair<>(mseActive.getBuffSourceId(), new Pair<>(mbsvhPriority.effect, mbsvhPriority.startTime)));
      }

      return priorityUpdateEffects;
   }

   private void propagateBuffEffectUpdates(Map<Integer, Pair<MapleStatEffect, Long>> retrievedEffects, Set<MapleBuffStat> retrievedStats, Set<MapleBuffStat> removedStats) {
      cancelInactiveBuffStats(retrievedStats, removedStats);
      if (retrievedStats.isEmpty()) {
         return;
      }

      Map<MapleBuffStat, Pair<Integer, MapleStatEffect>> maxBuffValue = new LinkedHashMap<>();
      for (MapleBuffStat mbs : retrievedStats) {
         MapleBuffStatValueHolder mbsvh = effects.get(mbs);
         if (mbsvh != null) {
            retrievedEffects.put(mbsvh.effect.getBuffSourceId(), new Pair<>(mbsvh.effect, mbsvh.startTime));
         }

         maxBuffValue.put(mbs, new Pair<>(Integer.MIN_VALUE, null));
      }

      Map<MapleStatEffect, Integer> updateEffects = new LinkedHashMap<>();

      List<MapleStatEffect> recalcMseList = new LinkedList<>();
      for (Entry<Integer, Pair<MapleStatEffect, Long>> re : retrievedEffects.entrySet()) {
         recalcMseList.add(re.getValue().getLeft());
      }

      boolean mageJob = this.getJobStyle() == MapleJob.MAGICIAN;
      do {
         List<MapleStatEffect> mseList = recalcMseList;
         recalcMseList = new LinkedList<>();

         for (MapleStatEffect mse : mseList) {
            int maxEffectiveStatup = Integer.MIN_VALUE;
            for (Pair<MapleBuffStat, Integer> st : mse.getStatups()) {
               MapleBuffStat mbs = st.getLeft();

               boolean relevantStatup = true;
               if (mbs == MapleBuffStat.WATK) {  // not relevant for mages
                  if (mageJob) {
                     relevantStatup = false;
                  }
               } else if (mbs == MapleBuffStat.MATK) { // not relevant for non-mages
                  if (!mageJob) {
                     relevantStatup = false;
                  }
               }

               Pair<Integer, MapleStatEffect> mbv = maxBuffValue.get(mbs);
               if (mbv == null) {
                  continue;
               }

               if (mbv.getLeft() < st.getRight()) {
                  MapleStatEffect msbe = mbv.getRight();
                  if (msbe != null) {
                     recalcMseList.add(msbe);
                  }

                  maxBuffValue.put(mbs, new Pair<>(st.getRight(), mse));

                  if (relevantStatup) {
                     if (maxEffectiveStatup < st.getRight()) {
                        maxEffectiveStatup = st.getRight();
                     }
                  }
               }
            }

            updateEffects.put(mse, maxEffectiveStatup);
         }
      } while (!recalcMseList.isEmpty());

      List<MapleStatEffect> updateEffectsList = BuffStatProcessor.getInstance().sortEffectsList(updateEffects);

      List<Pair<Integer, Pair<MapleStatEffect, Long>>> toUpdateEffects = new LinkedList<>();
      for (MapleStatEffect mse : updateEffectsList) {
         toUpdateEffects.add(new Pair<>(mse.getBuffSourceId(), retrievedEffects.get(mse.getBuffSourceId())));
      }

      List<Pair<MapleBuffStat, Integer>> activeStatups = new LinkedList<>();
      for (Pair<Integer, Pair<MapleStatEffect, Long>> lmse : toUpdateEffects) {
         Pair<MapleStatEffect, Long> msel = lmse.getRight();

         activeStatups.addAll(getActiveStatupsFromSourceid(lmse.getLeft()));

         msel.getLeft().updateBuffEffect(this, activeStatups, msel.getRight());
         activeStatups.clear();
      }

      List<Pair<Integer, Pair<MapleStatEffect, Long>>> priorityEffects = propagatePriorityBuffEffectUpdates(retrievedStats);
      for (Pair<Integer, Pair<MapleStatEffect, Long>> lmse : priorityEffects) {
         Pair<MapleStatEffect, Long> msel = lmse.getRight();

         activeStatups.addAll(getActiveStatupsFromSourceid(lmse.getLeft()));

         msel.getLeft().updateBuffEffect(this, activeStatups, msel.getRight());
         activeStatups.clear();
      }

      if (this.isRidingBattleship()) {
         List<Pair<MapleBuffStat, Integer>> statups = new ArrayList<>(1);
         statups.add(new Pair<>(MapleBuffStat.MONSTER_RIDING, 0));
         this.announce(MaplePacketCreator.giveBuff(1932000, 5221006, statups));
         this.announceBattleshipHp();
      }
   }

   private void addItemEffectHolderCount(MapleBuffStat stat) {
      Byte val = buffEffectsCount.get(stat);
      if (val != null) {
         val = (byte) (val + 1);
      } else {
         val = (byte) 1;
      }

      buffEffectsCount.put(stat, val);
   }

   public void registerEffect(MapleStatEffect effect, long startTime, long expirationTime, boolean isSilent) {
      if (effect.isDragonBlood()) {
         prepareDragonBlood(effect);
      } else if (effect.isBerserk()) {
         checkBerserk(isHidden());
      } else if (effect.isBeholder()) {
         final int beholder = DarkKnight.BEHOLDER;
         scheduler.cancel(MapleCharacterScheduler.Type.BEHODLER_HEAL);
         scheduler.cancel(MapleCharacterScheduler.Type.BEHOLDER_BUFF);
         executeIfHasSkill(DarkKnight.AURA_OF_BEHOLDER, (skill, skillLevel) -> scheduleAuraOfBeholder(beholder, skill, skillLevel));
         executeIfHasSkill(DarkKnight.HEX_OF_BEHOLDER, (skill, skillLevel) -> scheduleHexOfBeholder(beholder, skill, skillLevel));
      } else if (effect.isRecovery()) {
         int healInterval = (ServerConstants.USE_ULTRA_RECOVERY) ? 2000 : 5000;
         final byte heal = (byte) effect.getX();

         chrLock.lock();
         try {
            scheduler.cancel(MapleCharacterScheduler.Type.RECOVERY);
            scheduler.add(MapleCharacterScheduler.Type.RECOVERY, () -> {
               if (getBuffSource(MapleBuffStat.RECOVERY) == -1) {
                  chrLock.lock();
                  try {
                     scheduler.cancel(MapleCharacterScheduler.Type.RECOVERY);
                  } finally {
                     chrLock.unlock();
                  }

                  return;
               }

               addHP(heal);
               client.announce(MaplePacketCreator.showOwnRecovery(heal));
               getMap().broadcastMessage(MapleCharacter.this, MaplePacketCreator.showRecovery(id, heal), false);
            }, healInterval, healInterval);
         } finally {
            chrLock.unlock();
         }
      } else if (effect.getHpRRate() > 0 || effect.getMpRRate() > 0) {
         if (effect.getHpRRate() > 0) {
            extraHpRec = effect.getHpR();
            extraRecInterval = effect.getHpRRate();
         }

         if (effect.getMpRRate() > 0) {
            extraMpRec = effect.getMpR();
            extraRecInterval = effect.getMpRRate();
         }

         chrLock.lock();
         try {
            stopExtraTask();
            startExtraTask(extraHpRec, extraMpRec, extraRecInterval);   // HP & MP sharing the same task holder
         } finally {
            chrLock.unlock();
         }

      } else if (effect.isMapChair()) {
         startChairTask();
      }

      effLock.lock();
      chrLock.lock();
      try {
         Integer sourceId = effect.getBuffSourceId();
         Map<MapleBuffStat, MapleBuffStatValueHolder> toDeploy;
         Map<MapleBuffStat, MapleBuffStatValueHolder> appliedStatUps = new LinkedHashMap<>();

         for (Pair<MapleBuffStat, Integer> ps : effect.getStatups()) {
            appliedStatUps.put(ps.getLeft(), new MapleBuffStatValueHolder(effect, startTime, ps.getRight()));
         }

         boolean active = effect.isActive(this);
         if (ServerConstants.USE_BUFF_MOST_SIGNIFICANT) {
            toDeploy = new LinkedHashMap<>();
            Map<Integer, Pair<MapleStatEffect, Long>> retrievedEffects = new LinkedHashMap<>();
            Set<MapleBuffStat> retrievedStats = new LinkedHashSet<>();

            for (Entry<MapleBuffStat, MapleBuffStatValueHolder> statUp : appliedStatUps.entrySet()) {
               MapleBuffStatValueHolder mbsvh = effects.get(statUp.getKey());
               MapleBuffStatValueHolder statMbsvh = statUp.getValue();

               if (active) {
                  if (mbsvh == null || mbsvh.value < statMbsvh.value || (mbsvh.value == statMbsvh.value && mbsvh.effect.getStatups().size() <= statMbsvh.effect.getStatups().size())) {
                     toDeploy.put(statUp.getKey(), statMbsvh);
                  } else {
                     if (!BuffStatProcessor.getInstance().isSingletonStatup(statUp.getKey())) {
                        for (Pair<MapleBuffStat, Integer> mbs : mbsvh.effect.getStatups()) {
                           retrievedStats.add(mbs.getLeft());
                        }
                     }
                  }
               }

               addItemEffectHolderCount(statUp.getKey());
            }

            // should also propagate update from buffs shared with priority sourceids
            Set<MapleBuffStat> updated = appliedStatUps.keySet();
            for (MapleBuffStatValueHolder mbsvh : this.getAllStatups()) {
               if (BuffStatProcessor.getInstance().isPriorityBuffSourceid(mbsvh.effect.getBuffSourceId())) {
                  for (Pair<MapleBuffStat, Integer> p : mbsvh.effect.getStatups()) {
                     if (updated.contains(p.getLeft())) {
                        retrievedStats.add(p.getLeft());
                     }
                  }
               }
            }

            if (!isSilent) {
               addItemEffectHolder(sourceId, expirationTime, appliedStatUps);
               for (Entry<MapleBuffStat, MapleBuffStatValueHolder> statUp : toDeploy.entrySet()) {
                  effects.put(statUp.getKey(), statUp.getValue());
               }

               if (active) {
                  retrievedEffects.put(sourceId, new Pair<>(effect, startTime));
               }

               propagateBuffEffectUpdates(retrievedEffects, retrievedStats, new LinkedHashSet<>());
            }
         } else {
            for (Entry<MapleBuffStat, MapleBuffStatValueHolder> statUp : appliedStatUps.entrySet()) {
               addItemEffectHolderCount(statUp.getKey());
            }

            toDeploy = (active ? appliedStatUps : new LinkedHashMap<>());
         }

         addItemEffectHolder(sourceId, expirationTime, appliedStatUps);
         for (Entry<MapleBuffStat, MapleBuffStatValueHolder> statUp : toDeploy.entrySet()) {
            effects.put(statUp.getKey(), statUp.getValue());
         }
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }

      updateLocalStats();
   }

   private void scheduleAuraOfBeholder(int beholder, Skill skill, Integer skillLevel) {
      final MapleStatEffect healEffect = skill.getEffect(skillLevel);
      int healInterval = healEffect.getX() * 1000;
      scheduler.add(MapleCharacterScheduler.Type.BEHODLER_HEAL, () -> {
         if (awayFromWorld.get()) {
            return;
         }

         addHP(healEffect.getHp());
         client.announce(MaplePacketCreator.showOwnBuffEffect(beholder, 2));
         getMap().broadcastMessage(MapleCharacter.this, MaplePacketCreator.summonSkill(getId(), beholder, 5), true);
         getMap().broadcastMessage(MapleCharacter.this, MaplePacketCreator.showOwnBuffEffect(beholder, 2), false);
      }, healInterval, healInterval);
   }

   private void scheduleHexOfBeholder(int beholder, Skill skill, Integer skillLevel) {
      final MapleStatEffect buffEffect = skill.getEffect(skillLevel);
      int buffInterval = buffEffect.getX() * 1000;
      scheduler.add(MapleCharacterScheduler.Type.BEHOLDER_BUFF, () -> {
         if (awayFromWorld.get()) {
            return;
         }

         buffEffect.applyTo(MapleCharacter.this);
         client.announce(MaplePacketCreator.showOwnBuffEffect(beholder, 2));
         getMap().broadcastMessage(MapleCharacter.this, MaplePacketCreator.summonSkill(getId(), beholder, (int) (Math.random() * 3) + 6), true);
         getMap().broadcastMessage(MapleCharacter.this, MaplePacketCreator.showBuffeffect(getId(), beholder, 2), false);
      }, buffInterval, buffInterval);
   }

   public boolean unregisterChairBuff() {
      if (!ServerConstants.USE_CHAIR_EXTRAHEAL) {
         return false;
      }

      int skillId = ChairProcessor.getInstance().getJobMapChair(job);


      return applyIfHasSkill(skillId, (skill, skillLevel) -> {
         MapleStatEffect statEffect = skill.getEffect(skillLevel);
         return cancelEffect(statEffect, false, -1);
      });
   }

   private boolean registerChairBuff() {
      if (!ServerConstants.USE_CHAIR_EXTRAHEAL) {
         return false;
      }

      int skillId = ChairProcessor.getInstance().getJobMapChair(job);
      return applyIfHasSkill(skillId, (skill, skillLevel) -> {
         MapleStatEffect statEffect = skill.getEffect(skillLevel);
         statEffect.applyTo(this);
         return true;
      });
   }

   public int getChair() {
      return chair.get();
   }

   private void setChair(int chair) {
      this.chair.set(chair);
   }

   public String getChalkboard() {
      return this.chalktext;
   }

   public void setChalkboard(String text) {
      this.chalktext = text;
   }

   public MapleClient getClient() {
      return client;
   }

   public void setClient(MapleClient c) {
      this.client = c;
   }

   public AbstractPlayerInteraction getAbstractPlayerInteraction() {
      return client.getAbstractPlayerInteraction();
   }

   public final List<MapleQuestStatus> getCompletedQuests() {
      synchronized (quests) {
         List<MapleQuestStatus> ret = new LinkedList<>();
         for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus().equals(MapleQuestStatus.Status.COMPLETED)) {
               ret.add(q);
            }
         }

         return Collections.unmodifiableList(ret);
      }
   }

   public List<Ring> getCrushRings() {
      Collections.sort(crushRings);
      return crushRings;
   }

   public int getCurrentCI() {
      return ci;
   }

   public int getCurrentPage() {
      return currentPage;
   }

   public int getCurrentTab() {
      return currentTab;
   }

   public int getCurrentType() {
      return currentType;
   }

   public int getDojoEnergy() {
      return dojoEnergy;
   }

   public void setDojoEnergy(int x) {
      this.dojoEnergy = Math.min(x, 10000);
   }

   public boolean getDojoParty() {
      return mapid >= 925030100 && mapid < 925040000;
   }

   public int getDojoPoints() {
      return dojoPoints;
   }

   public void setDojoPoints(int x) {
      this.dojoPoints = x;
   }

   public int getDojoStage() {
      return dojoStage;
   }

   public void setDojoStage(int x) {
      this.dojoStage = x;
   }

   public Collection<MapleDoor> getDoors() {
      prtLock.lock();
      try {
         return (party != null ? Collections.unmodifiableCollection(party.getDoors().values()) : (pdoor != null ? Collections.singleton(pdoor) : new LinkedHashSet<>()));
      } finally {
         prtLock.unlock();
      }
   }

   public MapleDoor getPlayerDoor() {
      prtLock.lock();
      try {
         return pdoor;
      } finally {
         prtLock.unlock();
      }
   }

   public MapleDoor getMainTownDoor() {
      for (MapleDoor door : getDoors()) {
         if (door.getTownPortal().getId() == 0x80) {
            return door;
         }
      }

      return null;
   }

   public void applyPartyDoor(MapleDoor door, boolean partyUpdate) {
      MapleParty chrParty;
      prtLock.lock();
      try {
         if (!partyUpdate) {
            pdoor = door;
         }

         chrParty = getParty();
         if (chrParty != null) {
            chrParty.addDoor(id, door);
         }
      } finally {
         prtLock.unlock();
      }

      silentPartyUpdateInternal(chrParty);
   }

   public MapleDoor removePartyDoor(boolean partyUpdate) {
      MapleDoor ret = null;
      MapleParty chrParty;

      prtLock.lock();
      try {
         chrParty = getParty();
         if (chrParty != null) {
            chrParty.removeDoor(id);
         }

         if (!partyUpdate) {
            ret = pdoor;
            pdoor = null;
         }
      } finally {
         prtLock.unlock();
      }

      silentPartyUpdateInternal(chrParty);
      return ret;
   }

   public void removePartyDoor(MapleParty formerParty) {    // player is no longer registered at this party
      formerParty.removeDoor(id);
   }

   public int getEnergyBar() {
      return energybar;
   }

   public EventInstanceManager getEventInstance() {
      evtLock.lock();
      try {
         return eventInstance;
      } finally {
         evtLock.unlock();
      }
   }

   public void setEventInstance(EventInstanceManager eventInstance) {
      evtLock.lock();
      try {
         this.eventInstance = eventInstance;
      } finally {
         evtLock.unlock();
      }
   }

   public MapleMarriage getMarriageInstance() {
      EventInstanceManager eim = getEventInstance();

      if (eim != null || !(eim instanceof MapleMarriage)) {
         return (MapleMarriage) eim;
      } else {
         return null;
      }
   }

   public void resetExcluded(int petId) {
      chrLock.lock();
      try {
         Set<Integer> petExclude = excluded.get(petId);

         if (petExclude != null) {
            petExclude.clear();
         } else {
            excluded.put(petId, new LinkedHashSet<>());
         }
      } finally {
         chrLock.unlock();
      }
   }

   public void addExcluded(int petId, int x) {
      chrLock.lock();
      try {
         excluded.get(petId).add(x);
      } finally {
         chrLock.unlock();
      }
   }

   public void commitExcludedItems() {
      Map<Integer, Set<Integer>> petExcluded = this.getExcluded();

      chrLock.lock();
      try {
         excludedItems.clear();
      } finally {
         chrLock.unlock();
      }

      for (Map.Entry<Integer, Set<Integer>> pe : petExcluded.entrySet()) {
         byte petIndex = this.getPetIndex(pe.getKey());
         if (petIndex < 0) {
            continue;
         }

         Set<Integer> exclItems = pe.getValue();
         if (!exclItems.isEmpty()) {
            client.announce(MaplePacketCreator.loadExceptionList(this.getId(), pe.getKey(), petIndex, new ArrayList<>(exclItems)));

            chrLock.lock();
            try {
               excludedItems.addAll(exclItems);
            } finally {
               chrLock.unlock();
            }
         }
      }
   }

   public void exportExcludedItems(MapleClient c) {
      Map<Integer, Set<Integer>> petExcluded = this.getExcluded();
      for (Map.Entry<Integer, Set<Integer>> pe : petExcluded.entrySet()) {
         byte petIndex = this.getPetIndex(pe.getKey());
         if (petIndex < 0) {
            continue;
         }

         Set<Integer> exclItems = pe.getValue();
         if (!exclItems.isEmpty()) {
            c.announce(MaplePacketCreator.loadExceptionList(this.getId(), pe.getKey(), petIndex, new ArrayList<>(exclItems)));
         }
      }
   }

   private Map<Integer, Set<Integer>> getExcluded() {
      chrLock.lock();
      try {
         return Collections.unmodifiableMap(excluded);
      } finally {
         chrLock.unlock();
      }
   }

   public Set<Integer> getExcludedItems() {
      chrLock.lock();
      try {
         return Collections.unmodifiableSet(excludedItems);
      } finally {
         chrLock.unlock();
      }
   }

   public int getExp() {
      return exp.get();
   }

   public void setExp(int amount) {
      this.exp.set(amount);
   }

   public int getGachaExp() {
      return gachaexp.get();
   }

   public void setGachaExp(int amount) {
      this.gachaexp.set(amount);
   }

   public boolean hasNoviceExpRate() {
      return ServerConstants.USE_ENFORCE_NOVICE_EXPRATE && isBeginnerJob() && level < 11;
   }

   public int getExpRate() {
      if (hasNoviceExpRate()) {   // base exp rate 1x for early levels idea thanks to Vcoc
         return 1;
      }

      return expRate;
   }

   public int getCouponExpRate() {
      return expCoupon;
   }

   public int getRawExpRate() {
      return expRate / (expCoupon * getWorldServer().getExpRate());
   }

   public int getDropRate() {
      return dropRate;
   }

   public int getCouponDropRate() {
      return dropCoupon;
   }

   public int getRawDropRate() {
      return dropRate / (dropCoupon * getWorldServer().getDropRate());
   }

   public int getBossDropRate() {
      World w = getWorldServer();
      return (dropRate / w.getDropRate()) * w.getBossDropRate();
   }

   public int getMesoRate() {
      return mesoRate;
   }

   public int getCouponMesoRate() {
      return mesoCoupon;
   }

   public int getRawMesoRate() {
      return mesoRate / (mesoCoupon * getWorldServer().getMesoRate());
   }

   public int getQuestExpRate() {
      if (hasNoviceExpRate()) {
         return 1;
      }

      World w = getWorldServer();
      return w.getExpRate() * w.getQuestRate();
   }

   public int getQuestMesoRate() {
      World w = getWorldServer();
      return w.getMesoRate() * w.getQuestRate();
   }

   public float getCardRate(int itemId) {
      float rate = 100.0f;

      if (itemId == 0) {
         MapleStatEffect mseMeso = getBuffEffect(MapleBuffStat.MESO_UP_BY_ITEM);
         if (mseMeso != null) {
            rate += mseMeso.getCardRate(mapid, itemId);
         }
      } else {
         MapleStatEffect mseItem = getBuffEffect(MapleBuffStat.ITEM_UP_BY_ITEM);
         if (mseItem != null) {
            rate += mseItem.getCardRate(mapid, itemId);
         }
      }

      return rate / 100;
   }

   public int getFace() {
      return face;
   }

   public void setFace(int face) {
      this.face = face;
   }

   public int getFame() {
      return fame;
   }

   public void setFame(int fame) {
      this.fame = fame;
   }

   public MapleFamily getFamily() {
      if (familyEntry != null) {
         return familyEntry.getFamily();
      } else {
         return null;
      }
   }

   public MapleFamilyEntry getFamilyEntry() {
      return familyEntry;
   }

   public void setFamilyEntry(MapleFamilyEntry entry) {
      if (entry != null) {
         setFamilyId(entry.getFamily().getID());
      }
      this.familyEntry = entry;
   }

   public int getFamilyId() {
      return familyId;
   }

   public void setFamilyId(int familyId) {
      this.familyId = familyId;
   }

   public boolean getFinishedDojoTutorial() {
      return finishedDojoTutorial;
   }

   public void setUsedStorage() {
      usedStorage = true;
   }

   public List<Ring> getFriendshipRings() {
      Collections.sort(friendshipRings);
      return friendshipRings;
   }

   public int getGender() {
      return gender;
   }

   public void setGender(int gender) {
      this.gender = gender;
   }

   public boolean isMale() {
      return getGender() == 0;
   }

   public Optional<MapleGuild> getGuild() {
      try {
         return Server.getInstance().getGuild(getGuildId(), getWorld(), this);
      } catch (Exception ex) {
         ex.printStackTrace();
         return Optional.empty();
      }
   }

   public Optional<MapleAlliance> getAlliance() {
      if (mgc != null) {
         return getGuild().flatMap(guild -> Server.getInstance().getAlliance(guild.getAllianceId()));
      }
      return Optional.empty();
   }

   public int getGuildId() {
      return guildid;
   }

   public void setGuildId(int _id) {
      guildid = _id;
   }

   public int getGuildRank() {
      return guildRank;
   }

   public void setGuildRank(int _rank) {
      guildRank = _rank;
   }

   public int getHair() {
      return hair;
   }

   public void setHair(int hair) {
      this.hair = hair;
   }

   public MapleHiredMerchant getHiredMerchant() {
      return hiredMerchant;
   }

   public void setHiredMerchant(MapleHiredMerchant merchant) {
      this.hiredMerchant = merchant;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public int getInitialSpawnPoint() {
      return initialSpawnPoint;
   }

   public MapleInventory getInventory(MapleInventoryType type) {
      return inventory[type.ordinal()];
   }

   public int getItemEffect() {
      return itemEffect;
   }

   public void setItemEffect(int itemEffect) {
      this.itemEffect = itemEffect;
   }

   public boolean haveItemWithId(int itemId, boolean checkEquipped) {
      return (inventory[ItemConstants.getInventoryType(itemId).ordinal()].findById(itemId) != null)
            || (checkEquipped && inventory[MapleInventoryType.EQUIPPED.ordinal()].findById(itemId) != null);
   }

   public boolean haveItemEquipped(int itemId) {
      return (inventory[MapleInventoryType.EQUIPPED.ordinal()].findById(itemId) != null);
   }

   public boolean haveWeddingRing() {
      int[] rings = {1112806, 1112803, 1112807, 1112809};

      for (int ringId : rings) {
         if (haveItemWithId(ringId, true)) {
            return true;
         }
      }

      return false;
   }

   public int getItemQuantity(int itemId, boolean checkEquipped) {
      int count = inventory[ItemConstants.getInventoryType(itemId).ordinal()].countById(itemId);
      if (checkEquipped) {
         count += inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemId);
      }
      return count;
   }

   private int getCleanItemQuantity(int itemId, boolean checkEquipped) {
      int count = inventory[ItemConstants.getInventoryType(itemId).ordinal()].countNotOwnedById(itemId);
      if (checkEquipped) {
         count += inventory[MapleInventoryType.EQUIPPED.ordinal()].countNotOwnedById(itemId);
      }
      return count;
   }

   public MapleJob getJob() {
      return job;
   }

   public void setJob(MapleJob job) {
      this.job = job;
   }

   public int getJobRank() {
      return jobRank;
   }

   public int getJobRankMove() {
      return jobRankMove;
   }

   public int getJobType() {
      return job.getId() / 1000;
   }

   public Map<Integer, KeyBinding> getKeymap() {
      return keymap;
   }

   public long getLastUsedCashItem() {
      return lastUsedCashItem;
   }

   public void setLastUsedCashItem(long time) {
      this.lastUsedCashItem = time;
   }

   public int getLevel() {
      return level;
   }

   public void setLevel(int level) {
      this.level = level;
   }

   public int getFh() {
      Point pos = this.getPosition();
      pos.y -= 6;

      if (map.getFootholds().findBelow(pos) == null) {
         return 0;
      } else {
         return map.getFootholds().findBelow(pos).firstY();
      }
   }

   public int getMapId() {
      if (map != null) {
         return map.getId();
      }
      return mapid;
   }

   public void setMapId(int mapId) {
      this.mapid = mapId;
   }

   public Ring getMarriageRing() {
      return partnerId > 0 ? marriageRing : null;
   }

   public int getMasterLevel(Skill skill) {
      if (skills.get(skill) == null) {
         return 0;
      }
      return skills.get(skill).masterLevel();
   }

   public int getTotalStr() {
      return localstr;
   }

   public int getTotalDex() {
      return localdex;
   }

   public int getTotalInt() {
      return localint_;
   }

   public int getTotalLuk() {
      return localluk;
   }

   public int getTotalMagic() {
      return localmagic;
   }

   public int getTotalWatk() {
      return localwatk;
   }

   public int getMaxClassLevel() {
      return isCygnus() ? 120 : 200;
   }

   private int getMaxLevel() {
      if (!ServerConstants.USE_ENFORCE_JOB_LEVEL_RANGE || isGmJob()) {
         return getMaxClassLevel();
      }

      return GameConstants.getJobMaxLevel(job);
   }

   public int getMeso() {
      return meso.get();
   }

   //---- \/ \/ \/ \/ \/ \/ \/  NOT TESTED  \/ \/ \/ \/ \/ \/ \/ \/ \/ ----

   public int getMerchantMeso() {
      return merchantmeso;
   }

   public void setMerchantMeso(int set) {
      DatabaseConnection.getInstance().withConnection(connection -> CharacterAdministrator.getInstance().setMerchantMesos(connection, id, set));
      merchantmeso = set;
   }

   public int getMerchantNetMeso() {
      int elapsedDays = DatabaseConnection.getInstance().withConnectionResult(connection -> FredStorageProvider.getInstance().get(connection, id)).orElseThrow();

      if (elapsedDays > 100) {
         elapsedDays = 100;
      }

      long netMeso = merchantmeso; // negative mesos issues found thanks to Flash, Vcoc
      netMeso = (netMeso * (100 - elapsedDays)) / 100;
      return (int) netMeso;
   }

   public int getMesosTraded() {
      return mesosTraded;
   }

   //---- /\ /\ /\ /\ /\ /\ /\  NOT TESTED  /\ /\ /\ /\ /\ /\ /\ /\ /\ ----

   public int getMessengerPosition() {
      return messengerposition;
   }

   public void setMessengerPosition(int position) {
      this.messengerposition = position;
   }

   public MapleGuildCharacter getMGC() {
      return mgc;
   }

   public void setMGC(MapleGuildCharacter mgc) {
      this.mgc = mgc;
   }

   public MaplePartyCharacter getMPC() {
      if (mpc == null) {
         mpc = new MaplePartyCharacter(this);
      }
      return mpc;
   }

   public void setMPC(MaplePartyCharacter mpc) {
      this.mpc = mpc;
   }

   public int getTargetHpBarHash() {
      return this.targetHpBarHash;
   }

   public void setTargetHpBarHash(int mobHash) {
      this.targetHpBarHash = mobHash;
   }

   public long getTargetHpBarTime() {
      return this.targetHpBarTime;
   }

   public void setTargetHpBarTime(long timeNow) {
      this.targetHpBarTime = timeNow;
   }

   public void setPlayerAggro(int mobHash) {
      setTargetHpBarHash(mobHash);
      setTargetHpBarTime(System.currentTimeMillis());
   }

   public void resetPlayerAggro() {
      if (getWorldServer().unregisterDisabledServerMessage(id)) {
         client.announceServerMessage();
      }

      setTargetHpBarHash(0);
      setTargetHpBarTime(0);
   }

   public MapleMiniGame getMiniGame() {
      return miniGame;
   }

   public void setMiniGame(MapleMiniGame miniGame) {
      this.miniGame = miniGame;
   }

   public int getMiniGamePoints(MiniGameResult type, boolean isOmok) {
      if (isOmok) {
         switch (type) {
            case WIN:
               return omok.wins();
            case LOSS:
               return omok.losses();
            default:
               return omok.ties();
         }
      } else {
         switch (type) {
            case WIN:
               return matchCard.wins();
            case LOSS:
               return matchCard.losses();
            default:
               return matchCard.ties();
         }
      }
   }

   public MonsterBook getMonsterBook() {
      return monsterbook;
   }

   public int getMonsterBookCover() {
      return bookCover;
   }

   public void setMonsterBookCover(int bookCover) {
      this.bookCover = bookCover;
   }

   public MapleMount getMount() {
      return maplemount;
   }

   public Optional<MapleMessenger> getMessenger() {
      return Optional.ofNullable(messenger);
   }

   public void setMessenger(MapleMessenger messenger) {
      this.messenger = messenger;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getNoPets() {
      petLock.lock();
      try {
         int ret = 0;
         for (int i = 0; i < 3; i++) {
            if (pets[i] != null) {
               ret++;
            }
         }
         return ret;
      } finally {
         petLock.unlock();
      }
   }

   public MapleParty getParty() {
      prtLock.lock();
      try {
         return party;
      } finally {
         prtLock.unlock();
      }
   }

   public void setParty(MapleParty p) {
      prtLock.lock();
      try {
         if (p == null) {
            this.mpc = null;
            doorSlot = -1;

            party = null;
         } else {
            party = p;
         }
      } finally {
         prtLock.unlock();
      }
   }

   public int getPartyId() {
      prtLock.lock();
      try {
         return (party != null ? party.getId() : -1);
      } finally {
         prtLock.unlock();
      }
   }

   public List<MapleCharacter> getPartyMembersOnline() {
      List<MapleCharacter> list = new LinkedList<>();

      prtLock.lock();
      try {
         if (party != null) {
            for (MaplePartyCharacter mpc : party.getMembers()) {
               MapleCharacter mc = mpc.getPlayer();
               if (mc != null) {
                  list.add(mc);
               }
            }
         }
      } finally {
         prtLock.unlock();
      }

      return list;
   }

   public List<MapleCharacter> getPartyMembersOnSameMap() {
      List<MapleCharacter> list = new LinkedList<>();
      int thisMapHash = this.getMap().hashCode();

      prtLock.lock();
      try {
         if (party != null) {
            for (MaplePartyCharacter mpc : party.getMembers()) {
               MapleCharacter chr = mpc.getPlayer();
               if (chr != null) {
                  MapleMap chrMap = chr.getMap();
                  if (chrMap != null && chrMap.hashCode() == thisMapHash && chr.isLoggedinWorld()) {
                     list.add(chr);
                  }
               }
            }
         }
      } finally {
         prtLock.unlock();
      }

      return list;
   }

   public boolean isPartyMember(MapleCharacter chr) {
      return isPartyMember(chr.getId());
   }

   public boolean isPartyMember(int cid) {
      prtLock.lock();
      try {
         if (party != null) {
            return party.getMemberById(cid) != null;
         }
      } finally {
         prtLock.unlock();
      }

      return false;
   }

   public MaplePlayerShop getPlayerShop() {
      return playerShop;
   }

   public void setPlayerShop(MaplePlayerShop playerShop) {
      this.playerShop = playerShop;
   }

   public MapleRockPaperScissor getRPS() { // thanks inhyuk for suggesting RPS addition
      return rps;
   }

   public void setRPS(MapleRockPaperScissor rps) {
      this.rps = rps;
   }

   public void setGMLevel(int level) {
      this.gmLevel = Math.min(level, 6);
      this.gmLevel = Math.max(level, 0);
   }

   public void closePartySearchInteractions() {
      this.getWorldServer().getPartySearchCoordinator().unregisterPartyLeader(this);
      if (canRecvPartySearchInvite) {
         this.getWorldServer().getPartySearchCoordinator().detachPlayer(this);
      }
   }

   public void closePlayerInteractions() {
      closeNpcShop();
      closeTrade();
      closePlayerShop();
      closeMiniGame(true);
      closeRPS();
      closeHiredMerchant(false);
      closePlayerMessenger();

      client.closePlayerScriptInteractions();
      resetPlayerAggro();
   }

   private void closeNpcShop() {
      setShop(null);
   }

   private void closeTrade() {
      MapleTrade.cancelTrade(this, MapleTrade.TradeResult.PARTNER_CANCEL);
   }

   public void closePlayerShop() {
      MaplePlayerShop mps = this.getPlayerShop();
      if (mps == null) {
         return;
      }

      if (mps.isOwner(this)) {
         mps.setOpen(false);
         getWorldServer().unregisterPlayerShop(mps);

         for (MaplePlayerShopItem shopItem : mps.getItems()) {
            if (shopItem.bundles() >= 2) {
               Item iItem = shopItem.item().copy();
               iItem.quantity_$eq((short) (shopItem.bundles() * iItem.quantity()));
               MapleInventoryManipulator.addFromDrop(this.getClient(), iItem, false);
            } else if (shopItem.doesExist()) {
               MapleInventoryManipulator.addFromDrop(this.getClient(), shopItem.item(), true);
            }
         }
         mps.closeShop();
      } else {
         mps.removeVisitor(this);
      }
      this.setPlayerShop(null);
   }

   public void closeMiniGame(boolean forceClose) {
      MapleMiniGame game = this.getMiniGame();
      if (game == null) {
         return;
      }

      if (game.isOwner(this)) {
         game.closeRoom(forceClose);
      } else {
         game.removeVisitor(forceClose, this);
      }
   }

   public void closeHiredMerchant(boolean closeMerchant) {
      MapleHiredMerchant merchant = this.getHiredMerchant();
      if (merchant == null) {
         return;
      }

      if (closeMerchant) {
         if (merchant.isOwner(this) && merchant.getItems().isEmpty()) {
            merchant.forceClose();
         } else {
            merchant.removeVisitor(this);
            this.setHiredMerchant(null);
         }
      } else {
         if (merchant.isOwner(this)) {
            merchant.setOpen(true);
         } else {
            merchant.removeVisitor(this);
         }
         merchant.saveItems(false);
      }
   }

   public void closePlayerMessenger() {
      getMessenger().ifPresent(messenger -> {
         World w = getWorldServer();
         MapleMessengerCharacter messengerCharacter = new MapleMessengerCharacter(this, this.getMessengerPosition());

         w.leaveMessenger(messenger.getId(), messengerCharacter);
         this.setMessenger(null);
         this.setMessengerPosition(4);
      });
   }

   public MaplePet[] getPets() {
      petLock.lock();
      try {
         return Arrays.copyOf(pets, pets.length);
      } finally {
         petLock.unlock();
      }
   }

   public MaplePet getPet(int index) {
      if (index < 0) {
         return null;
      }

      petLock.lock();
      try {
         return pets[index];
      } finally {
         petLock.unlock();
      }
   }

   public byte getPetIndex(int petId) {
      petLock.lock();
      try {
         for (byte i = 0; i < 3; i++) {
            if (pets[i] != null) {
               if (pets[i].uniqueId() == petId) {
                  return i;
               }
            }
         }
         return -1;
      } finally {
         petLock.unlock();
      }
   }

   public byte getPetIndex(MaplePet pet) {
      petLock.lock();
      try {
         for (byte i = 0; i < 3; i++) {
            if (pets[i] != null) {
               if (pets[i].uniqueId() == pet.uniqueId()) {
                  return i;
               }
            }
         }
         return -1;
      } finally {
         petLock.unlock();
      }
   }

   public int getPossibleReports() {
      return possibleReports;
   }

   public final byte getQuestStatus(final int quest) {
      synchronized (quests) {
         MapleQuestStatus mqs = quests.get((short) quest);
         if (mqs != null) {
            return (byte) mqs.getStatus().getId();
         } else {
            return 0;
         }
      }
   }

   private MapleQuestStatus getMapleQuestStatus(final int quest) {
      synchronized (quests) {
         return quests.get((short) quest);
      }
   }

   public MapleQuestStatus getQuest(MapleQuest quest) {
      synchronized (quests) {
         if (!quests.containsKey(quest.getId())) {
            return new MapleQuestStatus(quest, MapleQuestStatus.Status.NOT_STARTED);
         }
         return quests.get(quest.getId());
      }
   }

   public final MapleQuestStatus getQuestNAdd(final MapleQuest quest) {
      synchronized (quests) {
         if (!quests.containsKey(quest.getId())) {
            final MapleQuestStatus status = new MapleQuestStatus(quest, MapleQuestStatus.Status.NOT_STARTED);
            quests.put(quest.getId(), status);
            return status;
         }
         return quests.get(quest.getId());
      }
   }

   public final MapleQuestStatus getQuestNoAdd(final MapleQuest quest) {
      synchronized (quests) {
         return quests.get(quest.getId());
      }
   }

   public boolean needQuestItem(int questid, int itemid) {
      if (questid <= 0) { //For non quest items :3
         return true;
      }

      int amountNeeded, questStatus = this.getQuestStatus(questid);
      if (questStatus == 0) {
         amountNeeded = MapleQuest.getInstance(questid).getStartItemAmountNeeded(itemid);
      } else if (questStatus != 1) {
         return false;
      } else {
         amountNeeded = MapleQuest.getInstance(questid).getCompleteItemAmountNeeded(itemid);
      }

      return amountNeeded > 0 && getInventory(ItemConstants.getInventoryType(itemid)).countById(itemid) < amountNeeded;
   }

   public int getRank() {
      return rank;
   }

   public int getRankMove() {
      return rankMove;
   }

   public void clearSavedLocation(SavedLocationType type) {
      savedLocations[type.ordinal()] = null;
   }

   public int peekSavedLocation(String type) {
      SavedLocation sl = savedLocations[SavedLocationType.fromString(type).ordinal()];
      if (sl == null) {
         return -1;
      }
      return sl.mapId();
   }

   public int getSavedLocation(String type) {
      int m = peekSavedLocation(type);
      clearSavedLocation(SavedLocationType.fromString(type));

      return m;
   }

   public String getSearch() {
      return search;
   }

   public void setSearch(String find) {
      search = find;
   }

   public MapleShop getShop() {
      return shop;
   }

   public void setShop(MapleShop shop) {
      this.shop = shop;
   }

   public Map<Skill, SkillEntry> getSkills() {
      return Collections.unmodifiableMap(skills);
   }

   public int getSkillLevel(int skillId) {
      Optional<Skill> skill = SkillFactory.getSkill(skillId);
      if (skill.isEmpty()) {
         return 0;
      }

      SkillEntry ret = skills.get(skill.get());
      if (ret == null) {
         return 0;
      }
      return ret.skillLevel();
   }

   public byte getSkillLevel(Skill skill) {
      if (skills.get(skill) == null) {
         return 0;
      }
      return skills.get(skill).skillLevel();
   }

   public long getSkillExpiration(int skillId) {
      Optional<Skill> skill = SkillFactory.getSkill(skillId);
      if (skill.isEmpty()) {
         return -1;
      }

      SkillEntry ret = skills.get(skill.get());
      if (ret == null) {
         return -1;
      }
      return ret.expiration();
   }

   public long getSkillExpiration(Skill skill) {
      if (skills.get(skill) == null) {
         return -1;
      }
      return skills.get(skill).expiration();
   }

   public MapleSkinColor getSkinColor() {
      return skinColor;
   }

   public void setSkinColor(MapleSkinColor skinColor) {
      this.skinColor = skinColor;
   }

   public void setSlot(int slotid) {
      slots = slotid;
   }

   public final List<MapleQuestStatus> getStartedQuests() {
      synchronized (quests) {
         List<MapleQuestStatus> ret = new LinkedList<>();
         for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus().equals(MapleQuestStatus.Status.STARTED)) {
               ret.add(q);
            }
         }
         return Collections.unmodifiableList(ret);
      }
   }

   public final int getStartedQuestsSize() {
      synchronized (quests) {
         int i = 0;
         for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus().equals(MapleQuestStatus.Status.STARTED)) {
               if (q.getQuest().getInfoNumber() > 0) {
                  i++;
               }
               i++;
            }
         }
         return i;
      }
   }

   public MapleStatEffect getStatForBuff(MapleBuffStat effect) {
      effLock.lock();
      chrLock.lock();
      try {
         MapleBuffStatValueHolder mbsvh = effects.get(effect);
         if (mbsvh == null) {
            return null;
         }
         return mbsvh.effect;
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   public MapleStorage getStorage() {
      return storage;
   }

   public Collection<MapleSummon> getSummonsValues() {
      return summons.values();
   }

   public void clearSummons() {
      summons.clear();
   }

   public MapleSummon getSummonByKey(int id) {
      return summons.get(id);
   }

   public boolean isSummonsEmpty() {
      return summons.isEmpty();
   }

   public boolean containsSummon(MapleSummon summon) {
      return summons.containsValue(summon);
   }

   public MapleTrade getTrade() {
      return trade;
   }

   public void setTrade(MapleTrade trade) {
      this.trade = trade;
   }

   public int getVanquisherKills() {
      return vanquisherKills;
   }

   public void setVanquisherKills(int x) {
      this.vanquisherKills = x;
   }

   public int getVanquisherStage() {
      return vanquisherStage;
   }

   public void setVanquisherStage(int x) {
      this.vanquisherStage = x;
   }

   public MapleMapObject[] getVisibleMapObjects() {
      return visibleMapObjects.toArray(new MapleMapObject[visibleMapObjects.size()]);
   }

   public int getWorld() {
      return world;
   }

   public void setWorld(int world) {
      this.world = world;
   }

   public World getWorldServer() {
      return Server.getInstance().getWorld(world);
   }

   public void giveCoolDowns(final int skillid, long starttime, long length) {
      if (skillid == 5221999) {
         this.battleShipHp = (int) length;
         addCooldown(skillid, 0, length);
      } else {
         long timeNow = Server.getInstance().getCurrentTime();
         int time = (int) ((length + starttime) - timeNow);
         addCooldown(skillid, timeNow, time);
      }
   }

   public int gmLevel() {
      return gmLevel;
   }

   private void guildUpdate() {
      mgc.setLevel(level);
      mgc.setJobId(job.getId());

      if (this.guildid < 1) {
         return;
      }

      try {
         Server.getInstance().memberLevelJobUpdate(this.mgc);
         //Server.getInstance().getGuild(guildid, world, mgc).gainGP(40);
         getGuild()
               .map(MapleGuild::getAllianceId)
               .ifPresent(id -> Server.getInstance().allianceMessage(id, MaplePacketCreator.updateAllianceJobLevel(this), getId(), -1));
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void handleEnergyChargeGain() { // to get here energychargelevel has to be > 0
      Optional<Skill> energyCharge;
      if (isCygnus()) {
         energyCharge = SkillFactory.getSkill(ThunderBreaker.ENERGY_CHARGE);
      } else {
         energyCharge = SkillFactory.getSkill(Marauder.ENERGY_CHARGE);
      }

      energyCharge.ifPresent(skill -> {
         MapleStatEffect statEffect = skill.getEffect(getSkillLevel(skill));
         TimerManager tMan = TimerManager.getInstance();
         if (energybar < 10000) {
            energybar += 102;
            if (energybar > 10000) {
               energybar = 10000;
            }
            List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.ENERGY_CHARGE, energybar));
            setBuffedValue(MapleBuffStat.ENERGY_CHARGE, energybar);
            client.announce(MaplePacketCreator.giveBuff(energybar, 0, stat));
            client.announce(MaplePacketCreator.showOwnBuffEffect(skill.getId(), 2));
            getMap().broadcastMessage(this, MaplePacketCreator.showBuffeffect(id, skill.getId(), 2));
            getMap().broadcastMessage(this, MaplePacketCreator.giveForeignBuff(energybar, stat));
         }
         if (energybar >= 10000 && energybar < 11000) {
            energybar = 15000;
            final MapleCharacter chr = this;
            tMan.schedule(() -> {
               energybar = 0;
               List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.ENERGY_CHARGE, energybar));
               setBuffedValue(MapleBuffStat.ENERGY_CHARGE, energybar);
               client.announce(MaplePacketCreator.giveBuff(energybar, 0, stat));
               getMap().broadcastMessage(chr, MaplePacketCreator.giveForeignBuff(energybar, stat));
            }, statEffect.getDuration());
         }
      });

   }

   public void handleOrbconsume() {
      int skillid = isCygnus() ? DawnWarrior.COMBO : Crusader.COMBO;
      SkillFactory.getSkill(skillid).ifPresent(combo -> {
         List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.COMBO, 1));
         setBuffedValue(MapleBuffStat.COMBO, 1);
         client.announce(MaplePacketCreator.giveBuff(skillid, combo.getEffect(getSkillLevel(combo)).getDuration() + (int) ((getBuffedStarttime(MapleBuffStat.COMBO) - System.currentTimeMillis())), stat));
         getMap().broadcastMessage(this, MaplePacketCreator.giveForeignBuff(getId(), stat), false);
      });
   }

   public boolean hasEntered(String script) {
      for (int mapId : entered.keySet()) {
         if (entered.get(mapId).equals(script)) {
            return true;
         }
      }
      return false;
   }

   public boolean hasEntered(String script, int mapId) {
      String e = entered.get(mapId);
      return script.equals(e);
   }

   public void hasGivenFame(MapleCharacter to) {
      lastfametime = System.currentTimeMillis();
      lastmonthfameids.add(to.getId());
      DatabaseConnection.getInstance().withConnection(connection -> FameLogAdministrator.getInstance().addForCharacter(connection, getId(), to.getId()));
   }

   public boolean hasMerchant() {
      return hasMerchant;
   }

   public boolean haveItem(int itemid) {
      return getItemQuantity(itemid, ItemConstants.isEquipment(itemid)) > 0;
   }

   public boolean hasEmptySlot(byte invType) {
      return getInventory(MapleInventoryType.getByType(invType)).getNextFreeSlot() > -1;
   }

   public void increaseGuildCapacity() {
      getGuild().ifPresent(guild -> {
         int cost = MapleGuildProcessor.getInstance().getIncreaseGuildCost(guild.getCapacity());

         if (getMeso() < cost) {
            MessageBroadcaster.getInstance().sendServerNotice(this, ServerNoticeType.POP_UP, "You don't have enough mesos.");
            return;
         }

         if (Server.getInstance().increaseGuildCapacity(guildid)) {
            gainMeso(-cost, true, false, true);
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(this, ServerNoticeType.POP_UP, "Your guild already reached the maximum capacity of players.");
         }
      });
   }

   private boolean canBuyback(int fee, boolean usingMesos) {
      return (usingMesos ? this.getMeso() : cashshop.getCash(1)) >= fee;
   }

   private void applyBuybackFee(int fee, boolean usingMesos) {
      if (usingMesos) {
         this.gainMeso(-fee);
      } else {
         cashshop.gainCash(1, -fee);
      }
   }

   private long getNextBuybackTime() {
      return lastBuyback + ServerConstants.BUYBACK_COOLDOWN_MINUTES * 60 * 1000;
   }

   private boolean isBuybackInvincible() {
      return Server.getInstance().getCurrentTime() - lastBuyback < 4200;
   }

   private int getBuybackFee() {
      float fee = ServerConstants.BUYBACK_FEE;
      int grade = Math.min(Math.max(level, 30), 120) - 30;

      fee += (grade * ServerConstants.BUYBACK_LEVEL_STACK_FEE);
      if (ServerConstants.USE_BUYBACK_WITH_MESOS) {
         fee *= ServerConstants.BUYBACK_MESO_MULTIPLIER;
      }

      return (int) Math.floor(fee);
   }

   public void showBuybackInfo() {
      String s = "#eBUYBACK STATUS#n\r\n\r\nCurrent buyback fee: #b" + getBuybackFee() + " " + (ServerConstants.USE_BUYBACK_WITH_MESOS ? "mesos" : "NX") + "#k\r\n\r\n";

      long timeNow = Server.getInstance().getCurrentTime();
      boolean avail = true;
      if (!isAlive()) {
         long timeLapsed = timeNow - lastDeathtime;
         long timeRemaining = ServerConstants.BUYBACK_RETURN_MINUTES * 60 * 1000 - (timeLapsed + Math.max(0, getNextBuybackTime() - timeNow));
         if (timeRemaining < 1) {
            s += "Buyback #e#rUNAVAILABLE#k#n";
            avail = false;
         } else {
            s += "Buyback countdown: #e#b" + MapleStringUtil.getTimeRemaining(ServerConstants.BUYBACK_RETURN_MINUTES * 60 * 1000 - timeLapsed) + "#k#n";
         }
         s += "\r\n";
      }

      if (timeNow < getNextBuybackTime() && avail) {
         s += "Buyback available in #r" + MapleStringUtil.getTimeRemaining(getNextBuybackTime() - timeNow) + "#k";
         s += "\r\n";
      }

      this.showHint(s);
   }

   public boolean couldBuyback() {  // Ronan's buyback system
      long timeNow = Server.getInstance().getCurrentTime();

      if (timeNow - lastDeathtime > ServerConstants.BUYBACK_RETURN_MINUTES * 60 * 1000) {
         MessageBroadcaster.getInstance().sendServerNotice(this, ServerNoticeType.PINK_TEXT, "The period of time to decide has expired, therefore you are unable to buyback.");
         return false;
      }

      long nextBuybacktime = getNextBuybackTime();
      if (timeNow < nextBuybacktime) {
         long timeLeft = nextBuybacktime - timeNow;
         MessageBroadcaster.getInstance().sendServerNotice(this, ServerNoticeType.PINK_TEXT, "Next buyback available in " + MapleStringUtil.getTimeRemaining(timeLeft) + ".");
         return false;
      }

      boolean usingMesos = ServerConstants.USE_BUYBACK_WITH_MESOS;
      int fee = getBuybackFee();

      if (!canBuyback(fee, usingMesos)) {
         MessageBroadcaster.getInstance().sendServerNotice(this, ServerNoticeType.PINK_TEXT, "You don't have " + fee + " " + (usingMesos ? "mesos" : "NX") + " to buyback.");
         return false;
      }

      lastBuyback = timeNow;
      applyBuybackFee(fee, usingMesos);
      return true;
   }

   public boolean isBuffFrom(MapleBuffStat stat, Skill skill) {
      effLock.lock();
      chrLock.lock();
      try {
         MapleBuffStatValueHolder mbsvh = effects.get(stat);
         if (mbsvh == null) {
            return false;
         }
         return mbsvh.effect.isSkill() && mbsvh.effect.getSourceId() == skill.getId();
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   public boolean isGmJob() {
      int jn = job.getJobNiche();
      return jn >= 8 && jn <= 9;
   }

   public boolean isCygnus() {
      return getJobType() == 1;
   }

   public boolean isAran() {
      return job.getId() >= 2000 && job.getId() <= 2112;
   }

   private boolean isBeginnerJob() {
      return (job.getId() == 0 || job.getId() == 1000 || job.getId() == 2000);
   }

   public boolean isGM() {
      return gmLevel > 1;
   }

   public void setGM(int level) {
      this.gmLevel = level;
   }

   public boolean isHidden() {
      return hidden;
   }

   public boolean isMapObjectVisible(MapleMapObject mo) {
      return visibleMapObjects.contains(mo);
   }

   public boolean isPartyLeader() {
      prtLock.lock();
      try {
         MapleParty party = getParty();
         return party != null && party.getLeaderId() == getId();
      } finally {
         prtLock.unlock();
      }
   }

   public boolean isGuildLeader() {    // true on guild master or jr. master
      return guildid > 0 && guildRank < 3;
   }

   public boolean attemptCatchFish(int baitLevel) {
      return ServerConstants.USE_FISHING_SYSTEM && GameConstants.isFishingArea(mapid) && this.getPosition().getY() > 0 && ItemConstants.isFishingChair(chair.get()) && this.getWorldServer().registerFisherPlayer(this, baitLevel);
   }

   public void leaveMap() {
      releaseControlledMonsters();
      visibleMapObjects.clear();
      setChair(-1);
      scheduler.cancel(MapleCharacterScheduler.Type.HP_DECREASE);

      AriantColiseum arena = this.getAriantColiseum();
      if (arena != null) {
         arena.leaveArena(this);
      }
   }

   private int getChangedJobSp(MapleJob newJob) {
      int curSp = getUsedSp(newJob) + getJobRemainingSp(newJob);
      int spGain = 0;
      int expectedSp = getJobLevelSp(level - 10, newJob, GameConstants.getJobBranch(newJob));
      if (curSp < expectedSp) {
         spGain += (expectedSp - curSp);
      }

      return getSpGain(spGain, curSp, newJob);
   }

   private int getUsedSp(MapleJob job) {
      int jobId = job.getId();
      int spUsed = 0;

      for (Entry<Skill, SkillEntry> s : this.getSkills().entrySet()) {
         Skill skill = s.getKey();
         if (GameConstants.isInJobTree(skill.getId(), jobId) && !skill.isBeginnerSkill()) {
            spUsed += s.getValue().skillLevel();
         }
      }

      return spUsed;
   }

   private int getJobLevelSp(int level, MapleJob job, int jobBranch) {
      if (MapleJobProcessor.getInstance().getJobStyleInternal(job.getId(), (byte) 0x40) == MapleJob.MAGICIAN) {
         level += 2;  // starts earlier, level 8
      }

      return 3 * level + GameConstants.getChangeJobSpUpgrade(jobBranch);
   }

   private int getJobMaxSp(MapleJob job) {
      int jobBranch = GameConstants.getJobBranch(job);
      int jobRange = GameConstants.getJobUpgradeLevelRange(jobBranch);
      return getJobLevelSp(jobRange, job, jobBranch);
   }

   private int getJobRemainingSp(MapleJob job) {
      int skillBook = GameConstants.getSkillBook(job.getId());

      int ret = 0;
      for (int i = 0; i <= skillBook; i++) {
         ret += this.getRemainingSp(i);
      }

      return ret;
   }

   private int getSpGain(int spGain, MapleJob job) {
      int curSp = getUsedSp(job) + getJobRemainingSp(job);
      return getSpGain(spGain, curSp, job);
   }

   private int getSpGain(int spGain, int curSp, MapleJob job) {
      int maxSp = getJobMaxSp(job);

      spGain = Math.min(spGain, maxSp - curSp);
      int jobBranch = GameConstants.getJobBranch(job);
      return spGain;
   }

   private void levelUpGainSp() {
      if (GameConstants.getJobBranch(job) == 0) {
         return;
      }

      int spGain = 3;
      if (ServerConstants.USE_ENFORCE_JOB_SP_RANGE && !GameConstants.hasSPTable(job)) {
         spGain = getSpGain(spGain, job);
      }

      if (spGain > 0) {
         gainSp(spGain, GameConstants.getSkillBook(job.getId()), true);
      }
   }

   public synchronized void levelUp(boolean takeexp) {
      boolean isBeginner = isBeginnerJob();
      if (ServerConstants.USE_AUTOASSIGN_STARTERS_AP && isBeginner && level < 11) {
         effLock.lock();
         statWlock.lock();
         try {
            gainAp(5, true);

            int str = 0, dex = 0;
            if (level < 6) {
               str += 5;
            } else {
               str += 4;
               dex += 1;
            }

            assignStrDexIntLuk(str, dex, 0, 0);
         } finally {
            statWlock.unlock();
            effLock.unlock();
         }
      } else {
         int remainingAp = 5;

         if (isCygnus()) {
            if (level > 10) {
               if (level <= 17) {
                  remainingAp += 2;
               } else if (level < 77) {
                  remainingAp++;
               }
            }
         }

         gainAp(remainingAp, true);
      }

      levelUpHealthAndManaPoints(isBeginner);

      if (takeexp) {
         exp.addAndGet(-ExpTable.getExpNeededForLevel(level));
         if (exp.get() < 0) {
            exp.set(0);
         }
      }

      level++;
      if (level >= getMaxClassLevel()) {
         exp.set(0);

         int maxClassLevel = getMaxClassLevel();
         if (level == maxClassLevel) {
            if (!this.isGM()) {
               if (ServerConstants.PLAYERNPC_AUTODEPLOY) {
                  ThreadManager.getInstance().newTask(() -> MaplePlayerNPC.spawnPlayerNPC(GameConstants.getHallOfFameMapid(job), MapleCharacter.this));
               }

               final String names = (getMedalText() + name);
               MessageBroadcaster.getInstance().sendWorldServerNotice(world, ServerNoticeType.LIGHT_BLUE, String.format(LEVEL_200, names, maxClassLevel, names));
            }
         }

         level = maxClassLevel; //To prevent levels past the maximum
      }

      levelUpGainSp();

      effLock.lock();
      statWlock.lock();
      try {
         recalcLocalStats();
         changeHpMp(localmaxhp, localmaxmp, true);

         List<Pair<MapleStat, Integer>> statup = new ArrayList<>(10);
         statup.add(new Pair<>(MapleStat.AVAILABLEAP, remainingAp));
         statup.add(new Pair<>(MapleStat.AVAILABLESP, remainingSp[GameConstants.getSkillBook(job.getId())]));
         statup.add(new Pair<>(MapleStat.HP, hp));
         statup.add(new Pair<>(MapleStat.MP, mp));
         statup.add(new Pair<>(MapleStat.EXP, exp.get()));
         statup.add(new Pair<>(MapleStat.LEVEL, level));
         statup.add(new Pair<>(MapleStat.MAXHP, clientmaxhp));
         statup.add(new Pair<>(MapleStat.MAXMP, clientmaxmp));
         statup.add(new Pair<>(MapleStat.STR, str));
         statup.add(new Pair<>(MapleStat.DEX, dex));

         client.announce(MaplePacketCreator.updatePlayerStats(statup, true, this));
      } finally {
         statWlock.unlock();
         effLock.unlock();
      }

      getMap().broadcastMessage(this, MaplePacketCreator.showForeignEffect(getId(), 0), false);
      setMPC(new MaplePartyCharacter(this));
      silentPartyUpdate();

      if (this.guildid > 0) {
         getGuild().ifPresent(guild -> guild.broadcast(MaplePacketCreator.levelUpMessage(2, level, name), this.getId()));
      }

      if (level % 20 == 0) {
         if (ServerConstants.USE_ADD_SLOTS_BY_LEVEL) {
            if (!isGM()) {
               for (byte i = 1; i < 5; i++) {
                  gainSlots(i, 4, true);
               }

               this.yellowMessage("You reached level " + level + ". Congratulations! As a token of your success, your inventory has been expanded a little bit.");
            }
         }
         if (ServerConstants.USE_ADD_RATES_BY_LEVEL) { //For the rate upgrade
            revertLastPlayerRates();
            setPlayerRates();
            this.yellowMessage("You managed to get level " + level + "! Getting experience and items seems a little easier now, huh?");
         }
      }

      if (ServerConstants.USE_PERFECT_PITCH && level >= 30) {
         //milestones?
         if (MapleInventoryManipulator.checkSpace(client, 4310000, (short) 1, "")) {
            MapleInventoryManipulator.addById(client, 4310000, (short) 1, "", -1);
         }
      } else if (level == 10) {
         Runnable r = () -> {
            if (leaveParty()) {
               showHint("You have reached #blevel 10#k, therefore you must leave your #rstarter party#k.");
            }
         };

         ThreadManager.getInstance().newTask(r);
      }

      levelUpMessages();
      guildUpdate();

      MapleFamilyProcessor.getInstance().giveReputationToCharactersSenior(getFamilyEntry(), level, getName());
   }

   /**
    * Address HP and MP additions on level up.
    *
    * @param isBeginner true if the character is a beginner
    */
   private void levelUpHealthAndManaPoints(boolean isBeginner) {
      int improvingMaxHPSkillId = -1;
      int improvingMaxMPSkillId = -1;

      int addHp = 0, addMp = 0;
      if (isBeginner) {
         addHp += Randomizer.rand(12, 16);
         addMp += Randomizer.rand(10, 12);
      } else if (job.isA(MapleJob.WARRIOR) || job.isA(MapleJob.DAWNWARRIOR1)) {
         improvingMaxHPSkillId = isCygnus() ? DawnWarrior.MAX_HP_INCREASE : Warrior.IMPROVED_MAXHP;
         if (job.isA(MapleJob.CRUSADER)) {
            improvingMaxMPSkillId = 1210000;
         } else if (job.isA(MapleJob.DAWNWARRIOR2)) {
            improvingMaxMPSkillId = 11110000;
         }
         addHp += Randomizer.rand(24, 28);
         addMp += Randomizer.rand(4, 6);
      } else if (job.isA(MapleJob.MAGICIAN) || job.isA(MapleJob.BLAZEWIZARD1)) {
         improvingMaxMPSkillId = isCygnus() ? BlazeWizard.INCREASING_MAX_MP : Magician.IMPROVED_MAX_MP_INCREASE;
         addHp += Randomizer.rand(10, 14);
         addMp += Randomizer.rand(22, 24);
      } else if (job.isA(MapleJob.BOWMAN) || job.isA(MapleJob.THIEF) || (job.getId() > 1299 && job.getId() < 1500)) {
         addHp += Randomizer.rand(20, 24);
         addMp += Randomizer.rand(14, 16);
      } else if (job.isA(MapleJob.GM)) {
         addHp += 30000;
         addMp += 30000;
      } else if (job.isA(MapleJob.PIRATE) || job.isA(MapleJob.THUNDERBREAKER1)) {
         improvingMaxHPSkillId = isCygnus() ? ThunderBreaker.IMPROVE_MAX_HP : Brawler.IMPROVE_MAX_HP;
         addHp += Randomizer.rand(22, 28);
         addMp += Randomizer.rand(18, 23);
      } else if (job.isA(MapleJob.ARAN1)) {
         addHp += Randomizer.rand(44, 48);
         int aids = Randomizer.rand(4, 8);
         addMp += aids + Math.floor(aids * 0.1);
      }

      Optional<Skill> improvingMaxHP = SkillFactory.getSkill(improvingMaxHPSkillId);
      if (improvingMaxHP.isPresent()) {
         int improvingMaxHPLevel = getSkillLevel(improvingMaxHP.get());
         if (improvingMaxHPLevel > 0 && (job.isA(MapleJob.WARRIOR) || job.isA(MapleJob.PIRATE) || job.isA(MapleJob.DAWNWARRIOR1) || job.isA(MapleJob.THUNDERBREAKER1))) {
            addHp += improvingMaxHP.get().getEffect(improvingMaxHPLevel).getX();
         }
      }

      Optional<Skill> improvingMaxMP = SkillFactory.getSkill(improvingMaxMPSkillId);
      if (improvingMaxMP.isPresent()) {
         int improvingMaxMPLevel = getSkillLevel(improvingMaxMP.get());
         if (improvingMaxMPLevel > 0 && (job.isA(MapleJob.MAGICIAN) || job.isA(MapleJob.CRUSADER) || job.isA(MapleJob.BLAZEWIZARD1))) {
            addMp += improvingMaxMP.get().getEffect(improvingMaxMPLevel).getX();
         }
      }

      if (ServerConstants.USE_RANDOMIZE_HPMP_GAIN) {
         if (getJobStyle() == MapleJob.MAGICIAN) {
            addMp += localint_ / 20;
         } else {
            addMp += localint_ / 10;
         }
      }

      addMaxMPMaxHP(addHp, addMp, true);
   }

   public boolean leaveParty() {
      MapleParty party;
      boolean partyLeader;

      prtLock.lock();
      try {
         party = getParty();
         partyLeader = isPartyLeader();
      } finally {
         prtLock.unlock();
      }

      if (party != null) {
         if (partyLeader) {
            party.assignNewLeader(client);
         }
         MaplePartyProcessor.getInstance().leaveParty(party, client);

         return true;
      } else {
         return false;
      }
   }

   private void levelUpMessages() {
      if (level % 5 != 0) { //Performance FTW?
         return;
      }
      if (level == 5) {
         yellowMessage("Aww, you're level 5, how cute!");
      } else if (level == 10) {
         yellowMessage("Henesys Party Quest is now open to you! Head over to Henesys, find some friends, and try it out!");
      } else if (level == 15) {
         yellowMessage("Half-way to your 2nd job advancement, nice work!");
      } else if (level == 20) {
         yellowMessage("You can almost Kerning Party Quest!");
      } else if (level == 25) {
         yellowMessage("You seem to be improving, but you are still not ready to move on to the next step.");
      } else if (level == 30) {
         yellowMessage("You have finally reached level 30! Try job advancing, after that try the Mushroom Castle!");
      } else if (level == 35) {
         yellowMessage("Hey did you hear about this mall that opened in Kerning? Try visiting the Kerning Mall.");
      } else if (level == 40) {
         yellowMessage("Do @rates to see what all your rates are!");
      } else if (level == 45) {
         yellowMessage("I heard that a rock and roll artist died during the grand opening of the Kerning Mall. People are naming him the Spirit of Rock.");
      } else if (level == 50) {
         yellowMessage("You seem to be growing very fast, would you like to test your new found strength with the mighty Zakum?");
      } else if (level == 55) {
         yellowMessage("You can now try out the Ludibrium Maze Party Quest!");
      } else if (level == 60) {
         yellowMessage("Feels good to be near the end of 2nd job, doesn't it?");
      } else if (level == 65) {
         yellowMessage("You're only 5 more levels away from 3rd job, not bad!");
      } else if (level == 70) {
         yellowMessage("I see many people wearing a teddy bear helmet. I should ask someone where they got it from.");
      } else if (level == 75) {
         yellowMessage("You have reached level 3 quarters!");
      } else if (level == 80) {
         yellowMessage("You think you are powerful enough? Try facing horntail!");
      } else if (level == 85) {
         yellowMessage("Did you know? The majority of people who hit level 85 in HeavenMS don't live to be 85 years old?");
      } else if (level == 90) {
         yellowMessage("Hey do you like the amusement park? I heard Spooky World is the best theme park around. I heard they sell cute teddy-bears.");
      } else if (level == 95) {
         yellowMessage("100% of people who hit level 95 in HeavenMS don't live to be 95 years old.");
      } else if (level == 100) {
         yellowMessage("Mid-journey so far... You just reached level 100! Now THAT's such a feat, however to manage the 200 you will need even more passion and determination than ever! Good hunting!");
      } else if (level == 105) {
         yellowMessage("Have you ever been to leafre? I heard they have dragons!");
      } else if (level == 110) {
         yellowMessage("I see many people wearing a teddy bear helmet. I should ask someone where they got it from.");
      } else if (level == 115) {
         yellowMessage("I bet all you can think of is level 120, huh? Level 115 gets no love.");
      } else if (level == 120) {
         yellowMessage("Are you ready to learn from the masters? Head over to your job instructor!");
      } else if (level == 125) {
         yellowMessage("The struggle for mastery books has begun, huh?");
      } else if (level == 130) {
         yellowMessage("You should try Temple of Time. It should be pretty decent EXP.");
      } else if (level == 135) {
         yellowMessage("I hope you're still not struggling for mastery books!");
      } else if (level == 140) {
         yellowMessage("You're well into 4th job at this point, great work!");
      } else if (level == 145) {
         yellowMessage("Level 145 is serious business!");
      } else if (level == 150) {
         yellowMessage("You have becomed quite strong, but the journey is not yet over.");
      } else if (level == 155) {
         yellowMessage("At level 155, Zakum should be a joke to you. Nice job!");
      } else if (level == 160) {
         yellowMessage("Level 160 is pretty impressive. Try taking a picture and putting it on Instagram.");
      } else if (level == 165) {
         yellowMessage("At this level, you should start looking into doing some boss runs.");
      } else if (level == 170) {
         yellowMessage("Level 170, huh? You have the heart of a champion.");
      } else if (level == 175) {
         yellowMessage("You came a long way from level 1. Amazing job so far.");
      } else if (level == 180) {
         yellowMessage("Have you ever tried taking a boss on by yourself? It is quite difficult.");
      } else if (level == 185) {
         yellowMessage("Legend has it that you're a legend.");
      } else if (level == 190) {
         yellowMessage("You only have 10 more levels to go until you hit 200!");
      } else if (level == 195) {
         yellowMessage("Nothing is stopping you at this point, level 195!");
      } else if (level == 200) {
         yellowMessage("Very nicely done! You have reached the so-long dreamed LEVEL 200!!! You are truly a hero among men, cheers upon you!");
      }
   }

   public void setPlayerRates() {
      this.expRate *= GameConstants.getPlayerBonusExpRate(this.level / 20);
      this.mesoRate *= GameConstants.getPlayerBonusMesoRate(this.level / 20);
      this.dropRate *= GameConstants.getPlayerBonusDropRate(this.level / 20);
   }

   private void revertLastPlayerRates() {
      this.expRate /= GameConstants.getPlayerBonusExpRate((this.level - 1) / 20);
      this.mesoRate /= GameConstants.getPlayerBonusMesoRate((this.level - 1) / 20);
      this.dropRate /= GameConstants.getPlayerBonusDropRate((this.level - 1) / 20);
   }

   public void setWorldRates() {
      World worldz = getWorldServer();
      this.expRate *= worldz.getExpRate();
      this.mesoRate *= worldz.getMesoRate();
      this.dropRate *= worldz.getDropRate();
   }

   public void revertWorldRates() {
      World worldz = getWorldServer();
      this.expRate /= worldz.getExpRate();
      this.mesoRate /= worldz.getMesoRate();
      this.dropRate /= worldz.getDropRate();
   }

   private void setCouponRates() {
      List<Integer> couponEffects;

      Collection<Item> cashItems = this.getInventory(MapleInventoryType.CASH).list();
      chrLock.lock();
      try {
         setActiveCoupons(cashItems);
         couponEffects = activateCouponsEffects();
      } finally {
         chrLock.unlock();
      }

      for (Integer couponId : couponEffects) {
         commitBuffCoupon(couponId);
      }
   }

   private void revertCouponRates() {
      revertCouponsEffects();
   }

   public void updateCouponRates() {
      MapleInventory cashInv = this.getInventory(MapleInventoryType.CASH);
      if (cashInv == null) {
         return;
      }

      if (cpnLock.tryLock()) {
         effLock.lock();
         chrLock.lock();
         cashInv.lockInventory();
         try {
            revertCouponRates();
            setCouponRates();
         } finally {
            cpnLock.unlock();

            cashInv.unlockInventory();
            chrLock.unlock();
            effLock.unlock();
         }
      }
   }

   public void resetPlayerRates() {
      expRate = 1;
      mesoRate = 1;
      dropRate = 1;

      expCoupon = 1;
      mesoCoupon = 1;
      dropCoupon = 1;
   }

   private int getCouponMultiplier(int couponId) {
      return activeCouponRates.get(couponId);
   }

   private void setExpCouponRate(int couponId, int couponQty) {
      this.expCoupon *= (getCouponMultiplier(couponId) * couponQty);
   }

   private void setDropCouponRate(int couponId, int couponQty) {
      this.dropCoupon *= (getCouponMultiplier(couponId) * couponQty);
      this.mesoCoupon *= (getCouponMultiplier(couponId) * couponQty);
   }

   private void revertCouponsEffects() {
      dispelBuffCoupons();

      this.expRate /= this.expCoupon;
      this.dropRate /= this.dropCoupon;
      this.mesoRate /= this.mesoCoupon;

      this.expCoupon = 1;
      this.dropCoupon = 1;
      this.mesoCoupon = 1;
   }

   private List<Integer> activateCouponsEffects() {
      List<Integer> toCommitEffect = new LinkedList<>();

      if (ServerConstants.USE_STACK_COUPON_RATES) {
         for (Entry<Integer, Integer> coupon : activeCoupons.entrySet()) {
            int couponId = coupon.getKey();
            int couponQty = coupon.getValue();

            toCommitEffect.add(couponId);

            if (ItemConstants.isExpCoupon(couponId)) {
               setExpCouponRate(couponId, couponQty);
            } else {
               setDropCouponRate(couponId, couponQty);
            }
         }
      } else {
         int maxExpRate = 1, maxDropRate = 1, maxExpCouponId = -1, maxDropCouponId = -1;

         for (Entry<Integer, Integer> coupon : activeCoupons.entrySet()) {
            int couponId = coupon.getKey();

            if (ItemConstants.isExpCoupon(couponId)) {
               if (maxExpRate < getCouponMultiplier(couponId)) {
                  maxExpCouponId = couponId;
                  maxExpRate = getCouponMultiplier(couponId);
               }
            } else {
               if (maxDropRate < getCouponMultiplier(couponId)) {
                  maxDropCouponId = couponId;
                  maxDropRate = getCouponMultiplier(couponId);
               }
            }
         }

         if (maxExpCouponId > -1) {
            toCommitEffect.add(maxExpCouponId);
         }
         if (maxDropCouponId > -1) {
            toCommitEffect.add(maxDropCouponId);
         }

         this.expCoupon = maxExpRate;
         this.dropCoupon = maxDropRate;
         this.mesoCoupon = maxDropRate;
      }

      this.expRate *= this.expCoupon;
      this.dropRate *= this.dropCoupon;
      this.mesoRate *= this.mesoCoupon;

      return toCommitEffect;
   }

   private void commitBuffCoupon(int couponid) {
      if (!isLoggedin() || getCashShop().isOpened()) {
         return;
      }

      MapleStatEffect mse = ii.getItemEffect(couponid);
      mse.applyTo(this);
   }

   private void dispelBuffCoupons() {
      List<MapleBuffStatValueHolder> allBuffs = getAllStatups();

      for (MapleBuffStatValueHolder mbsvh : allBuffs) {
         if (ItemConstants.isRateCoupon(mbsvh.effect.getSourceId())) {
            cancelEffect(mbsvh.effect, false, mbsvh.startTime);
         }
      }
   }

   public Set<Integer> getActiveCoupons() {
      chrLock.lock();
      try {
         return Collections.unmodifiableSet(activeCoupons.keySet());
      } finally {
         chrLock.unlock();
      }
   }

   private void setActiveCoupons(Collection<Item> cashItems) {
      activeCoupons.clear();
      activeCouponRates.clear();

      Map<Integer, Integer> coupons = Server.getInstance().getCouponRates();
      List<Integer> active = Server.getInstance().getActiveCoupons();

      for (Item it : cashItems) {
         if (ItemConstants.isRateCoupon(it.id()) && active.contains(it.id())) {
            Integer count = activeCoupons.get(it.id());

            if (count != null) {
               activeCoupons.put(it.id(), count + 1);
            } else {
               activeCoupons.put(it.id(), 1);
               activeCouponRates.put(it.id(), coupons.get(it.id()));
            }
         }
      }
   }

   public void addPlayerRing(Ring ring) {
      int ringItemId = ring.itemId();
      if (ItemConstants.isWeddingRing(ringItemId)) {
         this.addMarriageRing(ring);
      } else if (ring.itemId() > 1112012) {
         this.addFriendshipRing(ring);
      } else {
         this.addCrushRing(ring);
      }
   }

   public MapleCharacter generateCharacterEntry() {
      MapleCharacter ret = new MapleCharacter();

      ret.accountid = this.getAccountID();
      ret.id = this.getId();
      ret.name = this.getName();
      ret.gender = this.getGender();
      ret.skinColor = this.getSkinColor();
      ret.face = this.getFace();
      ret.hair = this.getHair();

      // skipping pets, probably unneeded here

      ret.level = this.getLevel();
      ret.job = this.getJob();
      ret.str = this.getStr();
      ret.dex = this.getDex();
      ret.int_ = this.getInt();
      ret.luk = this.getLuk();
      ret.hp = this.getHp();
      ret.setMaxHp(this.getMaxHp());
      ret.mp = this.getMp();
      ret.setMaxMp(this.getMaxMp());
      ret.remainingAp = this.getRemainingAp();
      ret.setRemainingSp(this.getRemainingSps());
      ret.exp.set(this.getExp());
      ret.fame = this.getFame();
      ret.gachaexp.set(this.getGachaExp());
      ret.mapid = this.getMapId();
      ret.initialSpawnPoint = this.getInitialSpawnPoint();

      ret.inventory[MapleInventoryType.EQUIPPED.ordinal()] = this.getInventory(MapleInventoryType.EQUIPPED);

      ret.gmLevel = this.gmLevel();
      ret.world = this.getWorld();
      ret.rank = this.getRank();
      ret.rankMove = this.getRankMove();
      ret.jobRank = this.getJobRank();
      ret.jobRankMove = this.getJobRankMove();

      return ret;
   }

   public void loadCharSkillPoints(String[] skillPoints) {
      int[] sps = new int[skillPoints.length];
      for (int i = 0; i < skillPoints.length; i++) {
         sps[i] = Integer.parseInt(skillPoints[i]);
      }

      setRemainingSp(sps);
   }

   public int getRemainingSp() {
      return getRemainingSp(job.getId()); //default
   }

   public void updateRemainingSp(int remainingSp) {
      updateRemainingSp(remainingSp, GameConstants.getSkillBook(job.getId()));
   }

   public void reloadQuestExpirations() {
      for (MapleQuestStatus mqs : quests.values()) {
         if (mqs.getExpirationTime() > 0) {
            questTimeLimit2(mqs.getQuest(), mqs.getExpirationTime());
         }
      }
   }

   public void yellowMessage(String m) {
      announce(MaplePacketCreator.sendYellowTip(m));
   }

   public void updateQuestMobCount(int id) {
      // It seems nexon uses monsters that don't exist in the WZ (except string) to merge multiple mobs together for these 3 monsters.
      // We also want to run mobKilled for both since there are some quest that don't use the updated ID...
      if (id == 1110100 || id == 1110130) {
         updateQuestMobCount(9101000);
      } else if (id == 2230101 || id == 2230131) {
         updateQuestMobCount(9101001);
      } else if (id == 1140100 || id == 1140130) {
         updateQuestMobCount(9101002);
      }

      int lastQuestProcessed = 0;
      try {
         synchronized (quests) {
            for (MapleQuestStatus q : quests.values()) {
               lastQuestProcessed = q.getQuest().getId();
               if (q.getStatus() == MapleQuestStatus.Status.COMPLETED || q.getQuest().canComplete(this, null)) {
                  continue;
               }
               String progress = q.getProgress(id);
               if (!progress.isEmpty() && Integer.parseInt(progress) >= q.getQuest().getMobAmountNeeded(id)) {
                  continue;
               }
               if (q.progress(id)) {
                  announceUpdateQuest(DelayedQuestUpdate.UPDATE, q, false);
               }
            }
         }
      } catch (Exception e) {
         FilePrinter.printError(FilePrinter.EXCEPTION_CAUGHT, e, "MapleCharacter.mobKilled. CID: " + this.id + " last Quest Processed: " + lastQuestProcessed);
      }
   }

   public MapleMount mount(int id, int skillid) {
      MapleMount mount = maplemount;
      mount.setItemId(id);
      mount.setSkillId(skillid);
      return mount;
   }

   private void playerDead() {
      if (this.getMap().isCPQMap()) {
         int losing = getMap().getDeathCP();
         if (getCP() < losing) {
            losing = getCP();
         }
         getMap().broadcastMessage(MaplePacketCreator.playerDiedMessage(getName(), losing, getTeam()));
         gainCP(-losing);
         return;
      }

      cancelAllBuffs(false);
      dispelDebuffs();
      lastDeathtime = Server.getInstance().getCurrentTime();

      EventInstanceManager eim = getEventInstance();
      if (eim != null) {
         eim.playerKilled(this);
      }
      int[] charmID = {5130000, 4031283, 4140903};
      int possesed = 0;
      int i;
      for (i = 0; i < charmID.length; i++) {
         int quantity = getItemQuantity(charmID[i], false);
         if (possesed == 0 && quantity > 0) {
            possesed = quantity;
            break;
         }
      }
      if (possesed > 0 && !GameConstants.isDojo(getMapId())) {
         MessageBroadcaster.getInstance().sendServerNotice(this, ServerNoticeType.PINK_TEXT, "You have used a safety charm, so your EXP points have not been decreased.");
         MapleInventoryManipulator.removeById(client, ItemConstants.getInventoryType(charmID[i]), charmID[i], 1, true, false);
      } else if (getJob() != MapleJob.BEGINNER) { //Hmm...
         if (!FieldLimit.NO_EXP_DECREASE.check(getMap().getFieldLimit())) {  // thanks Conrad for noticing missing FieldLimit check
            int XPdummy = ExpTable.getExpNeededForLevel(getLevel());

            if (getMap().isTown()) {    // thanks MindLove, SIayerMonkey, HaItsNotOver for noting players only lose 1% on town maps
               XPdummy /= 100;
            } else {
               if (getLuk() < 50) {    // thanks Taiketo, Quit, Fishanelli for noting player EXP loss are fixed, 50-LUK threshold
                  XPdummy /= 10;
               } else {
                  XPdummy /= 20;
               }
            }

            int curExp = getExp();
            if (curExp > XPdummy) {
               loseExp(XPdummy, false, false);
            } else {
               loseExp(curExp, false, false);
            }
         }
      }

      if (getBuffedValue(MapleBuffStat.MORPH) != null) {
         cancelEffectFromBuffStat(MapleBuffStat.MORPH);
      }

      if (getBuffedValue(MapleBuffStat.MONSTER_RIDING) != null) {
         cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
      }

      unsitChairInternal();
      client.announce(MaplePacketCreator.enableActions());
   }

   private void unsitChairInternal() {
      int chairid = chair.get();
      if (chairid >= 0) {
         if (ItemConstants.isFishingChair(chairid)) {
            this.getWorldServer().unregisterFisherPlayer(this);
         }

         setChair(-1);
         if (unregisterChairBuff()) {
            getMap().broadcastMessage(this, MaplePacketCreator.cancelForeignChairSkillEffect(this.getId()), false);
         }

         getMap().broadcastMessage(this, MaplePacketCreator.showChair(this.getId(), 0), false);
      }

      announce(MaplePacketCreator.cancelChair(-1));
   }

   public void sitChair(int itemId) {
      if (client.tryAcquireClient()) {
         try {
            if (this.isLoggedinWorld()) {
               if (itemId >= 1000000) {    // sit on item chair
                  if (chair.get() < 0) {
                     setChair(itemId);
                     getMap().broadcastMessage(this, MaplePacketCreator.showChair(this.getId(), itemId), false);
                  }
                  announce(MaplePacketCreator.enableActions());
               } else if (itemId >= 0) {    // sit on map chair
                  if (chair.get() < 0) {
                     setChair(itemId);
                     if (registerChairBuff()) {
                        getMap().broadcastMessage(this, MaplePacketCreator.giveForeignChairSkillEffect(this.getId()), false);
                     }
                     announce(MaplePacketCreator.cancelChair(itemId));
                  }
               } else {    // stand up
                  unsitChairInternal();
               }
            }
         } finally {
            client.releaseClient();
         }
      }
   }

   public void respawn(int returnMap) {
      respawn(null, returnMap);    // unspecified EIM, don't force EIM unregister in this case
   }

   public void respawn(EventInstanceManager eim, int returnMap) {
      if (eim != null) {
         eim.unregisterPlayer(this);    // some event scripts uses this...
      }
      changeMap(returnMap);

      cancelAllBuffs(false);  // thanks Oblivium91 for finding out players still could revive in area and take damage before returning to town
      updateHp(50);
      setStance(0);
   }

   private void prepareDragonBlood(final MapleStatEffect bloodEffect) {
      scheduler.cancel(MapleCharacterScheduler.Type.DRAGON_BLOOD);
      scheduler.add(MapleCharacterScheduler.Type.DRAGON_BLOOD, () -> {
         if (awayFromWorld.get()) {
            return;
         }

         addHP(-bloodEffect.getX());
         announce(MaplePacketCreator.showOwnBuffEffect(bloodEffect.getSourceId(), 5));
         getMap().broadcastMessage(MapleCharacter.this, MaplePacketCreator.showBuffeffect(getId(), bloodEffect.getSourceId(), 5), false);
      }, 4000, 4000);
   }

   private void recalcEquipStats() {
      if (equipchanged) {
         equipmaxhp = 0;
         equipmaxmp = 0;
         equipdex = 0;
         equipint_ = 0;
         equipstr = 0;
         equipluk = 0;
         equipmagic = 0;
         equipwatk = 0;
         //equipspeed = 0;
         //equipjump = 0;

         for (Item item : getInventory(MapleInventoryType.EQUIPPED)) {
            Equip equip = (Equip) item;
            equipmaxhp += equip.hp();
            equipmaxmp += equip.mp();
            equipdex += equip.dex();
            equipint_ += equip._int();
            equipstr += equip.str();
            equipluk += equip.luk();
            equipmagic += equip.matk() + equip._int();
            equipwatk += equip.watk();
            //equipspeed += equip.getSpeed();
            //equipjump += equip.getJump();
         }

         equipchanged = false;
      }

      localmaxhp += equipmaxhp;
      localmaxmp += equipmaxmp;
      localdex += equipdex;
      localint_ += equipint_;
      localstr += equipstr;
      localluk += equipluk;
      localmagic += equipmagic;
      localwatk += equipwatk;
   }

   public void reapplyLocalStats() {
      effLock.lock();
      chrLock.lock();
      statWlock.lock();
      try {
         localmaxhp = getMaxHp();
         localmaxmp = getMaxMp();
         localdex = getDex();
         localint_ = getInt();
         localstr = getStr();
         localluk = getLuk();
         localmagic = localint_;
         localwatk = 0;
         localchairrate = -1;

         recalcEquipStats();

         localmagic = Math.min(localmagic, 2000);

         Integer hbhp = getBuffedValue(MapleBuffStat.HYPERBODYHP);
         if (hbhp != null) {
            localmaxhp += (hbhp.doubleValue() / 100) * localmaxhp;
         }
         Integer hbmp = getBuffedValue(MapleBuffStat.HYPERBODYMP);
         if (hbmp != null) {
            localmaxmp += (hbmp.doubleValue() / 100) * localmaxmp;
         }

         localmaxhp = Math.min(30000, localmaxhp);
         localmaxmp = Math.min(30000, localmaxmp);

         MapleStatEffect combo = getBuffEffect(MapleBuffStat.ARAN_COMBO);
         if (combo != null) {
            localwatk += combo.getX();
         }

         if (energybar == 15000) {
            executeForSkill(isCygnus() ? ThunderBreaker.ENERGY_CHARGE : Marauder.ENERGY_CHARGE, (skill, skillLevel) -> {
               MapleStatEffect statEffect = skill.getEffect(skillLevel);
               localwatk += statEffect.getWatk();
            });
         }

         Integer mwarr = getBuffedValue(MapleBuffStat.MAPLE_WARRIOR);
         if (mwarr != null) {
            localstr += getStr() * mwarr / 100;
            localdex += getDex() * mwarr / 100;
            localint_ += getInt() * mwarr / 100;
            localluk += getLuk() * mwarr / 100;
         }
         if (job.isA(MapleJob.BOWMAN)) {

            if (job.isA(MapleJob.MARKSMAN)) {
               executeForSkill(3220004, (skill, skillLevel) -> localwatk += skill.getEffect(skillLevel).getX());
            } else if (job.isA(MapleJob.BOWMASTER)) {
               executeForSkill(3120005, (skill, skillLevel) -> localwatk += skill.getEffect(skillLevel).getX());
            }
         }

         Integer watkbuff = getBuffedValue(MapleBuffStat.WATK);
         if (watkbuff != null) {
            localwatk += watkbuff;
         }
         Integer matkbuff = getBuffedValue(MapleBuffStat.MATK);
         if (matkbuff != null) {
            localmagic += matkbuff;
         }

            /*
            Integer speedbuff = getBuffedValue(MapleBuffStat.SPEED);
            if (speedbuff != null) {
                localspeed += speedbuff.intValue();
            }
            Integer jumpbuff = getBuffedValue(MapleBuffStat.JUMP);
            if (jumpbuff != null) {
                localjump += jumpbuff.intValue();
            }
            */

         Integer blessing = getSkillLevel(10000000 * getJobType() + 12);
         if (blessing > 0) {
            localwatk += blessing;
            localmagic += blessing * 2;
         }

         if (job.isA(MapleJob.THIEF) || job.isA(MapleJob.BOWMAN) || job.isA(MapleJob.PIRATE) || job.isA(MapleJob.NIGHTWALKER1) || job.isA(MapleJob.WINDARCHER1)) {
            Item weapon_item = getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
            if (weapon_item != null) {
               MapleWeaponType weapon = ii.getWeaponType(weapon_item.id());
               boolean bow = weapon == MapleWeaponType.BOW;
               boolean crossbow = weapon == MapleWeaponType.CROSSBOW;
               boolean claw = weapon == MapleWeaponType.CLAW;
               boolean gun = weapon == MapleWeaponType.GUN;
               if (bow || crossbow || claw || gun) {
                  // Also calc stars into this.
                  MapleInventory inv = getInventory(MapleInventoryType.USE);
                  for (short i = 1; i <= inv.getSlotLimit(); i++) {
                     Item item = inv.getItem(i);
                     if (item != null) {
                        if ((claw && ItemConstants.isThrowingStar(item.id())) || (gun && ItemConstants.isBullet(item.id())) || (bow && ItemConstants.isArrowForBow(item.id())) || (crossbow && ItemConstants.isArrowForCrossBow(item.id()))) {
                           if (item.quantity() > 0) {
                              // Finally there!
                              localwatk += ii.getWatkForProjectile(item.id());
                              break;
                           }
                        }
                     }
                  }
               }
            }
            // Add throwing stars to dmg.
         }
      } finally {
         statWlock.unlock();
         chrLock.unlock();
         effLock.unlock();
      }
   }

   private List<Pair<MapleStat, Integer>> recalcLocalStats() {
      effLock.lock();
      chrLock.lock();
      statWlock.lock();
      try {
         List<Pair<MapleStat, Integer>> hpmpupdate = new ArrayList<>(2);
         int oldlocalmaxhp = localmaxhp;
         int oldlocalmaxmp = localmaxmp;

         reapplyLocalStats();

         if (ServerConstants.USE_FIXED_RATIO_HPMP_UPDATE) {
            if (localmaxhp != oldlocalmaxhp) {
               Pair<MapleStat, Integer> hpUpdate;

               if (transienthp == Float.NEGATIVE_INFINITY) {
                  hpUpdate = calcHpRatioUpdate(localmaxhp, oldlocalmaxhp);
               } else {
                  hpUpdate = calcHpRatioTransient();
               }

               hpmpupdate.add(hpUpdate);
            }

            if (localmaxmp != oldlocalmaxmp) {
               Pair<MapleStat, Integer> mpUpdate;

               if (transientmp == Float.NEGATIVE_INFINITY) {
                  mpUpdate = calcMpRatioUpdate(localmaxmp, oldlocalmaxmp);
               } else {
                  mpUpdate = calcMpRatioTransient();
               }

               hpmpupdate.add(mpUpdate);
            }
         }

         return hpmpupdate;
      } finally {
         statWlock.unlock();
         chrLock.unlock();
         effLock.unlock();
      }
   }

   private void updateLocalStats() {
      prtLock.lock();
      effLock.lock();
      statWlock.lock();
      try {
         int oldmaxhp = localmaxhp;
         List<Pair<MapleStat, Integer>> hpmpupdate = recalcLocalStats();
         enforceMaxHpMp();

         if (!hpmpupdate.isEmpty()) {
            client.announce(MaplePacketCreator.updatePlayerStats(hpmpupdate, true, this));
         }

         if (oldmaxhp != localmaxhp) {   // thanks Wh1SK3Y for pointing out a deadlock occuring related to party members HP
            updatePartyMemberHP();
         }
      } finally {
         statWlock.unlock();
         effLock.unlock();
         prtLock.unlock();
      }
   }

   public void receivePartyMemberHP() {
      prtLock.lock();
      try {
         if (party != null) {
            for (MapleCharacter partychar : this.getPartyMembersOnSameMap()) {
               announce(MaplePacketCreator.updatePartyMemberHP(partychar.getId(), partychar.getHp(), partychar.getCurrentMaxHp()));
            }
         }
      } finally {
         prtLock.unlock();
      }
   }

   public void removeAllCooldownsExcept(int id, boolean packet) {
      effLock.lock();
      chrLock.lock();
      try {
         ArrayList<CoolDownValueHolder> list = new ArrayList<>(coolDowns.values());
         for (CoolDownValueHolder mcvh : list) {
            if (mcvh.skillId() != id) {
               coolDowns.remove(mcvh.skillId());
               if (packet) {
                  client.announce(MaplePacketCreator.skillCooldown(mcvh.skillId(), 0));
               }
            }
         }
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   private void removeCooldown(int skillId) {
      effLock.lock();
      chrLock.lock();
      try {
         this.coolDowns.remove(skillId);
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   private void removePet(MaplePet pet, boolean shift_left) {
      petLock.lock();
      try {
         int slot = -1;
         for (int i = 0; i < 3; i++) {
            if (pets[i] != null) {
               if (pets[i].uniqueId() == pet.uniqueId()) {
                  pets[i] = null;
                  slot = i;
                  break;
               }
            }
         }
         if (shift_left) {
            if (slot > -1) {
               for (int i = slot; i < 3; i++) {
                  if (i != 2) {
                     pets[i] = pets[i + 1];
                  } else {
                     pets[i] = null;
                  }
               }
            }
         }
      } finally {
         petLock.unlock();
      }
   }

   public void removeVisibleMapObject(MapleMapObject mo) {
      visibleMapObjects.remove(mo);
   }

   public synchronized void resetStats() {
      if (!ServerConstants.USE_AUTOASSIGN_STARTERS_AP) {
         return;
      }

      effLock.lock();
      statWlock.lock();
      try {
         int tap = remainingAp + str + dex + int_ + luk, tsp = 1;
         int tstr = 4, tdex = 4, tint = 4, tluk = 4;

         switch (job.getId()) {
            case 100:
            case 1100:
            case 2100:
               tstr = 35;
               tsp += ((getLevel() - 10) * 3);
               break;
            case 200:
            case 1200:
               tint = 20;
               tsp += ((getLevel() - 8) * 3);
               break;
            case 300:
            case 1300:
            case 400:
            case 1400:
               tdex = 25;
               tsp += ((getLevel() - 10) * 3);
               break;
            case 500:
            case 1500:
               tdex = 20;
               tsp += ((getLevel() - 10) * 3);
               break;
         }

         tap -= tstr;
         tap -= tdex;
         tap -= tint;
         tap -= tluk;

         if (tap >= 0) {
            updateStrDexIntLukSp(tstr, tdex, tint, tluk, tap, tsp, GameConstants.getSkillBook(job.getId()));
         } else {
            FilePrinter.print(FilePrinter.EXCEPTION_CAUGHT, name + " tried to get their stats reseted, without having enough AP available.");
         }
      } finally {
         statWlock.unlock();
         effLock.unlock();
      }
   }

   public void resetBattleshipHp() {
      executeIfHasSkill(Corsair.BATTLE_SHIP, (skill, skillLevel) -> {
         int battleshipLevel = Math.max(getLevel() - 120, 0);  // thanks alex12 for noticing battleship HP issues for low-level players
         MapleCharacter.this.battleShipHp = 400 * skillLevel + (battleshipLevel * 200);
      });
   }

   public void resetEnteredScript() {
      entered.remove(map.getId());
   }

   public synchronized void saveCooldowns() {
      List<PlayerCoolDownValueHolder> listcd = getAllCooldowns();
      if (!listcd.isEmpty()) {
         DatabaseConnection.getInstance().withConnection(connection -> {
            CoolDownAdministrator.getInstance().deleteForCharacter(connection, getId());
            CoolDownAdministrator.getInstance().addCoolDownsForCharacter(connection, getId(), listcd);
         });
      }

      Map<MapleDisease, Pair<Long, MobSkill>> listds = getAllDiseases();
      if (!listds.isEmpty()) {
         DatabaseConnection.getInstance().withConnection(connection -> {
            PlayerDiseaseAdministrator.getInstance().deleteForCharacter(connection, getId());
            PlayerDiseaseAdministrator.getInstance().addPlayerDiseasesForCharacter(connection, getId(), listds.entrySet());
         });
      }
   }

   public void saveGuildStatus() {
      DatabaseConnection.getInstance().withConnection(connection -> CharacterAdministrator.getInstance().updateGuildStatus(connection, id, guildid, guildRank, allianceRank));
   }

   public void saveLocationOnWarp() {  // suggestion to remember the map before warp command thanks to Lei
      MaplePortal closest = map.findClosestPortal(getPosition());
      int curMapid = getMapId();

      for (int i = 0; i < savedLocations.length; i++) {
         if (savedLocations[i] == null) {
            savedLocations[i] = new SavedLocation(curMapid, closest != null ? closest.getId() : 0);
         }
      }
   }

   public void saveLocation(String type) {
      MaplePortal closest = map.findClosestPortal(getPosition());
      savedLocations[SavedLocationType.fromString(type).ordinal()] = new SavedLocation(getMapId(), closest != null ? closest.getId() : 0);
   }

   public void saveCharToDB() {
      if (ServerConstants.USE_AUTOSAVE) {
         Runnable r = () -> saveCharToDB(true);
         ThreadManager.getInstance().newTask(r);  //spawns a new thread to deal with this
      } else {
         saveCharToDB(true);
      }
   }

   //ItemFactory saveItems and monsterbook.saveCards are the most time consuming here.
   public synchronized void saveCharToDB(boolean notAutosave) {
      if (!loggedIn) {
         return;
      }

      Calendar c = Calendar.getInstance();

      if (notAutosave) {
         FilePrinter.print(FilePrinter.SAVING_CHARACTER, "Attempting to save " + name + " at " + c.getTime().toString());
      } else {
         FilePrinter.print(FilePrinter.AUTOSAVING_CHARACTER, "Attempting to autosave " + name + " at " + c.getTime().toString());
      }

      Server.getInstance().updateCharacterEntry(this);

      DatabaseConnection.getInstance().withExplicitCommitConnection(connection -> {
         updateCharacter(connection);
         updatePets();
         updatePetIgnores(connection);
         updateKeyMap(connection);
         updateSkillMacros(connection);

         List<Pair<Item, MapleInventoryType>> itemsWithType = new ArrayList<>();
         for (MapleInventory iv : inventory) {
            for (Item item : iv.list()) {
               itemsWithType.add(new Pair<>(item, iv.getType()));
            }
         }
         ItemFactory.INVENTORY.saveItems(itemsWithType, id, connection);

         updateSkills(connection);
         updateSavedLocations(connection);
         updateTeleportRockLocations(connection);
         updateBuddies(connection);
         updateAreaInfo(connection);
         updateEventStats(connection);
         updateQuestInfo(connection);

         MapleFamilyProcessor.getInstance().saveCharactersFamilyReputation(connection, getFamilyEntry());

         if (cashshop != null) {
            cashshop.save(connection);
         }

         connection.commit();

         if (storage != null && usedStorage) {
            storage.saveToDB(connection);
            usedStorage = false;
         }

         connection.commit();
      });
   }

   private void updateQuestInfo(Connection con) {
      CharacterProcessor.getInstance().deleteQuestProgressWhereCharacterId(con, id);

      synchronized (quests) {
         for (MapleQuestStatus q : quests.values()) {
            int questId = QuestStatusAdministrator.getInstance().create(con, id, q);
            QuestProgressAdministrator.getInstance().create(con, id, questId, q.getProgress().keySet().stream().map(key -> new Pair<>(key, q.getProgress(key))).collect(Collectors.toList()));
            MedalMapAdministrator.getInstance().create(con, id, questId, q.getMedalMaps());
         }
      }
   }

   private void updateEventStats(Connection con) {
      EventStatAdministrator.getInstance().deleteForCharacter(con, id);
      EventStatAdministrator.getInstance().create(con, id, events.entrySet());
   }

   private void updateAreaInfo(Connection con) {
      AreaInfoAdministrator.getInstance().deleteForCharacter(con, id);
      AreaInfoAdministrator.getInstance().create(con, id, area_info.entrySet());
   }

   private void updateBuddies(Connection con) {
      BuddyAdministrator.getInstance().deleteNotPendingForCharacter(con, id);
      BuddyAdministrator.getInstance().addBuddies(con, id, buddylist.getBuddies());
   }

   private void updateTeleportRockLocations(Connection con) {
      TeleportRockLocationAdministrator.getInstance().deleteForCharacter(con, id);
      TeleportRockLocationAdministrator.getInstance().create(con, id, trockmaps.stream().filter(id -> id != 999999999).collect(Collectors.toList()), 0);
      TeleportRockLocationAdministrator.getInstance().create(con, id, viptrockmaps.stream().filter(id -> id != 999999999).collect(Collectors.toList()), 1);
   }

   private void updateSavedLocations(Connection con) {
      SavedLocationAdministrator.getInstance().deleteForCharacter(con, id);

      Collection<Pair<String, SavedLocation>> locations = Arrays.stream(SavedLocationType.values())
            .filter(type -> savedLocations[type.ordinal()] != null)
            .map(type -> new Pair<>(type.name(), savedLocations[type.ordinal()]))
            .collect(Collectors.toList());
      SavedLocationAdministrator.getInstance().create(con, id, locations);
   }

   private void updateSkills(Connection con) {
      SkillAdministrator.getInstance().replace(con, id, skills.entrySet());
   }

   private void updateSkillMacros(Connection con) {
      SkillMacroAdministrator.getInstance().deleteForCharacter(con, id);
      SkillMacroAdministrator.getInstance().create(con, id, Arrays.stream(skillMacros).filter(Objects::nonNull).collect(Collectors.toList()));
   }

   private void updateKeyMap(Connection con) {
      KeyMapAdministrator.getInstance().deleteForCharacter(con, id);
      KeyMapAdministrator.getInstance().create(con, id, Collections.unmodifiableSet(keymap.entrySet()));
   }

   private void updatePetIgnores(Connection con) {
      for (Entry<Integer, Set<Integer>> es : getExcluded().entrySet()) {    // this set is already protected
         PetIgnoreAdministrator.getInstance().deletePetIgnore(con, es.getKey());
         PetIgnoreAdministrator.getInstance().create(con, es.getKey(), es.getValue());
      }
   }

   private void updatePets() {
      List<MaplePet> petList;
      petLock.lock();
      try {
         petList = Arrays.stream(pets).filter(Objects::nonNull).collect(Collectors.toList());
      } finally {
         petLock.unlock();
      }
      petList.forEach(pet -> PetProcessor.getInstance().saveToDb(pet));
   }

   private void updateCharacter(Connection con) {
      effLock.lock();
      statWlock.lock();
      prtLock.lock();
      try {
         StringBuilder sps = new StringBuilder();
         for (int value : remainingSp) {
            sps.append(value);
            sps.append(",");
         }
         String sp = sps.toString();

         int mapId;
         if (map == null || (cashshop != null && cashshop.isOpened())) {
            mapId = mapid;
         } else {
            if (map.getForcedReturnId() != 999999999) {
               mapId = map.getForcedReturnId();
            } else {
               mapId = getHp() < 1 ? map.getReturnMapId() : map.getId();
            }
         }

         int spawnPoint;
         if (map == null || map.getId() == 610020000 || map.getId() == 610020001) {  // reset to first spawnpoint on those maps
            spawnPoint = 0;
         } else {
            MaplePortal closest = map.findClosestPlayerSpawnpoint(getPosition());
            if (closest != null) {
               spawnPoint = closest.getId();
            } else {
               spawnPoint = 0;
            }
         }

         int partyId = party != null ? party.getId() : -1;
         int messengerId = messenger != null ? messenger.getId() : 0;
         int messengerPosition = messenger != null ? messengerposition : 4;

         int mountLevel = maplemount != null ? maplemount.getLevel() : 1;
         int mountExp = maplemount != null ? maplemount.getExp() : 0;
         int mountTiredness = maplemount != null ? maplemount.getTiredness() : 0;

         CharacterAdministrator.getInstance().update(con, id, level, fame, str, dex, luk, int_, Math.abs(exp.get()),
               Math.abs(gachaexp.get()), hp, mp, maxhp, maxmp, sp.substring(0, sp.length() - 1), remainingAp, gmLevel,
               skinColor.getId(), gender, job.getId(), hair, face, mapId, meso.get(), hpMpApUsed, spawnPoint,
               partyId, buddylist.capacity(), messengerId, messengerPosition, mountLevel, mountExp, mountTiredness,
               getSlots(1), getSlots(2), getSlots(3), getSlots(4), bookCover, vanquisherStage,
               dojoPoints, dojoStage, finishedDojoTutorial ? 1 : 0, vanquisherKills, matchCard.wins(), matchCard.losses(),
               matchCard.ties(), omok.wins(), omok.losses(), omok.ties(), dataString, questFame, jailExpiration, partnerId,
               marriageItemid, lastExpGainTime, ariantPoints, canRecvPartySearchInvite);
         monsterbook.saveCards(getId());
      } finally {
         statWlock.unlock();
         effLock.unlock();
         prtLock.unlock();
      }
   }

   public void sendPolice(int greason, String reason, int duration) {
      announce(MaplePacketCreator.sendPolice(String.format("You have been blocked by the#b %s Police for %s.#k", "HeavenMS", reason)));
      this.isbanned = true;
      TimerManager.getInstance().schedule(() -> client.disconnect(false, false), duration);
   }

   public void sendPolice(String text) {
      String message = getName() + " received this - " + text;
      if (Server.getInstance().isGmOnline(this.getWorld())) { //Alert and log if a GM is online
         Server.getInstance().broadcastGMMessage(this.getWorld(), MaplePacketCreator.sendYellowTip(message));
         FilePrinter.print(FilePrinter.AUTOBAN_WARNING, message);
      } else { //Auto DC and log if no GM is online
         client.disconnect(false, false);
         FilePrinter.print(FilePrinter.AUTOBAN_DC, message);
      }
      //Server.getInstance().broadcastGMMessage(0, MaplePacketCreator.serverNotice(1, getName() + " received this - " + text));
      //announce(MaplePacketCreator.sendPolice(text));
      //this.isbanned = true;
      //TimerManager.getInstance().schedule(new Runnable() {
      //    @Override
      //    public void run() {
      //        client.disconnect(false, false);
      //    }
      //}, 6000);
   }

   public void sendKeymap() {
      client.announce(MaplePacketCreator.getKeymap(keymap));
   }

   public void sendMacros() {
      // Always send the macro packet to fix a client side bug when switching characters.
      client.announce(MaplePacketCreator.getMacros(skillMacros));
   }

   public void setBuddyCapacity(int capacity) {
      buddylist.capacity_$eq(capacity);
      client.announce(MaplePacketCreator.updateBuddyCapacity(capacity));
   }

   public void setBuffedValue(MapleBuffStat effect, int value) {
      effLock.lock();
      chrLock.lock();
      try {
         MapleBuffStatValueHolder mbsvh = effects.get(effect);
         if (mbsvh == null) {
            return;
         }
         mbsvh.value = value;
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   public void setFinishedDojoTutorial() {
      this.finishedDojoTutorial = true;
   }

   public void setHasMerchantNoUpdate(boolean set) {
      hasMerchant = set;
   }

   public void setHasMerchant(boolean set) {
      DatabaseConnection.getInstance().withConnection(connection -> CharacterAdministrator.getInstance().setMerchant(connection, id, set));
      hasMerchant = set;
   }

   public void addMerchantMesos(int add) {
      int newAmount = (int) Math.min((long) merchantmeso + add, Integer.MAX_VALUE);
      DatabaseConnection.getInstance().withConnection(connection -> CharacterAdministrator.getInstance().setMerchantMesos(connection, id, newAmount));
      merchantmeso = newAmount;
   }

   public synchronized void withdrawMerchantMesos() {
      int merchantMeso = this.getMerchantNetMeso();
      int playerMeso = this.getMeso();

      if (merchantMeso > 0) {
         int possible = Integer.MAX_VALUE - playerMeso;

         if (possible > 0) {
            if (possible < merchantMeso) {
               this.gainMeso(possible, false);
               this.setMerchantMeso(merchantMeso - possible);
            } else {
               this.gainMeso(merchantMeso, false);
               this.setMerchantMeso(0);
            }
         }
      } else {
         int nextMeso = playerMeso + merchantMeso;

         if (nextMeso < 0) {
            this.gainMeso(-playerMeso, false);
            this.setMerchantMeso(merchantMeso + playerMeso);
         } else {
            this.gainMeso(merchantMeso, false);
            this.setMerchantMeso(0);
         }
      }
   }

   private void hpChangeAction(int oldHp) {
      boolean playerDied = false;
      if (hp <= 0) {
         if (oldHp > hp) {
            if (!isBuybackInvincible()) {
               playerDied = true;
            } else {
               hp = 1;
            }
         }
      }

      final boolean chrDied = playerDied;
      Runnable r = () -> {
         updatePartyMemberHP();    // thanks BHB (BHB88) for detecting a deadlock case within player stats.

         if (chrDied) {
            playerDead();
         } else {
            checkBerserk(isHidden());
         }
      };
      if (map != null) {
         map.registerCharacterStatUpdate(r);
      }
   }

   private Pair<MapleStat, Integer> calcHpRatioUpdate(int newHp, int oldHp) {
      int delta = newHp - oldHp;
      this.hp = calcHpRatioUpdate(hp, oldHp, delta);

      hpChangeAction(Short.MIN_VALUE);
      return new Pair<>(MapleStat.HP, hp);
   }

   private Pair<MapleStat, Integer> calcMpRatioUpdate(int newMp, int oldMp) {
      int delta = newMp - oldMp;
      this.mp = calcMpRatioUpdate(mp, oldMp, delta);
      return new Pair<>(MapleStat.MP, mp);
   }

   private Pair<MapleStat, Integer> calcHpRatioTransient() {
      this.hp = calcTransientRatio(transienthp * localmaxhp);

      hpChangeAction(Short.MIN_VALUE);
      return new Pair<>(MapleStat.HP, hp);
   }

   private Pair<MapleStat, Integer> calcMpRatioTransient() {
      this.mp = calcTransientRatio(transientmp * localmaxmp);
      return new Pair<>(MapleStat.MP, mp);
   }

   private int calcHpRatioUpdate(int curpoint, int maxpoint, int diffpoint) {
      int nextMax = Math.min(30000, maxpoint + diffpoint);

      float temp = curpoint * nextMax;
      int ret = (int) Math.ceil(temp / maxpoint);

      transienthp = (maxpoint > nextMax) ? ((float) curpoint) / maxpoint : ((float) ret) / nextMax;
      return ret;
   }

   private int calcMpRatioUpdate(int curpoint, int maxpoint, int diffpoint) {
      int nextMax = Math.min(30000, maxpoint + diffpoint);

      float temp = curpoint * nextMax;
      int ret = (int) Math.ceil(temp / maxpoint);

      transientmp = (maxpoint > nextMax) ? ((float) curpoint) / maxpoint : ((float) ret) / nextMax;
      return ret;
   }

   public boolean applyHpMpChange(int hpCon, int hpchange, int mpchange) {
      boolean zombify = hasDisease(MapleDisease.ZOMBIFY);

      effLock.lock();
      statWlock.lock();
      try {
         int nextHp = hp + hpchange, nextMp = mp + mpchange;
         boolean cannotApplyHp = hpchange != 0 && nextHp <= 0 && (!zombify || hpCon > 0);
         boolean cannotApplyMp = mpchange != 0 && nextMp < 0;

         if (cannotApplyHp || cannotApplyMp) {
            if (!isGM()) {
               return false;
            }

            if (cannotApplyHp) {
               nextHp = 1;
            }
         }

         updateHpMp(nextHp, nextMp);
      } finally {
         statWlock.unlock();
         effLock.unlock();
      }

      // autopot on HPMP deplete... thanks shavit for finding out D. Roar doesn't trigger autopot request
      if (hpchange < 0) {
         KeyBinding autohpPot = this.getKeymap().get(91);
         if (autohpPot != null) {
            int autohpItemid = autohpPot.action();
            Item autohpItem = this.getInventory(MapleInventoryType.USE).findById(autohpItemid);
            if (autohpItem != null) {
               PetAutopotProcessor.getInstance().runAutopotAction(client, autohpItem.position(), autohpItemid);
            }
         }
      }

      if (mpchange < 0) {
         KeyBinding autompPot = this.getKeymap().get(92);
         if (autompPot != null) {
            int autompItemid = autompPot.action();
            Item autompItem = this.getInventory(MapleInventoryType.USE).findById(autompItemid);
            if (autompItem != null) {
               PetAutopotProcessor.getInstance().runAutopotAction(client, autompItem.position(), autompItemid);
            }
         }
      }

      return true;
   }

   public void setMiniGamePoints(MapleCharacter visitor, int winnerslot, boolean omok) {
      GameData thisGameData;
      GameData visitorGameData;
      if (omok) {
         thisGameData = this.omok;
         visitorGameData = visitor.omok;
      } else {
         thisGameData = this.matchCard;
         visitorGameData = visitor.matchCard;
      }

      if (winnerslot == 1) {
         thisGameData.incrementWins();
         visitorGameData.incrementLosses();
      } else if (winnerslot == 2) {
         visitorGameData.incrementWins();
         thisGameData.incrementLosses();
      } else {
         thisGameData.incrementTies();
         visitorGameData.incrementTies();
      }
   }

   private void closeRPS() {
      MapleRockPaperScissor rps = this.rps;
      if (rps != null) {
         rps.dispose(client);
         setRPS(null);
      }
   }

   public int getDoorSlot() {
      if (doorSlot != -1) {
         return doorSlot;
      }
      return fetchDoorSlot();
   }

   public int fetchDoorSlot() {
      prtLock.lock();
      try {
         doorSlot = (party == null) ? 0 : party.getPartyDoor(this.getId());
         return doorSlot;
      } finally {
         prtLock.unlock();
      }
   }

   public byte getSlots(int type) {
      return type == MapleInventoryType.CASH.getType() ? 96 : inventory[type].getSlotLimit();
   }

   public boolean gainSlots(int type, int slots, boolean update) {
      slots += inventory[type].getSlotLimit();
      if (slots <= 96) {
         inventory[type].setSlotLimit(slots);

         this.saveCharToDB();
         if (update) {
            client.announce(MaplePacketCreator.updateInventorySlotLimit(type, slots));
         }

         return true;
      }

      return false;
   }

   public int sellAllItemsFromName(byte invTypeId, String name) {
      //player decides from which inventory items should be sold.
      MapleInventoryType type = MapleInventoryType.getByType(invTypeId);

      MapleInventory inv = getInventory(type);
      inv.lockInventory();
      try {
         Item it = inv.findByName(name);
         if (it == null) {
            return (-1);
         }

         return (sellAllItemsFromPosition(ii, type, it.position()));
      } finally {
         inv.unlockInventory();
      }
   }

   private int sellAllItemsFromPosition(MapleItemInformationProvider ii, MapleInventoryType type, short pos) {
      int mesoGain = 0;

      MapleInventory inv = getInventory(type);
      inv.lockInventory();
      try {
         for (short i = pos; i <= inv.getSlotLimit(); i++) {
            if (inv.getItem(i) == null) {
               continue;
            }
            mesoGain += standaloneSell(getClient(), ii, type, i, inv.getItem(i).quantity());
         }
      } finally {
         inv.unlockInventory();
      }

      return (mesoGain);
   }

   private int standaloneSell(MapleClient c, MapleItemInformationProvider ii, MapleInventoryType type, short slot, short quantity) {
      if (quantity == 0xFFFF || quantity == 0) {
         quantity = 1;
      }

      MapleInventory inv = getInventory(type);
      inv.lockInventory();
      try {
         Item item = inv.getItem(slot);
         if (item == null) { //Basic check
            return (0);
         }

         int itemid = item.id();
         if (ItemConstants.isRechargeable(itemid)) {
            quantity = item.quantity();
         } else if (ItemConstants.isWeddingToken(itemid) || ItemConstants.isWeddingRing(itemid)) {
            return (0);
         }

         if (quantity < 0) {
            return (0);
         }
         short iQuant = item.quantity();
         if (iQuant == 0xFFFF) {
            iQuant = 1;
         }

         if (quantity <= iQuant && iQuant > 0) {
            MapleInventoryManipulator.removeFromSlot(c, type, (byte) slot, quantity, false);
            int recvMesos = ii.getPrice(itemid, quantity);
            if (recvMesos > 0) {
               gainMeso(recvMesos, false);
               return (recvMesos);
            }
         }

         return (0);
      } finally {
         inv.unlockInventory();
      }
   }

   private List<Equip> getUpgradeableEquipped() {
      List<Equip> list = new LinkedList<>();

      for (Item item : getInventory(MapleInventoryType.EQUIPPED)) {
         if (ii.isUpgradeable(item.id())) {
            list.add((Equip) item);
         }
      }

      return list;
   }

   public boolean mergeAllItemsFromName(String name) {
      MapleInventoryType type = MapleInventoryType.EQUIP;

      MapleInventory inv = getInventory(type);
      inv.lockInventory();
      try {
         Item it = inv.findByName(name);
         if (it == null) {
            return false;
         }

         Map<StatUpgrade, Float> statups = new LinkedHashMap<>();
         mergeAllItemsFromPosition(statups, it.position());

         List<Pair<Equip, Map<StatUpgrade, Short>>> upgradeableEquipped = new LinkedList<>();
         Map<Equip, List<Pair<StatUpgrade, Integer>>> equipUpgrades = new LinkedHashMap<>();
         for (Equip eq : getUpgradeableEquipped()) {
            upgradeableEquipped.add(new Pair<>(eq, eq.getStats()));
            equipUpgrades.put(eq, new LinkedList<>());
         }

            /*
            for (Entry<StatUpgrade, Float> es : statups.entrySet()) {
                System.out.println(es);
            }
            */

         for (Entry<StatUpgrade, Float> e : statups.entrySet()) {
            double ev = Math.sqrt(e.getValue());

            Set<Equip> extraEquipped = new LinkedHashSet<>(equipUpgrades.keySet());
            List<Equip> statEquipped = getEquipsWithStat(upgradeableEquipped, e.getKey());
            float extraRate = (float) (0.2 * Math.random());

            if (!statEquipped.isEmpty()) {
               float statRate = 1.0f - extraRate;

               int statup = (int) Math.ceil((ev * statRate) / statEquipped.size());
               for (Equip statEq : statEquipped) {
                  equipUpgrades.get(statEq).add(new Pair<>(e.getKey(), statup));
                  extraEquipped.remove(statEq);
               }
            }

            if (!extraEquipped.isEmpty()) {
               int statup = (int) Math.round((ev * extraRate) / extraEquipped.size());
               if (statup > 0) {
                  for (Equip extraEq : extraEquipped) {
                     equipUpgrades.get(extraEq).add(new Pair<>(e.getKey(), statup));
                  }
               }
            }
         }

         MessageBroadcaster.getInstance().sendServerNotice(this, ServerNoticeType.LIGHT_BLUE, "EQUIPMENT MERGE operation results:");
         for (Entry<Equip, List<Pair<StatUpgrade, Integer>>> eqpUpg : equipUpgrades.entrySet()) {
            List<Pair<StatUpgrade, Integer>> eqpStatups = eqpUpg.getValue();
            if (!eqpStatups.isEmpty()) {
               Equip eqp = eqpUpg.getKey();
               ItemProcessor.getInstance().setMergeFlag(eqp);

               String showStr = " '" + MapleItemInformationProvider.getInstance().getName(eqp.id()) + "': ";
               String upgdStr = eqp.gainStats(eqpStatups).getLeft();

               this.forceUpdateItem(eqp);

               showStr += upgdStr;
               MessageBroadcaster.getInstance().sendServerNotice(this, ServerNoticeType.LIGHT_BLUE, showStr);
            }
         }

         return true;
      } finally {
         inv.unlockInventory();
      }
   }

   private void mergeAllItemsFromPosition(Map<StatUpgrade, Float> statups, short pos) {
      MapleInventory inv = getInventory(MapleInventoryType.EQUIP);
      inv.lockInventory();
      try {
         for (short i = pos; i <= inv.getSlotLimit(); i++) {
            standaloneMerge(statups, getClient(), MapleInventoryType.EQUIP, i, inv.getItem(i));
         }
      } finally {
         inv.unlockInventory();
      }
   }

   private void standaloneMerge(Map<StatUpgrade, Float> statups, MapleClient c, MapleInventoryType type, short slot, Item item) {
      short quantity;
      if (item == null || (quantity = item.quantity()) < 1 || ii.isCash(item.id()) || !ii.isUpgradeable(item.id()) || ItemProcessor.getInstance().hasMergeFlag(item)) {
         return;
      }

      Equip e = (Equip) item;
      for (Entry<StatUpgrade, Short> s : e.getStats().entrySet()) {
         Float newVal = statups.get(s.getKey());

         float incVal = s.getValue().floatValue();
         switch (s.getKey()) {
            case incPAD:
            case incMAD:
            case incPDD:
            case incMDD:
               incVal = (float) Math.log(incVal);
               break;
         }

         if (newVal != null) {
            newVal += incVal;
         } else {
            newVal = incVal;
         }

         statups.put(s.getKey(), newVal);
      }

      MapleInventoryManipulator.removeFromSlot(c, type, (byte) slot, quantity, false);
   }

   public void shiftPetsRight() {
      petLock.lock();
      try {
         if (pets[2] == null) {
            pets[2] = pets[1];
            pets[1] = pets[0];
            pets[0] = null;
         }
      } finally {
         petLock.unlock();
      }
   }

   private long getDojoTimeLeft() {
      return client.getChannelServer().getDojoFinishTime(map.getId()) - Server.getInstance().getCurrentTime();
   }

   public void showDojoClock() {
      if (map.isDojoFightMap()) {
         client.announce(MaplePacketCreator.getClock((int) (getDojoTimeLeft() / 1000)));
      }
   }

   public void timeoutFromDojo() {
      if (map.isDojoMap()) {
         client.getPlayer().changeMap(client.getChannelServer().getMapFactory().getMap(925020002));
      }
   }

   public void showUnderleveledInfo(MapleMonster mob) {
      long curTime = Server.getInstance().getCurrentTime();
      if (nextWarningTime < curTime) {
         nextWarningTime = curTime + (60 * 1000);   // show underlevel info again after 1 minute

         showHint("You have gained #rno experience#k from defeating #e#b" + mob.getName() + "#k#n (lv. #b" + mob.getLevel() + "#k)! Take note you must have around the same level as the mob to start earning EXP from it.");
      }
   }

   public void showMapOwnershipInfo(MapleCharacter mapOwner) {
      long curTime = Server.getInstance().getCurrentTime();
      if (nextWarningTime < curTime) {
         nextWarningTime = curTime + (60 * 1000);   // show underlevel info again after 1 minute

         String medal = "";
         Item medalItem = mapOwner.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -49);
         if (medalItem != null) {
            medal = "<" + ii.getName(medalItem.id()) + "> ";
         }

         List<String> strLines = new LinkedList<>();
         strLines.add("");
         strLines.add("");
         strLines.add("");
         strLines.add(this.getClient().getChannelServer().getServerMessage().isEmpty() ? 0 : 1, "Get off my lawn!!");

         this.announce(MaplePacketCreator.getAvatarMega(mapOwner, medal, this.getClient().getChannel(), 5390006, strLines, true));
      }
   }

   public void showHint(String msg) {
      showHint(msg, 500);
   }

   public void showHint(String msg, int length) {
      client.announceHint(msg, length);
   }

   public void showNote() {
      DatabaseConnection.getInstance().withConnectionResult(connection -> NoteProvider.getInstance().getFirstNote(connection, name)).ifPresent(notes ->
            client.announce(MaplePacketCreator.showNotes(notes, notes.size())));
   }

   public void silentGiveBuffs(List<Pair<Long, PlayerBuffValueHolder>> buffs) {
      buffs.forEach(pair -> pair.getRight().effect.silentApplyBuff(this, pair.getLeft()));
   }

   public void silentPartyUpdate() {
      silentPartyUpdateInternal(getParty());
   }

   private void silentPartyUpdateInternal(MapleParty chrParty) {
      if (chrParty != null) {
         getWorldServer().updateParty(chrParty.getId(), PartyOperation.SILENT_UPDATE, getMPC());
      }
   }

   public boolean skillIsCooling(int skillId) {
      effLock.lock();
      chrLock.lock();
      try {
         return coolDowns.containsKey(skillId);
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   public void runFullnessSchedule(int petSlot) {
      MaplePet pet = getPet(petSlot);
      if (pet == null) {
         return;
      }

      int newFullness = pet.fullness() - PetDataFactory.getHunger(pet.id());
      if (newFullness <= 5) {
         pet.fullness_$eq(15);
         PetProcessor.getInstance().saveToDb(pet);
         unequipPet(pet, true);
         MessageBroadcaster.getInstance().sendServerNotice(this, ServerNoticeType.LIGHT_BLUE, "Your pet grew hungry! Treat it some pet food to keep it healthy!");
      } else {
         pet.fullness_$eq(newFullness);
         PetProcessor.getInstance().saveToDb(pet);
         Item petz = getInventory(MapleInventoryType.CASH).getItem(pet.position());
         if (petz != null) {
            forceUpdateItem(petz);
         }
      }
   }

   public boolean runTirednessSchedule() {
      if (maplemount != null) {
         int tiredness = maplemount.incrementAndGetTiredness();

         this.getMap().broadcastMessage(MaplePacketCreator.updateMount(this.getId(), maplemount, false));
         if (tiredness > 99) {
            maplemount.setTiredness(99);
            this.dispelSkill(this.getJobType() * 10000000 + 1004);
            MessageBroadcaster.getInstance().sendServerNotice(this, ServerNoticeType.LIGHT_BLUE, "Your mount grew tired! Treat it some revitalizer before riding it again!");
            return false;
         }
      }

      return true;
   }

   public void startMapEffect(String msg, int itemId) {
      startMapEffect(msg, itemId, 30000);
   }

   private void startMapEffect(String msg, int itemId, int duration) {
      final MapleMapEffect mapEffect = new MapleMapEffect(msg, itemId);
      getClient().announce(mapEffect.makeStartData());
      TimerManager.getInstance().schedule(() -> getClient().announce(mapEffect.makeDestroyData()), duration);
   }

   public void unequipPet(MaplePet pet, boolean shift_left) {
      unequipPet(pet, shift_left, false);
   }

   private void unequipPet(MaplePet pet, boolean shift_left, boolean hunger) {
      byte petIdx = this.getPetIndex(pet);
      MaplePet chrPet = this.getPet(petIdx);

      if (chrPet != null) {
         chrPet.summoned_$eq(false);
         PetProcessor.getInstance().saveToDb(chrPet);
      }

      this.getClient().getWorldServer().unregisterPetHunger(this, petIdx);
      getMap().broadcastMessage(this, MaplePacketCreator.showPet(this, pet, true, hunger), true);

      removePet(pet, shift_left);
      commitExcludedItems();

      client.announce(MaplePacketCreator.petStatUpdate(this));
      client.announce(MaplePacketCreator.enableActions());
   }

   public void updateMacros(int position, SkillMacro updateMacro) {
      skillMacros[position] = updateMacro;
   }

   public void updatePartyMemberHP() {
      prtLock.lock();
      try {
         updatePartyMemberHPInternal();
      } finally {
         prtLock.unlock();
      }
   }

   private void updatePartyMemberHPInternal() {
      if (party != null) {
         int currentMaxHp = getCurrentMaxHp();
         int currentHp = getHp();

         getPartyMembersOnSameMap()
               .forEach(character -> character.announce(MaplePacketCreator.updatePartyMemberHP(getId(), currentHp, currentMaxHp)));
      }
   }

   public String getQuestInfo(int quest) {
      MapleQuestStatus qs = getQuest(MapleQuest.getInstance(quest));
      return qs.getInfo();
   }

   public void updateQuestInfo(int quest, String info) {
      MapleQuest q = MapleQuest.getInstance(quest);
      MapleQuestStatus qs = getQuest(q);
      qs.setInfo(info);

      synchronized (quests) {
         quests.put(q.getId(), qs);
      }

      announceUpdateQuest(DelayedQuestUpdate.UPDATE, qs, false);
      if (qs.getQuest().getInfoNumber() > 0) {
         announceUpdateQuest(DelayedQuestUpdate.UPDATE, qs, true);
      }
      announce(MaplePacketCreator.updateQuestInfo(qs.getQuest().getId(), qs.getNpc()));
   }

   public void awardQuestPoint(int awardedPoints) {
      if (ServerConstants.QUEST_POINT_REQUIREMENT < 1 || awardedPoints < 1) {
         return;
      }

      int delta;
      synchronized (quests) {
         questFame += awardedPoints;

         delta = questFame / ServerConstants.QUEST_POINT_REQUIREMENT;
         questFame %= ServerConstants.QUEST_POINT_REQUIREMENT;
      }

      if (delta > 0) {
         gainFame(delta);
      }
   }

   public enum DelayedQuestUpdate {    // quest updates allow player actions during NPC talk...
      UPDATE, FORFEIT, COMPLETE
   }

   private void announceUpdateQuestInternal(Pair<DelayedQuestUpdate, Object[]> questUpdate) {
      Object[] objs = questUpdate.getRight();

      switch (questUpdate.getLeft()) {
         case UPDATE:
            announce(MaplePacketCreator.updateQuest((MapleQuestStatus) objs[0], (Boolean) objs[1]));
            break;

         case FORFEIT:
            announce(MaplePacketCreator.forfeitQuest((Short) objs[0]));
            break;

         case COMPLETE:
            announce(MaplePacketCreator.completeQuest((Short) objs[0], (Long) objs[1]));
            break;
      }
   }

   public void announceUpdateQuest(DelayedQuestUpdate questUpdateType, Object... params) {
      Pair<DelayedQuestUpdate, Object[]> p = new Pair<>(questUpdateType, params);
      MapleClient c = this.getClient();
      if (c.getQM() != null || c.getCM() != null) {
         synchronized (npcUpdateQuests) {
            npcUpdateQuests.add(p);
         }
      } else {
         announceUpdateQuestInternal(p);
      }
   }

   public void flushDelayedUpdateQuests() {
      List<Pair<DelayedQuestUpdate, Object[]>> qmQuestUpdateList;

      synchronized (npcUpdateQuests) {
         qmQuestUpdateList = new ArrayList<>(npcUpdateQuests);
         npcUpdateQuests.clear();
      }

      for (Pair<DelayedQuestUpdate, Object[]> q : qmQuestUpdateList) {
         announceUpdateQuestInternal(q);
      }
   }

   public void updateQuest(MapleQuestStatus quest) {
      synchronized (quests) {
         quests.put(quest.getQuestID(), quest);
      }
      if (quest.getStatus().equals(MapleQuestStatus.Status.STARTED)) {
         announceUpdateQuest(DelayedQuestUpdate.UPDATE, quest, false);
         if (quest.getQuest().getInfoNumber() > 0) {
            announceUpdateQuest(DelayedQuestUpdate.UPDATE, quest, true);
         }
         announce(MaplePacketCreator.updateQuestInfo(quest.getQuest().getId(), quest.getNpc()));
      } else if (quest.getStatus().equals(MapleQuestStatus.Status.COMPLETED)) {
         MapleQuest mquest = quest.getQuest();
         short questid = mquest.getId();
         if (!mquest.isSameDayRepeatable() && !MapleQuest.isExploitableQuest(questid)) {
            awardQuestPoint(ServerConstants.QUEST_POINT_PER_QUEST_COMPLETE);
         }
         quest.setCompleted(quest.getCompleted() + 1);   // count quest completed Jayd's idea

         announceUpdateQuest(DelayedQuestUpdate.COMPLETE, questid, quest.getCompletionTime());
      } else if (quest.getStatus().equals(MapleQuestStatus.Status.NOT_STARTED)) {
         announceUpdateQuest(DelayedQuestUpdate.UPDATE, quest, false);
         if (quest.getQuest().getInfoNumber() > 0) {
            announceUpdateQuest(DelayedQuestUpdate.UPDATE, quest, true);
         }
      }
   }

   private void expireQuest(MapleQuest quest) {
      if (getQuestStatus(quest.getId()) == MapleQuestStatus.Status.COMPLETED.getId()) {
         return;
      }
      if (System.currentTimeMillis() < getMapleQuestStatus(quest.getId()).getExpirationTime()) {
         return;
      }

      announce(MaplePacketCreator.questExpire(quest.getId()));
      MapleQuestStatus newStatus = new MapleQuestStatus(quest, MapleQuestStatus.Status.NOT_STARTED);
      newStatus.setForfeited(getQuest(quest).getForfeited() + 1);
      updateQuest(newStatus);
   }

   public void cancelQuestExpirationTask() {
      evtLock.lock();
      try {
         scheduler.cancel(MapleCharacterScheduler.Type.QUEST_EXPIRE);
      } finally {
         evtLock.unlock();
      }
   }

   public void forfeitExpirableQuests() {
      evtLock.lock();
      try {
         for (MapleQuest quest : questExpirations.keySet()) {
            quest.forfeit(this);
         }

         questExpirations.clear();
      } finally {
         evtLock.unlock();
      }
   }

   public void questExpirationTask() {
      evtLock.lock();
      try {
         if (!questExpirations.isEmpty()) {
            scheduler.addIfNotExists(MapleCharacterScheduler.Type.QUEST_EXPIRE, this::runQuestExpireTask, 10 * 1000);
         }
      } finally {
         evtLock.unlock();
      }
   }

   private void runQuestExpireTask() {
      evtLock.lock();
      try {
         long timeNow = Server.getInstance().getCurrentTime();
         List<MapleQuest> expireList = new LinkedList<>();

         for (Entry<MapleQuest, Long> qe : questExpirations.entrySet()) {
            if (qe.getValue() <= timeNow) {
               expireList.add(qe.getKey());
            }
         }

         if (!expireList.isEmpty()) {
            for (MapleQuest quest : expireList) {
               expireQuest(quest);
               questExpirations.remove(quest);
            }

            if (questExpirations.isEmpty()) {
               scheduler.cancel(MapleCharacterScheduler.Type.QUEST_EXPIRE);
            }
         }
      } finally {
         evtLock.unlock();
      }
   }

   private void registerQuestExpire(MapleQuest quest, long time) {
      evtLock.lock();
      try {
         scheduler.addIfNotExists(MapleCharacterScheduler.Type.QUEST_EXPIRE, this::runQuestExpireTask, 10 * 1000);

         questExpirations.put(quest, Server.getInstance().getCurrentTime() + time);
      } finally {
         evtLock.unlock();
      }
   }

   public void questTimeLimit(final MapleQuest quest, int seconds) {
      registerQuestExpire(quest, seconds * 1000);
      announce(MaplePacketCreator.addQuestTimeLimit(quest.getId(), seconds * 1000));
   }

   public void questTimeLimit2(final MapleQuest quest, long expires) {
      long timeLeft = expires - System.currentTimeMillis();

      if (timeLeft <= 0) {
         expireQuest(quest);
      } else {
         registerQuestExpire(quest, timeLeft);
      }
   }

   public void updateSingleStat(MapleStat stat, int newval) {
      updateSingleStat(stat, newval, false);
   }

   private void updateSingleStat(MapleStat stat, int newval, boolean itemReaction) {
      announce(MaplePacketCreator.updatePlayerStats(Collections.singletonList(new Pair<>(stat, newval)), itemReaction, this));
   }

   public void announce(final byte[] packet) {
      client.announce(packet);
   }

   @Override
   public int getObjectId() {
      return getId();
   }

   @Override
   public void setObjectId(int id) {
   }

   @Override
   public MapleMapObjectType getType() {
      return MapleMapObjectType.PLAYER;
   }

   @Override
   public void sendDestroyData(MapleClient client) {
      client.announce(MaplePacketCreator.removePlayerFromMap(this.getObjectId()));
   }

   @Override
   public void sendSpawnData(MapleClient client) {
      if (!this.isHidden() || client.getPlayer().gmLevel() > 1) {
         client.announce(MaplePacketCreator.spawnPlayerMapObject(client, this, false));

         if (buffEffects.containsKey(ChairProcessor.getInstance().getJobMapChair(job))) { // mustn't effLock, chrLock this function
            client.announce(MaplePacketCreator.giveForeignChairSkillEffect(id));
         }
      }

      if (this.isHidden()) {
         List<Pair<MapleBuffStat, Integer>> dsstat = Collections.singletonList(new Pair<>(MapleBuffStat.DARKSIGHT, 0));
         getMap().broadcastGMMessage(this, MaplePacketCreator.giveForeignBuff(getId(), dsstat), false);
      }
   }

   @Override
   public String toString() {
      return name;
   }

   public int getLinkedLevel() {
      return linkedLevel;
   }

   public String getLinkedName() {
      return linkedName;
   }

   public CashShop getCashShop() {
      return cashshop;
   }

   public Set<NewYearCardRecord> getNewYearRecords() {
      return newyears;
   }

   public Set<NewYearCardRecord> getReceivedNewYearRecords() {
      return newyears.stream().filter(NewYearCardRecord::isReceiverCardReceived).collect(Collectors.toSet());
   }

   public NewYearCardRecord getNewYearRecord(int cardid) {
      return newyears.stream()
            .filter(newYearCardRecord -> newYearCardRecord.getId() == cardid)
            .findFirst()
            .orElse(null);
   }

   public void addNewYearRecord(NewYearCardRecord newyear) {
      newyears.add(newyear);
   }

   public void removeNewYearRecord(NewYearCardRecord newyear) {
      newyears.remove(newyear);
   }

   public long portalDelay() {
      return portaldelay;
   }

   public void blockPortal(String scriptName) {
      if (!blockedPortals.contains(scriptName) && scriptName != null) {
         blockedPortals.add(scriptName);
         client.announce(MaplePacketCreator.enableActions());
      }
   }

   public void unblockPortal(String scriptName) {
      if (blockedPortals.contains(scriptName) && scriptName != null) {
         blockedPortals.remove(scriptName);
      }
   }

   public List<String> getBlockedPortals() {
      return blockedPortals;
   }

   public boolean containsAreaInfo(int area, String info) {
      Short area_ = (short) area;
      if (area_info.containsKey(area_)) {
         return area_info.get(area_).contains(info);
      }
      return false;
   }

   public void updateAreaInfo(int area, String info) {
      area_info.put((short) area, info);
      announce(MaplePacketCreator.updateAreaInfo(area, info));
   }

   public Map<Short, String> getAreaInfos() {
      return area_info;
   }

   public void autoban(String reason) {
      if (this.isGM() || this.isBanned()) {  // thanks RedHat for noticing GM's being able to get banned
         return;
      }

      this.ban(reason);
      announce(MaplePacketCreator.sendPolice(String.format("You have been blocked by the#b %s Police for HACK reason.#k", "HeavenMS")));
      TimerManager.getInstance().schedule(() -> client.disconnect(false, false), 5000);

      MessageBroadcaster.getInstance().sendWorldServerNotice(world, ServerNoticeType.LIGHT_BLUE, MapleCharacter::isGM, StringUtil.makeMapleReadable(this.name) + " was autobanned for " + reason);
   }

   public void block(int reason, int days, String desc) {
      DatabaseConnection.getInstance().withConnection(connection -> AccountAdministrator.getInstance().setBan(connection, accountid, reason, days, desc));
   }

   public boolean isBanned() {
      return isbanned;
   }

   public List<Integer> getTrockMaps() {
      return trockmaps;
   }

   public List<Integer> getVipTrockMaps() {
      return viptrockmaps;
   }

   public void deleteFromTrocks(int map) {
      trockmaps.remove(Integer.valueOf(map));
      while (trockmaps.size() < 10) {
         trockmaps.add(999999999);
      }
   }

   public void addTrockMap() {
      int index = trockmaps.indexOf(999999999);
      if (index != -1) {
         trockmaps.set(index, getMapId());
      }
   }

   public void deleteFromVipTrocks(int map) {
      viptrockmaps.remove(Integer.valueOf(map));
      while (viptrockmaps.size() < 10) {
         viptrockmaps.add(999999999);
      }
   }

   public void addVipTrockMap() {
      int index = viptrockmaps.indexOf(999999999);
      if (index != -1) {
         viptrockmaps.set(index, getMapId());
      }
   }

   public AutobanManager getAutobanManager() {
      return autoban;
   }

   public void equippedItem(Equip equip) {
      int itemid = equip.id();

      if (itemid == 1122017) {
         this.equipPendantOfSpirit();
      } else if (itemid == 1812000) { // meso magnet
         equippedMesoMagnet = true;
      } else if (itemid == 1812001) { // item pouch
         equippedItemPouch = true;
      } else if (itemid == 1812007) { // item ignore pendant
         equippedPetItemIgnore = true;
      }
   }

   public void unequippedItem(Equip equip) {
      int itemid = equip.id();

      if (itemid == 1122017) {
         this.unequipPendantOfSpirit();
      } else if (itemid == 1812000) { // meso magnet
         equippedMesoMagnet = false;
      } else if (itemid == 1812001) { // item pouch
         equippedItemPouch = false;
      } else if (itemid == 1812007) { // item ignore pendant
         equippedPetItemIgnore = false;
      }
   }

   public boolean isEquippedMesoMagnet() {
      return equippedMesoMagnet;
   }

   public boolean isEquippedItemPouch() {
      return equippedItemPouch;
   }

   public boolean isEquippedPetItemIgnore() {
      return equippedPetItemIgnore;
   }

   private void equipPendantOfSpirit() {
      scheduler.addIfNotExists(MapleCharacterScheduler.Type.PENDANT_OF_SPIRIT, () -> {
         if (pendantExp < 3) {
            pendantExp++;
            MessageBroadcaster.getInstance().sendServerNotice(MapleCharacter.this, ServerNoticeType.PINK_TEXT, "Pendant of the Spirit has been equipped for " + pendantExp + " hour(s), you will now receive " + pendantExp + "0% bonus exp.");
         } else {
            scheduler.cancel(MapleCharacterScheduler.Type.PENDANT_OF_SPIRIT);
         }
      }, 3600000); //1 hour
   }

   private void unequipPendantOfSpirit() {
      scheduler.cancel(MapleCharacterScheduler.Type.PENDANT_OF_SPIRIT);
      pendantExp = 0;
   }

   private Collection<Item> getUpgradeableEquipList() {
      Collection<Item> fullList = getInventory(MapleInventoryType.EQUIPPED).list();
      if (ServerConstants.USE_EQUIPMNT_LVLUP_CASH) {
         return fullList;
      }
      return fullList.stream()
            .filter(item -> !ii.isCash(item.id()))
            .collect(Collectors.toCollection(LinkedHashSet::new));
   }

   public void increaseEquipExp(int expGain) {
      getUpgradeableEquipList().stream()
            .map(item -> (Equip) item)
            .filter(equip -> ii.getName(equip.id()) != null)
            .forEach(equip -> ItemProcessor.getInstance().gainItemExp(equip, client, expGain));
   }

   public void showAllEquipFeatures() {
      StringBuilder showMsg = new StringBuilder();

      getInventory(MapleInventoryType.EQUIPPED).list().stream()
            .map(item -> (Equip) item)
            .filter(equip -> ii.getName(equip.id()) != null)
            .forEach(equip -> showMsg.append(ItemProcessor.getInstance().showEquipFeatures(equip)));

      if (showMsg.length() > 0) {
         this.showHint("#ePLAYER EQUIPMENTS:#n\r\n\r\n" + showMsg, 400);
      }
   }

   public void broadcastMarriageMessage() {
      getGuild().ifPresent(guild -> guild.broadcast(MaplePacketCreator.marriageMessage(0, name)));

      MapleFamily family = this.getFamily();
      if (family != null) {
         MapleFamilyProcessor.getInstance().broadcast(MaplePacketCreator.marriageMessage(1, name));
      }
   }

   public Map<String, MapleEvents> getEvents() {
      return events;
   }

   public PartyQuest getPartyQuest() {
      return partyQuest;
   }

   public void setPartyQuest(PartyQuest pq) {
      this.partyQuest = pq;
   }

   public void setCpqTimer(ScheduledFuture timer) {
      scheduler.add(MapleCharacterScheduler.Type.CARNIVAL_PQ, timer);
   }

   public void clearCpqTimer() {
      scheduler.cancel(MapleCharacterScheduler.Type.CARNIVAL_PQ);
   }

   public final void empty(final boolean remove) {
      scheduler.cancelAll();

      unregisterChairBuff();

      evtLock.lock();

      if (maplemount != null) {
         maplemount.empty();
         maplemount = null;
      }
      if (remove) {
         partyQuest = null;
         events = null;
         mpc = null;
         mgc = null;
         party = null;
         MapleFamilyEntry familyEntry = getFamilyEntry();
         if (familyEntry != null) {
            familyEntry.setCharacter(null);
            setFamilyEntry(null);
         }

         getWorldServer().registerTimedMapObject(() -> {
            client = null;  // clients still triggers handlers a few times after disconnecting
            map = null;
            setListener(null);
         }, 5 * 60 * 1000);
      }
   }

   public void logOff() {
      this.loggedIn = false;
      DatabaseConnection.getInstance().withConnection(connection -> CharacterAdministrator.getInstance().logCharacterOut(connection, id));
   }

   public void setLoginTime(long time) {
      this.loginTime = time;
   }

   public long getLoggedInTime() {
      return System.currentTimeMillis() - loginTime;
   }

   public boolean isLoggedin() {
      return loggedIn;
   }

   public boolean getWhiteChat() {
      return isGM() && whiteChat;
   }

   public void toggleWhiteChat() {
      whiteChat = !whiteChat;
   }

   public boolean gotPartyQuestItem(String partyquestchar) {
      return dataString.contains(partyquestchar);
   }

   public void removePartyQuestItem(String letter) {
      if (gotPartyQuestItem(letter)) {
         dataString = dataString.substring(0, dataString.indexOf(letter)) + dataString.substring(dataString.indexOf(letter) + letter.length());
      }
   }

   public void setPartyQuestItemObtained(String partyquestchar) {
      if (!dataString.contains(partyquestchar)) {
         this.dataString += partyquestchar;
      }
   }

   public void createDragon() {
      dragon = new MapleDragon(this);
   }

   public MapleDragon getDragon() {
      return dragon;
   }

   public long getJailExpirationTimeLeft() {
      return jailExpiration - System.currentTimeMillis();
   }

   private void setFutureJailExpiration(long time) {
      jailExpiration = System.currentTimeMillis() + time;
   }

   public void addJailExpirationTime(long time) {
      long timeLeft = getJailExpirationTimeLeft();

      if (timeLeft <= 0) {
         setFutureJailExpiration(time);
      } else {
         setFutureJailExpiration(timeLeft + time);
      }
   }

   public void removeJailExpirationTime() {
      jailExpiration = 0;
   }

   public boolean registerNameChange(String newName) {
      long currentTimeMillis = System.currentTimeMillis();
      DatabaseConnection.getInstance().withConnection(connection -> {
         Optional<Timestamp> completionTime = NameChangeProvider.getInstance().getCompletionTimeByCharacterId(connection, getId());
         if (completionTime.isEmpty()) {
            return;
         }

         if (completionTime.get().getTime() + ServerConstants.NAME_CHANGE_COOLDOWN > currentTimeMillis) {
            return;
         }

         NameChangeAdministrator.getInstance().create(connection, getId(), getName(), newName);
      });
      return true;
   }

   public boolean cancelPendingNameChange() {
      DatabaseConnection.getInstance().withConnection(connection -> NameChangeAdministrator.getInstance().cancelPendingNameChange(connection, getId()));
      return true;
   }

   public void doPendingNameChange() { //called on logout
      if (!pendingNameChange) {
         return;
      }

      DatabaseConnection.getInstance().withConnection(connection ->
            NameChangeProvider.getInstance().getPendingNameChangeForCharacter(connection, getId()).ifPresent(result -> {
               CharacterAdministrator.getInstance().performNameChange(connection, result.characterId(), result.oldName(), result.newName(), result.id());
               FilePrinter.print(FilePrinter.CHANGE_CHARACTER_NAME, "Name change applied : from \"" + getName() + "\" to \"" + result.newName() + "\" at " + Calendar.getInstance().getTime().toString());
            }));
   }

   public int checkWorldTransferEligibility() {
      if (getLevel() < 20) {
         return 2;
      } else if (getClient().getTempBanCalendar() != null && getClient().getTempBanCalendar().getTimeInMillis() + (30 * 24 * 60 * 60 * 1000) < Calendar.getInstance().getTimeInMillis()) {
         return 3;
      } else if (isMarried()) {
         return 4;
      } else if (getGuildRank() < 2) {
         return 5;
      } else if (getFamily() != null) {
         return 8;
      } else {
         return 0;
      }
   }

   public boolean registerWorldTransfer(int newWorld) {
      long currentTimeMillis = System.currentTimeMillis();
      DatabaseConnection.getInstance().withConnection(connection -> {
         Timestamp completionTime = WorldTransferProvider.getInstance().getCompletionTimeByCharacterId(connection, getId());
         if (completionTime == null) {
            return;
         }

         if (completionTime.getTime() + ServerConstants.WORLD_TRANSFER_COOLDOWN > currentTimeMillis) {
            return;
         }

         WorldTransferAdministrator.getInstance().create(connection, getId(), getWorld(), newWorld);
      });
      return true;
   }

   public boolean cancelPendingWorldTranfer() {
      DatabaseConnection.getInstance().withConnection(connection -> WorldTransferAdministrator.getInstance().cancelPendingForCharacter(connection, getId()));
      return true;
   }

   public String getLastCommandMessage() {
      return this.commandtext;
   }

   public void setLastCommandMessage(String text) {
      this.commandtext = text;
   }

   public int getRewardPoints() {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> AccountProvider.getInstance().getRewardPoints(connection, accountid)).orElse(0);
   }

   public void setRewardPoints(int value) {
      DatabaseConnection.getInstance().withConnection(connection -> AccountAdministrator.getInstance().setRewardPoints(connection, accountid, value));
   }

   private void addReborns() {
      setReborns(getReborns() + 1);
   }

   public int getReborns() {
      if (!ServerConstants.USE_REBIRTH_SYSTEM) {
         yellowMessage("Rebirth system is not enabled!");
         throw new NotEnabledException();
      }
      return DatabaseConnection.getInstance().withConnectionResult(connection -> CharacterProvider.getInstance().countReborns(connection, id)).orElse(0);
   }

   private void setReborns(int value) {
      if (!ServerConstants.USE_REBIRTH_SYSTEM) {
         yellowMessage("Rebirth system is not enabled!");
         throw new NotEnabledException();
      }
      DatabaseConnection.getInstance().withConnection(connection -> CharacterAdministrator.getInstance().setReborns(connection, id, value));
   }

   public void executeReborn() {
      if (!ServerConstants.USE_REBIRTH_SYSTEM) {
         yellowMessage("Rebirth system is not enabled!");
         throw new NotEnabledException();
      }
      if (getLevel() != 200) {
         return;
      }
      addReborns();
      changeJob(MapleJob.BEGINNER);
      setLevel(0);
      levelUp(true);
   }

   public byte getTeam() {
      return team;
   }

   public void setTeam(int team) {
      this.team = (byte) team;
   }

   public MapleOla getOla() {
      return ola;
   }

   public void setOla(MapleOla ola) {
      this.ola = ola;
   }

   public MapleFitness getFitness() {
      return fitness;
   }

   public void setFitness(MapleFitness fit) {
      this.fitness = fit;
   }

   public long getLastSnowballAttack() {
      return snowballattack;
   }

   public void setLastSnowballAttack(long time) {
      this.snowballattack = time;
   }

   public void gainFestivalPoints(int gain) {
      this.FestivalPoints += gain;
   }

   public int getFestivalPoints() {
      return this.FestivalPoints;
   }

   public void setFestivalPoints(int points) {
      this.FestivalPoints = points;
   }

   public int getCP() {
      return cp;
   }

   private void setCP(int a) {
      this.cp = a;
   }

   public void addCP(int amount) {
      totalCP += amount;
      availableCP += amount;
   }

   public void useCP(int amount) {
      availableCP -= amount;
   }

   public void gainCP(int gain) {
      if (this.getMonsterCarnival() != null) {
         if (gain > 0) {
            this.setTotalCP(this.getTotalCP() + gain);
         }
         this.setCP(this.getCP() + gain);
         if (this.getParty() != null) {
            this.getMonsterCarnival().setCP(this.getMonsterCarnival().getCP(team) + gain, team);
            if (gain > 0) {
               this.getMonsterCarnival().setTotalCP(this.getMonsterCarnival().getTotalCP(team) + gain, team);
            }
         }
         if (this.getCP() > this.getTotalCP()) {
            this.setTotalCP(this.getCP());
         }
         this.getClient().announce(MaplePacketCreator.CPUpdate(false, this.getCP(), this.getTotalCP(), getTeam()));
         if (this.getParty() != null && getTeam() != -1) {
            this.getMap().broadcastMessage(MaplePacketCreator.CPUpdate(true, this.getMonsterCarnival().getCP(team), this.getMonsterCarnival().getTotalCP(team), getTeam()));
         }
      }
   }

   public int getTotalCP() {
      return totCP;
   }

   private void setTotalCP(int a) {
      this.totCP = a;
   }

   public void resetCP() {
      this.cp = 0;
      this.totCP = 0;
      this.monsterCarnival = null;
   }

   public MonsterCarnival getMonsterCarnival() {
      return monsterCarnival;
   }

   public void setMonsterCarnival(MonsterCarnival monsterCarnival) {
      this.monsterCarnival = monsterCarnival;
   }

   public AriantColiseum getAriantColiseum() {
      return ariantColiseum;
   }

   public void setAriantColiseum(AriantColiseum ariantColiseum) {
      this.ariantColiseum = ariantColiseum;
   }

   public void setMonsterCarnivalParty(MonsterCarnivalParty mcp) {
      this.monsterCarnivalParty = mcp;
   }

   public boolean canBeChallenged() {
      return !challenged;
   }

   public void setChallenged(boolean challenged) {
      this.challenged = challenged;
   }

   public void setAriantPoints(int ariantPoints) {
      this.ariantPoints = ariantPoints;
   }

   public void gainAriantPoints(int points) {
      this.ariantPoints += points;
   }

   public int getAriantPoints() {
      return this.ariantPoints;
   }

   public int getLanguage() {
      return getClient().getLanguage();
   }

   public enum FameStatus {
      OK, NOT_TODAY, NOT_THIS_MONTH
   }
}
