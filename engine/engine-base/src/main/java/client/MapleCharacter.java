package client;

import java.awt.Point;
import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;

import org.apache.mina.util.ConcurrentHashSet;

import client.autoban.AutoBanManager;
import client.database.data.GameData;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryProof;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.MapleWeaponType;
import client.inventory.ModifyInventory;
import client.inventory.StatUpgrade;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.keybind.MapleQuickSlotBinding;
import client.newyear.NewYearCardRecord;
import client.processor.BuffStatProcessor;
import client.processor.CashShopProcessor;
import client.processor.ChairProcessor;
import client.processor.CharacterProcessor;
import client.processor.ItemProcessor;
import client.processor.MapleFamilyProcessor;
import client.processor.MapleJobProcessor;
import client.processor.PartyProcessor;
import client.processor.PetProcessor;
import client.processor.SkillProcessor;
import client.processor.action.PetAutoPotProcessor;
import config.YamlConfig;
import constants.game.ExpTable;
import constants.game.GameConstants;
import constants.inventory.ItemConstants;
import constants.skills.Aran;
import constants.skills.Bishop;
import constants.skills.BlazeWizard;
import constants.skills.BowMaster;
import constants.skills.Brawler;
import constants.skills.Buccaneer;
import constants.skills.Corsair;
import constants.skills.Crusader;
import constants.skills.DarkKnight;
import constants.skills.DawnWarrior;
import constants.skills.Evan;
import constants.skills.FirePoisonArchMage;
import constants.skills.Hero;
import constants.skills.IceLighteningArchMagician;
import constants.skills.Magician;
import constants.skills.Marauder;
import constants.skills.Marksman;
import constants.skills.NightLord;
import constants.skills.Paladin;
import constants.skills.Priest;
import constants.skills.Shadower;
import constants.skills.ThunderBreaker;
import constants.skills.Warrior;
import database.DatabaseConnection;
import database.administrator.AccountAdministrator;
import database.administrator.AreaInfoAdministrator;
import database.administrator.CharacterAdministrator;
import database.administrator.CoolDownAdministrator;
import database.administrator.EventStatAdministrator;
import database.administrator.FameLogAdministrator;
import database.administrator.GuildAdministrator;
import database.administrator.KeyMapAdministrator;
import database.administrator.MedalMapAdministrator;
import database.administrator.NameChangeAdministrator;
import database.administrator.PetIgnoreAdministrator;
import database.administrator.PlayerDiseaseAdministrator;
import database.administrator.QuestProgressAdministrator;
import database.administrator.QuestStatusAdministrator;
import database.administrator.SavedLocationAdministrator;
import database.administrator.SkillAdministrator;
import database.administrator.SkillMacroAdministrator;
import database.administrator.TeleportRockLocationAdministrator;
import database.administrator.WorldTransferAdministrator;
import database.provider.AccountProvider;
import database.provider.CharacterProvider;
import database.provider.FredStorageProvider;
import database.provider.NameChangeProvider;
import database.provider.NoteProvider;
import database.provider.WorldTransferProvider;
import net.server.PlayerBuffValueHolder;
import net.server.PlayerCoolDownValueHolder;
import net.server.Server;
import net.server.SkillMacro;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.coordinator.world.MapleInviteCoordinator;
import net.server.guild.MapleAlliance;
import net.server.guild.MapleGuild;
import net.server.guild.MapleGuildCharacter;
import net.server.processor.MapleGuildProcessor;
import net.server.processor.MaplePartyProcessor;
import net.server.services.task.channel.FaceExpressionService;
import net.server.services.task.world.CharacterSaveService;
import net.server.services.type.ChannelServices;
import net.server.services.type.WorldServices;
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
import server.MapleMarriage;
import server.MapleShop;
import server.MapleStatEffect;
import server.MapleStorage;
import server.MapleTrade;
import server.MapleTradeResult;
import server.ScriptedItem;
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
import server.processor.MapleTradeProcessor;
import server.processor.QuestProcessor;
import server.processor.maps.MapleDoorProcessor;
import server.processor.maps.MapleMapObjectProcessor;
import server.quest.MapleQuest;
import tools.I18nMessage;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.MapleStringUtil;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.Randomizer;
import tools.ServerNoticeType;
import tools.SimpleMessage;
import tools.StringUtil;
import tools.exceptions.NotEnabledException;
import tools.packet.PacketInput;
import tools.packet.alliance.UpdateAllianceJobLevel;
import tools.packet.buff.CancelAbnormalStatus;
import tools.packet.buff.CancelBuff;
import tools.packet.buff.CancelForeignAbnormalStatus;
import tools.packet.buff.CancelForeignAbnormalStatusSlow;
import tools.packet.buff.CancelForeignBuff;
import tools.packet.buff.CancelForeignChairSkillEffect;
import tools.packet.buff.GiveAbnormalStatus;
import tools.packet.buff.GiveBuff;
import tools.packet.buff.GiveForeignAbnormalStatus;
import tools.packet.buff.GiveForeignAbnormalStatusSlow;
import tools.packet.buff.GiveForeignBuff;
import tools.packet.buff.GiveForeignChairSkillEffect;
import tools.packet.character.SkillCoolDown;
import tools.packet.character.SummonSkill;
import tools.packet.character.UpdateMount;
import tools.packet.character.UpdateSkill;
import tools.packet.field.obstacle.EnvironmentMoveList;
import tools.packet.field.set.WarpToMap;
import tools.packet.foreigneffect.CancelChair;
import tools.packet.foreigneffect.ShowBerserk;
import tools.packet.foreigneffect.ShowBuffEffect;
import tools.packet.foreigneffect.ShowChair;
import tools.packet.foreigneffect.ShowCombo;
import tools.packet.foreigneffect.ShowForeignEffect;
import tools.packet.foreigneffect.ShowRecovery;
import tools.packet.guild.GenericGuildMessage;
import tools.packet.inventory.InventoryFull;
import tools.packet.inventory.ModifyInventoryPacket;
import tools.packet.inventory.SlotLimitUpdate;
import tools.packet.message.GetAvatarMegaphone;
import tools.packet.message.GiveFameResponse;
import tools.packet.message.NotifyJobAdvance;
import tools.packet.message.NotifyLevelUp;
import tools.packet.message.NotifyMarriage;
import tools.packet.message.ReceiveFame;
import tools.packet.message.YellowTip;
import tools.packet.monster.carnival.MonsterCarnivalPartyPoints;
import tools.packet.monster.carnival.MonsterCarnivalPlayerDied;
import tools.packet.monster.carnival.MonsterCarnivalPointObtained;
import tools.packet.movement.MovePlayer;
import tools.packet.party.UpdateParty;
import tools.packet.party.UpdatePartyMemberHp;
import tools.packet.pet.PetExceptionList;
import tools.packet.quest.UpdateQuestInfo;
import tools.packet.quest.info.AddQuestTimeLimit;
import tools.packet.quest.info.QuestExpire;
import tools.packet.remove.RemoveDragon;
import tools.packet.remove.RemoveItem;
import tools.packet.remove.RemovePlayer;
import tools.packet.remove.RemoveSummon;
import tools.packet.report.SendPolice;
import tools.packet.showitemgaininchat.ShowOwnBerserk;
import tools.packet.showitemgaininchat.ShowOwnBuffEffect;
import tools.packet.showitemgaininchat.ShowOwnRecovery;
import tools.packet.spawn.SpawnSummon;
import tools.packet.stat.EnableActions;
import tools.packet.stat.UpdatePlayerStats;
import tools.packet.statusinfo.CompleteQuest;
import tools.packet.statusinfo.ShowEXPGain;
import tools.packet.statusinfo.ShowFameGain;
import tools.packet.statusinfo.ShowInventoryFull;
import tools.packet.statusinfo.ShowItemExpired;
import tools.packet.statusinfo.ShowItemUnavailable;
import tools.packet.statusinfo.ShowMesoGain;
import tools.packet.statusinfo.ShowQuestForfeit;
import tools.packet.statusinfo.UpdateAreaInfo;
import tools.packet.statusinfo.UpdateQuest;
import tools.packet.ui.GMEffect;
import tools.packet.ui.GetClock;
import tools.packet.ui.GetKeyMap;
import tools.packet.ui.GetMacros;
import tools.packet.ui.QuickSlotKey;
import tools.packet.ui.ShowNotes;
import tools.packet.wedding.WeddingPartnerTransfer;

public class MapleCharacter extends AbstractMapleCharacterObject {
   private static final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
   private final Map<Short, MapleQuestStatus> quests;
   private AriantColiseum ariantColiseum;
   private short totalCP, availableCP;
   private int world;
   private int accountId, id, level;
   private int rank, rankMove, jobRank, jobRankMove;
   private int gender, hair, face;
   private int fame, questFame;
   private int initialSpawnPoint;
   private int mapId;
   private int currentPage, currentType = 0, currentTab = 1;
   private int itemEffect;
   private int guildId, guildRank, allianceRank;
   private int messengerPosition = 4;
   private int slots = 0;
   private int energyBar;
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
   private long lastFameTime, lastUsedCashItem, lastExpression = 0, lastHealed, lastBuyback = 0, lastDeathTime, jailExpiration = -1;
   private transient int localStrength, localDexterity, localLuck, localIntelligence, localMagic, localWeaponAttack;
   private transient int equipMaxHp, equipMaxMp, equipStrength, equipDexterity, equipLuck, equipIntelligence, equipMagic,
         equipWeaponAttack, localChairHp, localChairMp;
   private int localChairRate;
   private boolean hidden, equipChanged = true, berserk, hasMerchant, hasSandboxItem = false, whiteChat = false,
         canRecvPartySearchInvite = true;
   private boolean equippedMesoMagnet = false, equippedItemPouch = false, equippedPetItemIgnore = false;
   private boolean usedSafetyCharm = false;
   private float autoPotHpAlert, autoPotMpAlert;
   private int linkedLevel = 0;
   private String linkedName = null;
   private boolean finishedDojoTutorial;
   private boolean usedStorage = false;
   private String name;
   private String chalkText;
   private String commandText;
   private String dataString;
   private String search = null;
   private AtomicBoolean mapTransitioning = new AtomicBoolean(true);
         // player client is currently trying to change maps or log in the game map
   private AtomicBoolean awayFromWorld = new AtomicBoolean(true);  // player is online, but on cash shop or mts
   private AtomicInteger exp = new AtomicInteger();
   private AtomicInteger gachaponExp = new AtomicInteger();
   private AtomicInteger meso = new AtomicInteger();
   private AtomicInteger chair = new AtomicInteger(-1);
   private int merchantMeso;
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
   private MapleMount mount;
   private Optional<MapleParty> party = Optional.empty();
   private MaplePet[] pets = new MaplePet[3];
   private MaplePlayerShop playerShop = null;
   private MapleShop shop = null;
   private MapleSkinColor skinColor = MapleSkinColor.NORMAL;
   private MapleStorage storage = null;
   private Optional<MapleTrade> trade = Optional.empty();
   private MonsterBook monsterbook;
   private CashShop cashshop;
   private Set<NewYearCardRecord> newYearCardRecords = new LinkedHashSet<>();
   private SavedLocation[] savedLocations;
   private SkillMacro[] skillMacros = new SkillMacro[5];
   private List<Integer> lastMonthFameIds;
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
   private Map<Integer, Map<MapleBuffStat, MapleBuffStatValueHolder>> buffEffects = new LinkedHashMap<>();
   private Map<Integer, Long> buffExpires = new LinkedHashMap<>();
   private Map<Integer, KeyBinding> keymap = new LinkedHashMap<>();
   private Map<Integer, MapleSummon> summons = new LinkedHashMap<>();
   private Map<Integer, CoolDownValueHolder> coolDowns = new LinkedHashMap<>();
   private EnumMap<MapleAbnormalStatus, Pair<AbnormalStatusValueHolder, MobSkill>> abnormalStatuses =
         new EnumMap<>(MapleAbnormalStatus.class);
   private Map<MapleAbnormalStatus, Long> abnormalStatusExpirationTimes = new LinkedHashMap<>();
   private MapleDoor pdoor = null;
   private Map<MapleQuest, Long> questExpirationTimes = new LinkedHashMap<>();
   private MapleCharacterScheduler scheduler;
   private Lock chrLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CHARACTER_CHR, true);
   private Lock evtLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CHARACTER_EVT, true);
   private Lock petLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CHARACTER_PET, true);
   private Lock prtLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CHARACTER_PRT);
   private Lock cpnLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CHARACTER_CPN);
   private Map<Integer, Set<Integer>> excluded = new LinkedHashMap<>();
   private Set<Integer> excludedItems = new LinkedHashSet<>();
   private Set<Integer> disabledPartySearchInvites = new LinkedHashSet<>();
   private long portalDelay = 0, lastCombo = 0;
   private short comboCounter = 0;
   private List<String> blockedPortals = new ArrayList<>();
   private Map<Short, String> area_info = new LinkedHashMap<>();
   private AutoBanManager autobanManager;
   private boolean isBanned = false;
   private boolean blockCashShop = false;
   private boolean allowExpGain = true;
   private byte pendantExp = 0, lastMobCount = 0, doorSlot = -1;
   private List<Integer> teleportRockMaps = new ArrayList<>();
   private List<Integer> vipTeleportRockMaps = new ArrayList<>();
   private Map<String, MapleEvents> events = new LinkedHashMap<>();
   private PartyQuest partyQuest = null;
   private List<Pair<DelayedQuestUpdate, Object[]>> npcUpdateQuests = new LinkedList<>();
   private MapleDragon dragon = null;
   private Ring marriageRing;
   private int marriageItemId = -1;
   private int partnerId = -1;
   private List<Ring> crushRings = new ArrayList<>();
   private List<Ring> friendshipRings = new ArrayList<>();
   private boolean loggedIn = false;
   private boolean useCS;  //chaos scroll upon crafting item.
   private long npcCd;
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
   private long snowballAttack;
   private MonsterCarnival monsterCarnival;
   private MonsterCarnivalParty monsterCarnivalParty = null;
   private int cp = 0;
   private int totCP = 0;
   private int FestivalPoints;
   private boolean challenged = false;
   private boolean pendingNameChange; //only used to change name on logout, not to be relied upon elsewhere
   private long loginTime;
   private byte[] quickSlotLoaded;
   private MapleQuickSlotBinding quickSlotBinding;

   public MapleCharacter(int id, int accountId, int str, int dex, int int_, int luk, int hp, int mp, int meso) {
      this();
      this.id = id;
      this.accountId = accountId;
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
         public void onHpMpPoolUpdate() {
            List<Pair<MapleStat, Integer>> hpMpUpdate = recalculateLocalStats();
            for (Pair<MapleStat, Integer> p : hpMpUpdate) {
               statUpdates.put(p.getLeft(), p.getRight());
            }

            if (hp > localMaxHp) {
               setHp(localMaxHp);
               statUpdates.put(MapleStat.HP, hp);
            }

            if (mp > localMaxMp) {
               setMp(localMaxMp);
               statUpdates.put(MapleStat.MP, mp);
            }
         }

         @Override
         public void onStatUpdate() {
            recalculateLocalStats();
         }

         @Override
         public void onAnnounceStatPoolUpdate() {
            List<Pair<MapleStat, Integer>> statup = new ArrayList<>(8);
            for (Map.Entry<MapleStat, Integer> s : statUpdates.entrySet()) {
               statup.add(new Pair<>(s.getKey(), s.getValue()));
            }
            PacketCreator.announce(client, new UpdatePlayerStats(statup, true, MapleCharacter.this));
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
      inventory[MapleInventoryType.CAN_HOLD.ordinal()] = new MapleInventoryProof(this);

      for (int i = 0; i < SavedLocationType.values().length; i++) {
         savedLocations[i] = null;
      }
      quests = new LinkedHashMap<>();
      setPosition(new Point(0, 0));
   }

   public void setMount(MapleMount mapleMount) {
      mount = mapleMount;
   }

   public void addSkill(Skill skill, SkillEntry skillEntry) {
      this.skills.put((skill), skillEntry);
   }

   public void addQuest(Short id, MapleQuestStatus questStatus) {
      this.quests.put(id, questStatus);
   }

   public void addVipTeleportRockMap(int id) {
      vipTeleportRockMaps.add(id);
   }

   public void addTeleportRockMap(int id) {
      teleportRockMaps.add(id);
   }

   public void setMessenger(MapleMessenger messenger, int position) {
      this.messenger = messenger;
      this.messengerPosition = position;
   }

   public void setInitialSpawnPoint(int spawnPoint) {
      this.initialSpawnPoint = spawnPoint;
   }

   public void initCashShop() {
      this.cashshop = CashShopProcessor.getInstance().initializeCashShop(accountId, id, getJobType());
   }

   public void initAutoBanManager() {
      autobanManager = new AutoBanManager(this);
   }

   public void setLinkedCharacterInformation(String name, int level) {
      linkedName = name;
      linkedLevel = level;
   }

   public void giveFame(int fromId, long time) {
      lastFameTime = (Math.max(lastFameTime, time));
      lastMonthFameIds.add(fromId);
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
      this.merchantMeso = merchantMeso;
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

   private static int calcTransientRatio(float transientPoint) {
      int ret = (int) transientPoint;
      return !(ret <= 0 && transientPoint > 0.0f) ? ret : 1;
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

   public boolean isLoggedInWorld() {
      return this.isLoggedIn() && !this.isAwayFromWorld();
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

   public void updatePartySearchAvailability(boolean partySearchAvailable) {
      if (partySearchAvailable) {
         if (canRecvPartySearchInvite && getParty().isEmpty()) {
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
         updatePartySearchAvailability(getParty().isEmpty());
      } else {
         this.getWorldServer().getPartySearchCoordinator().detachPlayer(this);
      }

      return canRecvPartySearchInvite;
   }

   public boolean isRecvPartySearchInviteEnabled() {
      return canRecvPartySearchInvite;
   }

   public void resetPartySearchInvite(int fromLeaderId) {
      disabledPartySearchInvites.remove(fromLeaderId);
   }

   public void disablePartySearchInvite(int fromLeaderId) {
      disabledPartySearchInvites.add(fromLeaderId);
   }

   public boolean hasDisabledPartySearchInvite(int fromLeaderId) {
      return disabledPartySearchInvites.contains(fromLeaderId);
   }

   public void setSessionTransitionState() {
      client.setCharacterOnSessionTransitionState(this.getId());
   }

   public boolean getCS() {
      return useCS;
   }

   public void setCS(boolean cs) {
      useCS = cs;
   }

   public long getNpcCoolDown() {
      return npcCd;
   }

   public void setNpcCoolDown(long d) {
      npcCd = d;
   }

   public int getOwlSearch() {
      return owlSearch;
   }

   public void setOwlSearch(int id) {
      owlSearch = id;
   }

   public void addCoolDown(int skillId, long startTime, long length) {
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

   public void updateCrushRing(Ring r) {
      crushRings = Stream.concat(crushRings.stream().filter(ring -> ring.ringId() != r.ringId()),
            Stream.of(r)).collect(Collectors.toUnmodifiableList());
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

   public void updatePlayerRing(Ring ring) {
      int ringItemId = ring.itemId();
      if (ItemConstants.isWeddingRing(ringItemId)) {
         this.updateMarriageRing(ring);
      } else if (ring.itemId() > 1112012) {
         this.updateFriendshipRing(ring);
      } else {
         this.updateCrushRing(ring);
      }
   }

   public int getMarriageItemId() {
      return marriageItemId;
   }

   public void setMarriageItemId(int itemId) {
      marriageItemId = itemId;
   }

   public int getPartnerId() {
      return partnerId;
   }

   public void setPartnerId(int partnerId) {
      this.partnerId = partnerId;
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
            return (Integer.parseInt(prop) == id || eim.getIntProperty("brideId") == id) && (mapId == 680000110
                  || mapId == 680000210);
         }
      }

      return false;
   }

   public int addDojoPointsByMap(int mapId) {
      int pts = 0;
      if (dojoPoints < 17000) {
         pts = 1 + ((mapId - 1) / 100 % 100) / 6;
         if (!GameConstants.isDojoPartyArea(this.getMapId())) {
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

   public void updateFriendshipRing(Ring r) {
      friendshipRings = Stream.concat(friendshipRings.stream().filter(ring -> ring.ringId() != r.ringId()),
            Stream.of(r)).collect(Collectors.toUnmodifiableList());
   }

   public void updateMarriageRing(Ring r) {
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
      DatabaseConnection.getInstance()
            .withConnection(connection -> AccountAdministrator.getInstance().setPermanentBan(connection, accountId, reason));
   }

   public int calculateMaxBaseDamage(int weaponAttack, MapleWeaponType weapon) {
      int mainStat, secondaryStat;
      if (getJob().isA(MapleJob.THIEF) && weapon == MapleWeaponType.DAGGER_OTHER) {
         weapon = MapleWeaponType.DAGGER_THIEVES;
      }

      if (weapon == MapleWeaponType.BOW || weapon == MapleWeaponType.CROSSBOW || weapon == MapleWeaponType.GUN) {
         mainStat = localDexterity;
         secondaryStat = localStrength;
      } else if (weapon == MapleWeaponType.CLAW || weapon == MapleWeaponType.DAGGER_THIEVES) {
         mainStat = localLuck;
         secondaryStat = localDexterity + localStrength;
      } else {
         mainStat = localStrength;
         secondaryStat = localDexterity;
      }
      return (int) Math.ceil(((weapon.getMaxDamageMultiplier() * mainStat + secondaryStat) / 100.0) * weaponAttack);
   }

   public int calculateMaxBaseDamage(int weaponAttack) {
      int maxBaseDamage;
      Item weapon_item = getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
      if (weapon_item != null) {
         maxBaseDamage = calculateMaxBaseDamage(weaponAttack, ii.getWeaponType(weapon_item.id()));
      } else {
         if (job.isA(MapleJob.PIRATE) || job.isA(MapleJob.THUNDER_BREAKER_1)) {
            double weaponMultiplier = 3;
            if (job.getId() % 100 != 0) {
               weaponMultiplier = 4.2;
            }

            int attack = (int) Math.min(Math.floor((2 * getLevel() + 31) / 3), 31);
            maxBaseDamage = (int) Math.ceil((localStrength * weaponMultiplier + localDexterity) * attack / 100.0);
         } else {
            maxBaseDamage = 1;
         }
      }
      return maxBaseDamage;
   }

   public int calculateMaxBaseMagicDamage(int magicAttack) {
      int maxBaseDamage = magicAttack;
      int totalInt = getTotalInt();

      if (totalInt > 2000) {
         maxBaseDamage -= 2000;
         maxBaseDamage += (int) ((0.09033024267 * totalInt) + 3823.8038);
      } else {
         maxBaseDamage -= totalInt;

         if (totalInt > 1700) {
            maxBaseDamage += (int) (0.1996049769 * Math.pow(totalInt, 1.300631341));
         } else {
            maxBaseDamage += (int) (0.1996049769 * Math.pow(totalInt, 1.290631341));
         }
      }

      return (maxBaseDamage * 107) / 100;
   }

   public short getCombo() {
      return comboCounter;
   }

   public void setCombo(short count) {
      if (count < comboCounter) {
         cancelEffectFromBuffStat(MapleBuffStat.ARAN_COMBO);
      }
      comboCounter = (short) Math.min(30000, count);
      if (count > 0) {
         PacketCreator.announce(this, new ShowCombo(comboCounter));
      }
   }

   public long getLastCombo() {
      return lastCombo;
   }

   public void setLastCombo(long time) {
      lastCombo = time;
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
      MaplePortal portal = map.findClosestPlayerSpawnPoint(this.position());
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
            PacketCreator.announce(this, new GMEffect(0x10, (byte) 0));
            List<MapleBuffStat> dsstat = Collections.singletonList(MapleBuffStat.DARK_SIGHT);
            getMap().broadcastGMMessage(this, new CancelForeignBuff(id, dsstat), false);
            getMap().broadcastSpawnPlayerMapObjectMessage(this, this, false);

            for (MapleSummon ms : this.getSummonsValues()) {
               getMap().broadcastNONGMMessage(this,
                     new SpawnSummon(ms.getOwner().getId(), ms.objectId(), ms.getSkill(), ms.getSkillLevel(),
                           ms.position(), ms.stance(), ms.getMovementType().getValue(), ms.isPuppet(), false)
                     , false);
            }

            for (MapleMapObject mo : this.getMap().getMonsters()) {
               MapleMonster m = (MapleMonster) mo;
               m.aggroUpdateController();
            }
         } else {
            this.hidden = true;
            PacketCreator.announce(this, new GMEffect(0x10, (byte) 1));
            if (!login) {
               getMap().broadcastNONGMMessage(this, new RemovePlayer(getId()), false);
            }
            getMap().broadcastGMMessage(this,
                  new GiveForeignBuff(id, Collections.singletonList(new Pair<>(MapleBuffStat.DARK_SIGHT, 0))), false);
            this.releaseControlledMonsters();
         }
         PacketCreator.announce(client, new EnableActions());
      }
   }

   public void hide(boolean hide) {
      hide(hide, false);
   }

   public void toggleHide(boolean login) {
      hide(!hidden);
   }

   public void cancelMagicDoor() {
      getAllStatIncreases().stream()
            .filter(valueHolder -> valueHolder.effect.isMagicDoor())
            .findFirst()
            .ifPresent(valueHolder -> cancelEffect(valueHolder.effect, false, valueHolder.startTime));
   }

   private void cancelPlayerBuffs(List<MapleBuffStat> buffStats) {
      if (client.getChannelServer().getPlayerStorage().getCharacterById(getId()).isPresent()) {
         updateLocalStats();
         PacketCreator.announce(client, new CancelBuff(buffStats));
         if (buffStats.size() > 0) {
            MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new CancelForeignBuff(getId(), buffStats), false, this);
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

   public void removeSandboxItems() {
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
                  MessageBroadcaster.getInstance().sendServerNotice(this, ServerNoticeType.PINK_TEXT,
                        I18nMessage.from("ITEM_SANDBOX_EXPIRE").with(ii.getName(item.id())));
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
      } else if (lastFameTime >= System.currentTimeMillis() - 3600000 * 24) {
         return FameStatus.NOT_TODAY;
      } else if (lastMonthFameIds.contains(from.getId())) {
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
         skills[0] = FirePoisonArchMage.BIG_BANG;
         skills[1] = FirePoisonArchMage.MANA_REFLECTION;
         skills[2] = FirePoisonArchMage.PARALYZE;
      } else if (jobId == 222) {
         skills[0] = IceLighteningArchMagician.BIG_BANG;
         skills[1] = IceLighteningArchMagician.MANA_REFLECTION;
         skills[2] = IceLighteningArchMagician.CHAIN_LIGHTNING;
      } else if (jobId == 232) {
         skills[0] = Bishop.BIG_BANG;
         skills[1] = Bishop.MANA_REFLECTION;
         skills[2] = Bishop.HOLY_SHIELD;
      } else if (jobId == 312) {
         skills[0] = BowMaster.BOW_EXPERT;
         skills[1] = BowMaster.HAMSTRING;
         skills[2] = BowMaster.SHARP_EYES;
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
         skills[1] = Corsair.BULLS_EYE;
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
            .flatMap(Optional::stream)
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
            MapleMapObjectProcessor.getInstance().sendDestroyData(this, chrC);
            MapleMapObjectProcessor.getInstance().sendSpawnData(this, chrC);
         }
      }

      // need to delay to ensure client side has finished reloading character data
      TimerManager.getInstance().schedule(() -> {
         MapleCharacter thisChr = MapleCharacter.this;
         MapleMap map = thisChr.getMap();

         if (map != null) {
            MasterBroadcaster.getInstance().sendToAllInMap(map, new ShowForeignEffect(thisChr.getId(), 8), false, thisChr);
         }
      }, 777);
   }

   public synchronized void changeJob(MapleJob newJob) {
      if (newJob == null) {
         return;//the fuck you doing idiot!
      }

      if (canRecvPartySearchInvite && getParty().isPresent()) {
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

         if (YamlConfig.config.server.USE_ENFORCE_JOB_SP_RANGE) {
            spGain = getChangedJobSp(newJob);
         }
      }

      if (spGain > 0) {
         gainSp(spGain, GameConstants.getSkillBook(newJob.getId()), true);
      }

      if (newJob.getId() % 100 >= 1) {
         if (this.isCygnus()) {
            gainAp(7, true);
         } else {
            if (YamlConfig.config.server.USE_STARTING_AP_4 || newJob.getId() % 10 >= 1) {
               gainAp(5, true);
            }
         }
      } else {
         if (YamlConfig.config.server.USE_STARTING_AP_4 && newJob.getId() % 1000 >= 1) {
            gainAp(4, true);
         }
      }

      if (!isGM()) {
         for (byte i = 1; i < 5; i++) {
            gainSlots(i, 4, true);
         }
      }

      int addHp = 0, addMp = 0;
      int job_ = job.getId() % 1000; // lame temp "fix"
      if (job_ == 100) {                      // 1st warrior
         addHp += Randomizer.rand(200, 250);
      } else if (job_ == 200) {               // 1st magician
         addMp += Randomizer.rand(100, 150);
      } else if (job_ % 100 == 0) {           // 1st others
         addHp += Randomizer.rand(100, 150);
         addHp += Randomizer.rand(25, 50);
      } else if (job_ > 0 && job_ < 200) {    // 2nd~4th warrior
         addHp += Randomizer.rand(300, 350);
      } else if (job_ < 300) {                // 2nd~4th magician
         addMp += Randomizer.rand(450, 500);
      } else if (job_ > 0) {                  // 2nd~4th others
         addHp += Randomizer.rand(300, 350);
         addMp += Randomizer.rand(150, 200);
      }

      effLock.lock();
      statWriteLock.lock();
      try {
         addMaxMPMaxHP(addHp, addMp, true);
         recalculateLocalStats();

         List<Pair<MapleStat, Integer>> statIncreases = new ArrayList<>(7);
         statIncreases.add(new Pair<>(MapleStat.HP, hp));
         statIncreases.add(new Pair<>(MapleStat.MP, mp));
         statIncreases.add(new Pair<>(MapleStat.MAX_HP, clientMaxHp));
         statIncreases.add(new Pair<>(MapleStat.MAX_MP, clientMaxMp));
         statIncreases.add(new Pair<>(MapleStat.AVAILABLE_AP, remainingAp));
         statIncreases.add(new Pair<>(MapleStat.AVAILABLE_SP, remainingSp[GameConstants.getSkillBook(job.getId())]));
         statIncreases.add(new Pair<>(MapleStat.JOB, job.getId()));
         PacketCreator.announce(client, new UpdatePlayerStats(statIncreases, true, this));
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
      }

      setMPC(new MaplePartyCharacter(this));
      silentPartyUpdate();

      if (dragon != null) {
         MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new RemoveDragon(dragon.objectId()));
         dragon = null;
      }

      if (this.guildId > 0) {
         getGuild().ifPresent(guild -> MasterBroadcaster.getInstance()
               .sendToGuild(guild, new NotifyJobAdvance(0, job.getId(), name), false, this.getId()));
      }

      MapleFamily family = getFamily();
      if (family != null) {
         MasterBroadcaster.getInstance()
               .sendToFamily(family, character -> PacketCreator.create(new NotifyJobAdvance(1, job.getId(), name)), false, this);
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

      if (YamlConfig.config.server.USE_ANNOUNCE_CHANGEJOB) {
         if (!this.isGM()) {
            MessageBroadcaster.getInstance().sendServerNoticeToAcquaintances(this, ServerNoticeType.LIGHT_BLUE,
                  I18nMessage.from("ANNOUNCE_CHANGE_JOB").with(GameConstants.ordinal(GameConstants.getJobBranch(newJob)), name,
                        GameConstants.getJobName(this.job.getId())));
         }
      }
   }

   public void changeKeyBinding(int key, KeyBinding keybinding) {
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
      MasterBroadcaster.getInstance()
            .sendToAllInMap(getMap(), new MovePlayer(id, MapleMapObjectProcessor.getInstance().getIdleMovementBytes(this)), false,
                  this);
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

   public void changeMapBanish(int mapId, String portal, String msg) {
      if (YamlConfig.config.server.USE_SPIKES_AVOID_BANISH) {
         for (Item it : this.getInventory(MapleInventoryType.EQUIPPED).list()) {
            if ((it.flag() & ItemConstants.SPIKES) == ItemConstants.SPIKES) {
               return;
            }
         }
      }

      int banMap = this.getMapId();
      int banSp = this.getMap().findClosestPlayerSpawnPoint(this.position()).getId();
      long banTime = System.currentTimeMillis();

      if (msg != null) {
         MessageBroadcaster.getInstance().sendServerNotice(this, ServerNoticeType.PINK_TEXT, SimpleMessage.from(msg));
      }

      MapleMap map_ = getWarpMap(mapId);
      MaplePortal portal_ = map_.getPortal(portal);
      changeMap(map_, portal_ != null ? portal_ : map_.getRandomPlayerSpawnPoint());

      setBanishPlayerData(banMap, banSp, banTime);
   }

   public void changeMap(int map) {
      MapleMap warpMap = getWarpMap(map);
      changeMap(warpMap, warpMap.getRandomPlayerSpawnPoint());
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
      changeMapInternal(to, pto.getPosition(),
            PacketCreator.create(new WarpToMap(getClient().getChannel(), to.getId(), pto.getId(), getHp())));
      canWarpMap = false;

      canWarpCounter--;
      if (canWarpCounter == 0) {
         canWarpMap = true;
      }

      eventAfterChangedMap(this.getMapId());
   }

   public void changeMap(final int target, final Point pos) {
      canWarpCounter++;

      eventChangedMap(target);
      MapleMap to = getWarpMap(target);
      changeMapInternal(to, pos,
            PacketCreator.create(new WarpToMap(getClient().getChannel(), to.getId(), 0x80, getHp(), Optional.ofNullable(pos))));
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
      changeMapInternal(to, pos,
            PacketCreator.create(new WarpToMap(getClient().getChannel(), to.getId(), 0x80, getHp(), Optional.ofNullable(pos))));
      canWarpMap = false;

      canWarpCounter--;
      if (canWarpCounter == 0) {
         canWarpMap = true;
      }

      eventAfterChangedMap(this.getMapId());
   }

   public void forceChangeMap(final MapleMap target, final MaplePortal pto) {
      // will actually enter the map given as parameter, regardless of being an event map or whatnot

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

         mapEim.registerPlayer(this, false);
      }

      changeMapInternal(target, pto.getPosition(),
            PacketCreator.create(new WarpToMap(getClient().getChannel(), target.getId(), pto.getId(), getHp())));
      canWarpMap = false;

      canWarpCounter--;
      if (canWarpCounter == 0) {
         canWarpMap = true;
      }

      eventAfterChangedMap(this.getMapId());
   }

   private boolean buffMapProtection() {
      int thisMapId = mapId;
      int returnMapId = client.getChannelServer().getMapFactory().getMap(thisMapId).getReturnMapId();

      effLock.lock();
      chrLock.lock();
      try {
         for (Entry<MapleBuffStat, MapleBuffStatValueHolder> mbs : effects.entrySet()) {
            if (mbs.getKey() == MapleBuffStat.MAP_PROTECTION) {
               byte value = (byte) mbs.getValue().value;

               if (value == 1 && ((returnMapId == 211000000 && thisMapId != 200082300) || returnMapId == 193000000)) {
                  return true;        //protection from cold
               } else {
                  return value == 2 && (returnMapId == 230000000 || thisMapId == 200082300);        //breathing underwater
               }
            }
         }
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }

      for (Item it : this.getInventory(MapleInventoryType.EQUIPPED).list()) {
         if ((it.flag() & ItemConstants.COLD) == ItemConstants.COLD && ((returnMapId == 211000000 && thisMapId != 200082300)
               || returnMapId == 193000000)) {
            return true;        //protection from cold
         }
      }

      return false;
   }

   public List<Integer> getLastVisitedMapIds() {
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
      List<WeakReference<MapleMap>> mapIds;

      petLock.lock();
      try {
         mapIds = new LinkedList<>(lastVisitedMaps);
      } finally {
         petLock.unlock();
      }

      List<MapleCharacter> partyMembers = new LinkedList<>();
      for (MapleCharacter mc : (exPartyMembers != null) ? exPartyMembers : this.getPartyMembersOnline()) {
         if (mc.isLoggedInWorld()) {
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
      for (WeakReference<MapleMap> mapRef : mapIds) {
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

   public void collectAbnormalStatuses() {
      for (MapleCharacter chr : map.getAllPlayers()) {
         int cid = chr.getId();

         for (Entry<MapleAbnormalStatus, Pair<Long, MobSkill>> abnormalStatusPairEntry : chr.getAlAbnormalStatuses().entrySet()) {
            MapleAbnormalStatus abnormalStatus = abnormalStatusPairEntry.getKey();
            MobSkill skill = abnormalStatusPairEntry.getValue().getRight();

            if (abnormalStatus != MapleAbnormalStatus.SLOW) {
               PacketCreator.announce(this,
                     new GiveForeignAbnormalStatus(cid, Collections.singletonList(new Pair<>(abnormalStatus, skill.x())), skill));
            } else {
               PacketCreator.announce(this,
                     new GiveForeignAbnormalStatusSlow(cid, Collections.singletonList(new Pair<>(abnormalStatus, skill.x())),
                           skill));
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
            if (lastVisitedMaps.size() == YamlConfig.config.server.MAP_VISITED_SIZE) {
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
               .ifPresent(character -> PacketCreator.announce(character, new WeddingPartnerTransfer(id, mapId)));
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
      MapleTradeProcessor.getInstance().cancelTrade(this, MapleTradeResult.UNSUCCESSFUL_ANOTHER_MAP);
      this.closePlayerInteractions();

      MapleParty e = getParty().map(MapleParty::getEnemy).orElse(null);
      client.announce(warpPacket);
      map.removePlayer(this);
      if (client.getChannelServer().getPlayerStorage().getCharacterById(getId()).isPresent()) {
         map = to;
         setPosition(pos);
         map.addPlayer(this);
         visitMap(map);

         prtLock.lock();
         try {
            party.ifPresent(reference -> {
               mpc.setMapId(to.getId());
               PacketCreator.announce(client, new UpdateParty(client.getChannel(), reference, PartyOperation.SILENT_UPDATE, null));
               updatePartyMemberHPInternal();
            });
         } finally {
            prtLock.unlock();
         }
         MapleCharacter.this.getParty().ifPresent(reference -> {
            reference.setEnemy(e);
            silentPartyUpdateInternal(reference);
         });

         if (getMap().getHPDec() > 0) {
            resetHpDecreaseTask();
         }
      } else {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.MAPLE_MAP,
               "Character " + this.getName() + " got stuck when moving to map " + map.getId() + ".");
         client.disconnect(true, false);
         return;
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
         PacketCreator.announce(this, new EnvironmentMoveList(map.getEnvironment().entrySet()));
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

   public void changeSkillLevel(Skill skill, byte newLevel, int newMasterLevel, long expiration) {
      if (newLevel > -1) {
         skills.put(skill, new SkillEntry(newLevel, newMasterLevel, expiration));
         if (!GameConstants.isHiddenSkills(skill.getId())) {
            PacketCreator.announce(client, new UpdateSkill(skill.getId(), newLevel, newMasterLevel, expiration));
         }
      } else {
         skills.remove(skill);
         PacketCreator.announce(client,
               new UpdateSkill(skill.getId(), newLevel, newMasterLevel, -1)); //Shouldn't use expiration anymore :)
         DatabaseConnection.getInstance().withConnection(
               connection -> SkillAdministrator.getInstance().deleteForSkillCharacter(connection, skill.getId(), id));
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
      if (job.equals(MapleJob.DARK_KNIGHT)) {
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

         PacketCreator.announce(client, new ShowOwnBerserk(skillLevel, MapleCharacter.this.berserk));
         if (!isHidden) {
            MasterBroadcaster.getInstance()
                  .sendToAllInMap(getMap(), new ShowBerserk(getId(), skillLevel, MapleCharacter.this.berserk), false,
                        MapleCharacter.this);
         } else {
            getMap().broadcastGMMessage(MapleCharacter.this, new ShowBerserk(getId(), skillLevel, MapleCharacter.this.berserk),
                  false);
         }
      }, 5000, 3000);
   }

   public void checkMessenger() {
      getMessenger().ifPresent(messenger -> {
         if (messengerPosition > -1 && messengerPosition < 4) {
            World world = getWorldServer();
            world.silentJoinMessenger(messenger.getId(), new MapleMessengerCharacter(this, messengerPosition), messengerPosition);
            world.updateMessenger(messenger.getId(), name, client.getChannel());
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

   public boolean applyConsumeOnPickup(final int itemId) {
      if (itemId / 1000000 == 2) {
         if (ii.isConsumeOnPickup(itemId)) {
            if (ItemConstants.isPartyItem(itemId)) {
               List<MapleCharacter> partyCharacters = this.getPartyMembersOnSameMap();

               if (!ItemConstants.isPartyAllCure(itemId)) {
                  MapleStatEffect mse = ii.getItemEffect(itemId);

                  if (!partyCharacters.isEmpty()) {
                     for (MapleCharacter mc : partyCharacters) {
                        mse.applyTo(mc);
                     }
                  } else {
                     mse.applyTo(this);
                  }
               } else {
                  if (!partyCharacters.isEmpty()) {
                     for (MapleCharacter mc : partyCharacters) {
                        mc.dispelAbnormalStatuses();
                     }
                  } else {
                     this.dispelAbnormalStatuses();
                  }
               }
            } else {
               ii.getItemEffect(itemId).applyTo(this);
            }

            if (itemId / 10000 == 238) {
               this.getMonsterBook().addCard(client, itemId);
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

      if (ob instanceof MapleMapItem mapItem) {
         if (System.currentTimeMillis() - mapItem.getDropTime() < 400 || !mapItem.canBePickedBy(this)) {
            PacketCreator.announce(client, new EnableActions());
            return;
         }

         List<MapleCharacter> partyCharacters = new LinkedList<>();
         if (mapItem.getMeso() > 0 && !mapItem.isPickedUp()) {
            partyCharacters = getPartyMembersOnSameMap();
         }

         ScriptedItem itemScript = null;
         mapItem.lockItem();
         try {
            if (mapItem.isPickedUp()) {
               PacketCreator.announce(client, new ShowItemUnavailable());
               PacketCreator.announce(client, new EnableActions());
               return;
            }

            boolean isPet = petIndex > -1;
            PacketInput pickupPacket = new RemoveItem(mapItem.objectId(), (isPet) ? 5 : 2, this.getId(), isPet, petIndex);

            Item mItem = mapItem.getItem();
            boolean hasSpaceInventory;
            if (mapItem.getItemId() == 4031865 || mapItem.getItemId() == 4031866 || mapItem.getMeso() > 0 || ii
                  .isConsumeOnPickup(mapItem.getItemId()) || (hasSpaceInventory =
                  MapleInventoryManipulator.checkSpace(client, mapItem.getItemId(), mItem.quantity(), mItem.owner()))) {
               int mapId = this.getMapId();

               if ((mapId > 209000000 && mapId < 209000016) || (mapId >= 990000500 && mapId <= 990000502)) {
                  if (!mapItem.isPlayerDrop() || mapItem.getDropper().objectId() == client.getPlayer().objectId()) {
                     if (mapItem.getMeso() > 0) {
                        if (!partyCharacters.isEmpty()) {
                           int mesosamm = mapItem.getMeso() / partyCharacters.size();
                           for (MapleCharacter character : partyCharacters) {
                              if (character.isLoggedInWorld()) {
                                 character.gainMeso(mesosamm, true, true, false);
                              }
                           }
                        } else {
                           this.gainMeso(mapItem.getMeso(), true, true, false);
                        }

                        this.getMap().pickItemDrop(pickupPacket, mapItem);
                     } else if (mapItem.getItemId() == 4031865 || mapItem.getItemId() == 4031866) {
                        // Add NX to account, show effect and make item disappear
                        int nxGain = mapItem.getItemId() == 4031865 ? 100 : 250;
                        this.getCashShop().gainCash(1, nxGain);

                        showHint("You have earned #e#b" + nxGain + " NX#k#n. (" + this.getCashShop().getCash(1) + " NX)", 300);

                        this.getMap().pickItemDrop(pickupPacket, mapItem);
                     } else if (MapleInventoryManipulator.addFromDrop(client, mItem, true).isPresent()) {
                        this.getMap().pickItemDrop(pickupPacket, mapItem);
                     } else {
                        PacketCreator.announce(client, new EnableActions());
                        return;
                     }
                  } else {
                     PacketCreator.announce(client, new ShowItemUnavailable());
                     PacketCreator.announce(client, new EnableActions());
                     return;
                  }
                  PacketCreator.announce(client, new EnableActions());
                  return;
               }

               if (!this.needQuestItem(mapItem.getQuest(), mapItem.getItemId())) {
                  PacketCreator.announce(client, new ShowItemUnavailable());
                  PacketCreator.announce(client, new EnableActions());
                  return;
               }

               if (mapItem.getMeso() > 0) {
                  if (!partyCharacters.isEmpty()) {
                     int mesosamm = mapItem.getMeso() / partyCharacters.size();
                     for (MapleCharacter character : partyCharacters) {
                        if (character.isLoggedInWorld()) {
                           character.gainMeso(mesosamm, true, true, false);
                        }
                     }
                  } else {
                     this.gainMeso(mapItem.getMeso(), true, true, false);
                  }
               } else if (mItem.id() / 10000 == 243) {
                  ScriptedItem info = ii.getScriptedItemInfo(mItem.id());
                  if (info != null && info.runOnPickup()) {
                     itemScript = info;
                  } else {
                     if (MapleInventoryManipulator.addFromDrop(client, mItem, true).isEmpty()) {
                        PacketCreator.announce(client, new EnableActions());
                        return;
                     }
                  }
               } else if (mapItem.getItemId() == 4031865 || mapItem.getItemId() == 4031866) {
                  // Add NX to account, show effect and make item disappear
                  int nxGain = mapItem.getItemId() == 4031865 ? 100 : 250;
                  this.getCashShop().gainCash(1, nxGain);

                  showHint("You have earned #e#b" + nxGain + " NX#k#n. (" + this.getCashShop().getCash(1) + " NX)", 300);
               } else if (applyConsumeOnPickup(mItem.id())) {
               } else if (MapleInventoryManipulator.addFromDrop(client, mItem, true).isPresent()) {
                  if (mItem.id() == 4031868) {
                     updateAriantScore();
                  }
               } else {
                  PacketCreator.announce(client, new EnableActions());
                  return;
               }

               this.getMap().pickItemDrop(pickupPacket, mapItem);
            } else if (!hasSpaceInventory) {
               PacketCreator.announce(client, new InventoryFull());
               PacketCreator.announce(client, new ShowInventoryFull());
            }
         } finally {
            mapItem.unlockItem();
         }

         if (itemScript != null) {
            ItemScriptManager ism = ItemScriptManager.getInstance();
            ism.runItemScript(client, itemScript);
         }
      }
      PacketCreator.announce(client, new EnableActions());
   }

   public int countItem(int itemId) {
      return inventory[ItemConstants.getInventoryType(itemId).ordinal()].countById(itemId);
   }

   public boolean canHold(int itemId) {
      return canHold(itemId, 1);
   }

   public boolean canHold(int itemId, int quantity) {
      return client.getAbstractPlayerInteraction().canHold(itemId, quantity);
   }

   public boolean canHoldUniques(List<Integer> itemIds) {
      return itemIds.stream().noneMatch(itemId -> (ii.isPickupRestricted(itemId) && haveItem(itemId)));
   }

   public boolean isRidingBattleship() {
      Integer bv = getBuffedValue(MapleBuffStat.MONSTER_RIDING);
      return bv != null && bv.equals(Corsair.BATTLE_SHIP);
   }

   public void announceBattleshipHp() {
      PacketCreator.announce(this, new SkillCoolDown(5221999, battleShipHp));
   }

   public void decreaseBattleshipHp(int decrease) {
      this.battleShipHp -= decrease;
      if (battleShipHp <= 0) {
         SkillFactory.getSkill(Corsair.BATTLE_SHIP).ifPresent(skill -> {
                  int coolDown = skill.getEffect(getSkillLevel(skill)).getCoolDown();
                  PacketCreator.announce(this, new SkillCoolDown(Corsair.BATTLE_SHIP, coolDown));
                  addCoolDown(Corsair.BATTLE_SHIP, Server.getInstance().getCurrentTime(), coolDown * 1000);
                  removeCoolDown(5221999);
                  cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
               }
         );
      } else {
         announceBattleshipHp();
         addCoolDown(5221999, 0, Long.MAX_VALUE);
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

   private void stopChairTask() {
      chrLock.lock();
      try {
         scheduler.cancel(MapleCharacterScheduler.Type.CHAIR_RECOVERY);
      } finally {
         chrLock.unlock();
      }
   }

   private void updateChairHealStats() {
      statReadLock.lock();
      try {
         if (localChairRate != -1) {
            return;
         }
      } finally {
         statReadLock.unlock();
      }

      effLock.lock();
      statWriteLock.lock();
      try {
         Pair<Integer, Pair<Integer, Integer>> p = ChairProcessor.getInstance().getChairTaskIntervalRate(localMaxHp, localMaxMp);

         localChairRate = p.getLeft();
         localChairHp = p.getRight().getLeft();
         localChairMp = p.getRight().getRight();
      } finally {
         statWriteLock.unlock();
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
         healInterval = localChairRate;
      } finally {
         effLock.unlock();
      }

      chrLock.lock();
      try {
         stopChairTask();
         scheduler.add(MapleCharacterScheduler.Type.CHAIR_RECOVERY, () -> {
            updateChairHealStats();
            final int healHP = localChairHp;
            final int healMP = localChairMp;

            if (MapleCharacter.this.getHp() < localMaxHp) {
               byte recHP = (byte) (healHP / YamlConfig.config.server.CHAIR_EXTRA_HEAL_MULTIPLIER);

               PacketCreator.announce(client, new ShowOwnRecovery(recHP));
               MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new ShowRecovery(id, recHP), false, this);
            } else if (MapleCharacter.this.getMp() >= localMaxMp) {
               stopChairTask();    // optimizing schedule management when player is already with full pool.
            }

            addHpMp(healHP, healMP);
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
         if (getBuffSource(MapleBuffStat.HP_RECOVERY) == -1 && getBuffSource(MapleBuffStat.MP_RECOVERY) == -1) {
            stopExtraTask();
            return;
         }

         if (MapleCharacter.this.getHp() < localMaxHp) {
            if (healHP > 0) {
               PacketCreator.announce(client, new ShowOwnRecovery(healHP));
               MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new ShowRecovery(id, healHP), false, MapleCharacter.this);
            }
         }

         addHpMp(healHP, healMP);
      }, healInterval, healInterval);
   }

   public void disbandGuild() {
      if (guildId < 1 || guildRank != 1) {
         return;
      }
      try {
         MapleGuildProcessor.getInstance().disbandGuild(guildId);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void dispel() {
      if (!(YamlConfig.config.server.USE_UNDISPEL_HOLY_SHIELD && this.hasActiveBuff(Bishop.HOLY_SHIELD))) {
         getAllStatIncreases().stream()
               .filter(buffStatValueHolder -> buffStatValueHolder.effect.isSkill()
                     && buffStatValueHolder.effect.getBuffSourceId() != Aran.COMBO_ABILITY)
               .forEach(buffStatValueHolder -> cancelEffect(buffStatValueHolder.effect, false, buffStatValueHolder.startTime));
      }
   }

   public final boolean hasDisease(final MapleAbnormalStatus dis) {
      chrLock.lock();
      try {
         return abnormalStatuses.containsKey(dis);
      } finally {
         chrLock.unlock();
      }
   }

   private int getAbnormalStatusSize() {
      chrLock.lock();
      try {
         return abnormalStatuses.size();
      } finally {
         chrLock.unlock();
      }
   }

   public Map<MapleAbnormalStatus, Pair<Long, MobSkill>> getAlAbnormalStatuses() {
      chrLock.lock();
      try {
         long currentTime = Server.getInstance().getCurrentTime();
         Map<MapleAbnormalStatus, Pair<Long, MobSkill>> ret = new LinkedHashMap<>();

         for (Entry<MapleAbnormalStatus, Long> de : abnormalStatusExpirationTimes.entrySet()) {
            Pair<AbnormalStatusValueHolder, MobSkill> dee = abnormalStatuses.get(de.getKey());
            AbnormalStatusValueHolder abnormalStatusValueHolder = dee.getLeft();

            ret.put(de.getKey(),
                  new Pair<>(abnormalStatusValueHolder.length() - (currentTime - abnormalStatusValueHolder.startTime()),
                        dee.getRight()));
         }

         return ret;
      } finally {
         chrLock.unlock();
      }
   }

   public void silentApplyDiseases(Map<MapleAbnormalStatus, Pair<Long, MobSkill>> diseaseMap) {
      chrLock.lock();
      try {
         long curTime = Server.getInstance().getCurrentTime();

         for (Entry<MapleAbnormalStatus, Pair<Long, MobSkill>> di : diseaseMap.entrySet()) {
            long expTime = curTime + di.getValue().getLeft();

            abnormalStatusExpirationTimes.put(di.getKey(), expTime);
            abnormalStatuses.put(di.getKey(),
                  new Pair<>(new AbnormalStatusValueHolder(curTime, di.getValue().getLeft()), di.getValue().getRight()));
         }
      } finally {
         chrLock.unlock();
      }
   }

   public void announceAbnormalStatuses() {
      Set<Entry<MapleAbnormalStatus, Pair<AbnormalStatusValueHolder, MobSkill>>> abnormalStatuses;

      chrLock.lock();
      try {
         if (!this.isLoggedInWorld()) {
            return;
         }

         abnormalStatuses = new LinkedHashSet<>(this.abnormalStatuses.entrySet());
      } finally {
         chrLock.unlock();
      }

      for (Entry<MapleAbnormalStatus, Pair<AbnormalStatusValueHolder, MobSkill>> abnormalStatusPairEntry : abnormalStatuses) {
         MapleAbnormalStatus abnormalStatus = abnormalStatusPairEntry.getKey();
         MobSkill skill = abnormalStatusPairEntry.getValue().getRight();
         if (abnormalStatus != MapleAbnormalStatus.SLOW) {
            MasterBroadcaster.getInstance().sendToAllInMap(map,
                  new GiveForeignAbnormalStatus(id, Collections.singletonList(new Pair<>(abnormalStatus, skill.x())), skill));
         } else {
            MasterBroadcaster.getInstance().sendToAllInMap(map,
                  new GiveForeignAbnormalStatusSlow(id, Collections.singletonList(new Pair<>(abnormalStatus, skill.x())), skill));
         }
      }
   }

   public void giveAbnormalStatus(final MapleAbnormalStatus abnormalStatus, MobSkill skill) {
      if (!hasDisease(abnormalStatus) && getAbnormalStatusSize() < 2) {
         if (!(abnormalStatus == MapleAbnormalStatus.SEDUCE || abnormalStatus == MapleAbnormalStatus.STUN)) {
            if (hasActiveBuff(Bishop.HOLY_SHIELD)) {
               return;
            }
         }

         chrLock.lock();
         try {
            long curTime = Server.getInstance().getCurrentTime();
            abnormalStatusExpirationTimes.put(abnormalStatus, curTime + skill.duration());
            abnormalStatuses.put(abnormalStatus, new Pair<>(new AbnormalStatusValueHolder(curTime, skill.duration()), skill));
         } finally {
            chrLock.unlock();
         }

         if (abnormalStatus == MapleAbnormalStatus.SEDUCE && chair.get() < 0) {
            sitChair(-1);
         }

         final List<Pair<MapleAbnormalStatus, Integer>> abnormalStatusList =
               Collections.singletonList(new Pair<>(abnormalStatus, skill.x()));
         PacketCreator.announce(client, new GiveAbnormalStatus(abnormalStatusList, skill));

         if (abnormalStatus != MapleAbnormalStatus.SLOW) {
            MasterBroadcaster.getInstance()
                  .sendToAllInMap(getMap(), new GiveForeignAbnormalStatus(id, abnormalStatusList, skill), false, this);
         } else {
            MasterBroadcaster.getInstance()
                  .sendToAllInMap(getMap(), new GiveForeignAbnormalStatusSlow(id, abnormalStatusList, skill), false, this);
         }
      }
   }

   public void dispelAbnormalStatus(MapleAbnormalStatus abnormalStatus) {
      if (hasDisease(abnormalStatus)) {
         long mask = abnormalStatus.getValue();
         PacketCreator.announce(this, new CancelAbnormalStatus(mask));

         if (abnormalStatus != MapleAbnormalStatus.SLOW) {
            MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new CancelForeignAbnormalStatus(id, mask), false, this);
         } else {
            MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new CancelForeignAbnormalStatusSlow(id), false, this);
         }

         chrLock.lock();
         try {
            abnormalStatuses.remove(abnormalStatus);
            abnormalStatusExpirationTimes.remove(abnormalStatus);
         } finally {
            chrLock.unlock();
         }
      }
   }

   public void dispelAbnormalStatuses() {
      dispelAbnormalStatus(MapleAbnormalStatus.CURSE);
      dispelAbnormalStatus(MapleAbnormalStatus.DARKNESS);
      dispelAbnormalStatus(MapleAbnormalStatus.POISON);
      dispelAbnormalStatus(MapleAbnormalStatus.SEAL);
      dispelAbnormalStatus(MapleAbnormalStatus.WEAKEN);
      dispelAbnormalStatus(MapleAbnormalStatus.SLOW);
   }

   public void cancelAllAbnormalStatuses() {
      chrLock.lock();
      try {
         abnormalStatuses.clear();
         abnormalStatusExpirationTimes.clear();
      } finally {
         chrLock.unlock();
      }
   }

   private void dispelSkill(int skillId) {
      List<MapleBuffStatValueHolder> allBuffs = getAllStatIncreases();
      for (MapleBuffStatValueHolder buffStatValueHolder : allBuffs) {
         if (skillId == 0) {
            if (buffStatValueHolder.effect.isSkill() && (buffStatValueHolder.effect.getSourceId() % 10000000 == 1004
                  || SkillProcessor.getInstance().dispelSkills(buffStatValueHolder.effect.getSourceId()))) {
               cancelEffect(buffStatValueHolder.effect, false, buffStatValueHolder.startTime);
            }
         } else if (buffStatValueHolder.effect.isSkill() && buffStatValueHolder.effect.getSourceId() == skillId) {
            cancelEffect(buffStatValueHolder.effect, false, buffStatValueHolder.startTime);
         }
      }
   }

   public void changeFaceExpression(int emote) {
      long timeNow = Server.getInstance().getCurrentTime();
      if (timeNow - lastExpression > 2000) {
         lastExpression = timeNow;
         FaceExpressionService service =
               (FaceExpressionService) client.getChannelServer().getServiceAccess(ChannelServices.FACE_EXPRESSION);
         service.registerFaceExpression(map, this, emote);
      }
   }

   private void doHurtHp() {
      if (!(this.getInventory(MapleInventoryType.EQUIPPED).findById(getMap().getHPDecProtect()) != null || buffMapProtection())) {
         addHP(-getMap().getHPDec());
         lastHpDec = Server.getInstance().getCurrentTime();
      }
   }

   private void startHpDecreaseTask(long lastHpTask) {
      scheduler.add(MapleCharacterScheduler.Type.HP_DECREASE, this::doHurtHp, YamlConfig.config.server.MAP_DAMAGE_OVERTIME_INTERVAL,
            YamlConfig.config.server.MAP_DAMAGE_OVERTIME_INTERVAL - lastHpTask);
   }

   public void resetHpDecreaseTask() {
      scheduler.cancel(MapleCharacterScheduler.Type.HP_DECREASE);
      long lastHpTask = Server.getInstance().getCurrentTime() - lastHpDec;
      startHpDecreaseTask((lastHpTask > YamlConfig.config.server.MAP_DAMAGE_OVERTIME_INTERVAL) ?
            YamlConfig.config.server.MAP_DAMAGE_OVERTIME_INTERVAL : lastHpTask);
   }

   public void enteredScript(String script, int mapId) {
      if (!entered.containsKey(mapId)) {
         entered.put(mapId, script);
      }
   }

   public void equipChanged() {
      getMap().broadcastUpdateCharLookMessage(this, this);
      equipChanged = true;
      updateLocalStats();
      getMessenger()
            .ifPresent(messenger -> getWorldServer().updateMessenger(messenger, getName(), getWorld(), client.getChannel()));
   }

   public void cancelDiseaseExpireTask() {
      scheduler.cancel(MapleCharacterScheduler.Type.DISEASE_EXPIRE);
   }

   public void diseaseExpireTask() {
      scheduler.addIfNotExists(MapleCharacterScheduler.Type.DISEASE_EXPIRE, () -> {
         Set<MapleAbnormalStatus> toExpire = new LinkedHashSet<>();

         chrLock.lock();
         try {
            long curTime = Server.getInstance().getCurrentTime();

            for (Entry<MapleAbnormalStatus, Long> de : abnormalStatusExpirationTimes.entrySet()) {
               if (de.getValue() < curTime) {
                  toExpire.add(de.getKey());
               }
            }
         } finally {
            chrLock.unlock();
         }

         for (MapleAbnormalStatus d : toExpire) {
            dispelAbnormalStatus(d);
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

         toCancel.forEach(buffStatValueHolder -> cancelEffect(buffStatValueHolder.effect, false, buffStatValueHolder.startTime));
      }, 1500);
   }

   public void cancelSkillCoolDownTask() {
      scheduler.cancel(MapleCharacterScheduler.Type.SKILL_COOL_DOWN);
   }

   public void skillCoolDownTask() {
      scheduler.addIfNotExists(MapleCharacterScheduler.Type.SKILL_COOL_DOWN, () -> {
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
            CoolDownValueHolder coolDownValueHolder = bel.getValue();
            if (curTime >= coolDownValueHolder.startTime() + coolDownValueHolder.length()) {
               removeCoolDown(coolDownValueHolder.skillId());
               PacketCreator.announce(client, new SkillCoolDown(coolDownValueHolder.skillId(), 0));
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

         long expiration, currentTime = System.currentTimeMillis();
         Set<Skill> keys = getSkills().keySet();
         for (Skill key : keys) {
            SkillEntry skill = getSkills().get(key);
            if (skill.expiration() != -1 && skill.expiration() < currentTime) {
               changeSkillLevel(key, (byte) -1, 0, -1);
            }
         }

         List<Item> toBeRemoved = new ArrayList<>();
         for (MapleInventory inv : inventory) {
            for (Item item : inv.list()) {
               expiration = item.expiration();

               if (expiration != -1 && (expiration < currentTime) && ((item.flag() & ItemConstants.LOCK) == ItemConstants.LOCK)) {
                  short lock = item.flag();
                  lock &= ~(ItemConstants.LOCK);
                  item = Item.newBuilder(item)
                        .setFlag(ItemProcessor.getInstance().setFlag(item.id(),
                              lock)) //Probably need a check, else people can make expiring items into permanent items...
                        .setExpiration(-1)
                        .build();
                  forceUpdateItem(item);   //TEST :3
               } else if (expiration != -1 && expiration < currentTime) {
                  if (!ItemConstants.isPet(item.id())) {
                     PacketCreator.announce(client, new ShowItemExpired(item.id()));
                     toBeRemoved.add(item);
                     if (ItemConstants.isRateCoupon(item.id())) {
                        deletedCoupon = true;
                     }
                  } else {
                     MaplePet pet = item.pet();
                     if (pet != null) {
                        PetProcessor.getInstance().unequipPet(this, getPetIndex(pet), true);
                     }

                     if (ItemConstants.isExpirablePet(item.id())) {
                        PacketCreator.announce(client, new ShowItemExpired(item.id()));
                        toBeRemoved.add(item);
                     } else {
                        item = Item.newBuilder(item).setExpiration(-1).build();
                        forceUpdateItem(item);
                     }
                  }
               }
            }

            if (!toBeRemoved.isEmpty()) {
               for (Item item : toBeRemoved) {
                  MapleInventoryManipulator.removeFromSlot(client, inv.getType(), item.position(), item.quantity(), true);
               }

               MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
               for (Item item : toBeRemoved) {
                  List<Integer> toAdd = new ArrayList<>();
                  Pair<Integer, String> replace = ii.getReplaceOnExpire(item.id());
                  if (replace.left > 0) {
                     toAdd.add(replace.left);
                     if (!replace.right.isEmpty()) {
                        MessageBroadcaster.getInstance()
                              .sendServerNotice(MapleCharacter.this, ServerNoticeType.NOTICE, SimpleMessage.from(replace.right));
                     }
                  }
                  for (Integer itemId : toAdd) {
                     MapleInventoryManipulator.addById(client, itemId, (short) 1);
                  }
               }

               toBeRemoved.clear();
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
      PacketCreator.announce(client, new ModifyInventoryPacket(true, mods));
   }

   public void gainGachaponExp() {
      int expGain = 0;
      long currentGachaponExp = gachaponExp.get();
      if ((currentGachaponExp + exp.get()) >= ExpTable.getExpNeededForLevel(level)) {
         expGain += ExpTable.getExpNeededForLevel(level) - exp.get();

         int nextNeed = ExpTable.getExpNeededForLevel(level + 1);
         if (currentGachaponExp - expGain >= nextNeed) {
            expGain += nextNeed;
         }

         this.gachaponExp.set((int) (currentGachaponExp - expGain));
      } else {
         expGain = this.gachaponExp.getAndSet(0);
      }
      gainExp(expGain, false, true);
      updateSingleStat(MapleStat.GACHAPON_EXP, this.gachaponExp.get());
   }

   public void addGachaponExp(int gain) {
      updateSingleStat(MapleStat.GACHAPON_EXP, gachaponExp.addAndGet(gain));
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
      if (hasDisease(MapleAbnormalStatus.CURSE)) {
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

      PacketCreator.announce(client, new ShowEXPGain((int) gain, equip, party, inChat, white));
   }

   private synchronized void gainExpInternal(long gain, int equip, int party, boolean show, boolean inChat, boolean white) {
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
            PacketCreator.announce(fromPlayer, new GiveFameResponse(mode, getName(), thisFame));
            PacketCreator.announce(this, new ReceiveFame(mode, fromPlayer.getName()));
         } else {
            PacketCreator.announce(this, new ShowFameGain(delta));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean canHoldMeso(int gain) {
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
         nextMeso = (long) meso.get() + gain;
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
            PacketCreator.announce(client, new ShowMesoGain(gain, inChat));
         }
      } else {
         PacketCreator.announce(client, new EnableActions());
      }
   }

   public void genericGuildMessage(int code) {
      PacketCreator.announce(client, new GenericGuildMessage((byte) code));
   }

   public int getAccountID() {
      return accountId;
   }

   public List<PlayerCoolDownValueHolder> getAllCoolDowns() {
      effLock.lock();
      chrLock.lock();
      try {
         return coolDowns.values().stream()
               .map(valueHolder -> new PlayerCoolDownValueHolder(valueHolder.skillId(), valueHolder.startTime(),
                     valueHolder.length()))
               .collect(Collectors.toList());
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
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

   public BuddyList getBuddyList() {
      return buddylist;
   }

   public void modifyBuddyList(Function<BuddyList, BuddyList> modifier) {
      this.buddylist = modifier.apply(buddylist);
   }

   public Long getBuffedStartTime(MapleBuffStat effect) {
      effLock.lock();
      chrLock.lock();
      try {
         MapleBuffStatValueHolder buffStatValueHolder = effects.get(effect);
         if (buffStatValueHolder == null) {
            return null;
         }
         return buffStatValueHolder.startTime;
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   public Integer getBuffedValue(MapleBuffStat effect) {
      effLock.lock();
      chrLock.lock();
      try {
         MapleBuffStatValueHolder buffStatValueHolder = effects.get(effect);
         if (buffStatValueHolder == null) {
            return null;
         }
         return buffStatValueHolder.value;
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   public int getBuffSource(MapleBuffStat stat) {
      effLock.lock();
      chrLock.lock();
      try {
         MapleBuffStatValueHolder buffStatValueHolder = effects.get(stat);
         if (buffStatValueHolder == null) {
            return -1;
         }
         return buffStatValueHolder.effect.getSourceId();
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   public MapleStatEffect getBuffEffect(MapleBuffStat stat) {
      effLock.lock();
      chrLock.lock();
      try {
         MapleBuffStatValueHolder buffStatValueHolder = effects.get(stat);
         if (buffStatValueHolder == null) {
            return null;
         } else {
            return buffStatValueHolder.effect;
         }
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   private List<MapleBuffStatValueHolder> getAllStatIncreases() {
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
         long currentTime = Server.getInstance().getCurrentTime();

         Map<Integer, PlayerBuffValueHolder> ret = new LinkedHashMap<>();
         for (Map<MapleBuffStat, MapleBuffStatValueHolder> bel : buffEffects.values()) {
            for (MapleBuffStatValueHolder buffStatValueHolder : bel.values()) {
               int sourceId = buffStatValueHolder.effect.getBuffSourceId();
               if (!ret.containsKey(sourceId)) {
                  ret.put(sourceId,
                        new PlayerBuffValueHolder((int) (currentTime - buffStatValueHolder.startTime), buffStatValueHolder.effect));
               }
            }
         }
         return new ArrayList<>(ret.values());
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   public boolean hasBuffFromSourceId(int sourceId) {
      effLock.lock();
      chrLock.lock();
      try {
         return buffEffects.containsKey(sourceId);
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   public boolean hasBuffFromSourceIdDirty(int sourceId) {
      return buffEffects.containsKey(sourceId);
   }

   public boolean hasActiveBuff(int sourceId) {
      LinkedList<MapleBuffStatValueHolder> allBuffs;

      effLock.lock();
      chrLock.lock();
      try {
         allBuffs = new LinkedList<>(effects.values());
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }

      for (MapleBuffStatValueHolder buffStatValueHolder : allBuffs) {
         if (buffStatValueHolder.effect.getBuffSourceId() == sourceId) {
            return true;
         }
      }
      return false;
   }

   private List<Pair<MapleBuffStat, Integer>> getActiveStatIncreasesFromSourceId(int sourceId) { // already under effLock & chrLock
      List<Pair<MapleBuffStat, Integer>> ret = new ArrayList<>();
      List<Pair<MapleBuffStat, Integer>> statIncreases = new ArrayList<>();

      for (Entry<MapleBuffStat, MapleBuffStatValueHolder> bel : buffEffects.get(sourceId).entrySet()) {
         MapleBuffStat mbs = bel.getKey();
         MapleBuffStatValueHolder buffStatValueHolder = effects.get(bel.getKey());

         Pair<MapleBuffStat, Integer> p;
         if (buffStatValueHolder != null) {
            p = new Pair<>(mbs, buffStatValueHolder.value);
         } else {
            p = new Pair<>(mbs, 0);
         }

         if (!BuffStatProcessor.getInstance().isSingletonStatup(mbs)) {
            ret.add(p);
         } else {
            statIncreases.add(p);
         }
      }

      ret.sort(Comparator.comparing(Pair::getLeft));

      if (!statIncreases.isEmpty()) {
         statIncreases.sort(Comparator.comparing(Pair::getLeft));

         ret.addAll(statIncreases);
      }

      return ret;
   }

   private void addItemEffectHolder(Integer sourceId, long expirationTime,
                                    Map<MapleBuffStat, MapleBuffStatValueHolder> statIncreases) {
      buffEffects.put(sourceId, statIncreases);
      buffExpires.put(sourceId, expirationTime);
   }

   private boolean removeEffectFromItemEffectHolder(Integer sourceId, MapleBuffStat buffStat) {
      Map<MapleBuffStat, MapleBuffStatValueHolder> lbe = buffEffects.get(sourceId);

      if (lbe.remove(buffStat) != null) {
         buffEffectsCount.put(buffStat, (byte) (buffEffectsCount.get(buffStat) - 1));

         if (lbe.isEmpty()) {
            buffEffects.remove(sourceId);
            buffExpires.remove(sourceId);
         }

         return true;
      }

      return false;
   }

   private void removeItemEffectHolder(Integer sourceId) {
      Map<MapleBuffStat, MapleBuffStatValueHolder> be = buffEffects.remove(sourceId);
      if (be != null) {
         for (Entry<MapleBuffStat, MapleBuffStatValueHolder> bei : be.entrySet()) {
            buffEffectsCount.put(bei.getKey(), (byte) (buffEffectsCount.get(bei.getKey()) - 1));
         }
      }

      buffExpires.remove(sourceId);
   }

   private MapleBuffStatValueHolder fetchBestEffectFromItemEffectHolder(MapleBuffStat mbs) {
      Pair<Integer, Integer> max = new Pair<>(Integer.MIN_VALUE, 0);
      MapleBuffStatValueHolder buffStatValueHolder = null;
      for (Entry<Integer, Map<MapleBuffStat, MapleBuffStatValueHolder>> bpl : buffEffects.entrySet()) {
         MapleBuffStatValueHolder buffStatValueHolderInternal = bpl.getValue().get(mbs);
         if (buffStatValueHolderInternal != null) {
            if (!buffStatValueHolderInternal.effect.isActive(this)) {
               continue;
            }

            if (buffStatValueHolderInternal.value > max.left) {
               max = new Pair<>(buffStatValueHolderInternal.value, buffStatValueHolderInternal.effect.getStatups().size());
               buffStatValueHolder = buffStatValueHolderInternal;
            } else if (buffStatValueHolderInternal.value == max.left
                  && buffStatValueHolderInternal.effect.getStatups().size() > max.right) {
               max = new Pair<>(buffStatValueHolderInternal.value, buffStatValueHolderInternal.effect.getStatups().size());
               buffStatValueHolder = buffStatValueHolderInternal;
            }
         }
      }

      if (buffStatValueHolder != null) {
         effects.put(mbs, buffStatValueHolder);
      }
      return buffStatValueHolder;
   }

   private void extractBuffValue(int sourceId, MapleBuffStat stat) {
      chrLock.lock();
      try {
         removeEffectFromItemEffectHolder(sourceId, stat);
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
            for (Entry<MapleBuffStat, MapleBuffStatValueHolder> valueHolderEntry : bpl.getValue().entrySet()) {
               System.out.print(valueHolderEntry.getKey().name() + valueHolderEntry.getValue().value + ", ");
            }
            System.out.println();
         }
         System.out.println("-------------------");

         System.out.println("IN ACTION:");
         for (Entry<MapleBuffStat, MapleBuffStatValueHolder> bpl : effects.entrySet()) {
            System.out.println(bpl.getKey().name() + " -> " + MapleItemInformationProvider.getInstance()
                  .getName(bpl.getValue().effect.getSourceId()));
         }
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   public void cancelAllBuffs(boolean softCancel) {
      if (softCancel) {
         effLock.lock();
         chrLock.lock();
         try {
            cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
            cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
            cancelEffectFromBuffStat(MapleBuffStat.COMBO);

            effects.clear();

            for (Integer sourceId : new ArrayList<>(buffEffects.keySet())) {
               removeItemEffectHolder(sourceId);
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
               for (Entry<MapleBuffStat, MapleBuffStatValueHolder> valueHolderEntry : bpl.getValue().entrySet()) {
                  mseBuffs.put(valueHolderEntry.getValue().effect, valueHolderEntry.getValue().startTime);
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

   private List<Pair<MapleBuffStat, MapleBuffStatValueHolder>> deregisterBuffStats(
         Map<MapleBuffStat, MapleBuffStatValueHolder> stats) {
      chrLock.lock();
      try {
         List<Pair<MapleBuffStat, MapleBuffStatValueHolder>> effectsToCancel = new ArrayList<>(stats.size());
         for (Entry<MapleBuffStat, MapleBuffStatValueHolder> stat : stats.entrySet()) {
            int sourceId = stat.getValue().effect.getBuffSourceId();

            if (!buffEffects.containsKey(sourceId)) {
               buffExpires.remove(sourceId);
            }

            MapleBuffStat mbs = stat.getKey();
            effectsToCancel.add(new Pair<>(mbs, stat.getValue()));

            MapleBuffStatValueHolder buffStatValueHolder = effects.get(mbs);
            if (buffStatValueHolder != null && buffStatValueHolder.effect.getBuffSourceId() == sourceId) {
               buffStatValueHolder.bestApplied = true;
               effects.remove(mbs);

               if (mbs == MapleBuffStat.RECOVERY) {
                  scheduler.cancel(MapleCharacterScheduler.Type.RECOVERY);
               } else if (mbs == MapleBuffStat.SUMMON || mbs == MapleBuffStat.PUPPET) {
                  int summonId = buffStatValueHolder.effect.getSourceId();

                  MapleSummon summon = summons.get(summonId);
                  if (summon != null) {
                     MasterBroadcaster.getInstance()
                           .sendToAllInMapRange(getMap(), new RemoveSummon(summon.getOwner().getId(), summon.objectId(), true),
                                 summon.position());
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
               } else if (mbs == MapleBuffStat.DRAGON_BLOOD) {
                  scheduler.cancel(MapleCharacterScheduler.Type.DRAGON_BLOOD);
               } else if (mbs == MapleBuffStat.HP_RECOVERY || mbs == MapleBuffStat.MP_RECOVERY) {
                  if (mbs == MapleBuffStat.HP_RECOVERY) {
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

      prtLock.lock();
      effLock.lock();
      try {
         ret = cancelEffect(effect, overwrite, startTime, true);
      } finally {
         prtLock.unlock();
         effLock.unlock();
      }

      if (effect.isMagicDoor() && ret) {
         prtLock.lock();
         effLock.lock();
         try {
            if (!hasBuffFromSourceId(Priest.MYSTIC_DOOR)) {
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

      boolean active = mse.isActive(this);
      if (active) {
         return !activeEffects.contains(mse);
      } else {
         return activeEffects.contains(mse);
      }
   }

   public void updateActiveEffects() {
      effLock.lock();
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

            MapleBuffStatValueHolder buffStatValueHolder = effects.get(mbs);
            if (buffStatValueHolder != null) {
               buffStatValueHolder.effect.getStatups().forEach(pair -> retrievedStats.add(pair.getLeft()));
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

   private List<Pair<MapleBuffStat, MapleBuffStatValueHolder>> cancelEffectInternal(MapleStatEffect effect, boolean overwrite,
                                                                                    long startTime,
                                                                                    Set<MapleBuffStat> removedStats) {
      Map<MapleBuffStat, MapleBuffStatValueHolder> buffStatValueHolderMap = null;
      MapleBuffStat buffStat;
      if (!overwrite) {
         buffStatValueHolderMap = extractCurrentBuffStats(effect);
      } else if ((buffStat = BuffStatProcessor.getInstance().getSingletonStatupFromEffect(effect))
            != null) {   // removing all effects of a buff having non-shareable buff stat.
         MapleBuffStatValueHolder buffStatValueHolder = effects.get(buffStat);
         if (buffStatValueHolder != null) {
            buffStatValueHolderMap = extractCurrentBuffStats(buffStatValueHolder.effect);
         }
      }

      if (buffStatValueHolderMap
            == null) {            // all else, is dropping ALL current stat increases that uses same stats as the given effect
         buffStatValueHolderMap = extractLeastRelevantStatEffectsIfFull(effect);
      }

      if (effect.isMapChair()) {
         stopChairTask();
      }

      List<Pair<MapleBuffStat, MapleBuffStatValueHolder>> toCancel = deregisterBuffStats(buffStatValueHolderMap);
      if (effect.isMonsterRiding()) {
         this.getClient().getWorldServer().unregisterMountHunger(getId());
         modifyMount(mapleMount -> mapleMount.setActive(false));
      }

      if (!overwrite) {
         removedStats.addAll(buffStatValueHolderMap.keySet());
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
               MapleBuffStatValueHolder buffStatValueHolder = bel.getValue().get(stat);
               if (buffStatValueHolder != null) {
                  cancelList.add(new Pair<>(bel.getKey(), buffStatValueHolder));
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
            for (Entry<MapleBuffStat, MapleBuffStatValueHolder> valueHolderEntry : buffList.entrySet()) {
               stats.put(valueHolderEntry.getKey(), valueHolderEntry.getValue());
               buffEffectsCount.put(valueHolderEntry.getKey(), (byte) (buffEffectsCount.get(valueHolderEntry.getKey()) - 1));
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

         for (Entry<Integer, Map<MapleBuffStat, MapleBuffStatValueHolder>> mapEntry : buffEffects.entrySet()) {
            for (Entry<MapleBuffStat, MapleBuffStatValueHolder> valueHolderEntry : mapEntry.getValue().entrySet()) {
               MapleBuffStat mbs = valueHolderEntry.getKey();
               Byte b = stats.get(mbs);

               if (b != null) {
                  stats.put(mbs, (byte) (b + 1));
                  if (valueHolderEntry.getValue().value < minStatBuffs.get(mbs).value) {
                     minStatBuffs.put(mbs, valueHolderEntry.getValue());
                  }
               } else {
                  stats.put(mbs, (byte) 1);
                  minStatBuffs.put(mbs, valueHolderEntry.getValue());
               }
            }
         }

         Set<MapleBuffStat> effectStatIncreases = effect.getStatups().stream().map(Pair::getLeft).collect(Collectors.toSet());
         for (Entry<MapleBuffStat, Byte> it : stats.entrySet()) {
            boolean uniqueBuff = BuffStatProcessor.getInstance().isSingletonStatup(it.getKey());

            if (it.getValue() >= (!uniqueBuff ? YamlConfig.config.server.MAX_MONITORED_BUFFSTATS : 1) && effectStatIncreases
                  .contains(it.getKey())) {
               MapleBuffStatValueHolder buffStatValueHolder = minStatBuffs.get(it.getKey());

               Map<MapleBuffStat, MapleBuffStatValueHolder> buffStatValueHolderMap =
                     buffEffects.get(buffStatValueHolder.effect.getBuffSourceId());
               buffStatValueHolderMap.remove(it.getKey());
               buffEffectsCount.put(it.getKey(), (byte) (buffEffectsCount.get(it.getKey()) - 1));

               if (buffStatValueHolderMap.isEmpty()) {
                  buffEffects.remove(buffStatValueHolder.effect.getBuffSourceId());
               }
               extractedStatBuffs.put(it.getKey(), buffStatValueHolder);
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
         PacketCreator.announce(client, new CancelBuff(inactiveStats));
         MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new CancelForeignBuff(getId(), inactiveStats), false, this);
      }
   }

   private List<Pair<Integer, Pair<MapleStatEffect, Long>>> propagatePriorityBuffEffectUpdates(Set<MapleBuffStat> retrievedStats) {
      Map<MapleBuffStatValueHolder, MapleStatEffect> yokeStats = new LinkedHashMap<>();

      Set<MapleBuffStatValueHolder> valueHolderLinkedHashSet = new LinkedHashSet<>(getAllStatIncreases());

      for (MapleBuffStatValueHolder buffStatValueHolder : valueHolderLinkedHashSet) {
         MapleStatEffect mse = buffStatValueHolder.effect;
         int buffSourceId = mse.getBuffSourceId();
         if (BuffStatProcessor.getInstance().isPriorityBuffSourceId(buffSourceId) && !hasActiveBuff(buffSourceId)) {
            for (Pair<MapleBuffStat, Integer> ps : mse.getStatups()) {
               MapleBuffStat mbs = ps.getLeft();
               if (retrievedStats.contains(mbs)) {
                  MapleBuffStatValueHolder valueHolder = effects.get(mbs);
                  yokeStats.put(buffStatValueHolder, valueHolder.effect);
               }
            }
         }
      }

      return yokeStats.entrySet().stream()
            .map(entry -> new Pair<>(entry.getValue().getBuffSourceId(),
                  new Pair<>(entry.getKey().effect, entry.getKey().startTime)))
            .collect(Collectors.toList());
   }

   private void propagateBuffEffectUpdates(Map<Integer, Pair<MapleStatEffect, Long>> retrievedEffects,
                                           Set<MapleBuffStat> retrievedStats, Set<MapleBuffStat> removedStats) {
      cancelInactiveBuffStats(retrievedStats, removedStats);
      if (retrievedStats.isEmpty()) {
         return;
      }

      Map<MapleBuffStat, Pair<Integer, MapleStatEffect>> maxBuffValue = new LinkedHashMap<>();
      for (MapleBuffStat mbs : retrievedStats) {
         MapleBuffStatValueHolder buffStatValueHolder = effects.get(mbs);
         if (buffStatValueHolder != null) {
            retrievedEffects.put(buffStatValueHolder.effect.getBuffSourceId(),
                  new Pair<>(buffStatValueHolder.effect, buffStatValueHolder.startTime));
         }

         maxBuffValue.put(mbs, new Pair<>(Integer.MIN_VALUE, null));
      }

      Map<MapleStatEffect, Integer> updateEffects = new LinkedHashMap<>();

      List<MapleStatEffect> recalcMseList = new LinkedList<>();
      for (Entry<Integer, Pair<MapleStatEffect, Long>> re : retrievedEffects.entrySet()) {
         recalcMseList.add(re.getValue().getLeft());
      }

      boolean isMagicianJob = this.getJobStyle() == MapleJob.MAGICIAN;
      do {
         List<MapleStatEffect> mseList = recalcMseList;
         recalcMseList = new LinkedList<>();

         for (MapleStatEffect mse : mseList) {
            int maxEffectiveStatIncrease = Integer.MIN_VALUE;
            for (Pair<MapleBuffStat, Integer> st : mse.getStatups()) {
               MapleBuffStat mbs = st.getLeft();

               boolean relevantStatIncrease = true;
               if (mbs == MapleBuffStat.WEAPON_ATTACK) {  // not relevant for magicians
                  if (isMagicianJob) {
                     relevantStatIncrease = false;
                  }
               } else if (mbs == MapleBuffStat.MAGIC_ATTACK) { // not relevant for non-magicians
                  if (!isMagicianJob) {
                     relevantStatIncrease = false;
                  }
               }

               Pair<Integer, MapleStatEffect> mbv = maxBuffValue.get(mbs);
               if (mbv == null) {
                  continue;
               }

               if (mbv.getLeft() < st.getRight()) {
                  MapleStatEffect statEffect = mbv.getRight();
                  if (statEffect != null) {
                     recalcMseList.add(statEffect);
                  }

                  maxBuffValue.put(mbs, new Pair<>(st.getRight(), mse));

                  if (relevantStatIncrease) {
                     if (maxEffectiveStatIncrease < st.getRight()) {
                        maxEffectiveStatIncrease = st.getRight();
                     }
                  }
               }
            }

            updateEffects.put(mse, maxEffectiveStatIncrease);
         }
      } while (!recalcMseList.isEmpty());

      List<MapleStatEffect> updateEffectsList = BuffStatProcessor.getInstance().sortEffectsList(updateEffects);
      List<Pair<Integer, Pair<MapleStatEffect, Long>>> toUpdateEffects = updateEffectsList.stream()
            .map(entry -> new Pair<>(entry.getBuffSourceId(), retrievedEffects.get(entry.getBuffSourceId())))
            .collect(Collectors.toList());

      List<Pair<MapleBuffStat, Integer>> activeStatIncreases = new LinkedList<>();
      for (Pair<Integer, Pair<MapleStatEffect, Long>> pairs : toUpdateEffects) {
         Pair<MapleStatEffect, Long> msel = pairs.getRight();

         activeStatIncreases.addAll(getActiveStatIncreasesFromSourceId(pairs.getLeft()));

         msel.getLeft().updateBuffEffect(this, activeStatIncreases, msel.getRight());
         activeStatIncreases.clear();
      }

      List<Pair<Integer, Pair<MapleStatEffect, Long>>> priorityEffects = propagatePriorityBuffEffectUpdates(retrievedStats);
      for (Pair<Integer, Pair<MapleStatEffect, Long>> lmse : priorityEffects) {
         Pair<MapleStatEffect, Long> msel = lmse.getRight();

         activeStatIncreases.addAll(getActiveStatIncreasesFromSourceId(lmse.getLeft()));

         msel.getLeft().updateBuffEffect(this, activeStatIncreases, msel.getRight());
         activeStatIncreases.clear();
      }

      if (this.isRidingBattleship()) {
         List<Pair<MapleBuffStat, Integer>> statIncreases = Collections.singletonList(new Pair<>(MapleBuffStat.MONSTER_RIDING, 0));
         PacketCreator.announce(this, new GiveBuff(1932000, 5221006, statIncreases));
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
         int healInterval = (YamlConfig.config.server.USE_ULTRA_RECOVERY) ? 2000 : 5000;
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
               PacketCreator.announce(client, new ShowOwnRecovery(heal));
               MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new ShowRecovery(id, heal), false, MapleCharacter.this);
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

      prtLock.lock();
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
         if (YamlConfig.config.server.USE_BUFF_MOST_SIGNIFICANT) {
            toDeploy = new LinkedHashMap<>();
            Map<Integer, Pair<MapleStatEffect, Long>> retrievedEffects = new LinkedHashMap<>();
            Set<MapleBuffStat> retrievedStats = new LinkedHashSet<>();

            for (Entry<MapleBuffStat, MapleBuffStatValueHolder> statUp : appliedStatUps.entrySet()) {
               MapleBuffStatValueHolder buffStatValueHolder = effects.get(statUp.getKey());
               MapleBuffStatValueHolder buffStatValueHolder1 = statUp.getValue();

               if (active) {
                  if (buffStatValueHolder == null || buffStatValueHolder.value < buffStatValueHolder1.value || (
                        buffStatValueHolder.value == buffStatValueHolder1.value
                              && buffStatValueHolder.effect.getStatups().size() <= buffStatValueHolder1.effect.getStatups()
                              .size())) {
                     toDeploy.put(statUp.getKey(), buffStatValueHolder1);
                  } else {
                     if (!BuffStatProcessor.getInstance().isSingletonStatup(statUp.getKey())) {
                        for (Pair<MapleBuffStat, Integer> mbs : buffStatValueHolder.effect.getStatups()) {
                           retrievedStats.add(mbs.getLeft());
                        }
                     }
                  }
               }

               addItemEffectHolderCount(statUp.getKey());
            }

            Set<MapleBuffStat> updated = appliedStatUps.keySet();
            for (MapleBuffStatValueHolder buffStatValueHolder : this.getAllStatIncreases()) {
               if (BuffStatProcessor.getInstance().isPriorityBuffSourceId(buffStatValueHolder.effect.getBuffSourceId())) {
                  for (Pair<MapleBuffStat, Integer> p : buffStatValueHolder.effect.getStatups()) {
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
         prtLock.unlock();
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
         PacketCreator.announce(client, new ShowOwnBuffEffect(beholder, 2));
         MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new SummonSkill(getId(), beholder, 5), true, MapleCharacter.this);
         MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new ShowOwnBuffEffect(beholder, 2), false, MapleCharacter.this);
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
         PacketCreator.announce(client, new ShowOwnBuffEffect(beholder, 2));
         MasterBroadcaster.getInstance()
               .sendToAllInMap(getMap(), new SummonSkill(getId(), beholder, (int) (Math.random() * 3) + 6), true,
                     MapleCharacter.this);
         MasterBroadcaster.getInstance()
               .sendToAllInMap(getMap(), new ShowBuffEffect(getId(), beholder, 2, (byte) 3), false, MapleCharacter.this);
      }, buffInterval, buffInterval);
   }

   public boolean unregisterChairBuff() {
      if (!YamlConfig.config.server.USE_CHAIR_EXTRAHEAL) {
         return false;
      }

      int skillId = ChairProcessor.getInstance().getJobMapChair(job);

      return applyIfHasSkill(skillId, (skill, skillLevel) -> {
         MapleStatEffect statEffect = skill.getEffect(skillLevel);
         return cancelEffect(statEffect, false, -1);
      });
   }

   private boolean registerChairBuff() {
      if (!YamlConfig.config.server.USE_CHAIR_EXTRAHEAL) {
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
      return this.chalkText;
   }

   public void setChalkboard(String text) {
      this.chalkText = text;
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

   private List<MapleQuestStatus> getQuests() {
      synchronized (quests) {
         return new ArrayList<>(quests.values());
      }
   }

   public final List<MapleQuestStatus> getCompletedQuests() {
      List<MapleQuestStatus> ret = new LinkedList<>();
      for (MapleQuestStatus qs : getQuests()) {
         if (qs.getStatus().equals(MapleQuestStatus.Status.COMPLETED)) {
            ret.add(qs);
         }
      }
      return Collections.unmodifiableList(ret);
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
         return party.map(mapleParty -> Collections.unmodifiableCollection(mapleParty.getDoors().values()))
               .orElseGet(() -> pdoor != null ? Collections.singleton(pdoor) : new LinkedHashSet<>());
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
      Optional<MapleParty> chrParty;
      prtLock.lock();
      try {
         if (!partyUpdate) {
            pdoor = door;
         }

         chrParty = getParty();
         chrParty.ifPresent(reference -> reference.addDoor(id, door));
      } finally {
         prtLock.unlock();
      }

      chrParty.ifPresent(this::silentPartyUpdateInternal);
   }

   public MapleDoor removePartyDoor(boolean partyUpdate) {
      MapleDoor ret = null;
      Optional<MapleParty> chrParty;

      prtLock.lock();
      try {
         chrParty = getParty();
         chrParty.ifPresent(reference -> reference.removeDoor(id));

         if (!partyUpdate) {
            ret = pdoor;
            pdoor = null;
         }
      } finally {
         prtLock.unlock();
      }

      chrParty.ifPresent(this::silentPartyUpdateInternal);
      return ret;
   }

   public void removePartyDoor(MapleParty formerParty) {    // player is no longer registered at this party
      formerParty.removeDoor(id);
   }

   public int getEnergyBar() {
      return energyBar;
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
            PacketCreator.announce(client, new PetExceptionList(this.getId(), pe.getKey(), petIndex, new ArrayList<>(exclItems)));

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
            PacketCreator.announce(c, new PetExceptionList(this.getId(), pe.getKey(), petIndex, new ArrayList<>(exclItems)));
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

   public int getGachaponExperience() {
      return gachaponExp.get();
   }

   public void setGachaponExperience(int amount) {
      this.gachaponExp.set(amount);
   }

   public boolean hasNoviceExpRate() {
      return YamlConfig.config.server.USE_ENFORCE_NOVICE_EXPRATE && isBeginnerJob() && level < 11;
   }

   public int getExpRate() {
      if (hasNoviceExpRate()) {
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
            rate += mseMeso.getCardRate(mapId, itemId);
         }
      } else {
         MapleStatEffect mseItem = getBuffEffect(MapleBuffStat.ITEM_UP_BY_ITEM);
         if (mseItem != null) {
            rate += mseItem.getCardRate(mapId, itemId);
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
      return guildId;
   }

   public void setGuildId(int _id) {
      guildId = _id;
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
      Point pos = this.position();
      pos.y -= 6;

      if (map.getFootholds().findBelow(pos) == null) {
         return 0;
      } else {
         return map.getFootholds().findBelow(pos).firstPoint().y;
      }
   }

   public int getMapId() {
      if (map != null) {
         return map.getId();
      }
      return mapId;
   }

   public void setMapId(int mapId) {
      this.mapId = mapId;
   }

   public Ring getMarriageRing() {
      return partnerId > 0 ? marriageRing : null;
   }

   public int getMasterLevel(int skillId) {
      Optional<Skill> skill = SkillFactory.getSkill(skillId);
      if (skill.isEmpty() || skills.get(skill.get()) == null) {
         return 0;
      }
      return skills.get(skill.get()).masterLevel();
   }

   public int getMasterLevel(Skill skill) {
      if (skills.get(skill) == null) {
         return 0;
      }
      return skills.get(skill).masterLevel();
   }

   public int getTotalStr() {
      return localStrength;
   }

   public int getTotalDex() {
      return localDexterity;
   }

   public int getTotalInt() {
      return localIntelligence;
   }

   public int getTotalLuk() {
      return localLuck;
   }

   public int getTotalMagic() {
      return localMagic;
   }

   public int getTotalWeaponAttack() {
      return localWeaponAttack;
   }

   public int getMaxClassLevel() {
      return isCygnus() ? 120 : 200;
   }

   private int getMaxLevel() {
      if (!YamlConfig.config.server.USE_ENFORCE_JOB_LEVEL_RANGE || isGmJob()) {
         return getMaxClassLevel();
      }

      return GameConstants.getJobMaxLevel(job);
   }

   public int getMeso() {
      return meso.get();
   }

   //---- \/ \/ \/ \/ \/ \/ \/  NOT TESTED  \/ \/ \/ \/ \/ \/ \/ \/ \/ ----

   public int getMerchantMeso() {
      return merchantMeso;
   }

   public void setMerchantMeso(int set) {
      DatabaseConnection.getInstance()
            .withConnection(connection -> CharacterAdministrator.getInstance().setMerchantMesos(connection, id, set));
      merchantMeso = set;
   }

   public int getMerchantNetMeso() {
      int elapsedDays = DatabaseConnection.getInstance()
            .withConnectionResult(connection -> FredStorageProvider.getInstance().get(connection, id)).orElseThrow();

      if (elapsedDays > 100) {
         elapsedDays = 100;
      }

      long netMeso = merchantMeso;
      netMeso = (netMeso * (100 - elapsedDays)) / 100;
      return (int) netMeso;
   }

   public int getMesosTraded() {
      return mesosTraded;
   }

   public int getMessengerPosition() {
      return messengerPosition;
   }

   public void setMessengerPosition(int position) {
      this.messengerPosition = position;
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
         return switch (type) {
            case WIN -> omok.wins();
            case LOSS -> omok.losses();
            default -> omok.ties();
         };
      } else {
         return switch (type) {
            case WIN -> matchCard.wins();
            case LOSS -> matchCard.losses();
            default -> matchCard.ties();
         };
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
      return mount;
   }

   public void modifyMount(Function<MapleMount, MapleMount> modifier) {
      this.mount = modifier.apply(this.mount);
   }

   public Boolean hasMount() {
      return mount != null;
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

   public Optional<MapleParty> getParty() {
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
            party = Optional.empty();
         } else {
            party = Optional.of(p);
         }
      } finally {
         prtLock.unlock();
      }
   }

   public int getPartyId() {
      prtLock.lock();
      try {
         return party.map(MapleParty::getId).orElse(-1);
      } finally {
         prtLock.unlock();
      }
   }

   public List<MapleCharacter> getPartyMembersOnline() {
      prtLock.lock();
      try {
         return party.map(MapleParty::getPartyMembers).orElse(Collections.emptyList()).stream()
               .map(MaplePartyCharacter::getPlayer)
               .flatMap(Optional::stream)
               .filter(MapleCharacter::isLoggedInWorld)
               .collect(Collectors.toList());
      } finally {
         prtLock.unlock();
      }
   }

   public List<MapleCharacter> getPartyMembersOnSameMap() {
      int thisMapHash = this.getMap().hashCode();

      prtLock.lock();
      try {
         return party.map(MapleParty::getPartyMembers).orElse(Collections.emptyList()).stream()
               .map(MaplePartyCharacter::getPlayer)
               .flatMap(Optional::stream)
               .filter(character -> character.getMap() != null && character.getMap().hashCode() == thisMapHash && character
                     .isLoggedInWorld())
               .collect(Collectors.toList());
      } finally {
         prtLock.unlock();
      }
   }

   public boolean isPartyMember(MapleCharacter chr) {
      return isPartyMember(chr.getId());
   }

   public boolean isPartyMember(int cid) {
      prtLock.lock();
      try {
         return party.map(reference -> reference.isMember(cid)).orElse(false);
      } finally {
         prtLock.unlock();
      }
   }

   public MaplePlayerShop getPlayerShop() {
      return playerShop;
   }

   public void setPlayerShop(MaplePlayerShop playerShop) {
      this.playerShop = playerShop;
   }

   public MapleRockPaperScissor getRPS() {
      return rps;
   }

   public void setRPS(MapleRockPaperScissor rps) {
      this.rps = rps;
   }

   public void setGMLevel(int level) {
      this.gmLevel = Math.min(level, 6);
      this.gmLevel = Math.max(level, 0);
      whiteChat = gmLevel >= 4;
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
      MapleTradeProcessor.getInstance().cancelTrade(this, MapleTradeResult.PARTNER_CANCEL);
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
               iItem = iItem.updateQuantity((short) (shopItem.bundles() * iItem.quantity()));
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

   public boolean hasSummonedPet() {
      petLock.lock();
      try {
         return Arrays.stream(pets).anyMatch(MaplePet::summoned);
      } finally {
         petLock.unlock();
      }
   }

   public MaplePet[] getPets() {
      petLock.lock();
      try {
         return Arrays.copyOf(pets, pets.length);
      } finally {
         petLock.unlock();
      }
   }

   public MaplePet[] updateAndGetPets(Function<MaplePet, MaplePet> modifier) {
      petLock.lock();
      try {
         pets = Arrays.stream(pets).filter(Objects::nonNull).map(modifier).toArray(MaplePet[]::new);
         return Arrays.copyOf(pets, pets.length);
      } finally {
         petLock.unlock();
      }
   }

   public MaplePet updateAndGetPet(int slot, Function<MaplePet, MaplePet> modifier) {
      if (slot < 0) {
         return null;
      }

      petLock.lock();
      try {
         pets[slot] = modifier.apply(pets[slot]);
         return pets[slot];
      } finally {
         petLock.unlock();
      }
   }

   public MaplePet getPet(int index) {
      if (index < 0 || index >= pets.length) {
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

   public MapleQuestStatus getQuest(final int quest) {
      return getQuest(QuestProcessor.getInstance().getQuest(quest));
   }

   public MapleQuestStatus getQuest(MapleQuest quest) {
      synchronized (quests) {
         short questId = quest.id();
         MapleQuestStatus qs = quests.get(questId);
         if (qs == null) {
            qs = new MapleQuestStatus(quest, MapleQuestStatus.Status.NOT_STARTED);
            quests.put(questId, qs);
         }
         return qs;
      }
   }

   public final MapleQuestStatus getQuestNAdd(final MapleQuest quest) {
      synchronized (quests) {
         if (!quests.containsKey(quest.id())) {
            final MapleQuestStatus status = new MapleQuestStatus(quest, MapleQuestStatus.Status.NOT_STARTED);
            quests.put(quest.id(), status);
            return status;
         }
         return quests.get(quest.id());
      }
   }

   public final MapleQuestStatus getQuestNoAdd(final MapleQuest quest) {
      synchronized (quests) {
         return quests.get(quest.id());
      }
   }

   public boolean needQuestItem(int questId, int itemId) {
      if (questId <= 0) { //For non quest items :3
         return true;
      }

      int amountNeeded, questStatus = this.getQuestStatus(questId);
      if (questStatus == 0) {
         amountNeeded = QuestProcessor.getInstance().getQuest(questId).getStartItemAmountNeeded(itemId);
         if (amountNeeded == Integer.MIN_VALUE) {
            return false;
         }
      } else if (questStatus != 1) {
         return false;
      } else {
         amountNeeded = QuestProcessor.getInstance().getQuest(questId).getCompleteItemAmountNeeded(itemId);
         if (amountNeeded == Integer.MAX_VALUE) {
            return true;
         }
      }

      return getInventory(ItemConstants.getInventoryType(itemId)).countById(itemId) < amountNeeded;
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

   public void setSlot(int slotId) {
      slots = slotId;
   }

   public final List<MapleQuestStatus> getStartedQuests() {
      List<MapleQuestStatus> ret = new LinkedList<>();
      for (MapleQuestStatus qs : getQuests()) {
         if (qs.getStatus().equals(MapleQuestStatus.Status.STARTED)) {
            ret.add(qs);
         }
      }
      return Collections.unmodifiableList(ret);
   }

   public MapleStatEffect getStatForBuff(MapleBuffStat effect) {
      effLock.lock();
      chrLock.lock();
      try {
         MapleBuffStatValueHolder buffStatValueHolder = effects.get(effect);
         if (buffStatValueHolder == null) {
            return null;
         }
         return buffStatValueHolder.effect;
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

   public Optional<MapleTrade> getTrade() {
      return trade;
   }

   public void setTrade(MapleTrade trade) {
      this.trade = Optional.ofNullable(trade);
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
      return visibleMapObjects.toArray(new MapleMapObject[0]);
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

   public void giveCoolDowns(final int skillId, long startTime, long length) {
      if (skillId == 5221999) {
         this.battleShipHp = (int) length;
         addCoolDown(skillId, 0, length);
      } else {
         long timeNow = Server.getInstance().getCurrentTime();
         int time = (int) ((length + startTime) - timeNow);
         addCoolDown(skillId, timeNow, time);
      }
   }

   public int gmLevel() {
      return gmLevel;
   }

   private void guildUpdate() {
      mgc.setLevel(level);
      mgc.setJobId(job.getId());

      if (this.guildId < 1) {
         return;
      }

      try {
         MapleGuildProcessor.getInstance().memberLevelJobUpdate(mgc);
         getGuild()
               .map(MapleGuild::getAllianceId)
               .ifPresent(id -> Server.getInstance()
                     .allianceMessage(id, new UpdateAllianceJobLevel(id, this.getGuildId(), getId(), getLevel(), getJob().getId()),
                           getId(), -1));
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void handleEnergyChargeGain() {
      Optional<Skill> energyCharge;
      if (isCygnus()) {
         energyCharge = SkillFactory.getSkill(ThunderBreaker.ENERGY_CHARGE);
      } else {
         energyCharge = SkillFactory.getSkill(Marauder.ENERGY_CHARGE);
      }

      energyCharge.ifPresent(skill -> {
         MapleStatEffect statEffect = skill.getEffect(getSkillLevel(skill));
         TimerManager tMan = TimerManager.getInstance();
         if (energyBar < 10000) {
            energyBar += 102;
            if (energyBar > 10000) {
               energyBar = 10000;
            }
            List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.ENERGY_CHARGE, energyBar));
            setBuffedValue(MapleBuffStat.ENERGY_CHARGE, energyBar);
            PacketCreator.announce(client, new GiveBuff(energyBar, 0, stat));
            PacketCreator.announce(this, new ShowOwnBuffEffect(skill.getId(), 2));
            getMap().broadcastMessage(this, new ShowBuffEffect(id, skill.getId(), 2, (byte) 3));
            getMap().broadcastMessage(this, new GiveForeignBuff(energyBar, stat));
         }
         if (energyBar >= 10000 && energyBar < 11000) {
            energyBar = 15000;
            final MapleCharacter chr = this;
            tMan.schedule(() -> {
               energyBar = 0;
               List<Pair<MapleBuffStat, Integer>> stat =
                     Collections.singletonList(new Pair<>(MapleBuffStat.ENERGY_CHARGE, energyBar));
               setBuffedValue(MapleBuffStat.ENERGY_CHARGE, energyBar);
               PacketCreator.announce(client, new GiveBuff(energyBar, 0, stat));
               getMap().broadcastMessage(chr, new GiveForeignBuff(energyBar, stat));
            }, statEffect.getDuration());
         }
      });
   }

   public void handleOrbConsume() {
      int skillId = isCygnus() ? DawnWarrior.COMBO : Crusader.COMBO;
      SkillFactory.getSkill(skillId).ifPresent(combo -> {
         List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.COMBO, 1));
         setBuffedValue(MapleBuffStat.COMBO, 1);
         PacketCreator.announce(client, new GiveBuff(skillId,
               combo.getEffect(getSkillLevel(combo)).getDuration() + (int) ((getBuffedStartTime(MapleBuffStat.COMBO) - System
                     .currentTimeMillis())), stat));
         MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new GiveForeignBuff(getId(), stat), false, this);
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
      lastFameTime = System.currentTimeMillis();
      lastMonthFameIds.add(to.getId());
      DatabaseConnection.getInstance()
            .withConnection(connection -> FameLogAdministrator.getInstance().addForCharacter(connection, getId(), to.getId()));
   }

   public boolean hasMerchant() {
      return hasMerchant;
   }

   public boolean haveItem(int itemId) {
      return getItemQuantity(itemId, ItemConstants.isEquipment(itemId)) > 0;
   }

   public boolean hasEmptySlot(byte invType) {
      return getInventory(MapleInventoryType.getByType(invType)).getNextFreeSlot() > -1;
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
      return lastBuyback + YamlConfig.config.server.BUYBACK_COOLDOWN_MINUTES * 60 * 1000;
   }

   private boolean isBuybackInvincible() {
      return Server.getInstance().getCurrentTime() - lastBuyback < 4200;
   }

   private int getBuybackFee() {
      float fee = YamlConfig.config.server.BUYBACK_FEE;
      int grade = Math.min(Math.max(level, 30), 120) - 30;

      fee += (grade * YamlConfig.config.server.BUYBACK_LEVEL_STACK_FEE);
      if (YamlConfig.config.server.USE_BUYBACK_WITH_MESOS) {
         fee *= YamlConfig.config.server.BUYBACK_MESO_MULTIPLIER;
      }

      return (int) Math.floor(fee);
   }

   public void showBuybackInfo() {
      String s = "#eBUYBACK STATUS#n\r\n\r\nCurrent buyback fee: #b" + getBuybackFee() + " " + (
            YamlConfig.config.server.USE_BUYBACK_WITH_MESOS ? "mesos" : "NX") + "#k\r\n\r\n";

      long timeNow = Server.getInstance().getCurrentTime();
      boolean avail = true;
      if (!isAlive()) {
         long timeLapsed = timeNow - lastDeathTime;
         long timeRemaining = YamlConfig.config.server.BUYBACK_RETURN_MINUTES * 60 * 1000 - (timeLapsed + Math
               .max(0, getNextBuybackTime() - timeNow));
         if (timeRemaining < 1) {
            s += "Buyback #e#rUNAVAILABLE#k#n";
            avail = false;
         } else {
            s += "Buyback countdown: #e#b" + MapleStringUtil
                  .getTimeRemaining(YamlConfig.config.server.BUYBACK_RETURN_MINUTES * 60 * 1000 - timeLapsed) + "#k#n";
         }
         s += "\r\n";
      }

      if (timeNow < getNextBuybackTime() && avail) {
         s += "Buyback available in #r" + MapleStringUtil.getTimeRemaining(getNextBuybackTime() - timeNow) + "#k";
         s += "\r\n";
      } else {
         s += "Buyback #bavailable#k";
      }

      this.showHint(s);
   }

   public boolean couldBuyback() {
      long timeNow = Server.getInstance().getCurrentTime();

      if (timeNow - lastDeathTime > YamlConfig.config.server.BUYBACK_RETURN_MINUTES * 60 * 1000) {
         MessageBroadcaster.getInstance()
               .sendServerNotice(this, ServerNoticeType.PINK_TEXT, I18nMessage.from("BUY_BACK_TIME_EXPIRE"));
         return false;
      }

      long nextBuyBackTime = getNextBuybackTime();
      if (timeNow < nextBuyBackTime) {
         long timeLeft = nextBuyBackTime - timeNow;
         MessageBroadcaster.getInstance().sendServerNotice(this, ServerNoticeType.PINK_TEXT,
               I18nMessage.from("BUY_BACK_TIME_NEXT").with(MapleStringUtil.getTimeRemaining(timeLeft)));
         return false;
      }

      boolean usingMesos = YamlConfig.config.server.USE_BUYBACK_WITH_MESOS;
      int fee = getBuybackFee();

      if (!canBuyback(fee, usingMesos)) {
         if (usingMesos) {
            MessageBroadcaster.getInstance()
                  .sendServerNotice(this, ServerNoticeType.PINK_TEXT, I18nMessage.from("BUY_BACK_NOT_ENOUGH_MESOS").with(fee));
         } else {
            MessageBroadcaster.getInstance()
                  .sendServerNotice(this, ServerNoticeType.PINK_TEXT, I18nMessage.from("BUY_BACK_NOT_ENOUGH_NX").with(fee));
         }
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
         MapleBuffStatValueHolder buffStatValueHolder = effects.get(stat);
         if (buffStatValueHolder == null) {
            return false;
         }
         return buffStatValueHolder.effect.isSkill() && buffStatValueHolder.effect.getSourceId() == skill.getId();
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
         return getParty().map(reference -> reference.getLeaderId() == getId()).orElse(false);
      } finally {
         prtLock.unlock();
      }
   }

   public boolean isGuildLeader() {    // true on guild master or jr. master
      return guildId > 0 && guildRank < 3;
   }

   public boolean attemptCatchFish(int baitLevel) {
      return YamlConfig.config.server.USE_FISHING_SYSTEM && GameConstants.isFishingArea(mapId) && this.position().getY() > 0
            && ItemConstants.isFishingChair(chair.get()) && this.getWorldServer().registerFisherPlayer(this, baitLevel);
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
      if (YamlConfig.config.server.USE_ENFORCE_JOB_SP_RANGE && !GameConstants.hasSPTable(job)) {
         spGain = getSpGain(spGain, job);
      }

      if (spGain > 0) {
         gainSp(spGain, GameConstants.getSkillBook(job.getId()), true);
      }
   }

   public synchronized void levelUp(boolean takeExp) {
      boolean isBeginner = isBeginnerJob();
      if (YamlConfig.config.server.USE_AUTOASSIGN_STARTERS_AP && isBeginner && level < 11) {
         effLock.lock();
         statWriteLock.lock();
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
            statWriteLock.unlock();
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

      if (takeExp) {
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
               if (YamlConfig.config.server.PLAYERNPC_AUTODEPLOY) {
                  ThreadManager.getInstance()
                        .newTask(() -> MaplePlayerNPC.spawnPlayerNPC(GameConstants.getHallOfFameMapId(job), MapleCharacter.this));
               }

               final String names = (getMedalText() + name);
               MessageBroadcaster.getInstance().sendWorldServerNotice(world, ServerNoticeType.LIGHT_BLUE,
                     I18nMessage.from("LEVEL_200").with(names, maxClassLevel, names));
            }
         }

         level = maxClassLevel; //To prevent levels past the maximum
      }

      levelUpGainSp();

      effLock.lock();
      statWriteLock.lock();
      try {
         recalculateLocalStats();
         changeHpMp(localMaxHp, localMaxMp, true);

         List<Pair<MapleStat, Integer>> statIncreases = new ArrayList<>(10);
         statIncreases.add(new Pair<>(MapleStat.AVAILABLE_AP, remainingAp));
         statIncreases.add(new Pair<>(MapleStat.AVAILABLE_SP, remainingSp[GameConstants.getSkillBook(job.getId())]));
         statIncreases.add(new Pair<>(MapleStat.HP, hp));
         statIncreases.add(new Pair<>(MapleStat.MP, mp));
         statIncreases.add(new Pair<>(MapleStat.EXP, exp.get()));
         statIncreases.add(new Pair<>(MapleStat.LEVEL, level));
         statIncreases.add(new Pair<>(MapleStat.MAX_HP, clientMaxHp));
         statIncreases.add(new Pair<>(MapleStat.MAX_MP, clientMaxMp));
         statIncreases.add(new Pair<>(MapleStat.STR, str));
         statIncreases.add(new Pair<>(MapleStat.DEX, dex));

         PacketCreator.announce(client, new UpdatePlayerStats(statIncreases, true, this));
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
      }

      MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new ShowForeignEffect(getId(), 0), false, this);
      setMPC(new MaplePartyCharacter(this));
      silentPartyUpdate();

      if (this.guildId > 0) {
         getGuild().ifPresent(
               guild -> MasterBroadcaster.getInstance().sendToGuild(guild, new NotifyLevelUp(2, level, name), false, this.getId()));
      }

      if (level % 20 == 0) {
         if (YamlConfig.config.server.USE_ADD_SLOTS_BY_LEVEL) {
            if (!isGM()) {
               for (byte i = 1; i < 5; i++) {
                  gainSlots(i, 4, true);
               }
               MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("INVENTORY_EXPANSION_ON_LEVEL").with(level));
            }
         }
         if (YamlConfig.config.server.USE_ADD_RATES_BY_LEVEL) { //For the rate upgrade
            revertLastPlayerRates();
            setPlayerRates();
            MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("USER_INCREASE_RATES_ON_LEVEL").with(level));
         }
      }

      if (YamlConfig.config.server.USE_PERFECT_PITCH && level >= 30) {
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
      } else if (job.isA(MapleJob.WARRIOR) || job.isA(MapleJob.DAWN_WARRIOR_1)) {
         improvingMaxHPSkillId = isCygnus() ? DawnWarrior.MAX_HP_INCREASE : Warrior.IMPROVED_MAX_HP;
         if (job.isA(MapleJob.CRUSADER)) {
            improvingMaxMPSkillId = 1210000;
         } else if (job.isA(MapleJob.DAWN_WARRIOR_2)) {
            improvingMaxMPSkillId = 11110000;
         }
         addHp += Randomizer.rand(24, 28);
         addMp += Randomizer.rand(4, 6);
      } else if (job.isA(MapleJob.MAGICIAN) || job.isA(MapleJob.BLAZE_WIZARD_1)) {
         improvingMaxMPSkillId = isCygnus() ? BlazeWizard.INCREASING_MAX_MP : Magician.IMPROVED_MAX_MP_INCREASE;
         addHp += Randomizer.rand(10, 14);
         addMp += Randomizer.rand(22, 24);
      } else if (job.isA(MapleJob.BOWMAN) || job.isA(MapleJob.THIEF) || (job.getId() > 1299 && job.getId() < 1500)) {
         addHp += Randomizer.rand(20, 24);
         addMp += Randomizer.rand(14, 16);
      } else if (job.isA(MapleJob.GM)) {
         addHp += 30000;
         addMp += 30000;
      } else if (job.isA(MapleJob.PIRATE) || job.isA(MapleJob.THUNDER_BREAKER_1)) {
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
         if (improvingMaxHPLevel > 0 && (job.isA(MapleJob.WARRIOR) || job.isA(MapleJob.PIRATE) || job.isA(MapleJob.DAWN_WARRIOR_1)
               || job.isA(MapleJob.THUNDER_BREAKER_1))) {
            addHp += improvingMaxHP.get().getEffect(improvingMaxHPLevel).getX();
         }
      }

      Optional<Skill> improvingMaxMP = SkillFactory.getSkill(improvingMaxMPSkillId);
      if (improvingMaxMP.isPresent()) {
         int improvingMaxMPLevel = getSkillLevel(improvingMaxMP.get());
         if (improvingMaxMPLevel > 0 && (job.isA(MapleJob.MAGICIAN) || job.isA(MapleJob.CRUSADER) || job
               .isA(MapleJob.BLAZE_WIZARD_1))) {
            addMp += improvingMaxMP.get().getEffect(improvingMaxMPLevel).getX();
         }
      }

      if (YamlConfig.config.server.USE_RANDOMIZE_HPMP_GAIN) {
         if (getJobStyle() == MapleJob.MAGICIAN) {
            addMp += localIntelligence / 20;
         } else {
            addMp += localIntelligence / 10;
         }
      }

      addMaxMPMaxHP(addHp, addMp, true);
   }

   public boolean leaveParty() {
      if (getParty().isPresent()) {
         boolean partyLeader = isPartyLeader();
         if (partyLeader) {
            MaplePartyProcessor.getInstance().assignNewLeader(getParty().get());
         }
         MaplePartyProcessor.getInstance().leaveParty(getParty().get(), this);
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
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_5"));
      } else if (level == 10) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_10"));
      } else if (level == 15) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_15"));
      } else if (level == 20) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_20"));
      } else if (level == 25) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_25"));
      } else if (level == 30) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_30"));
      } else if (level == 35) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_35"));
      } else if (level == 40) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_40"));
      } else if (level == 45) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_45"));
      } else if (level == 50) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_50"));
      } else if (level == 55) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_55"));
      } else if (level == 60) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_60"));
      } else if (level == 65) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_65"));
      } else if (level == 70) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_70"));
      } else if (level == 75) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_75"));
      } else if (level == 80) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_80"));
      } else if (level == 85) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_85"));
      } else if (level == 90) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_90"));
      } else if (level == 95) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_95"));
      } else if (level == 100) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_100"));
      } else if (level == 105) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_105"));
      } else if (level == 110) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_110"));
      } else if (level == 115) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_115"));
      } else if (level == 120) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_120"));
      } else if (level == 125) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_125"));
      } else if (level == 130) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_130"));
      } else if (level == 135) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_135"));
      } else if (level == 140) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_140"));
      } else if (level == 145) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_145"));
      } else if (level == 150) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_150"));
      } else if (level == 155) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_155"));
      } else if (level == 160) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_160"));
      } else if (level == 165) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_165"));
      } else if (level == 170) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_170"));
      } else if (level == 175) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_175"));
      } else if (level == 180) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_180"));
      } else if (level == 185) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_185"));
      } else if (level == 190) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_190"));
      } else if (level == 195) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_195"));
      } else if (level == 200) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("LEVEL_MESSAGE_200"));
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
      World world = getWorldServer();
      this.expRate *= world.getExpRate();
      this.mesoRate *= world.getMesoRate();
      this.dropRate *= world.getDropRate();
   }

   public void revertWorldRates() {
      World world = getWorldServer();
      this.expRate /= world.getExpRate();
      this.mesoRate /= world.getMesoRate();
      this.dropRate /= world.getDropRate();
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

      effLock.lock();
      chrLock.lock();
      cashInv.lockInventory();
      try {
         revertCouponRates();
         setCouponRates();
      } finally {
         cashInv.unlockInventory();
         chrLock.unlock();
         effLock.unlock();
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

      if (YamlConfig.config.server.USE_STACK_COUPON_RATES) {
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

   private void commitBuffCoupon(int couponId) {
      if (!isLoggedIn() || getCashShop().isOpened()) {
         return;
      }

      MapleStatEffect mse = ii.getItemEffect(couponId);
      mse.applyTo(this);
   }

   private void dispelBuffCoupons() {
      List<MapleBuffStatValueHolder> allBuffs = getAllStatIncreases();

      for (MapleBuffStatValueHolder buffStatValueHolder : allBuffs) {
         if (ItemConstants.isRateCoupon(buffStatValueHolder.effect.getSourceId())) {
            cancelEffect(buffStatValueHolder.effect, false, buffStatValueHolder.startTime);
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

      ret.accountId = this.getAccountID();
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
      ret.gachaponExp.set(this.getGachaponExperience());
      ret.mapId = this.getMapId();
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

   public void reloadQuestExpiration() {
      getStartedQuests().stream()
            .filter(questStatus -> questStatus.getExpirationTime() > 0)
            .forEach(questStatus -> questTimeLimit2(questStatus.getQuest(), questStatus.getExpirationTime()));
   }

   public void raiseQuestMobCount(int id) {
      // It seems nexon uses monsters that don't exist in the WZ (except string) to merge multiple mobs together for these 3 monsters.
      // We also want to run mobKilled for both since there are some quest that don't use the updated ID...
      if (id == 1110100 || id == 1110130) {
         raiseQuestMobCount(9101000);
      } else if (id == 2230101 || id == 2230131) {
         raiseQuestMobCount(9101001);
      } else if (id == 1140100 || id == 1140130) {
         raiseQuestMobCount(9101002);
      }

      int lastQuestProcessed = 0;
      try {
         synchronized (quests) {
            for (MapleQuestStatus qs : getQuests()) {
               lastQuestProcessed = qs.getQuest().id();
               if (qs.getStatus() == MapleQuestStatus.Status.COMPLETED || qs.getQuest().canComplete(this, null)) {
                  continue;
               }
               if (qs.progress(id)) {
                  announceUpdateQuest(DelayedQuestUpdate.UPDATE, qs, false);
                  if (qs.getInfoNumber() > 0) {
                     announceUpdateQuest(DelayedQuestUpdate.UPDATE, qs, true);
                  }
               }
            }
         }
      } catch (Exception e) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION_CAUGHT, e,
               "MapleCharacter.mobKilled. CID: " + this.id + " last Quest Processed: " + lastQuestProcessed);
      }
   }

   public MapleMount mount(int id, int skillId) {
      this.mount = new MapleMount(id, skillId);
      return this.mount;
   }

   private void playerDead() {
      if (this.getMap().isCPQMap()) {
         int losing = getMap().getDeathCP();
         if (getCP() < losing) {
            losing = getCP();
         }
         int finalLosing = losing;
         MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new MonsterCarnivalPlayerDied(getName(), finalLosing, getTeam()));
         gainCP(-losing);
         return;
      }

      cancelAllBuffs(false);
      dispelAbnormalStatuses();
      lastDeathTime = Server.getInstance().getCurrentTime();

      EventInstanceManager eim = getEventInstance();
      if (eim != null) {
         eim.playerKilled(this);
      }
      int[] charmID = {5130000, 4031283, 4140903};
      int possessed = 0;
      int i;
      for (i = 0; i < charmID.length; i++) {
         int quantity = getItemQuantity(charmID[i], false);
         if (possessed == 0 && quantity > 0) {
            possessed = quantity;
            break;
         }
      }
      if (possessed > 0 && !GameConstants.isDojo(getMapId())) {
         MessageBroadcaster.getInstance().sendServerNotice(this, ServerNoticeType.PINK_TEXT, I18nMessage.from("SAFETY_CHARM_USE"));
         MapleInventoryManipulator.removeById(client, ItemConstants.getInventoryType(charmID[i]), charmID[i], 1, true, false);
         usedSafetyCharm = true;
      } else if (getJob() != MapleJob.BEGINNER) { //Hmm...
         if (!FieldLimit.NO_EXP_DECREASE.check(getMap().getFieldLimit())) {
            int expDummy = ExpTable.getExpNeededForLevel(getLevel());

            if (getMap().isTown()) {
               expDummy /= 100;
            } else {
               if (getLuk() < 50) {
                  expDummy /= 10;
               } else {
                  expDummy /= 20;
               }
            }

            int curExp = getExp();
            loseExp(Math.min(curExp, expDummy), false, false);
         }
      }

      if (getBuffedValue(MapleBuffStat.MORPH) != null) {
         cancelEffectFromBuffStat(MapleBuffStat.MORPH);
      }

      if (getBuffedValue(MapleBuffStat.MONSTER_RIDING) != null) {
         cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
      }

      standFromChairInternal();
      PacketCreator.announce(client, new EnableActions());
   }

   private void standFromChairInternal() {
      int chairId = chair.get();
      if (chairId >= 0) {
         if (ItemConstants.isFishingChair(chairId)) {
            this.getWorldServer().unregisterFisherPlayer(this);
         }

         setChair(-1);
         if (unregisterChairBuff()) {
            MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new CancelForeignChairSkillEffect(this.getId()), false, this);
         }

         MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new ShowChair(this.getId(), 0), false, this);
      }

      PacketCreator.announce(this, new CancelChair(-1));
   }

   public void sitChair(int itemId) {
      if (this.isLoggedInWorld()) {
         if (itemId >= 1000000) {    // sit on item chair
            if (chair.get() < 0) {
               setChair(itemId);
               MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new ShowChair(this.getId(), itemId), false, this);
            }
            PacketCreator.announce(client, new EnableActions());
         } else if (itemId >= 0) {    // sit on map chair
            if (chair.get() < 0) {
               setChair(itemId);
               if (registerChairBuff()) {
                  MasterBroadcaster.getInstance()
                        .sendToAllInMap(getMap(), new GiveForeignChairSkillEffect(this.getId()), false, this);
               }
               PacketCreator.announce(this, new CancelChair(itemId));
            }
         } else {    // stand up
            standFromChairInternal();
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

      cancelAllBuffs(false);
      if (usedSafetyCharm) {
         addHpMp((int) Math.ceil(this.getClientMaxHp() * 0.3), (int) Math.ceil(this.getClientMaxMp() * 0.3));
      } else {
         updateHp(50);
      }
      setStance(0);
   }

   private void prepareDragonBlood(final MapleStatEffect bloodEffect) {
      scheduler.cancel(MapleCharacterScheduler.Type.DRAGON_BLOOD);
      scheduler.add(MapleCharacterScheduler.Type.DRAGON_BLOOD, () -> {
         if (awayFromWorld.get()) {
            return;
         }

         addHP(-bloodEffect.getX());
         PacketCreator.announce(this, new ShowOwnBuffEffect(bloodEffect.getSourceId(), 5));
         MasterBroadcaster.getInstance()
               .sendToAllInMap(getMap(), new ShowBuffEffect(getId(), bloodEffect.getSourceId(), 5, (byte) 3), false,
                     MapleCharacter.this);
      }, 4000, 4000);
   }

   private void recalculateEquipStats() {
      if (equipChanged) {
         equipMaxHp = 0;
         equipMaxMp = 0;
         equipDexterity = 0;
         equipIntelligence = 0;
         equipStrength = 0;
         equipLuck = 0;
         equipMagic = 0;
         equipWeaponAttack = 0;

         for (Item item : getInventory(MapleInventoryType.EQUIPPED)) {
            Equip equip = (Equip) item;
            equipMaxHp += equip.hp();
            equipMaxMp += equip.mp();
            equipDexterity += equip.dex();
            equipIntelligence += equip.intelligence();
            equipStrength += equip.str();
            equipLuck += equip.luk();
            equipMagic += equip.matk() + equip.intelligence();
            equipWeaponAttack += equip.watk();
         }

         equipChanged = false;
      }

      localMaxHp += equipMaxHp;
      localMaxMp += equipMaxMp;
      localDexterity += equipDexterity;
      localIntelligence += equipIntelligence;
      localStrength += equipStrength;
      localLuck += equipLuck;
      localMagic += equipMagic;
      localWeaponAttack += equipWeaponAttack;
   }

   public void reapplyLocalStats() {
      effLock.lock();
      chrLock.lock();
      statWriteLock.lock();
      try {
         localMaxHp = getMaxHp();
         localMaxMp = getMaxMp();
         localDexterity = getDex();
         localIntelligence = getInt();
         localStrength = getStr();
         localLuck = getLuk();
         localMagic = localIntelligence;
         localWeaponAttack = 0;
         localChairRate = -1;

         recalculateEquipStats();

         localMagic = Math.min(localMagic, 2000);

         Integer hyperBodyHp = getBuffedValue(MapleBuffStat.HYPER_BODY_HP);
         if (hyperBodyHp != null) {
            localMaxHp += (hyperBodyHp.doubleValue() / 100) * localMaxHp;
         }
         Integer hyperBodyMp = getBuffedValue(MapleBuffStat.HYPER_BODY_MP);
         if (hyperBodyMp != null) {
            localMaxMp += (hyperBodyMp.doubleValue() / 100) * localMaxMp;
         }

         localMaxHp = Math.min(30000, localMaxHp);
         localMaxMp = Math.min(30000, localMaxMp);

         MapleStatEffect combo = getBuffEffect(MapleBuffStat.ARAN_COMBO);
         if (combo != null) {
            localWeaponAttack += combo.getX();
         }

         if (energyBar == 15000) {
            executeForSkill(isCygnus() ? ThunderBreaker.ENERGY_CHARGE : Marauder.ENERGY_CHARGE, (skill, skillLevel) -> {
               MapleStatEffect statEffect = skill.getEffect(skillLevel);
               localWeaponAttack += statEffect.getWeaponAttack();
            });
         }

         Integer mapleWarrior = getBuffedValue(MapleBuffStat.MAPLE_WARRIOR);
         if (mapleWarrior != null) {
            localStrength += getStr() * mapleWarrior / 100;
            localDexterity += getDex() * mapleWarrior / 100;
            localIntelligence += getInt() * mapleWarrior / 100;
            localLuck += getLuk() * mapleWarrior / 100;
         }
         if (job.isA(MapleJob.BOWMAN)) {

            if (job.isA(MapleJob.MARKSMAN)) {
               executeForSkill(3220004, (skill, skillLevel) -> localWeaponAttack += skill.getEffect(skillLevel).getX());
            } else if (job.isA(MapleJob.BOW_MASTER)) {
               executeForSkill(3120005, (skill, skillLevel) -> localWeaponAttack += skill.getEffect(skillLevel).getX());
            }
         }

         Integer weaponAttackBuff = getBuffedValue(MapleBuffStat.WEAPON_ATTACK);
         if (weaponAttackBuff != null) {
            localWeaponAttack += weaponAttackBuff;
         }
         Integer magicAttackBuff = getBuffedValue(MapleBuffStat.MAGIC_ATTACK);
         if (magicAttackBuff != null) {
            localMagic += magicAttackBuff;
         }

         int blessing = getSkillLevel(10000000 * getJobType() + 12);
         if (blessing > 0) {
            localWeaponAttack += blessing;
            localMagic += blessing * 2;
         }

         if (job.isA(MapleJob.THIEF) || job.isA(MapleJob.BOWMAN) || job.isA(MapleJob.PIRATE) || job.isA(MapleJob.NIGHT_WALKER_1)
               || job.isA(MapleJob.WIND_ARCHER_1)) {
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
                        if ((claw && ItemConstants.isThrowingStar(item.id())) || (gun && ItemConstants.isBullet(item.id())) || (bow
                              && ItemConstants.isArrowForBow(item.id())) || (crossbow && ItemConstants
                              .isArrowForCrossBow(item.id()))) {
                           if (item.quantity() > 0) {
                              // Finally there!
                              localWeaponAttack += ii.getWeaponAttackForProjectile(item.id());
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
         statWriteLock.unlock();
         chrLock.unlock();
         effLock.unlock();
      }
   }

   private List<Pair<MapleStat, Integer>> recalculateLocalStats() {
      effLock.lock();
      chrLock.lock();
      statWriteLock.lock();
      try {
         List<Pair<MapleStat, Integer>> hpMpUpdate = new ArrayList<>(2);
         int oldLocalMaxHp = localMaxHp;
         int oldLocalMaxMp = localMaxMp;

         reapplyLocalStats();

         if (YamlConfig.config.server.USE_FIXED_RATIO_HPMP_UPDATE) {
            if (localMaxHp != oldLocalMaxHp) {
               Pair<MapleStat, Integer> hpUpdate;

               if (transientHp == Float.NEGATIVE_INFINITY) {
                  hpUpdate = calcHpRatioUpdate(localMaxHp, oldLocalMaxHp);
               } else {
                  hpUpdate = calcHpRatioTransient();
               }

               hpMpUpdate.add(hpUpdate);
            }

            if (localMaxMp != oldLocalMaxMp) {
               Pair<MapleStat, Integer> mpUpdate;

               if (transientMp == Float.NEGATIVE_INFINITY) {
                  mpUpdate = calcMpRatioUpdate(localMaxMp, oldLocalMaxMp);
               } else {
                  mpUpdate = calcMpRatioTransient();
               }

               hpMpUpdate.add(mpUpdate);
            }
         }

         return hpMpUpdate;
      } finally {
         statWriteLock.unlock();
         chrLock.unlock();
         effLock.unlock();
      }
   }

   private void updateLocalStats() {
      prtLock.lock();
      effLock.lock();
      statWriteLock.lock();
      try {
         int oldMaxHp = localMaxHp;
         List<Pair<MapleStat, Integer>> hpMpUpdate = recalculateLocalStats();
         enforceMaxHpMp();

         if (!hpMpUpdate.isEmpty()) {
            PacketCreator.announce(client, new UpdatePlayerStats(hpMpUpdate, true, this));
         }

         if (oldMaxHp != localMaxHp) {
            updatePartyMemberHP();
         }
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
         prtLock.unlock();
      }
   }

   public void receivePartyMemberHP() {
      prtLock.lock();
      try {
         if (party.isPresent()) {
            getPartyMembersOnSameMap().parallelStream()
                  .forEach(character -> PacketCreator.announce(this,
                        new UpdatePartyMemberHp(character.getId(), character.getHp(), character.getCurrentMaxHp())));
         }
      } finally {
         prtLock.unlock();
      }
   }

   public void removeAllCoolDownsExcept(int id, boolean packet) {
      effLock.lock();
      chrLock.lock();
      try {
         ArrayList<CoolDownValueHolder> list = new ArrayList<>(coolDowns.values());
         for (CoolDownValueHolder valueHolder : list) {
            if (valueHolder.skillId() != id) {
               coolDowns.remove(valueHolder.skillId());
               if (packet) {
                  PacketCreator.announce(client, new SkillCoolDown(valueHolder.skillId(), 0));
               }
            }
         }
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   private void removeCoolDown(int skillId) {
      effLock.lock();
      chrLock.lock();
      try {
         this.coolDowns.remove(skillId);
      } finally {
         chrLock.unlock();
         effLock.unlock();
      }
   }

   public void removePet(MaplePet pet, boolean shift_left) {
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
      if (!YamlConfig.config.server.USE_AUTOASSIGN_STARTERS_AP) {
         return;
      }

      effLock.lock();
      statWriteLock.lock();
      try {
         int tap = remainingAp + str + dex + int_ + luk, tsp = 1;
         int tstr = 4, tdex = 4, tint = 4, tluk = 4;

         switch (job.getId()) {
            case 100, 1100, 2100 -> {
               tstr = 35;
               tsp += ((getLevel() - 10) * 3);
            }
            case 200, 1200 -> {
               tint = 20;
               tsp += ((getLevel() - 8) * 3);
            }
            case 300, 1300, 400, 1400 -> {
               tdex = 25;
               tsp += ((getLevel() - 10) * 3);
            }
            case 500, 1500 -> {
               tdex = 20;
               tsp += ((getLevel() - 10) * 3);
            }
         }

         tap -= tstr;
         tap -= tdex;
         tap -= tint;
         tap -= tluk;

         if (tap >= 0) {
            updateStrDexIntLukSp(tstr, tdex, tint, tluk, tap, tsp, GameConstants.getSkillBook(job.getId()));
         } else {
            LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.EXCEPTION_CAUGHT,
                  name + " tried to get their stats reset, without having enough AP available.");
         }
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
      }
   }

   public void resetBattleshipHp() {
      executeIfHasSkill(Corsair.BATTLE_SHIP, (skill, skillLevel) -> {
         int battleshipLevel = Math.max(getLevel() - 120, 0);
         MapleCharacter.this.battleShipHp = 400 * skillLevel + (battleshipLevel * 200);
      });
   }

   public void resetEnteredScript() {
      entered.remove(map.getId());
   }

   public synchronized void saveCoolDowns() {
      List<PlayerCoolDownValueHolder> coolDowns = getAllCoolDowns();
      if (!coolDowns.isEmpty()) {
         DatabaseConnection.getInstance().withConnection(connection -> {
            CoolDownAdministrator.getInstance().deleteForCharacter(connection, getId());
            CoolDownAdministrator.getInstance().addCoolDownsForCharacter(connection, getId(), coolDowns);
         });
      }

      Map<MapleAbnormalStatus, Pair<Long, MobSkill>> diseases = getAlAbnormalStatuses();
      if (!diseases.isEmpty()) {
         DatabaseConnection.getInstance().withConnection(connection -> {
            PlayerDiseaseAdministrator.getInstance().deleteForCharacter(connection, getId());
            PlayerDiseaseAdministrator.getInstance().addPlayerDiseasesForCharacter(connection, getId(), diseases.entrySet());
         });
      }
   }

   public void saveGuildStatus() {
      DatabaseConnection.getInstance().withConnection(
            connection -> CharacterAdministrator.getInstance().updateGuildStatus(connection, id, guildId, guildRank, allianceRank));
   }

   public void saveLocationOnWarp() {
      MaplePortal closest = map.findClosestPortal(this.position());
      int currentMapId = getMapId();

      for (int i = 0; i < savedLocations.length; i++) {
         if (savedLocations[i] == null) {
            savedLocations[i] = new SavedLocation(currentMapId, closest != null ? closest.getId() : 0);
         }
      }
   }

   public void saveLocation(String type) {
      MaplePortal closest = map.findClosestPortal(this.position());
      savedLocations[SavedLocationType.fromString(type).ordinal()] =
            new SavedLocation(getMapId(), closest != null ? closest.getId() : 0);
   }

   public void saveCharToDB() {
      if (YamlConfig.config.server.USE_AUTOSAVE) {
         Runnable r = () -> saveCharToDB(true);
         CharacterSaveService service = (CharacterSaveService) getWorldServer().getServiceAccess(WorldServices.SAVE_CHARACTER);
         service.registerSaveCharacter(this.getId(), r);
      } else {
         saveCharToDB(true);
      }
   }

   public synchronized void saveCharToDB(boolean notAutoSave) {
      if (!loggedIn) {
         return;
      }

      Calendar c = Calendar.getInstance();

      if (notAutoSave) {
         LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.SAVING_CHARACTER,
               "Attempting to save " + name + " at " + c.getTime().toString());
      } else {
         LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.AUTOSAVING_CHARACTER,
               "Attempting to auto save " + name + " at " + c.getTime().toString());
      }

      Server.getInstance().updateCharacterEntry(this);

      DatabaseConnection.getInstance().withConnection(entityManager -> {
         entityManager.getTransaction().begin();

         updateCharacter(entityManager);
         updatePets();
         updatePetIgnores(entityManager);
         updateKeyMap(entityManager);
         CharacterProcessor.getInstance().createQuickSlots(entityManager, this);
         updateSkillMacros(entityManager);

         List<Pair<Item, MapleInventoryType>> itemsWithType = new ArrayList<>();
         for (MapleInventory iv : inventory) {
            for (Item item : iv.list()) {
               itemsWithType.add(new Pair<>(item, iv.getType()));
            }
         }
         ItemFactory.INVENTORY.saveItems(itemsWithType, id, entityManager);

         updateSkills(entityManager);
         updateSavedLocations(entityManager);
         updateTeleportRockLocations(entityManager);
         updateAreaInfo(entityManager);
         updateEventStats(entityManager);
         updateQuestInfo(entityManager);

         MapleFamilyProcessor.getInstance().saveCharactersFamilyReputation(entityManager, getFamilyEntry());

         if (cashshop != null) {
            CashShopProcessor.getInstance().save(entityManager, cashshop);
         }

         entityManager.getTransaction().commit();
         entityManager.getTransaction().begin();

         if (storage != null && usedStorage) {
            storage.saveToDB(entityManager);
            usedStorage = false;
         }

         entityManager.getTransaction().commit();
      });
   }

   private void updateQuestInfo(EntityManager entityManager) {
      CharacterProcessor.getInstance().deleteQuestProgressWhereCharacterId(entityManager, id);

      synchronized (quests) {
         for (MapleQuestStatus q : getQuests()) {
            int questId = QuestStatusAdministrator.getInstance().create(entityManager, id, q);
            QuestProgressAdministrator.getInstance().create(entityManager, id, questId,
                  q.getProgress().keySet().stream().map(key -> new Pair<>(key, q.getProgress(key))).collect(Collectors.toList()));
            MedalMapAdministrator.getInstance().create(entityManager, id, questId, q.getMedalMaps());
         }
      }
   }

   private void updateEventStats(EntityManager entityManager) {
      EventStatAdministrator.getInstance().deleteForCharacter(entityManager, id);
      EventStatAdministrator.getInstance().create(entityManager, id, events.entrySet());
   }

   private void updateAreaInfo(EntityManager entityManager) {
      AreaInfoAdministrator.getInstance().deleteForCharacter(entityManager, id);
      AreaInfoAdministrator.getInstance().create(entityManager, id, area_info.entrySet());
   }

   private void updateTeleportRockLocations(EntityManager entityManager) {
      TeleportRockLocationAdministrator.getInstance().deleteForCharacter(entityManager, id);
      TeleportRockLocationAdministrator.getInstance()
            .create(entityManager, id, teleportRockMaps.stream().filter(id -> id != 999999999).collect(Collectors.toList()), 0);
      TeleportRockLocationAdministrator.getInstance()
            .create(entityManager, id, vipTeleportRockMaps.stream().filter(id -> id != 999999999).collect(Collectors.toList()), 1);
   }

   private void updateSavedLocations(EntityManager entityManager) {
      SavedLocationAdministrator.getInstance().deleteForCharacter(entityManager, id);

      Collection<Pair<String, SavedLocation>> locations = Arrays.stream(SavedLocationType.values())
            .filter(type -> savedLocations[type.ordinal()] != null)
            .map(type -> new Pair<>(type.name(), savedLocations[type.ordinal()]))
            .collect(Collectors.toList());
      SavedLocationAdministrator.getInstance().create(entityManager, id, locations);
   }

   private void updateSkills(EntityManager entityManager) {
      SkillAdministrator.getInstance().replace(entityManager, id, skills.entrySet());
   }

   private void updateSkillMacros(EntityManager entityManager) {
      SkillMacroAdministrator.getInstance().deleteForCharacter(entityManager, id);
      SkillMacroAdministrator.getInstance()
            .create(entityManager, id, Arrays.stream(skillMacros).filter(Objects::nonNull).collect(Collectors.toList()));
   }

   private void updateKeyMap(EntityManager entityManager) {
      KeyMapAdministrator.getInstance().deleteForCharacter(entityManager, id);
      KeyMapAdministrator.getInstance().create(entityManager, id, Collections.unmodifiableSet(keymap.entrySet()));
   }

   private void updatePetIgnores(EntityManager entityManager) {
      for (Entry<Integer, Set<Integer>> es : getExcluded().entrySet()) {    // this set is already protected
         PetIgnoreAdministrator.getInstance().deletePetIgnore(entityManager, es.getKey());
         PetIgnoreAdministrator.getInstance().create(entityManager, es.getKey(), es.getValue());
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

   private void updateCharacter(EntityManager entityManager) {
      effLock.lock();
      statWriteLock.lock();
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
            mapId = this.mapId;
         } else {
            if (map.getForcedReturnId() != 999999999) {
               mapId = map.getForcedReturnId();
            } else {
               mapId = getHp() < 1 ? map.getReturnMapId() : map.getId();
            }
         }

         int spawnPoint;
         if (map == null || map.getId() == 610020000 || map.getId() == 610020001) {  // reset to first spawn point on those maps
            spawnPoint = 0;
         } else {
            MaplePortal closest = map.findClosestPlayerSpawnPoint(this.position());
            if (closest != null) {
               spawnPoint = closest.getId();
            } else {
               spawnPoint = 0;
            }
         }

         int partyId = party.map(MapleParty::getId).orElse(-1);
         int messengerId = messenger != null ? messenger.getId() : 0;
         int messengerPosition = messenger != null ? this.messengerPosition : 4;

         int mountLevel = mount != null ? mount.level() : 1;
         int mountExp = mount != null ? mount.exp() : 0;
         int mountTiredness = mount != null ? mount.tiredness() : 0;

         CharacterAdministrator.getInstance().update(entityManager, id, level, fame, str, dex, luk, int_, Math.abs(exp.get()),
               Math.abs(gachaponExp.get()), hp, mp, maxHp, maxMp, sp.substring(0, sp.length() - 1), remainingAp, gmLevel,
               skinColor.getId(), gender, job.getId(), hair, face, mapId, meso.get(), hpMpApUsed, spawnPoint,
               partyId, messengerId, messengerPosition, mountLevel, mountExp, mountTiredness,
               getSlots(1), getSlots(2), getSlots(3), getSlots(4), bookCover, vanquisherStage,
               dojoPoints, dojoStage, finishedDojoTutorial ? 1 : 0, vanquisherKills, matchCard.wins(), matchCard.losses(),
               matchCard.ties(), omok.wins(), omok.losses(), omok.ties(), dataString, questFame, jailExpiration, partnerId,
               marriageItemId, lastExpGainTime, ariantPoints, canRecvPartySearchInvite);
         monsterbook.saveCards(getId());
      } finally {
         statWriteLock.unlock();
         effLock.unlock();
         prtLock.unlock();
      }
   }

   public void sendPolice(int greason, String reason, int duration) {
      PacketCreator.announce(this,
            new SendPolice(String.format("You have been blocked by the#b %s Police for %s.#k", "HeavenMS", reason)));
      this.isBanned = true;
      TimerManager.getInstance().schedule(() -> client.disconnect(false, false), duration);
   }

   public void sendPolice(String text) {
      String message = getName() + " received this - " + text;
      if (Server.getInstance().isGmOnline(this.getWorld())) { //Alert and log if a GM is online
         Server.getInstance().broadcastGMMessage(this.getWorld(), PacketCreator.create(new YellowTip(message)));
         LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.AUTOBAN_WARNING, message);
      } else { //Auto DC and log if no GM is online
         client.disconnect(false, false);
         LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.AUTOBAN_DC, message);
      }
   }

   public void sendKeymap() {
      PacketCreator.announce(client, new GetKeyMap(keymap));
   }

   public void sendMacros() {
      // Always send the macro packet to fix a client side bug when switching characters.
      PacketCreator.announce(client, new GetMacros(skillMacros));
   }

   public SkillMacro[] getMacros() {
      return skillMacros;
   }

   public void setBuffedValue(MapleBuffStat effect, int value) {
      effLock.lock();
      chrLock.lock();
      try {
         MapleBuffStatValueHolder buffStatValueHolder = effects.get(effect);
         if (buffStatValueHolder == null) {
            return;
         }
         buffStatValueHolder.value = value;
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
      DatabaseConnection.getInstance()
            .withConnection(connection -> CharacterAdministrator.getInstance().setMerchant(connection, id, set));
      hasMerchant = set;
   }

   public void addMerchantMesos(int add) {
      int newAmount = (int) Math.min((long) merchantMeso + add, Integer.MAX_VALUE);
      DatabaseConnection.getInstance()
            .withConnection(connection -> CharacterAdministrator.getInstance().setMerchantMesos(connection, id, newAmount));
      merchantMeso = newAmount;
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
         updatePartyMemberHP();

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
      this.hp = calcTransientRatio(transientHp * localMaxHp);

      hpChangeAction(Short.MIN_VALUE);
      return new Pair<>(MapleStat.HP, hp);
   }

   private Pair<MapleStat, Integer> calcMpRatioTransient() {
      this.mp = calcTransientRatio(transientMp * localMaxMp);
      return new Pair<>(MapleStat.MP, mp);
   }

   private int calcHpRatioUpdate(int currentPoint, int maxPoint, int diffPoint) {
      int nextMax = Math.min(30000, maxPoint + diffPoint);

      float temp = currentPoint * nextMax;
      int ret = (int) Math.ceil(temp / maxPoint);

      transientHp = (maxPoint > nextMax) ? ((float) currentPoint) / maxPoint : ((float) ret) / nextMax;
      return ret;
   }

   private int calcMpRatioUpdate(int currentPoint, int maxPoint, int diffPoint) {
      int nextMax = Math.min(30000, maxPoint + diffPoint);

      float temp = currentPoint * nextMax;
      int ret = (int) Math.ceil(temp / maxPoint);

      transientMp = (maxPoint > nextMax) ? ((float) currentPoint) / maxPoint : ((float) ret) / nextMax;
      return ret;
   }

   public boolean applyHpMpChange(int hpCon, int hpChange, int mpChange) {
      boolean zombify = hasDisease(MapleAbnormalStatus.ZOMBIFY);

      effLock.lock();
      statWriteLock.lock();
      try {
         int nextHp = hp + hpChange, nextMp = mp + mpChange;
         boolean cannotApplyHp = hpChange != 0 && nextHp <= 0 && (!zombify || hpCon > 0);
         boolean cannotApplyMp = mpChange != 0 && nextMp < 0;

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
         statWriteLock.unlock();
         effLock.unlock();
      }

      if (hpChange < 0) {
         KeyBinding autoHpPot = this.getKeymap().get(91);
         if (autoHpPot != null) {
            int autoHpItemId = autoHpPot.action();
            float autoHpAlert = this.getAutoPotHpAlert();
            if (((float) this.getHp()) / this.getCurrentMaxHp() <= autoHpAlert) {
               Item autoHpItem = this.getInventory(MapleInventoryType.USE).findById(autoHpItemId);
               if (autoHpItem != null) {
                  this.setAutoPotHpAlert(0.9f * autoHpAlert);
                  PetAutoPotProcessor.getInstance().runAutoPotAction(client, autoHpItem.position(), autoHpItemId);
               }
            }
         }
      }

      if (mpChange < 0) {
         KeyBinding autoMpPot = this.getKeymap().get(92);
         if (autoMpPot != null) {
            int autoMpItemId = autoMpPot.action();
            float autoMpAlert = this.getAutoPotMpAlert();
            if (((float) this.getMp()) / this.getCurrentMaxMp() <= autoMpAlert) {
               Item autoMpItem = this.getInventory(MapleInventoryType.USE).findById(autoMpItemId);
               if (autoMpItem != null) {
                  this.setAutoPotMpAlert(0.9f * autoMpAlert);
                  PetAutoPotProcessor.getInstance().runAutoPotAction(client, autoMpItem.position(), autoMpItemId);
               }
            }
         }
      }

      return true;
   }

   public void setMiniGamePoints(MapleCharacter visitor, int winnerSlot, boolean omok) {
      GameData thisGameData;
      GameData visitorGameData;
      if (omok) {
         thisGameData = this.omok;
         visitorGameData = visitor.omok;
      } else {
         thisGameData = this.matchCard;
         visitorGameData = visitor.matchCard;
      }

      if (winnerSlot == 1) {
         thisGameData = thisGameData.incrementWins();
         visitorGameData = visitorGameData.incrementLosses();
      } else if (winnerSlot == 2) {
         visitorGameData = visitorGameData.incrementWins();
         thisGameData = thisGameData.incrementLosses();
      } else {
         thisGameData = thisGameData.incrementTies();
         visitorGameData = visitorGameData.incrementTies();
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
         doorSlot = party.map(reference -> reference.getPartyDoor(getId())).orElse((byte) 0);
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
            PacketCreator.announce(client, new SlotLimitUpdate(type, slots));
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

   private int standaloneSell(MapleClient c, MapleItemInformationProvider ii, MapleInventoryType type, short slot,
                              short quantity) {
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

         int itemId = item.id();
         if (ItemConstants.isRechargeable(itemId)) {
            quantity = item.quantity();
         } else if (ItemConstants.isWeddingToken(itemId) || ItemConstants.isWeddingRing(itemId)) {
            return (0);
         }

         if (quantity < 0) {
            return (0);
         }
         short itemQuantity = item.quantity();
         if (itemQuantity == 0xFFFF) {
            itemQuantity = 1;
         }

         if (quantity <= itemQuantity && itemQuantity > 0) {
            MapleInventoryManipulator.removeFromSlot(c, type, (byte) slot, quantity, false);
            int recvMesos = ii.getPrice(itemId, quantity);
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

         Map<StatUpgrade, Float> statIncreases = new LinkedHashMap<>();
         mergeAllItemsFromPosition(statIncreases, it.position());

         List<Pair<Equip, Map<StatUpgrade, Short>>> upgradeableEquipped = new LinkedList<>();
         Map<Equip, List<Pair<StatUpgrade, Integer>>> equipUpgrades = new LinkedHashMap<>();
         for (Equip eq : getUpgradeableEquipped()) {
            upgradeableEquipped.add(new Pair<>(eq, eq.getStats()));
            equipUpgrades.put(eq, new LinkedList<>());
         }

         for (Entry<StatUpgrade, Float> e : statIncreases.entrySet()) {
            double ev = Math.sqrt(e.getValue());

            Set<Equip> extraEquipped = new LinkedHashSet<>(equipUpgrades.keySet());
            List<Equip> statEquipped = getEquipsWithStat(upgradeableEquipped, e.getKey());
            float extraRate = (float) (0.2 * Math.random());

            if (!statEquipped.isEmpty()) {
               float statRate = 1.0f - extraRate;

               int statIncrease = (int) Math.ceil((ev * statRate) / statEquipped.size());
               for (Equip statEq : statEquipped) {
                  equipUpgrades.get(statEq).add(new Pair<>(e.getKey(), statIncrease));
                  extraEquipped.remove(statEq);
               }
            }

            if (!extraEquipped.isEmpty()) {
               int statIncrease = (int) Math.round((ev * extraRate) / extraEquipped.size());
               if (statIncrease > 0) {
                  for (Equip extraEq : extraEquipped) {
                     equipUpgrades.get(extraEq).add(new Pair<>(e.getKey(), statIncrease));
                  }
               }
            }
         }

         MessageBroadcaster.getInstance()
               .sendServerNotice(this, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("EQUIPMENT_MERGE_TITLE"));
         for (Entry<Equip, List<Pair<StatUpgrade, Integer>>> eqpUpg : equipUpgrades.entrySet()) {
            List<Pair<StatUpgrade, Integer>> equipStatIncreases = eqpUpg.getValue();
            if (!equipStatIncreases.isEmpty()) {
               Equip eqp = eqpUpg.getKey();
               eqp = ItemProcessor.getInstance().setMergeFlag(eqp);

               String showStr = " '" + MapleItemInformationProvider.getInstance().getName(eqp.id()) + "': ";
               Pair<Equip, Pair<StringBuilder, Pair<Boolean, Boolean>>> result = eqp.gainStats(equipStatIncreases);
               String upgradeStrength = result.getRight().getLeft().toString();
               eqp = result.getLeft();
               forceUpdateItem(eqp);

               showStr += upgradeStrength;
               MessageBroadcaster.getInstance().sendServerNotice(this, ServerNoticeType.LIGHT_BLUE, SimpleMessage.from(showStr));
            }
         }

         return true;
      } finally {
         inv.unlockInventory();
      }
   }

   private void mergeAllItemsFromPosition(Map<StatUpgrade, Float> statUpgrades, short pos) {
      MapleInventory inv = getInventory(MapleInventoryType.EQUIP);
      inv.lockInventory();
      try {
         for (short i = pos; i <= inv.getSlotLimit(); i++) {
            standaloneMerge(statUpgrades, getClient(), MapleInventoryType.EQUIP, i, inv.getItem(i));
         }
      } finally {
         inv.unlockInventory();
      }
   }

   private void standaloneMerge(Map<StatUpgrade, Float> statUpgrades, MapleClient c, MapleInventoryType type,
                                short slot, Item item) {
      short quantity;
      if (item == null || (quantity = item.quantity()) < 1 || ii.isCash(item.id()) || !ii.isUpgradeable(item.id()) || ItemProcessor
            .getInstance().hasMergeFlag(item)) {
         return;
      }

      Equip e = (Equip) item;
      for (Entry<StatUpgrade, Short> s : e.getStats().entrySet()) {
         Float newVal = statUpgrades.get(s.getKey());

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

         statUpgrades.put(s.getKey(), newVal);
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
      if (GameConstants.isDojoBossArea(map.getId())) {
         PacketCreator.announce(client, new GetClock((int) (getDojoTimeLeft() / 1000)));
      }
   }

   public void showUnderLeveledInfo(MapleMonster mob) {
      long curTime = Server.getInstance().getCurrentTime();
      if (nextWarningTime < curTime) {
         nextWarningTime = curTime + (60 * 1000);

         showHint("You have gained #rno experience#k from defeating #e#b" + mob.getName() + "#k#n (lv. #b" + mob.getLevel()
               + "#k)! Take note you must have around the same level as the mob to start earning EXP from it.");
      }
   }

   public void showMapOwnershipInfo(MapleCharacter mapOwner) {
      long curTime = Server.getInstance().getCurrentTime();
      if (nextWarningTime < curTime) {
         nextWarningTime = curTime + (60 * 1000);   // show under level info again after 1 minute

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

         PacketCreator
               .announce(this, new GetAvatarMegaphone(mapOwner, medal, this.getClient().getChannel(), 5390006, strLines, true));
      }
   }

   public void showHint(String msg) {
      showHint(msg, 500);
   }

   public void showHint(String msg, int length) {
      client.announceHint(msg, length);
   }

   public void showNote() {
      DatabaseConnection.getInstance().withConnectionResult(connection -> NoteProvider.getInstance().getFirstNote(connection, name))
            .ifPresent(notes ->
                  PacketCreator.announce(client, new ShowNotes(notes)));
   }

   public void silentGiveBuffs(List<Pair<Long, PlayerBuffValueHolder>> buffs) {
      buffs.forEach(pair -> pair.getRight().effect.silentApplyBuff(this, pair.getLeft()));
   }

   public void silentPartyUpdate() {
      getParty().ifPresent(this::silentPartyUpdateInternal);
   }

   private void silentPartyUpdateInternal(MapleParty chrParty) {
      if (chrParty != null) {
         MaplePartyProcessor.getInstance().updateParty(chrParty, PartyOperation.SILENT_UPDATE, getMPC());
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

   public boolean runTirednessSchedule() {
      if (mount != null) {
         modifyMount(MapleMount::incrementTiredness);
         int tiredness = mount.tiredness();

         MasterBroadcaster.getInstance()
               .sendToAllInMap(getMap(), new UpdateMount(this.getId(), mount.level(), mount.exp(), mount.tiredness(), false));
         if (tiredness > 99) {
            modifyMount(mapleMount -> mapleMount.updateTiredness(99));
            this.dispelSkill(this.getJobType() * 10000000 + 1004);
            MessageBroadcaster.getInstance()
                  .sendServerNotice(this, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("MOUNT_TIREDNESS_HIGH"));
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
      if (party.isPresent()) {
         int currentMaxHp = getCurrentMaxHp();
         int currentHp = getHp();

         getPartyMembersOnSameMap()
               .forEach(character -> PacketCreator.announce(character, new UpdatePartyMemberHp(getId(), currentHp, currentMaxHp)));
      }
   }

   public void setQuestProgress(int id, int infoNumber, String progress) {
      MapleQuest q = QuestProcessor.getInstance().getQuest(id);
      MapleQuestStatus qs = getQuest(q);

      if (qs.getInfoNumber() == infoNumber && infoNumber > 0) {
         MapleQuest iq = QuestProcessor.getInstance().getQuest(infoNumber);
         MapleQuestStatus iqs = getQuest(iq);
         iqs.setProgress(0, progress);
      } else {
         qs.setProgress(infoNumber,
               progress);   // quest progress is thoroughly a string match, infoNumber is actually another quest id
      }

      announceUpdateQuest(DelayedQuestUpdate.UPDATE, qs, false);
      if (qs.getInfoNumber() > 0) {
         announceUpdateQuest(DelayedQuestUpdate.UPDATE, qs, true);
      }
   }

   public void awardQuestPoint(int awardedPoints) {
      if (YamlConfig.config.server.QUEST_POINT_REQUIREMENT < 1 || awardedPoints < 1) {
         return;
      }

      int delta;
      synchronized (quests) {
         questFame += awardedPoints;

         delta = questFame / YamlConfig.config.server.QUEST_POINT_REQUIREMENT;
         questFame %= YamlConfig.config.server.QUEST_POINT_REQUIREMENT;
      }

      if (delta > 0) {
         gainFame(delta);
      }
   }

   public enum DelayedQuestUpdate {    // quest updates allow player actions during NPC talk...
      UPDATE, FORFEIT, COMPLETE, INFO
   }

   private void announceUpdateQuestInternal(MapleCharacter chr, Pair<DelayedQuestUpdate, Object[]> questUpdate) {
      Object[] objs = questUpdate.getRight();

      switch (questUpdate.getLeft()) {
         case UPDATE -> {
            MapleQuestStatus questStatus = (MapleQuestStatus) objs[0];
            MapleQuest quest = questStatus.getQuest();
            PacketCreator.announce(this,
                  new UpdateQuest(quest.id(), questStatus.getStatus().getId(), questStatus.getInfoNumber(),
                        questStatus.getProgressData(), (Boolean) objs[1]));
         }
         case FORFEIT -> PacketCreator.announce(this, new ShowQuestForfeit((Short) objs[0]));
         case COMPLETE -> PacketCreator.announce(this, new CompleteQuest((Short) objs[0], (Long) objs[1]));
         case INFO -> {
            MapleQuestStatus qs = (MapleQuestStatus) objs[0];
            PacketCreator.announce(this, new UpdateQuestInfo(qs.getQuest().id(), qs.getNpc()));
         }
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
         announceUpdateQuestInternal(this, p);
      }
   }

   public void flushDelayedUpdateQuests() {
      List<Pair<DelayedQuestUpdate, Object[]>> qmQuestUpdateList;

      synchronized (npcUpdateQuests) {
         qmQuestUpdateList = new ArrayList<>(npcUpdateQuests);
         npcUpdateQuests.clear();
      }

      for (Pair<DelayedQuestUpdate, Object[]> q : qmQuestUpdateList) {
         announceUpdateQuestInternal(this, q);
      }
   }

   public void updateQuestStatus(MapleQuestStatus qs) {
      synchronized (quests) {
         quests.put(qs.getQuestID(), qs);
      }
      if (qs.getStatus().equals(MapleQuestStatus.Status.STARTED)) {
         announceUpdateQuest(DelayedQuestUpdate.UPDATE, qs, false);
         if (qs.getInfoNumber() > 0) {
            announceUpdateQuest(DelayedQuestUpdate.UPDATE, qs, true);
         }
         announceUpdateQuest(DelayedQuestUpdate.INFO, qs);
      } else if (qs.getStatus().equals(MapleQuestStatus.Status.COMPLETED)) {
         MapleQuest quest = qs.getQuest();
         short questId = quest.id();
         if (!quest.isSameDayRepeatable() && !QuestProcessor.getInstance().isExploitableQuest(questId)) {
            awardQuestPoint(YamlConfig.config.server.QUEST_POINT_PER_QUEST_COMPLETE);
         }
         qs.setCompleted(qs.getCompleted() + 1);

         announceUpdateQuest(DelayedQuestUpdate.COMPLETE, questId, qs.getCompletionTime());
      } else if (qs.getStatus().equals(MapleQuestStatus.Status.NOT_STARTED)) {
         announceUpdateQuest(DelayedQuestUpdate.UPDATE, qs, false);
         if (qs.getInfoNumber() > 0) {
            announceUpdateQuest(DelayedQuestUpdate.UPDATE, qs, true);
         }
      }
   }

   private void expireQuest(MapleQuest quest) {
      if (quest.forfeit(this)) {
         PacketCreator.announce(this, new QuestExpire(quest.id()));
      }
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
         for (MapleQuest quest : questExpirationTimes.keySet()) {
            quest.forfeit(this);
         }

         questExpirationTimes.clear();
      } finally {
         evtLock.unlock();
      }
   }

   public void questExpirationTask() {
      evtLock.lock();
      try {
         if (!questExpirationTimes.isEmpty()) {
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

         for (Entry<MapleQuest, Long> qe : questExpirationTimes.entrySet()) {
            if (qe.getValue() <= timeNow) {
               expireList.add(qe.getKey());
            }
         }

         if (!expireList.isEmpty()) {
            for (MapleQuest quest : expireList) {
               expireQuest(quest);
               questExpirationTimes.remove(quest);
            }

            if (questExpirationTimes.isEmpty()) {
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

         questExpirationTimes.put(quest, Server.getInstance().getCurrentTime() + time);
      } finally {
         evtLock.unlock();
      }
   }

   public void questTimeLimit(final MapleQuest quest, int seconds) {
      registerQuestExpire(quest, seconds * 1000);
      PacketCreator.announce(this, new AddQuestTimeLimit(quest.id(), seconds * 1000));
   }

   public void questTimeLimit2(final MapleQuest quest, long expires) {
      long timeLeft = expires - System.currentTimeMillis();

      if (timeLeft <= 0) {
         expireQuest(quest);
      } else {
         registerQuestExpire(quest, timeLeft);
      }
   }

   public void updateSingleStat(MapleStat stat, int newValue) {
      updateSingleStat(stat, newValue, false);
   }

   private void updateSingleStat(MapleStat stat, int newValue, boolean itemReaction) {
      PacketCreator
            .announce(client, new UpdatePlayerStats(Collections.singletonList(new Pair<>(stat, newValue)), itemReaction, this));
   }

   public void announce(final byte[] packet) {
      client.announce(packet);
   }

   @Override
   public int objectId() {
      return getId();
   }

   @Override
   public MapleMapObjectType type() {
      return MapleMapObjectType.PLAYER;
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
      return newYearCardRecords;
   }

   public Set<NewYearCardRecord> getReceivedNewYearRecords() {
      return newYearCardRecords.stream().filter(NewYearCardRecord::receiverReceivedCard).collect(Collectors.toSet());
   }

   public NewYearCardRecord getNewYearRecord(int cardId) {
      return newYearCardRecords.stream()
            .filter(newYearCardRecord -> newYearCardRecord.id() == cardId)
            .findFirst()
            .orElse(null);
   }

   public void addNewYearRecord(NewYearCardRecord newYearCardRecord) {
      newYearCardRecords.add(newYearCardRecord);
   }

   public void removeNewYearRecord(NewYearCardRecord newYearCardRecord) {
      newYearCardRecords.remove(newYearCardRecord);
   }

   public long portalDelay() {
      return portalDelay;
   }

   public void blockPortal(String scriptName) {
      if (!blockedPortals.contains(scriptName) && scriptName != null) {
         blockedPortals.add(scriptName);
         PacketCreator.announce(client, new EnableActions());
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
      PacketCreator.announce(this, new UpdateAreaInfo(area, info));
   }

   public Map<Short, String> getAreaInfos() {
      return area_info;
   }

   public void autoBan(String reason) {
      if (this.isGM() || this.isBanned()) {
         return;
      }

      this.ban(reason);
      PacketCreator.announce(this,
            new SendPolice(String.format("You have been blocked by the#b %s Police for HACK reason.#k", "HeavenMS")));
      TimerManager.getInstance().schedule(() -> client.disconnect(false, false), 5000);

      MessageBroadcaster.getInstance().sendWorldServerNotice(world, ServerNoticeType.LIGHT_BLUE, MapleCharacter::isGM,
            SimpleMessage.from(StringUtil.makeMapleReadable(this.name) + " was auto banned for " + reason));
   }

   public void block(int reason, int days, String desc) {
      DatabaseConnection.getInstance()
            .withConnection(connection -> AccountAdministrator.getInstance().setBan(connection, accountId, reason, days, desc));
   }

   public boolean isBanned() {
      return isBanned;
   }

   public List<Integer> getTeleportRockMaps() {
      return teleportRockMaps;
   }

   public List<Integer> getVipTeleportRockMaps() {
      return vipTeleportRockMaps;
   }

   public void deleteTeleportRockMap(int map) {
      teleportRockMaps.remove(Integer.valueOf(map));
      while (teleportRockMaps.size() < 10) {
         teleportRockMaps.add(999999999);
      }
   }

   public void addTeleportRockMap() {
      int index = teleportRockMaps.indexOf(999999999);
      if (index != -1) {
         teleportRockMaps.set(index, getMapId());
      }
   }

   public void deleteVipTeleportRockMap(int map) {
      vipTeleportRockMaps.remove(Integer.valueOf(map));
      while (vipTeleportRockMaps.size() < 10) {
         vipTeleportRockMaps.add(999999999);
      }
   }

   public void addVipTeleportRockMap() {
      int index = vipTeleportRockMaps.indexOf(999999999);
      if (index != -1) {
         vipTeleportRockMaps.set(index, getMapId());
      }
   }

   public AutoBanManager getAutoBanManager() {
      return autobanManager;
   }

   public void equippedItem(Equip equip) {
      int itemId = equip.id();

      if (itemId == 1122017) {
         this.equipPendantOfSpirit();
      } else if (itemId == 1812000) { // meso magnet
         equippedMesoMagnet = true;
      } else if (itemId == 1812001) { // item pouch
         equippedItemPouch = true;
      } else if (itemId == 1812007) { // item ignore pendant
         equippedPetItemIgnore = true;
      }
   }

   public void unequippedItem(Equip equip) {
      int itemId = equip.id();

      if (itemId == 1122017) {
         this.unequipPendantOfSpirit();
      } else if (itemId == 1812000) { // meso magnet
         equippedMesoMagnet = false;
      } else if (itemId == 1812001) { // item pouch
         equippedItemPouch = false;
      } else if (itemId == 1812007) { // item ignore pendant
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
            MessageBroadcaster.getInstance().sendServerNotice(MapleCharacter.this, ServerNoticeType.PINK_TEXT,
                  I18nMessage.from("PENDANT_OF_SPIRIT_MESSAGE").with(pendantExp, pendantExp));
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
      if (YamlConfig.config.server.USE_EQUIPMNT_LVLUP_CASH) {
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
      getGuild().ifPresent(guild -> MasterBroadcaster.getInstance().sendToGuild(guild, new NotifyMarriage(0, name)));

      MapleFamily family = this.getFamily();
      if (family != null) {
         MasterBroadcaster.getInstance()
               .sendToFamily(family, familyEntry -> PacketCreator.create(new NotifyMarriage(1, name)), false, this);
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

      emptyMount();
      if (remove) {
         partyQuest = null;
         events = null;
         mpc = null;
         mgc = null;
         party = Optional.empty();
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

   private void emptyMount() {
      if (mount != null) {
         getClient().getWorldServer().unregisterMountHunger(getId());
         mount = null;
      }
   }

   public void logOff() {
      this.loggedIn = false;
      DatabaseConnection.getInstance()
            .withConnection(connection -> CharacterAdministrator.getInstance().logCharacterOut(connection, id));
   }

   public void setLoginTime(long time) {
      this.loginTime = time;
   }

   public long getLoggedInTime() {
      return System.currentTimeMillis() - loginTime;
   }

   public byte[] getQuickSlotLoaded() {
      return quickSlotLoaded;
   }

   public void setQuickSlotLoaded(byte[] quickSlotLoaded) {
      this.quickSlotLoaded = quickSlotLoaded;
   }

   public MapleQuickSlotBinding getQuickSlotBinding() {
      return quickSlotBinding;
   }

   public void setQuickSlotBinding(MapleQuickSlotBinding quickSlotBinding) {
      this.quickSlotBinding = quickSlotBinding;
   }

   public boolean isLoggedIn() {
      return loggedIn;
   }

   public boolean getWhiteChat() {
      return isGM() && whiteChat;
   }

   public void toggleWhiteChat() {
      whiteChat = !whiteChat;
   }

   public boolean gotPartyQuestItem(String partyQuestCharacter) {
      return dataString.contains(partyQuestCharacter);
   }

   public void removePartyQuestItem(String letter) {
      if (gotPartyQuestItem(letter)) {
         dataString = dataString.substring(0, dataString.indexOf(letter)) + dataString
               .substring(dataString.indexOf(letter) + letter.length());
      }
   }

   public void setPartyQuestItemObtained(String partyQuestCharacter) {
      if (!dataString.contains(partyQuestCharacter)) {
         this.dataString += partyQuestCharacter;
      }
   }

   public void sendQuickMap() {
      if (getQuickSlotBinding() == null) {
         setQuickSlotBinding(new MapleQuickSlotBinding(MapleQuickSlotBinding.DEFAULT_QUICK_SLOTS));
      }
      PacketCreator.announce(this, new QuickSlotKey(getQuickSlotBinding().getQuickSlotKeyMapped()));
   }

   public void createDragon() {
      dragon = MapleMapObjectProcessor.getInstance().createDragon(this);
   }

   public MapleDragon getDragon() {
      return dragon;
   }

   public void setAutoPotHpAlert(float hpPortion) {
      autoPotHpAlert = hpPortion;
   }

   public float getAutoPotHpAlert() {
      return autoPotHpAlert;
   }

   public void setAutoPotMpAlert(float mpPortion) {
      autoPotMpAlert = mpPortion;
   }

   public float getAutoPotMpAlert() {
      return autoPotMpAlert;
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

         if (completionTime.get().getTime() + YamlConfig.config.server.NAME_CHANGE_COOLDOWN > currentTimeMillis) {
            return;
         }

         NameChangeAdministrator.getInstance().create(connection, getId(), getName(), newName);
      });
      return true;
   }

   public boolean cancelPendingNameChange() {
      DatabaseConnection.getInstance()
            .withConnection(connection -> NameChangeAdministrator.getInstance().cancelPendingNameChange(connection, getId()));
      return true;
   }

   public void doPendingNameChange() { //called on logout
      if (!pendingNameChange) {
         return;
      }

      DatabaseConnection.getInstance().withConnection(connection ->
            NameChangeProvider.getInstance().getPendingNameChangeForCharacter(connection, getId()).ifPresent(result -> {
               CharacterAdministrator.getInstance()
                     .performNameChange(connection, result.characterId(), result.oldName(), result.newName(), result.id());
               LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.CHANGE_CHARACTER_NAME,
                     "Name change applied : from \"" + getName() + "\" to \"" + result.newName() + "\" at " + Calendar.getInstance()
                           .getTime().toString());
            }));
   }

   public int checkWorldTransferEligibility() {
      if (getLevel() < 20) {
         return 2;
      } else if (getClient().getTempBanCalendar() != null
            && getClient().getTempBanCalendar().getTimeInMillis() + (30 * 24 * 60 * 60 * 1000) < Calendar.getInstance()
            .getTimeInMillis()) {
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

         if (completionTime.getTime() + YamlConfig.config.server.WORLD_TRANSFER_COOLDOWN > currentTimeMillis) {
            return;
         }

         WorldTransferAdministrator.getInstance().create(connection, getId(), getWorld(), newWorld);
      });
      return true;
   }

   public boolean cancelPendingWorldTransfer() {
      DatabaseConnection.getInstance()
            .withConnection(connection -> WorldTransferAdministrator.getInstance().cancelPendingForCharacter(connection, getId()));
      return true;
   }

   public String getLastCommandMessage() {
      return this.commandText;
   }

   public void setLastCommandMessage(String text) {
      this.commandText = text;
   }

   public int getRewardPoints() {
      return DatabaseConnection.getInstance()
            .withConnectionResult(connection -> AccountProvider.getInstance().getRewardPoints(connection, accountId)).orElse(0);
   }

   public void setRewardPoints(int value) {
      DatabaseConnection.getInstance()
            .withConnection(connection -> AccountAdministrator.getInstance().setRewardPoints(connection, accountId, value));
   }

   private void addReborns() {
      setReborns(getReborns() + 1);
   }

   public int getReborns() {
      if (!YamlConfig.config.server.USE_REBIRTH_SYSTEM) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("REBIRTH_NOT_ENABLED"));
         throw new NotEnabledException();
      }
      return DatabaseConnection.getInstance()
            .withConnectionResult(connection -> CharacterProvider.getInstance().countReborns(connection, id)).orElse(0);
   }

   private void setReborns(int value) {
      if (!YamlConfig.config.server.USE_REBIRTH_SYSTEM) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("REBIRTH_NOT_ENABLED"));
         throw new NotEnabledException();
      }
      DatabaseConnection.getInstance()
            .withConnection(connection -> CharacterAdministrator.getInstance().setReborns(connection, id, value));
   }

   public void executeReborn() {
      if (!YamlConfig.config.server.USE_REBIRTH_SYSTEM) {
         MessageBroadcaster.getInstance().yellowMessage(this, I18nMessage.from("REBIRTH_NOT_ENABLED"));
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
      return snowballAttack;
   }

   public void setLastSnowballAttack(long time) {
      this.snowballAttack = time;
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
         if (this.getParty().isPresent()) {
            this.getMonsterCarnival().setCP(this.getMonsterCarnival().getCP(team) + gain, team);
            if (gain > 0) {
               this.getMonsterCarnival().setTotalCP(this.getMonsterCarnival().getTotalCP(team) + gain, team);
            }
         }
         if (this.getCP() > this.getTotalCP()) {
            this.setTotalCP(this.getCP());
         }

         PacketCreator.announce(this, new MonsterCarnivalPointObtained(this.getCP(), this.getTotalCP()));
         if (this.getParty().isPresent() && getTeam() != -1) {
            MasterBroadcaster.getInstance().sendToAllInMap(getMap(),
                  new MonsterCarnivalPartyPoints(getTeam(), this.getMonsterCarnival().getCP(team),
                        this.getMonsterCarnival().getTotalCP(team)));
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

   public Locale getLocale() {
      return getClient().getLocale();
   }

   public enum FameStatus {
      OK, NOT_TODAY, NOT_THIS_MONTH
   }
}
