package server.expeditions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import client.MapleCharacter;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantLock;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.channel.Channel;
import server.TimerManager;
import server.life.MapleMonster;
import server.maps.MapleMap;
import tools.LogHelper;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.ui.GetClock;
import tools.packet.ui.StopClock;

public class MapleExpedition {

   private static final int[] EXPEDITION_BOSSES = {
         8800000,// - Zakum's first body
         8800001,// - Zakum's second body
         8800002,// - Zakum's third body
         8800003,// - Zakum's Arm 1
         8800004,// - Zakum's Arm 2
         8800005,// - Zakum's Arm 3
         8800006,// - Zakum's Arm 4
         8800007,// - Zakum's Arm 5
         8800008,// - Zakum's Arm 6
         8800009,// - Zakum's Arm 7
         8800010,// - Zakum's Arm 8
         8810000,// - Horntail's Left Head
         8810001,// - Horntail's Right Head
         8810002,// - Horntail's Head A
         8810003,// - Horntail's Head B
         8810004,// - Horntail's Head C
         8810005,// - Horntail's Left Hand
         8810006,// - Horntail's Right Hand
         8810007,// - Horntail's Wings
         8810008,// - Horntail's Legs
         8810009,// - Horntail's Tails
         9420546,// - Scarlion Boss
         9420547,// - Scarlion Boss
         9420548,// - Angry Scarlion Boss
         9420549,// - Furious Scarlion Boss
         9420541,// - Targa
         9420542,// - Targa
         9420543,// - Angry Targa
         9420544,// - Furious Targa
   };

   private MapleCharacter leader;
   private MapleExpeditionType type;
   private boolean registering;
   private MapleMap startMap;
   private List<String> bossLogs;
   private ScheduledFuture<?> schedule;
   private Map<Integer, String> members = new ConcurrentHashMap<>();
   private List<Integer> banned = new CopyOnWriteArrayList<>();
   private long startTime;
   private Properties props = new Properties();
   private boolean silent;
   private int minSize, maxSize;
   private MonitoredReentrantLock pL = MonitoredReentrantLockFactory.createLock(MonitoredLockType.EIM_PARTY, true);

   public MapleExpedition(MapleCharacter player, MapleExpeditionType met, boolean sil, int minPlayers, int maxPlayers) {
      leader = player;
      members.put(player.getId(), player.getName());
      startMap = player.getMap();
      type = met;
      silent = sil;
      minSize = (minPlayers != 0) ? minPlayers : type.getMinSize();
      maxSize = (maxPlayers != 0) ? maxPlayers : type.getMaxSize();
      bossLogs = new CopyOnWriteArrayList<>();
   }

   public int getMinSize() {
      return minSize;
   }

   public int getMaxSize() {
      return maxSize;
   }

   public void beginRegistration() {
      registering = true;
      PacketCreator.announce(leader, new GetClock(type.getRegistrationTime() * 60));
      if (!silent) {
         MessageBroadcaster.getInstance().sendMapServerNotice(startMap, ServerNoticeType.LIGHT_BLUE, character -> character != leader, I18nMessage.from("EXPEDITION_CAPTAIN_DECLARED").with(leader.getName()));
         MessageBroadcaster.getInstance().sendServerNotice(leader, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("EXPEDITION_BEGIN_REGISTRATION"));
      }
      scheduleRegistrationEnd();
   }

   private void scheduleRegistrationEnd() {
      final MapleExpedition expedition = this;
      startTime = System.currentTimeMillis() + type.getRegistrationTime() * 60 * 1000;

      schedule = TimerManager.getInstance().schedule(() -> {
         if (registering) {
            expedition.removeChannelExpedition(startMap.getChannelServer());
            if (!silent) {
               MessageBroadcaster.getInstance().sendMapServerNotice(startMap, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("EXPEDITION_TIME_LIMIT"));
            }

            dispose(false);
         }
      }, type.getRegistrationTime() * 60 * 1000);
   }

   public void dispose(boolean log) {
      broadcastToExpedition(PacketCreator.create(new StopClock()));

      if (schedule != null) {
         schedule.cancel(false);
      }
      if (log && !registering) {
         LogHelper.logExpedition(this);
      }
   }

   public void finishRegistration() {
      registering = false;
   }

   public void start() {
      finishRegistration();
      registerExpeditionAttempt();
      broadcastToExpedition(PacketCreator.create(new StopClock()));
      if (!silent) {
         MessageBroadcaster.getInstance().sendServerNotice(getActiveMembers(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("EXPEDITION_START"));
      }
      startTime = System.currentTimeMillis();
      MessageBroadcaster.getInstance().sendWorldServerNotice(startMap.getWorld(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("EXPEDITION_START_WITH_LEADER").with(type.toString(), leader.getName()));
   }

   public String addMember(MapleCharacter player) {
      if (!registering) {
         return "Sorry, this expedition is already underway. Registration is closed!";
      }
      if (banned.contains(player.getId())) {
         return "Sorry, you've been banned from this expedition by #b" + leader.getName() + "#k.";
      }
      if (members.size() >= this.getMaxSize()) { //Would be a miracle if anybody ever saw this
         return "Sorry, this expedition is full!";
      }

      int channel = this.getRecruitingMap().getChannelServer().getId();
      if (!MapleExpeditionBossLog.attemptBoss(player.getId(), channel, this, false)) {
         return "Sorry, you've already reached the quota of attempts for this expedition! Try again another day...";
      }

      members.put(player.getId(), player.getName());
      PacketCreator.announce(player, new GetClock((int) (startTime - System.currentTimeMillis()) / 1000));
      if (!silent) {
         MessageBroadcaster.getInstance().sendServerNotice(getActiveMembers(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("EXPEDITION_PLAYER_JOINED").with(player.getName()));
      }
      return "You have registered for the expedition successfully!";
   }

   public int addMemberInt(MapleCharacter player) {
      if (!registering) {
         return 1; //"Sorry, this expedition is already underway. Registration is closed!";
      }
      if (banned.contains(player.getId())) {
         return 2; //"Sorry, you've been banned from this expedition by #b" + leader.getName() + "#k.";
      }
      if (members.size() >= this.getMaxSize()) { //Would be a miracle if anybody ever saw this
         return 3; //"Sorry, this expedition is full!";
      }

      members.put(player.getId(), player.getName());
      PacketCreator.announce(player, new GetClock((int) (startTime - System.currentTimeMillis()) / 1000));
      if (!silent) {
         MessageBroadcaster.getInstance().sendServerNotice(getActiveMembers(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("EXPEDITION_PLAYER_JOINED").with(player.getName()));
      }
      return 0; //"You have registered for the expedition successfully!";
   }

   private void registerExpeditionAttempt() {
      int channel = this.getRecruitingMap().getChannelServer().getId();

      for (MapleCharacter chr : getActiveMembers()) {
         MapleExpeditionBossLog.attemptBoss(chr.getId(), channel, this, true);
      }
   }

   private void broadcastToExpedition(byte[] packet) {
      for (MapleCharacter chr : getActiveMembers()) {
         chr.announce(packet);
      }
   }

   public boolean removeMember(MapleCharacter chr) {
      if (members.remove(chr.getId()) != null) {
         PacketCreator.announce(chr, new StopClock());
         if (!silent) {
            MessageBroadcaster.getInstance().sendServerNotice(getActiveMembers(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("EXPEDITION_PLAYER_LEFT").with(chr.getName()));
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("EXPEDITION_LEFT"));
         }
         return true;
      }

      return false;
   }

   public void ban(Entry<Integer, String> chr) {
      int cid = chr.getKey();
      if (!banned.contains(cid)) {
         banned.add(cid);
         members.remove(cid);

         if (!silent) {
            MessageBroadcaster.getInstance().sendServerNotice(getActiveMembers(), ServerNoticeType.LIGHT_BLUE, I18nMessage.from("EXPEDITION_PLAYER_BANNED").with(chr.getValue()));
         }

         startMap.getWorldServer().getPlayerStorage().getCharacterById(cid)
               .filter(MapleCharacter::isLoggedInWorld).ifPresent(character -> {
            PacketCreator.announce(character, new StopClock());
            if (!silent) {
               MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("EXPEDITION_BANNED"));
            }
            if (MapleExpeditionType.ARIANT.equals(type) || MapleExpeditionType.ARIANT1.equals(type) || MapleExpeditionType.ARIANT2.equals(type)) {
               character.changeMap(980010000);
            }

         });
      }
   }

   public void monsterKilled(MapleCharacter chr, MapleMonster mob) {
      for (int expeditionBoss : EXPEDITION_BOSSES) {
         if (mob.id() == expeditionBoss) { //If the monster killed was a boss
            String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            bossLogs.add(">" + mob.getName() + " was killed after " + LogHelper.getTimeString(startTime) + " - " + timeStamp + "\r\n");
            return;
         }
      }
   }

   public void setProperty(String key, String value) {
      pL.lock();
      try {
         props.setProperty(key, value);
      } finally {
         pL.unlock();
      }
   }

   public String getProperty(String key) {
      pL.lock();
      try {
         return props.getProperty(key);
      } finally {
         pL.unlock();
      }
   }

   public MapleExpeditionType getType() {
      return type;
   }

   public List<MapleCharacter> getActiveMembers() {
      return getMembers().keySet().stream()
            .map(id -> startMap.getWorldServer().getPlayerStorage().getCharacterById(id))
            .flatMap(Optional::stream)
            .filter(MapleCharacter::isLoggedInWorld)
            .collect(Collectors.toList());
   }

   public Map<Integer, String> getMembers() {
      return new HashMap<>(members);
   }

   public List<Entry<Integer, String>> getMemberList() {
      List<Entry<Integer, String>> memberList = new LinkedList<>();
      Entry<Integer, String> leaderEntry = null;

      for (Entry<Integer, String> e : getMembers().entrySet()) {
         if (!isLeader(e.getKey())) {
            memberList.add(e);
         } else {
            leaderEntry = e;
         }
      }

      if (leaderEntry != null) {
         memberList.add(0, leaderEntry);
      }

      return memberList;
   }

   public final boolean isExpeditionTeamTogether() {
      List<MapleCharacter> chars = getActiveMembers();
      if (chars.size() <= 1) {
         return true;
      }

      Iterator<MapleCharacter> iterator = chars.iterator();
      MapleCharacter mc = iterator.next();
      int mapId = mc.getMapId();

      for (; iterator.hasNext(); ) {
         mc = iterator.next();
         if (mc.getMapId() != mapId) {
            return false;
         }
      }

      return true;
   }

   public final void warpExpeditionTeam(int warpFrom, int warpTo) {
      List<MapleCharacter> players = getActiveMembers();

      for (MapleCharacter chr : players) {
         if (chr.getMapId() == warpFrom) {
            chr.changeMap(warpTo);
         }
      }
   }

   public final void warpExpeditionTeam(int warpTo) {
      List<MapleCharacter> players = getActiveMembers();

      for (MapleCharacter chr : players) {
         chr.changeMap(warpTo);
      }
   }

   public final void warpExpeditionTeamToMapSpawnPoint(int warpFrom, int warpTo, int toSp) {
      List<MapleCharacter> players = getActiveMembers();

      for (MapleCharacter chr : players) {
         if (chr.getMapId() == warpFrom) {
            chr.changeMap(warpTo, toSp);
         }
      }
   }

   public final void warpExpeditionTeamToMapSpawnPoint(int warpTo, int toSp) {
      List<MapleCharacter> players = getActiveMembers();

      for (MapleCharacter chr : players) {
         chr.changeMap(warpTo, toSp);
      }
   }

   public final boolean addChannelExpedition(Channel ch) {
      return ch.addExpedition(this);
   }

   public final void removeChannelExpedition(Channel ch) {
      ch.removeExpedition(this);
   }

   public MapleCharacter getLeader() {
      return leader;
   }

   public MapleMap getRecruitingMap() {
      return startMap;
   }

   public boolean contains(MapleCharacter player) {
      return members.containsKey(player.getId()) || isLeader(player);
   }

   public boolean isLeader(MapleCharacter player) {
      return isLeader(player.getId());
   }

   public boolean isLeader(int playerId) {
      return leader.getId() == playerId;
   }

   public boolean isRegistering() {
      return registering;
   }

   public boolean isInProgress() {
      return !registering;
   }

   public long getStartTime() {
      return startTime;
   }

   public List<String> getBossLogs() {
      return bossLogs;
   }
}
