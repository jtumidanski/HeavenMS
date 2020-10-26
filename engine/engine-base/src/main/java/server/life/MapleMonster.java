package server.life;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleFamilyEntry;
import client.Skill;
import client.SkillFactory;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import config.YamlConfig;
import constants.MapleJob;
import constants.skills.Crusader;
import constants.skills.FirePoisonMagician;
import constants.skills.Hermit;
import constants.skills.IceLighteningMagician;
import constants.skills.NightLord;
import constants.skills.NightWalker;
import constants.skills.Priest;
import constants.skills.Shadower;
import constants.skills.WhiteKnight;
import net.server.audit.LockCollector;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantLock;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.channel.Channel;
import net.server.coordinator.world.MapleMonsterAggroCoordinator;
import net.server.services.task.channel.MobAnimationService;
import net.server.services.task.channel.MobClearSkillService;
import net.server.services.task.channel.MobStatusService;
import net.server.services.task.channel.OverallService;
import net.server.services.type.ChannelServices;
import net.server.world.MapleParty;
import scripting.event.EventInstanceManager;
import server.MapleStatEffect;
import server.TimerManager;
import server.loot.MapleLootManager;
import server.maps.MapleMap;
import server.maps.MapleMapObjectType;
import server.maps.MapleSummon;
import server.processor.QuestProcessor;
import server.processor.maps.MapleMapObjectProcessor;
import tools.I18nMessage;
import tools.IntervalBuilder;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.Randomizer;
import tools.ServerNoticeType;
import tools.SimpleMessage;
import tools.packet.PacketInput;
import tools.packet.field.effect.PlaySound;
import tools.packet.field.effect.ShowBossHP;
import tools.packet.field.effect.ShowEffect;
import tools.packet.monster.ApplyMonsterStatus;
import tools.packet.monster.CancelMonsterStatus;
import tools.packet.monster.DamageMonster;
import tools.packet.monster.HealMonster;
import tools.packet.monster.ShowMonsterHP;
import tools.packet.movement.MoveMonster;
import tools.packet.remove.RemoveSummon;
import tools.packet.spawn.ControlMonster;
import tools.packet.spawn.SpawnSummon;
import tools.packet.spawn.StopMonsterControl;

public class MapleMonster extends AbstractLoadedMapleLife {

   private final HashMap<Integer, AtomicLong> takenDamage = new HashMap<>();
   private ChangeableStats changeableStats = null;  //unused, v83 WZs offers no support for changeable stats.
   private MapleMonsterStats stats;
   private AtomicInteger hp = new AtomicInteger(1);
   private AtomicLong maxHpPlusHeal = new AtomicLong(1);
   private int mp;
   private WeakReference<MapleCharacter> controller = new WeakReference<>(null);
   private boolean controllerHasAggro, controllerKnowsAboutAggro, controllerHasPuppet;
   private Collection<MonsterListener> listeners = new LinkedList<>();
   private EnumMap<MonsterStatus, MonsterStatusEffect> monsterStatuses = new EnumMap<>(MonsterStatus.class);
   private ArrayList<MonsterStatus> alreadyBuffed = new ArrayList<>();
   private MapleMap map;
   private int VenomMultiplier = 0;
   private boolean fake = false;
   private boolean dropsDisabled = false;
   private List<Pair<Integer, Integer>> usedSkills = new ArrayList<>();
   private Map<Pair<Integer, Integer>, Integer> skillsUsed = new HashMap<>();
   private Set<Integer> usedAttacks = new HashSet<>();
   private Set<Integer> calledMobObjectIds = null;
   private WeakReference<MapleMonster> callerMob = new WeakReference<>(null);
   private List<Integer> stolenItems = new ArrayList<>(5);
   private int team;
   private int parentMobOid = 0;
   private int spawnEffect = 0;
   private ScheduledFuture<?> monsterItemDrop = null;
   private Runnable removeAfterAction = null;
   private boolean availablePuppetUpdate = true;

   private MonitoredReentrantLock externalLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.MOB_EXT);
   private MonitoredReentrantLock monsterLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.MOB, true);
   private MonitoredReentrantLock statusLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.MOB_STATUS);
   private MonitoredReentrantLock animationLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.MOB_ANI);
   private MonitoredReentrantLock aggroUpdateLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.MOB_AGGRO);

   public MapleMonster(int id, MapleMonsterStats stats) {
      super(id);
      initWithStats(stats);
   }

   public MapleMonster(MapleMonster monster) {
      super(monster);
      initWithStats(monster.stats);
   }

   private static boolean isWhiteExpGain(MapleCharacter chr, Map<Integer, Float> personalRatio, double standardDeviationRatio) {
      Float pr = personalRatio.get(chr.getId());
      if (pr == null) {
         return false;
      }

      return pr >= standardDeviationRatio;
   }

   private static double calcExperienceStandDevThreshold(List<Float> entryExpRatio, int totalEntries) {
      float avgExpReward = 0.0f;
      for (Float exp : entryExpRatio) {
         avgExpReward += exp;
      }
      avgExpReward /= totalEntries;

      float varExpReward = 0.0f;
      for (Float exp : entryExpRatio) {
         varExpReward += Math.pow(exp - avgExpReward, 2);
      }
      varExpReward /= entryExpRatio.size();

      return avgExpReward + Math.sqrt(varExpReward);
   }

   private static int expValueToInteger(double exp) {
      if (exp > Integer.MAX_VALUE) {
         exp = Integer.MAX_VALUE;
      } else if (exp < Integer.MIN_VALUE) {
         exp = Integer.MIN_VALUE;
      }

      return (int) Math.round(exp);
   }

   private static void aggroMonsterControl(MapleClient c, MapleMonster mob, boolean immediateAggro) {
      PacketCreator.announce(c, new ControlMonster(mob, false, immediateAggro));
      mob.announceMonsterStatus(c);
   }

   public void lockMonster() {
      externalLock.lock();
   }

   public void unlockMonster() {
      externalLock.unlock();
   }

   private void initWithStats(MapleMonsterStats baseStats) {
      setStance(5);
      this.stats = baseStats.copy();
      hp.set(stats.hp());
      mp = stats.mp();

      maxHpPlusHeal.set(hp.get());
   }

   public int getSpawnEffect() {
      return spawnEffect;
   }

   public void setSpawnEffect(int spawnEffect) {
      this.spawnEffect = spawnEffect;
   }

   public void disableDrops() {
      this.dropsDisabled = true;
   }

   public void enableDrops() {
      this.dropsDisabled = false;
   }

   public boolean dropsDisabled() {
      return dropsDisabled;
   }

   public int getParentMobOid() {
      return parentMobOid;
   }

   public void setParentMobOid(int parentMobId) {
      this.parentMobOid = parentMobId;
   }

   public int countAvailableMobSummons(int summonsSize, int skillLimit) {
      int summonsCount;

      Set<Integer> calledObjectIds = this.calledMobObjectIds;
      if (calledObjectIds != null) {
         summonsCount = calledObjectIds.size();
      } else {
         summonsCount = 0;
      }

      return Math.min(summonsSize, skillLimit - summonsCount);
   }

   public void addSummonedMob(MapleMonster mob) {
      Set<Integer> calledObjectIds = this.calledMobObjectIds;
      if (calledObjectIds == null) {
         calledObjectIds = Collections.synchronizedSet(new HashSet<>());
         this.calledMobObjectIds = calledObjectIds;
      }

      calledObjectIds.add(mob.objectId());
      mob.setSummonerMob(this);
   }

   private void removeSummonedMob(int mobOid) {
      Set<Integer> calledObjectIds = this.calledMobObjectIds;
      if (calledObjectIds != null) {
         calledObjectIds.remove(mobOid);
      }
   }

   private void setSummonerMob(MapleMonster mob) {
      this.callerMob = new WeakReference<>(mob);
   }

   private void dispatchClearSummons() {
      MapleMonster caller = this.callerMob.get();
      if (caller != null) {
         caller.removeSummonedMob(this.objectId());
      }

      this.calledMobObjectIds = null;
   }

   public void pushRemoveAfterAction(Runnable run) {
      this.removeAfterAction = run;
   }

   public Runnable popRemoveAfterAction() {
      Runnable r = this.removeAfterAction;
      this.removeAfterAction = null;

      return r;
   }

   public int getHp() {
      return hp.get();
   }

   public synchronized void addHp(int hp) {
      if (this.hp.get() <= 0) {
         return;
      }
      this.hp.addAndGet(hp);
   }

   public synchronized void setStartingHp(int hp) {
      stats = stats.setHp(hp);
      this.hp.set(hp);
   }

   public int getMaxHp() {
      return stats.hp();
   }

   public int getMp() {
      return mp;
   }

   public void setMp(int mp) {
      if (mp < 0) {
         mp = 0;
      }
      this.mp = mp;
   }

   public int getMaxMp() {
      return stats.mp();
   }

   public int getExp() {
      return stats.exp();
   }

   public int getLevel() {
      return stats.level();
   }

   public int getCP() {
      return stats.cp();
   }

   public int getTeam() {
      return team;
   }

   public void setTeam(int team) {
      this.team = team;
   }

   public int getVenomMulti() {
      return this.VenomMultiplier;
   }

   public void setVenomMulti(int multiplier) {
      this.VenomMultiplier = multiplier;
   }

   public MapleMonsterStats getStats() {
      return stats;
   }

   public boolean isBoss() {
      return stats.isBoss();
   }

   public void setBoss(boolean boss) {
      this.stats = this.stats.setBoss(boss);
   }

   public int getAnimationTime(String name) {
      return stats.getAnimationTime(name);
   }

   private List<Integer> getRevives() {
      return stats.revives();
   }

   private byte getTagColor() {
      return stats.tagColor();
   }

   private byte getTagBgColor() {
      return stats.tagBackgroundColor();
   }

   public void setHpZero() {     // force HP = 0
      applyAndGetHpDamage(Integer.MAX_VALUE, false);
   }

   private boolean applyAnimationIfRoaming(int attackPos, MobSkill skill) {   // roam: not casting attack or skill animations
      if (!animationLock.tryLock()) {
         return false;
      }

      try {
         long animationTime;

         if (skill == null) {
            animationTime = MapleMonsterInformationProvider.getInstance().getMobAttackAnimationTime(this.id(), attackPos);
         } else {
            animationTime = MapleMonsterInformationProvider.getInstance().getMobSkillAnimationTime(skill);
         }

         if (animationTime > 0) {
            MobAnimationService service =
                  (MobAnimationService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_ANIMATION);
            return service.registerMobOnAnimationEffect(map.getId(), this.hashCode(), animationTime);
         } else {
            return true;
         }
      } finally {
         animationLock.unlock();
      }
   }

   public synchronized Integer applyAndGetHpDamage(int delta, boolean stayAlive) {
      int curHp = hp.get();
      if (curHp <= 0) {       // this monster is already dead
         return null;
      }

      if (delta >= 0) {
         if (stayAlive) {
            curHp--;
         }
         int trueDamage = Math.min(curHp, delta);

         hp.addAndGet(-trueDamage);
         return trueDamage;
      } else {
         int trueHeal = -delta;
         int hp2Heal = curHp + trueHeal;
         int maxHp = getMaxHp();

         if (hp2Heal > maxHp) {
            trueHeal -= (hp2Heal - maxHp);
         }

         hp.addAndGet(trueHeal);
         return trueHeal;
      }
   }

   public synchronized void disposeMapObject() {     // mob is no longer associated with the map it was in
      hp.set(-1);
   }

   public void broadcastMobHpBar(MapleCharacter from) {
      if (hasBossHPBar()) {
         from.setPlayerAggro(this.hashCode());
         from.getMap().broadcastBossHpMessage(this, this.hashCode(), makeBossHPBarPacket(), this.position());
      } else if (!isBoss()) {
         int remainingHP = (int) Math.max(1, hp.get() * 100f / getMaxHp());
         //byte[] packet = PacketCreator.create(new ShowMonsterHP(this.objectId(), remainingHP));
         if (from.getParty().isPresent()) {
            from.getParty()
                  .map(MapleParty::getMembers).orElse(Collections.emptyList()).parallelStream()
                  .map(partyCharacter -> from.getMap().getCharacterById(partyCharacter.getId()))
                  .filter(Objects::nonNull)
                  .forEach(character -> PacketCreator.announce(character, new ShowMonsterHP(this.objectId(), remainingHP)));
         } else {
            PacketCreator.announce(from, new ShowMonsterHP(this.objectId(), remainingHP));
         }
      }
   }

   public boolean damage(MapleCharacter attacker, int damage, boolean stayAlive) {
      boolean lastHit = false;

      this.lockMonster();
      try {
         if (!this.isAlive()) {
            return false;
         }

            /* pyramid not implemented
            Pair<Integer, Integer> cool = this.getStats().getCool();
            if (cool != null) {
                Pyramid pq = (Pyramid) chr.getPartyQuest();
                if (pq != null) {
                    if (damage > 0) {
                        if (damage >= cool.getLeft()) {
                            if ((Math.random() * 100) < cool.getRight()) {
                                pq.cool();
                            } else {
                                pq.kill();
                            }
                        } else {
                            pq.kill();
                        }
                    } else {
                        pq.miss();
                    }
                    killed = true;
                }
            }
            */

         if (damage > 0) {
            this.applyDamage(attacker, damage, stayAlive, false);
            if (!this.isAlive()) {  // monster just died
               lastHit = true;
            }
         }
      } finally {
         this.unlockMonster();
      }

      return lastHit;
   }

   private void applyDamage(MapleCharacter from, int damage, boolean stayAlive, boolean fake) {
      Integer trueDamage = applyAndGetHpDamage(damage, stayAlive);
      if (trueDamage == null) {
         return;
      }

      if (YamlConfig.config.server.USE_DEBUG) {
         MessageBroadcaster.getInstance().sendServerNotice(from, ServerNoticeType.PINK_TEXT,
               I18nMessage.from("DEBUG_MONSTER_DAMAGE").with(this.id(), this.objectId()));
      }

      if (!fake) {
         dispatchMonsterDamaged(from, trueDamage);
      }

      if (!takenDamage.containsKey(from.getId())) {
         takenDamage.put(from.getId(), new AtomicLong(trueDamage));
      } else {
         takenDamage.get(from.getId()).addAndGet(trueDamage);
      }

      broadcastMobHpBar(from);
   }

   public void applyFakeDamage(MapleCharacter from, int damage, boolean stayAlive) {
      applyDamage(from, damage, stayAlive, true);
   }

   public void heal(int hp, int mp) {
      Integer hpHealed = applyAndGetHpDamage(-hp, false);
      if (hpHealed == null) {
         return;
      }

      int mp2Heal = getMp() + mp;
      int maxMp = getMaxMp();
      if (mp2Heal >= maxMp) {
         mp2Heal = maxMp;
      }
      setMp(mp2Heal);

      if (hp > 0) {
         MasterBroadcaster.getInstance().sendToAllInMap(getMap(), new HealMonster(this.objectId(), hp, getHp(), getMaxHp()));
      }

      maxHpPlusHeal.addAndGet(hpHealed);
      dispatchMonsterHealed(hpHealed);
   }

   public boolean isAttackedBy(MapleCharacter chr) {
      return takenDamage.containsKey(chr.getId());
   }

   private void distributePlayerExperience(MapleCharacter chr, float exp, float partyBonusMod, int totalPartyLevel,
                                           boolean highestPartyDamage, boolean whiteExpGain, boolean hasPartySharers) {
      float playerExp = (YamlConfig.config.server.EXP_SPLIT_COMMON_MOD * chr.getLevel()) / totalPartyLevel;
      if (highestPartyDamage) {
         playerExp += YamlConfig.config.server.EXP_SPLIT_MVP_MOD;
      }

      playerExp *= exp;
      float bonusExp = partyBonusMod * playerExp;

      this.giveExpToCharacter(chr, playerExp, bonusExp, whiteExpGain, hasPartySharers);
      giveFamilyRep(chr.getFamilyEntry());
   }

   private void distributePartyExperience(Map<MapleCharacter, Long> partyParticipation, float expPerDmg,
                                          Set<MapleCharacter> underLeveled, Map<Integer, Float> personalRatio,
                                          double standardDeviationRatio) {
      IntervalBuilder leechInterval = new IntervalBuilder();
      leechInterval.addInterval(this.getLevel() - YamlConfig.config.server.EXP_SPLIT_LEVEL_INTERVAL,
            this.getLevel() + YamlConfig.config.server.EXP_SPLIT_LEVEL_INTERVAL);

      long maxDamage = 0, partyDamage = 0;
      MapleCharacter participationMvp = null;
      for (Entry<MapleCharacter, Long> e : partyParticipation.entrySet()) {
         long entryDamage = e.getValue();
         partyDamage += entryDamage;

         if (maxDamage < entryDamage) {
            maxDamage = entryDamage;
            participationMvp = e.getKey();
         }

         int chrLevel = e.getKey().getLevel();
         leechInterval.addInterval(chrLevel - YamlConfig.config.server.EXP_SPLIT_LEECH_INTERVAL,
               chrLevel + YamlConfig.config.server.EXP_SPLIT_LEECH_INTERVAL);
      }

      List<MapleCharacter> expMembers = new LinkedList<>();
      int totalPartyLevel = 0;

      if (YamlConfig.config.server.USE_ENFORCE_MOB_LEVEL_RANGE) {
         for (MapleCharacter member : partyParticipation.keySet().iterator().next().getPartyMembersOnSameMap()) {
            if (!leechInterval.inInterval(member.getLevel())) {
               underLeveled.add(member);
               continue;
            }

            totalPartyLevel += member.getLevel();
            expMembers.add(member);
         }
      } else {
         for (MapleCharacter member : partyParticipation.keySet().iterator().next().getPartyMembersOnSameMap()) {
            totalPartyLevel += member.getLevel();
            expMembers.add(member);
         }
      }

      int membersSize = expMembers.size();
      float participationExp = partyDamage * expPerDmg;

      boolean hasPartySharers = membersSize > 1;
      float partyBonusMod = hasPartySharers ? 0.05f * membersSize : 0.0f;

      for (MapleCharacter mc : expMembers) {
         distributePlayerExperience(mc, participationExp, partyBonusMod, totalPartyLevel, mc == participationMvp,
               isWhiteExpGain(mc, personalRatio, standardDeviationRatio), hasPartySharers);
         giveFamilyRep(mc.getFamilyEntry());
      }
   }

   private void distributeExperience(int killerId) {
      if (isAlive()) {
         return;
      }

      Map<MapleParty, Map<MapleCharacter, Long>> partyExpDist = new HashMap<>();
      Map<MapleCharacter, Long> soloExpDist = new HashMap<>();

      Map<Integer, MapleCharacter> mapPlayers = map.getMapAllPlayers();

      int totalEntries =
            0;   // counts "participant parties", players who no longer are available in the map is an "independent party"
      for (Entry<Integer, AtomicLong> e : takenDamage.entrySet()) {
         MapleCharacter chr = mapPlayers.get(e.getKey());
         if (chr != null) {
            long damage = e.getValue().longValue();

            Optional<MapleParty> p = chr.getParty();
            if (p.isPresent()) {
               Map<MapleCharacter, Long> partyParticipation = partyExpDist.get(p.get());
               if (partyParticipation == null) {
                  partyParticipation = new HashMap<>(6);
                  partyExpDist.put(p.get(), partyParticipation);

                  totalEntries += 1;
               }

               partyParticipation.put(chr, damage);
            } else {
               soloExpDist.put(chr, damage);
               totalEntries += 1;
            }
         } else {
            totalEntries += 1;
         }
      }

      long totalDamage = maxHpPlusHeal.get();
      int mobExp = getExp();
      float expPerDmg = ((float) mobExp) / totalDamage;

      Map<Integer, Float> personalRatio = new HashMap<>();
      List<Float> entryExpRatio = new LinkedList<>();
      for (Entry<MapleCharacter, Long> e : soloExpDist.entrySet()) {
         float ratio = ((float) e.getValue()) / totalDamage;

         personalRatio.put(e.getKey().getId(), ratio);
         entryExpRatio.add(ratio);
      }

      for (Map<MapleCharacter, Long> m : partyExpDist.values()) {
         float ratio = 0.0f;
         for (Entry<MapleCharacter, Long> e : m.entrySet()) {
            float chrRatio = ((float) e.getValue()) / totalDamage;

            personalRatio.put(e.getKey().getId(), chrRatio);
            ratio += chrRatio;
         }

         entryExpRatio.add(ratio);
      }

      double standardDeviationRatio = calcExperienceStandDevThreshold(entryExpRatio, totalEntries);

      Set<MapleCharacter> underLeveled = new HashSet<>();
      for (Entry<MapleCharacter, Long> chrParticipation : soloExpDist.entrySet()) {
         float exp = chrParticipation.getValue() * expPerDmg;
         MapleCharacter chr = chrParticipation.getKey();

         distributePlayerExperience(chr, exp, 0.0f, chr.getLevel(), true,
               isWhiteExpGain(chr, personalRatio, standardDeviationRatio), false);
      }

      for (Map<MapleCharacter, Long> partyParticipation : partyExpDist.values()) {
         distributePartyExperience(partyParticipation, expPerDmg, underLeveled, personalRatio, standardDeviationRatio);
      }

      EventInstanceManager eim = getMap().getEventInstance();
      if (eim != null) {
         MapleCharacter chr = mapPlayers.get(killerId);
         if (chr != null) {
            eim.monsterKilled(chr, this);
         }
      }

      for (MapleCharacter mc : underLeveled) {
         mc.showUnderLeveledInfo(this);
      }
   }

   private float getStatusExpMultiplier(MapleCharacter attacker, boolean hasPartySharers) {
      float multiplier = 1.0f;

      Integer holySymbol = attacker.getBuffedValue(MapleBuffStat.HOLY_SYMBOL);
      if (holySymbol != null) {
         if (YamlConfig.config.server.USE_FULL_HOLY_SYMBOL) {
            multiplier *= (1.0 + (holySymbol.doubleValue() / 100.0));
         } else {
            multiplier *= (1.0 + (holySymbol.doubleValue() / (hasPartySharers ? 100.0 : 500.0)));
         }
      }

      statusLock.lock();
      try {
         MonsterStatusEffect mse = monsterStatuses.get(MonsterStatus.SHOWDOWN);
         if (mse != null) {
            multiplier *= (1.0 + (mse.getStatuses().get(MonsterStatus.SHOWDOWN).doubleValue() / 100.0));
         }
      } finally {
         statusLock.unlock();
      }

      return multiplier;
   }

   private void giveExpToCharacter(MapleCharacter attacker, Float personalExp, Float partyExp, boolean white,
                                   boolean hasPartySharers) {
      if (attacker.isAlive()) {
         if (personalExp != null) {
            personalExp *= getStatusExpMultiplier(attacker, hasPartySharers);
            personalExp *= attacker.getExpRate();
         } else {
            personalExp = 0.0f;
         }

         Integer expBonus = attacker.getBuffedValue(MapleBuffStat.EXP_INCREASE);
         if (expBonus != null) {
            personalExp += expBonus;
         }

         int _personalExp = expValueToInteger(personalExp); // assuming no negative xp here

         if (partyExp != null) {
            partyExp *= getStatusExpMultiplier(attacker, hasPartySharers);
            partyExp *= attacker.getExpRate();
            partyExp *= YamlConfig.config.server.PARTY_BONUS_EXP_RATE;
         } else {
            partyExp = 0.0f;
         }

         int _partyExp = expValueToInteger(partyExp);

         attacker.gainExp(_personalExp, _partyExp, true, false, white);
         attacker.increaseEquipExp(_personalExp);
         QuestProcessor.getInstance().raiseQuestMobCount(attacker, id());
      }
   }

   public List<MonsterDropEntry> retrieveRelevantDrops() {
      if (this.getStats().isFriendly()) {
         return MapleMonsterInformationProvider.getInstance().retrieveEffectiveDrop(this.id());
      }

      Map<Integer, MapleCharacter> mapCharacters = map.getMapAllPlayers();

      List<MapleCharacter> lootChars = new LinkedList<>();
      for (Integer cid : takenDamage.keySet()) {
         MapleCharacter chr = mapCharacters.get(cid);
         if (chr != null && chr.isLoggedInWorld()) {
            lootChars.add(chr);
         }
      }

      return MapleLootManager.retrieveRelevantDrops(this.id(), lootChars);
   }

   public MapleCharacter killBy(final MapleCharacter killer) {
      distributeExperience(killer != null ? killer.getId() : 0);

      final Pair<MapleCharacter, Boolean> lastController = aggroRemoveController();
      final List<Integer> toSpawn = this.getRevives();
      if (toSpawn != null) {
         final MapleMap reviveMap = map;
         if (toSpawn.contains(9300216) && reviveMap.getId() > 925000000 && reviveMap.getId() < 926000000) {
            MasterBroadcaster.getInstance().sendToAllInMap(reviveMap, new PlaySound("Dojang/clear"));
            MasterBroadcaster.getInstance().sendToAllInMap(reviveMap, new ShowEffect("dojang/end/clear"));
         }
         Pair<Integer, String> timeMob = reviveMap.getTimeMob();
         if (timeMob != null) {
            if (toSpawn.contains(timeMob.getLeft())) {
               MessageBroadcaster.getInstance()
                     .sendMapServerNotice(reviveMap, ServerNoticeType.LIGHT_BLUE, SimpleMessage.from(timeMob.getRight()));
            }
         }

         if (toSpawn.size() > 0) {
            final EventInstanceManager eim = this.getMap().getEventInstance();

            TimerManager.getInstance().schedule(() -> {
               MapleCharacter controller = lastController.getLeft();
               boolean aggro = lastController.getRight();

               for (Integer mid : toSpawn) {
                  MapleLifeFactory.getMonster(mid).ifPresent(mob -> {
                     mob.setPosition(MapleMonster.this.position());
                     mob.setFh(fh());
                     mob.setParentMobOid(MapleMonster.this.objectId());

                     if (dropsDisabled()) {
                        mob.disableDrops();
                     }
                     reviveMap.spawnMonster(mob);

                     if (mob.id() >= 8810010 && mob.id() <= 8810017 && reviveMap.isHorntailDefeated()) {
                        boolean htKilled;
                        MapleMonster ht = reviveMap.getMonsterById(8810018);

                        if (ht != null) {
                           ht.lockMonster();
                           try {
                              htKilled = ht.isAlive();
                              ht.setHpZero();
                           } finally {
                              ht.unlockMonster();
                           }

                           if (htKilled) {
                              reviveMap.killMonster(ht, killer, true);
                           }
                        }

                        for (int i = 8810017; i >= 8810010; i--) {
                           reviveMap.killMonster(reviveMap.getMonsterById(i), killer, true);
                        }
                     } else if (controller != null) {
                        mob.aggroSwitchController(controller, aggro);
                     }

                     if (eim != null) {
                        eim.reviveMonster(mob);
                     }
                  });
               }
            }, getAnimationTime("die1"));
         }
      } else {  // is this even necessary?
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, "[CRITICAL LOSS] toSpawn is null for " + this.getName());
      }

      MapleCharacter looter = map.getCharacterById(getCharacterIdWithHighestDamage());
      return looter != null ? looter : killer;
   }

   public void dropFromFriendlyMonster(long delay) {
      final MapleMonster m = this;
      monsterItemDrop = TimerManager.getInstance().register(() -> {
         if (!m.isAlive()) {
            if (monsterItemDrop != null) {
               monsterItemDrop.cancel(false);
            }

            return;
         }

         MapleMap map = m.getMap();
         List<MapleCharacter> chrList = map.getAllPlayers();
         if (!chrList.isEmpty()) {
            MapleCharacter chr = chrList.get(0);

            EventInstanceManager eim = map.getEventInstance();
            if (eim != null) {
               eim.friendlyItemDrop(m);
            }

            map.dropFromFriendlyMonster(chr, m);
         }
      }, delay, delay);
   }

   private void dispatchRaiseQuestMobCount() {
      Set<Integer> attackerCharacterIds = takenDamage.keySet();
      if (!attackerCharacterIds.isEmpty()) {
         Map<Integer, MapleCharacter> mapChars = map.getMapPlayers();
         if (!mapChars.isEmpty()) {
            int mobId = id();

            for (Integer characterId : attackerCharacterIds) {
               MapleCharacter chr = mapChars.get(characterId);

               if (chr != null && chr.isLoggedInWorld()) {
                  QuestProcessor.getInstance().raiseQuestMobCount(chr, mobId);
               }
            }
         }
      }
   }

   public void dispatchMonsterKilled(boolean hasKiller) {
      processMonsterKilled(hasKiller);

      EventInstanceManager eim = getMap().getEventInstance();
      if (eim != null) {
         if (!this.getStats().isFriendly()) {
            eim.monsterKilled(this, hasKiller);
         } else {
            eim.friendlyKilled(this, hasKiller);
         }
      }
   }

   private synchronized void processMonsterKilled(boolean hasKiller) {
      if (!hasKiller) {    // players won't gain EXP from a mob that has no killer, but a quest count they should
         dispatchRaiseQuestMobCount();
      }

      this.aggroClearDamages();
      this.dispatchClearSummons();

      MonsterListener[] listenersList;
      statusLock.lock();
      try {
         listenersList = listeners.toArray(new MonsterListener[0]);
      } finally {
         statusLock.unlock();
      }

      for (MonsterListener listener : listenersList) {
         listener.monsterKilled(getAnimationTime("die1"));
      }

      statusLock.lock();
      try {
         monsterStatuses.clear();
         alreadyBuffed.clear();
         listeners.clear();
      } finally {
         statusLock.unlock();
      }
   }

   private void dispatchMonsterDamaged(MapleCharacter from, int trueDmg) {
      MonsterListener[] listenersList;
      statusLock.lock();
      try {
         listenersList = listeners.toArray(new MonsterListener[0]);
      } finally {
         statusLock.unlock();
      }

      for (MonsterListener listener : listenersList) {
         listener.monsterDamaged(from, trueDmg);
      }
   }

   private void dispatchMonsterHealed(int trueHeal) {
      MonsterListener[] listenersList;
      statusLock.lock();
      try {
         listenersList = listeners.toArray(new MonsterListener[0]);
      } finally {
         statusLock.unlock();
      }

      for (MonsterListener listener : listenersList) {
         listener.monsterHealed(trueHeal);
      }
   }

   private void giveFamilyRep(MapleFamilyEntry entry) {
      if (entry != null) {
         int repGain = isBoss() ? YamlConfig.config.server.FAMILY_REP_PER_BOSS_KILL : YamlConfig.config.server.FAMILY_REP_PER_KILL;
         if (getMaxHp() <= 1) {
            repGain = 0; //don't count trash mobs
         }
         entry.giveReputationToSenior(repGain, true);
      }
   }

   public int getCharacterIdWithHighestDamage() {
      int curId = 0;
      long curDmg = 0;

      for (Entry<Integer, AtomicLong> damage : takenDamage.entrySet()) {
         curId = damage.getValue().get() >= curDmg ? damage.getKey() : curId;
         curDmg = damage.getKey() == curId ? damage.getValue().get() : curDmg;
      }

      return curId;
   }

   public boolean isAlive() {
      return this.hp.get() > 0;
   }

   public void addListener(MonsterListener listener) {
      statusLock.lock();
      try {
         listeners.add(listener);
      } finally {
         statusLock.unlock();
      }
   }

   public MapleCharacter getController() {
      return controller.get();
   }

   private void setController(MapleCharacter controller) {
      this.controller = new WeakReference<>(controller);
   }

   public boolean isControllerHasAggro() {
      return !fake && controllerHasAggro;
   }

   private void setControllerHasAggro(boolean controllerHasAggro) {
      if (!fake) {
         this.controllerHasAggro = controllerHasAggro;
      }
   }

   public boolean isControllerKnowsAboutAggro() {
      return !fake && controllerKnowsAboutAggro;
   }

   private void setControllerKnowsAboutAggro(boolean controllerKnowsAboutAggro) {
      if (!fake) {
         this.controllerKnowsAboutAggro = controllerKnowsAboutAggro;
      }
   }

   private void setControllerHasPuppet(boolean controllerHasPuppet) {
      this.controllerHasPuppet = controllerHasPuppet;
   }

   public byte[] makeBossHPBarPacket() {
      return PacketCreator.create(new ShowBossHP(id(), getHp(), getMaxHp(), getTagColor(), getTagBgColor()));
   }

   public boolean hasBossHPBar() {
      return isBoss() && getTagColor() > 0;
   }

   public void broadcastMonsterStatus() {
      Collection<MonsterStatusEffect> mseList = this.getMonsterStatuses().values();
      for (MapleCharacter chr : map.getAllPlayers()) {
         announceMonsterStatusInternal(chr.getClient(), mseList);
      }
   }

   public void announceMonsterStatus(MapleClient client) {
      announceMonsterStatusInternal(client, this.getMonsterStatuses().values());
   }

   public void announceMonsterStatusInternal(MapleClient client, Collection<MonsterStatusEffect> mseList) {
      for (MonsterStatusEffect mse : mseList) {
         PacketCreator.announce(client, new ApplyMonsterStatus(id(), mse, null));
      }
   }

   @Override
   public MapleMapObjectType type() {
      return MapleMapObjectType.MONSTER;
   }

   public boolean isMobile() {
      return stats.isMobile();
   }

   @Override
   public boolean isFacingLeft() {
      int fixedStance = stats.fixedStance();
      if (fixedStance != 0) {
         return Math.abs(fixedStance) % 2 == 1;
      }

      return super.isFacingLeft();
   }

   public ElementalEffectiveness getElementalEffectiveness(Element e) {
      statusLock.lock();
      try {
         if (monsterStatuses.get(MonsterStatus.DOOM) != null) {
            return ElementalEffectiveness.NORMAL; // like blue snails
         }
      } finally {
         statusLock.unlock();
      }

      return getMonsterEffectiveness(e);
   }

   private ElementalEffectiveness getMonsterEffectiveness(Element e) {
      monsterLock.lock();
      try {
         return stats.getEffectiveness(e);
      } finally {
         monsterLock.unlock();
      }
   }

   private MapleCharacter getActiveController() {
      MapleCharacter chr = getController();

      if (chr != null && chr.isLoggedInWorld() && chr.getMap() == this.getMap()) {
         return chr;
      } else {
         return null;
      }
   }

   private void broadcastMonsterStatusMessage(PacketInput packet) {
      MasterBroadcaster.getInstance().sendToAllInMapRange(map, packet, this.position());

      MapleCharacter chrController = getActiveController();
      if (chrController != null && !chrController.isMapObjectVisible(MapleMonster.this)) {
         PacketCreator.announce(chrController, packet);
      }
   }

   private int broadcastStatusEffect(final MonsterStatusEffect status) {
      int animationTime = status.getSkill().getAnimationTime();
      broadcastMonsterStatusMessage(new ApplyMonsterStatus(this.objectId(), status, null));

      return animationTime;
   }

   public boolean applyStatus(MapleCharacter from, final MonsterStatusEffect status, boolean poison, long duration) {
      return applyStatus(from, status, poison, duration, false);
   }

   public boolean applyStatus(MapleCharacter from, final MonsterStatusEffect status, boolean poison, long duration, boolean venom) {
      switch (getMonsterEffectiveness(status.getSkill().getElement())) {
         case IMMUNE:
         case STRONG:
         case NEUTRAL:
            return false;
         case NORMAL:
         case WEAK:
            break;
         default: {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION,
                  "Unknown elemental effectiveness: " + getMonsterEffectiveness(status.getSkill().getElement()));
            return false;
         }
      }

      if (status.getSkill().getId() == FirePoisonMagician.ELEMENT_COMPOSITION) { // fp compo
         ElementalEffectiveness effectiveness = getMonsterEffectiveness(Element.POISON);
         if (effectiveness == ElementalEffectiveness.IMMUNE || effectiveness == ElementalEffectiveness.STRONG) {
            return false;
         }
      } else if (status.getSkill().getId() == IceLighteningMagician.ELEMENT_COMPOSITION) { // il compo
         ElementalEffectiveness effectiveness = getMonsterEffectiveness(Element.ICE);
         if (effectiveness == ElementalEffectiveness.IMMUNE || effectiveness == ElementalEffectiveness.STRONG) {
            return false;
         }
      } else if (status.getSkill().getId() == NightLord.VENOMOUS_STAR || status.getSkill().getId() == Shadower.VENOMOUS_STAB
            || status.getSkill().getId() == NightWalker.VENOM) {// venom
         if (getMonsterEffectiveness(Element.POISON) == ElementalEffectiveness.WEAK) {
            return false;
         }
      }
      if (poison && hp.get() <= 1) {
         return false;
      }

      final Map<MonsterStatus, Integer> statuses = status.getStatuses();
      if (stats.isBoss()) {
         if (!(statuses.containsKey(MonsterStatus.SPEED)
               && statuses.containsKey(MonsterStatus.NINJA_AMBUSH)
               && statuses.containsKey(MonsterStatus.WEAPON_ATTACK))) {
            return false;
         }
      }

      final Channel ch = map.getChannelServer();
      final int mapId = map.getId();
      if (statuses.size() > 0) {
         statusLock.lock();
         try {
            for (MonsterStatus stat : statuses.keySet()) {
               final MonsterStatusEffect oldEffect = monsterStatuses.get(stat);
               if (oldEffect != null) {
                  oldEffect.removeActiveStatus(stat);
                  if (oldEffect.getStatuses().isEmpty()) {
                     MobStatusService service =
                           (MobStatusService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_STATUS);
                     service.interruptMobStatus(mapId, oldEffect);
                  }
               }
            }
         } finally {
            statusLock.unlock();
         }
      }

      final Runnable cancelTask = () -> {
         if (isAlive()) {
            broadcastMonsterStatusMessage(new CancelMonsterStatus(MapleMonster.this.objectId(), status.getStatuses()));
         }

         statusLock.lock();
         try {
            for (MonsterStatus stat : status.getStatuses().keySet()) {
               monsterStatuses.remove(stat);
            }
         } finally {
            statusLock.unlock();
         }

         setVenomMulti(0);
      };

      Runnable overtimeAction = null;
      int overtimeDelay = -1;

      int animationTime;
      if (poison) {
         int poisonLevel = from.getSkillLevel(status.getSkill());
         int poisonDamage = Math.min(Short.MAX_VALUE, (int) (getMaxHp() / (70.0 - poisonLevel) + 0.999));
         status.setValue(MonsterStatus.POISON, poisonDamage);
         animationTime = broadcastStatusEffect(status);

         overtimeAction = new DamageTask(poisonDamage, from, status, 0);
         overtimeDelay = 1000;
      } else if (venom) {
         if (from.getJob() == MapleJob.NIGHT_LORD || from.getJob() == MapleJob.SHADOWER || from.getJob()
               .isA(MapleJob.NIGHT_WALKER_3)) {
            int poisonLevel, magicAttack, jobId = from.getJob().getId();
            int skillId = (jobId == 412 ? NightLord.VENOMOUS_STAR : (jobId == 422 ? Shadower.VENOMOUS_STAB : NightWalker.VENOM));

            Optional<Skill> skill = SkillFactory.getSkill(skillId);
            if (skill.isEmpty()) {
               return false;
            }
            poisonLevel = from.getSkillLevel(skill.get());
            if (poisonLevel <= 0) {
               return false;
            }
            magicAttack = skill.get().getEffect(poisonLevel).getMagicAttack();
            int luk = from.getLuk();
            int maxDmg = (int) Math.ceil(Math.min(Short.MAX_VALUE, 0.2 * luk * magicAttack));
            int minDmg = (int) Math.ceil(Math.min(Short.MAX_VALUE, 0.1 * luk * magicAttack));
            int gap = maxDmg - minDmg;
            if (gap == 0) {
               gap = 1;
            }
            int poisonDamage = 0;
            for (int i = 0; i < getVenomMulti(); i++) {
               poisonDamage += (Randomizer.nextInt(gap) + minDmg);
            }
            poisonDamage = Math.min(Short.MAX_VALUE, poisonDamage);
            status.setValue(MonsterStatus.VENOMOUS_WEAPON, poisonDamage);
            status.setValue(MonsterStatus.POISON, poisonDamage);
            animationTime = broadcastStatusEffect(status);

            overtimeAction = new DamageTask(poisonDamage, from, status, 0);
            overtimeDelay = 1000;
         } else {
            return false;
         }
            /*
        } else if (status.getSkill().getId() == Hermit.SHADOW_WEB || status.getSkill().getId() == NightWalker.SHADOW_WEB) { //Shadow Web
            int webDamage = (int) (getMaxHp() / 50.0 + 0.999);
            status.setValue(MonsterStatus.SHADOW_WEB, Integer.valueOf(webDamage));
            animationTime = broadcastStatusEffect(status);

            overtimeAction = new DamageTask(webDamage, from, status, 1);
            overtimeDelay = 3500;
            */
      } else if (status.getSkill().getId() == 4121004 || status.getSkill().getId() == 4221004) { // Ninja Ambush
         Optional<Skill> skill = SkillFactory.getSkill(status.getSkill().getId());
         if (skill.isPresent()) {
            final byte level = from.getSkillLevel(skill.get());
            final int damage = (int) ((from.getStr() + from.getLuk()) * ((3.7 * skill.get().getEffect(level).getDamage()) / 100));

            status.setValue(MonsterStatus.NINJA_AMBUSH, damage);

            overtimeAction = new DamageTask(damage, from, status, 2);
            overtimeDelay = 1000;
         }
         animationTime = broadcastStatusEffect(status);
      } else {
         animationTime = broadcastStatusEffect(status);
      }

      statusLock.lock();
      try {
         for (MonsterStatus stat : status.getStatuses().keySet()) {
            monsterStatuses.put(stat, status);
            alreadyBuffed.add(stat);
         }
      } finally {
         statusLock.unlock();
      }

      MobStatusService service = (MobStatusService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_STATUS);
      service.registerMobStatus(mapId, status, cancelTask, duration + animationTime - 100, overtimeAction, overtimeDelay);
      return true;
   }

   public final void dispelSkill(final MobSkill skillId) {
      List<MonsterStatus> toCancel = new ArrayList<>();
      for (Entry<MonsterStatus, MonsterStatusEffect> effects : monsterStatuses.entrySet()) {
         MonsterStatusEffect mse = effects.getValue();
         if (mse.getMobSkill() != null && mse.getMobSkill().skillId() == skillId.skillId()) { //not checking for level.
            toCancel.add(effects.getKey());
         }
      }
      for (MonsterStatus stat : toCancel) {
         removeMobStatus(stat);
      }
   }

   public void applyMonsterBuff(final Map<MonsterStatus, Integer> stats, final int x, int skillId, long duration, MobSkill skill,
                                final List<Integer> reflection) {
      final Runnable cancelTask = () -> {
         if (isAlive()) {
            broadcastMonsterStatusMessage(new CancelMonsterStatus(MapleMonster.this.objectId(), stats));

            statusLock.lock();
            try {
               for (final MonsterStatus stat : stats.keySet()) {
                  monsterStatuses.remove(stat);
               }
            } finally {
               statusLock.unlock();
            }
         }
      };
      final MonsterStatusEffect effect = new MonsterStatusEffect(stats, null, skill, true);
      broadcastMonsterStatusMessage(new ApplyMonsterStatus(this.objectId(), effect, reflection));

      statusLock.lock();
      try {
         for (MonsterStatus stat : stats.keySet()) {
            monsterStatuses.put(stat, effect);
            alreadyBuffed.add(stat);
         }
      } finally {
         statusLock.unlock();
      }

      MobStatusService service = (MobStatusService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_STATUS);
      service.registerMobStatus(map.getId(), effect, cancelTask, duration);
   }

   public void refreshMobPosition() {
      resetMobPosition(this.position());
   }

   public void resetMobPosition(Point newPoint) {
      aggroRemoveController();
      setPosition(newPoint);
      MasterBroadcaster.getInstance().sendToAllInMap(map, new MoveMonster(this.objectId(), false, -1, 0, 0, 0, this.position(),
            MapleMapObjectProcessor.getInstance().getIdleMovementBytes(this)));
      map.moveMonster(this, this.position());

      aggroUpdateController();
   }

   private void removeMobStatus(MonsterStatus monsterStatus) {
      MonsterStatusEffect oldEffect;
      statusLock.lock();
      try {
         oldEffect = monsterStatuses.remove(monsterStatus);
      } finally {
         statusLock.unlock();
      }

      if (oldEffect != null) {
         broadcastMonsterStatusMessage(new CancelMonsterStatus(this.objectId(), oldEffect.getStatuses()));
      }
   }

   public void removeMobStatus(int skillId) {
      MonsterStatus[] statIncreases =
            {MonsterStatus.WEAPON_ATTACK_UP, MonsterStatus.WEAPON_DEFENSE_UP, MonsterStatus.MAGIC_ATTACK_UP, MonsterStatus.MAGIC_DEFENSE_UP};
      statusLock.lock();
      try {
         if (skillId == Hermit.SHADOW_MESO) {
            removeMobStatus(statIncreases[1]);
            removeMobStatus(statIncreases[3]);
         } else if (skillId == Priest.DISPEL) {
            for (MonsterStatus ms : statIncreases) {
               removeMobStatus(ms);
            }
         } else {    // is a crash skill
            int i = (skillId == Crusader.ARMOR_CRASH ? 1 : (skillId == WhiteKnight.MAGIC_CRASH ? 2 : 0));
            removeMobStatus(statIncreases[i]);

            if (YamlConfig.config.server.USE_ANTI_IMMUNITY_CRASH) {
               if (skillId == Crusader.ARMOR_CRASH) {
                  if (!isBuffed(MonsterStatus.WEAPON_REFLECT)) {
                     removeMobStatus(MonsterStatus.WEAPON_IMMUNITY);
                  }
                  if (!isBuffed(MonsterStatus.MAGIC_REFLECT)) {
                     removeMobStatus(MonsterStatus.MAGIC_IMMUNITY);
                  }
               } else if (skillId == WhiteKnight.MAGIC_CRASH) {
                  if (!isBuffed(MonsterStatus.MAGIC_REFLECT)) {
                     removeMobStatus(MonsterStatus.MAGIC_IMMUNITY);
                  }
               } else {
                  if (!isBuffed(MonsterStatus.WEAPON_REFLECT)) {
                     removeMobStatus(MonsterStatus.WEAPON_IMMUNITY);
                  }
               }
            }
         }
      } finally {
         statusLock.unlock();
      }
   }

   public boolean isBuffed(MonsterStatus status) {
      statusLock.lock();
      try {
         return monsterStatuses.containsKey(status);
      } finally {
         statusLock.unlock();
      }
   }

   public boolean isFake() {
      monsterLock.lock();
      try {
         return fake;
      } finally {
         monsterLock.unlock();
      }
   }

   public void setFake(boolean fake) {
      monsterLock.lock();
      try {
         this.fake = fake;
      } finally {
         monsterLock.unlock();
      }
   }

   public MapleMap getMap() {
      return map;
   }

   public void setMap(MapleMap map) {
      this.map = map;
   }

   public MapleMonsterAggroCoordinator getMapAggroCoordinator() {
      return map.getAggroCoordinator();
   }

   public List<Pair<Integer, Integer>> getSkills() {
      return stats.skills();
   }

   public boolean hasSkill(int skillId, int level) {
      return stats.hasSkill(skillId, level);
   }

   public int getSkillPos(int skillId, int level) {
      int pos = 0;
      for (Pair<Integer, Integer> ms : this.getSkills()) {
         if (ms.getLeft() == skillId && ms.getRight() == level) {
            return pos;
         }

         pos++;
      }

      return -1;
   }

   public boolean canUseSkill(MobSkill toUse, boolean apply) {
      if (toUse == null) {
         return false;
      }

      int useSkillId = toUse.skillId();
      if (useSkillId >= 143 && useSkillId <= 145) {
         if (this.isBuffed(MonsterStatus.WEAPON_REFLECT) || this.isBuffed(MonsterStatus.MAGIC_REFLECT)) {
            return false;
         }
      }

      monsterLock.lock();
      try {
         for (Pair<Integer, Integer> skill : usedSkills) {
            if (skill.getLeft() == useSkillId && skill.getRight() == toUse.skillId()) {
               return false;
            }
         }

         int mpCon = toUse.mpCon();
         if (mp < mpCon) {
            return false;
         }

            /*
            if (!this.applyAnimationIfRoaming(-1, toUse)) {
                return false;
            }
            */

         if (apply) {
            this.usedSkill(toUse);
         }
      } finally {
         monsterLock.unlock();
      }

      return true;
   }

   private void usedSkill(MobSkill skill) {
      final int skillId = skill.skillId(), level = skill.skillId();
      long coolTime = skill.coolTime();

      monsterLock.lock();
      try {
         mp -= skill.mpCon();

         Pair<Integer, Integer> skillKey = new Pair<>(skillId, level);
         this.usedSkills.add(skillKey);

         Integer useCount = this.skillsUsed.remove(skillKey);
         if (useCount != null) {
            this.skillsUsed.put(skillKey, useCount + 1);
         } else {
            this.skillsUsed.put(skillKey, 1);
         }
      } finally {
         monsterLock.unlock();
      }

      final MapleMonster mons = this;
      MapleMap mmap = mons.getMap();
      Runnable r = () -> mons.clearSkill(skillId, level);

      MobClearSkillService service =
            (MobClearSkillService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_CLEAR_SKILL);
      service.registerMobClearSkillAction(mmap.getId(), r, coolTime);
   }

   private void clearSkill(int skillId, int level) {
      monsterLock.lock();
      try {
         int index = -1;
         for (Pair<Integer, Integer> skill : usedSkills) {
            if (skill.getLeft() == skillId && skill.getRight() == level) {
               index = usedSkills.indexOf(skill);
               break;
            }
         }
         if (index != -1) {
            usedSkills.remove(index);
         }
      } finally {
         monsterLock.unlock();
      }
   }

   public int canUseAttack(int attackPos, boolean isSkill) {
      monsterLock.lock();
      try {
            /*
            if (usedAttacks.contains(attackPos)) {
                return -1;
            }
            */

         Pair<Integer, Integer> attackInfo = MapleMonsterInformationProvider.getInstance().getMobAttackInfo(this.id(), attackPos);
         if (attackInfo == null) {
            return -1;
         }

         int mpCon = attackInfo.getLeft();
         if (mp < mpCon) {
            return -1;
         }

            /*
            if (!this.applyAnimationIfRoaming(attackPos, null)) {
                return -1;
            }
            */

         usedAttack(attackPos, mpCon, attackInfo.getRight());
         return 1;
      } finally {
         monsterLock.unlock();
      }
   }

   private void usedAttack(final int attackPos, int mpCon, int coolTime) {
      monsterLock.lock();
      try {
         mp -= mpCon;
         usedAttacks.add(attackPos);

         final MapleMonster mons = this;
         MapleMap mmap = mons.getMap();
         Runnable r = () -> mons.clearAttack(attackPos);

         MobClearSkillService service =
               (MobClearSkillService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_CLEAR_SKILL);
         service.registerMobClearSkillAction(mmap.getId(), r, coolTime);
      } finally {
         monsterLock.unlock();
      }
   }

   private void clearAttack(int attackPos) {
      monsterLock.lock();
      try {
         usedAttacks.remove(attackPos);
      } finally {
         monsterLock.unlock();
      }
   }

   public int getNoSkills() {
      return this.stats.getNoSkills();
   }

   public boolean isFirstAttack() {
      return this.stats.firstAttack();
   }

   public int getBuffToGive() {
      return this.stats.buffToGive();
   }

   public String getName() {
      return stats.name();
   }

   public void addStolen(int itemId) {
      stolenItems.add(itemId);
   }

   public List<Integer> getStolen() {
      return stolenItems;
   }

   public void setTempEffectiveness(Element e, ElementalEffectiveness ee, long milli) {
      monsterLock.lock();
      try {
         final Element fE = e;
         final ElementalEffectiveness fEE = stats.getEffectiveness(e);
         if (!fEE.equals(ElementalEffectiveness.WEAK)) {
            stats = stats.setEffectiveness(e, ee);

            MapleMap mmap = this.getMap();
            Runnable r = () -> {
               monsterLock.lock();
               try {
                  stats = stats.removeEffectiveness(fE);
                  stats = stats.setEffectiveness(fE, fEE);
               } finally {
                  monsterLock.unlock();
               }
            };

            MobClearSkillService service =
                  (MobClearSkillService) mmap.getChannelServer().getServiceAccess(ChannelServices.MOB_CLEAR_SKILL);
            service.registerMobClearSkillAction(mmap.getId(), r, milli);
         }
      } finally {
         monsterLock.unlock();
      }
   }

   public Collection<MonsterStatus> alreadyBuffedStats() {
      statusLock.lock();
      try {
         return Collections.unmodifiableCollection(alreadyBuffed);
      } finally {
         statusLock.unlock();
      }
   }

   public BanishInfo getBanish() {
      return stats.banish();
   }

   public int getDropPeriodTime() {
      return stats.dropPeriod();
   }

   public int getPADamage() {
      return stats.paDamage();
   }

   public Map<MonsterStatus, MonsterStatusEffect> getMonsterStatuses() {
      statusLock.lock();
      try {
         return new HashMap<>(monsterStatuses);
      } finally {
         statusLock.unlock();
      }
   }

   public MonsterStatusEffect getStatus(MonsterStatus ms) {
      statusLock.lock();
      try {
         return monsterStatuses.get(ms);
      } finally {
         statusLock.unlock();
      }
   }

   // ---- one can always have fun trying these pieces of codes below in-game rofl ----

   public final ChangeableStats getChangedStats() {
      return changeableStats;
   }

   public final int getMobMaxHp() {
      if (changeableStats != null) {
         return changeableStats.hp();
      }
      return stats.hp();
   }

   public final void setOverrideStats(final OverrideMonsterStats overrideMonsterStats) {
      this.changeableStats = new ChangeableStats(stats, overrideMonsterStats);
      this.hp.set(overrideMonsterStats.hp());
      this.mp = overrideMonsterStats.mp();
   }

   public final void changeLevel(final int newLevel) {
      changeLevel(newLevel, true);
   }

   public final void changeLevel(final int newLevel, boolean pqMob) {
      if (!stats.changeable()) {
         return;
      }
      this.changeableStats = new ChangeableStats(stats, newLevel, pqMob);
      this.hp.set(changeableStats.hp());
      this.mp = changeableStats.mp();
   }

   private float getDifficultyRate(final int difficulty) {
      return switch (difficulty) {
         case 6 -> (7.7f);
         case 5 -> (5.6f);
         case 4 -> (3.2f);
         case 3 -> (2.1f);
         case 2 -> (1.4f);
         default -> (1.0f);
      };
   }

   private void changeLevelByDifficulty(final int difficulty, boolean pqMob) {
      changeLevel((int) (this.getLevel() * getDifficultyRate(difficulty)), pqMob);
   }

   public final void changeDifficulty(final int difficulty, boolean pqMob) {
      changeLevelByDifficulty(difficulty, pqMob);
   }

   // ---------------------------------------------------------------------------------

   private boolean isPuppetInVicinity(MapleSummon summon) {
      return summon.position().distanceSq(this.position()) < 177777;
   }

   public boolean isCharacterPuppetInVicinity(MapleCharacter chr) {
      MapleStatEffect mse = chr.getBuffEffect(MapleBuffStat.PUPPET);
      if (mse != null) {
         MapleSummon summon = chr.getSummonByKey(mse.getSourceId());

         // check whether mob is currently under a puppet's field of action or not
         if (summon != null) {
            return isPuppetInVicinity(summon);
         } else {
            map.getAggroCoordinator().removePuppetAggro(chr.getId());
         }
      }

      return false;
   }

   public boolean isLeadingPuppetInVicinity() {
      MapleCharacter chrController = this.getActiveController();

      if (chrController != null) {
         return this.isCharacterPuppetInVicinity(chrController);
      }

      return false;
   }

   private MapleCharacter getNextControllerCandidate() {
      int minControlled = Integer.MAX_VALUE;
      MapleCharacter newController = null;

      int minControlledDead = Integer.MAX_VALUE;
      MapleCharacter newControllerDead = null;

      MapleCharacter newControllerWithPuppet = null;

      for (MapleCharacter chr : getMap().getAllPlayers()) {
         if (!chr.isHidden()) {
            int ctrlMonsSize = chr.getNumControlledMonsters();

            if (isCharacterPuppetInVicinity(chr)) {
               newControllerWithPuppet = chr;
               break;
            } else if (chr.isAlive()) {
               if (ctrlMonsSize < minControlled) {
                  minControlled = ctrlMonsSize;
                  newController = chr;
               }
            } else {
               if (ctrlMonsSize < minControlledDead) {
                  minControlledDead = ctrlMonsSize;
                  newControllerDead = chr;
               }
            }
         }
      }

      if (newControllerWithPuppet != null) {
         return newControllerWithPuppet;
      } else if (newController != null) {
         return newController;
      } else {
         return newControllerDead;
      }
   }

   /**
    * Removes control-ability status from the current controller of this mob.
    */
   private Pair<MapleCharacter, Boolean> aggroRemoveController() {
      MapleCharacter chrController;
      boolean hadAggro;

      aggroUpdateLock.lock();
      try {
         chrController = getActiveController();
         hadAggro = isControllerHasAggro();

         this.setController(null);
         this.setControllerHasAggro(false);
         this.setControllerKnowsAboutAggro(false);
      } finally {
         aggroUpdateLock.unlock();
      }

      if (chrController != null) { // this can/should only happen when a hidden gm attacks the monster
         PacketCreator.announce(chrController, new StopMonsterControl(this.objectId()));
         chrController.stopControllingMonster(this);
      }

      return new Pair<>(chrController, hadAggro);
   }

   /**
    * Pass over the mob control-ability and updates aggro status on the new
    * player controller.
    */
   public void aggroSwitchController(MapleCharacter newController, boolean immediateAggro) {
      if (aggroUpdateLock.tryLock()) {
         try {
            MapleCharacter prevController = getController();
            if (prevController == newController) {
               return;
            }

            aggroRemoveController();
            if (!(newController != null && newController.isLoggedInWorld() && newController.getMap() == this.getMap())) {
               return;
            }

            this.setController(newController);
            this.setControllerHasAggro(immediateAggro);
            this.setControllerKnowsAboutAggro(false);
            this.setControllerHasPuppet(false);
         } finally {
            aggroUpdateLock.unlock();
         }

         this.aggroUpdatePuppetVisibility();
         aggroMonsterControl(newController.getClient(), this, immediateAggro);
         newController.controlMonster(this);
      }
   }

   public void aggroAddPuppet(MapleCharacter player) {
      MapleMonsterAggroCoordinator aggroCoordinator = map.getAggroCoordinator();
      aggroCoordinator.addPuppetAggro(player);

      aggroUpdatePuppetController(player);

      if (this.isControllerHasAggro()) {
         this.aggroUpdatePuppetVisibility();
      }
   }

   public void aggroRemovePuppet(MapleCharacter player) {
      MapleMonsterAggroCoordinator aggroCoordinator = map.getAggroCoordinator();
      aggroCoordinator.removePuppetAggro(player.getId());

      aggroUpdatePuppetController(null);

      if (this.isControllerHasAggro()) {
         this.aggroUpdatePuppetVisibility();
      }
   }

   /**
    * Auto-magically finds a new controller for the given monster from the chars
    * on the map it is from...
    */
   public void aggroUpdateController() {
      MapleCharacter chrController = this.getActiveController();
      if (chrController != null && chrController.isAlive()) {
         return;
      }

      MapleCharacter newController = getNextControllerCandidate();
      if (newController == null) {    // was a new controller found? (if not no one is on the map)
         return;
      }

      this.aggroSwitchController(newController, false);
   }

   /**
    * Finds a new controller for the given monster from the chars with deployed
    * puppet nearby on the map it is from...
    */
   private void aggroUpdatePuppetController(MapleCharacter newController) {
      MapleCharacter chrController = this.getActiveController();
      boolean updateController = false;

      if (chrController != null && chrController.isAlive()) {
         if (isCharacterPuppetInVicinity(chrController)) {
            return;
         }
      } else {
         updateController = true;
      }

      if (newController == null || !isCharacterPuppetInVicinity(newController)) {
         MapleMonsterAggroCoordinator aggroCoordinator = map.getAggroCoordinator();

         List<Integer> puppetOwners = aggroCoordinator.getPuppetAggroList();
         List<Integer> toRemovePuppets = new LinkedList<>();

         for (Integer cid : puppetOwners) {
            MapleCharacter chr = map.getCharacterById(cid);

            if (chr != null) {
               if (isCharacterPuppetInVicinity(chr)) {
                  newController = chr;
                  break;
               }
            } else {
               toRemovePuppets.add(cid);
            }
         }

         for (Integer cid : toRemovePuppets) {
            aggroCoordinator.removePuppetAggro(cid);
         }

         if (newController == null) {    // was a new controller found? (if not there's no puppet nearby)
            if (updateController) {
               aggroUpdateController();
            }

            return;
         }
      } else if (chrController == newController) {
         this.aggroUpdatePuppetVisibility();
      }

      this.aggroSwitchController(newController, this.isControllerHasAggro());
   }

   /**
    * Ensures control-ability removal of the current player controller, and
    * fetches for any player on the map to start controlling in place.
    */
   public void aggroRedirectController() {
      this.aggroRemoveController();   // don't care if new controller not found, at least remove current controller
      this.aggroUpdateController();
   }

   /**
    * Returns the current aggro status on the specified player, or null if the
    * specified player is currently not this mob's controller.
    */
   public Boolean aggroMoveLifeUpdate(MapleCharacter player) {
      MapleCharacter chrController = getController();
      if (chrController != null && player.getId() == chrController.getId()) {
         boolean aggro = this.isControllerHasAggro();
         if (aggro) {
            this.setControllerKnowsAboutAggro(true);
         }

         return aggro;
      } else {
         return null;
      }
   }

   /**
    * Refreshes auto aggro for the player passed as parameter, does nothing if
    * there is already an active controller for this mob.
    */
   public void aggroAutoAggroUpdate(MapleCharacter player) {
      MapleCharacter chrController = this.getActiveController();

      if (chrController == null) {
         this.aggroSwitchController(player, true);
      } else if (chrController.getId() == player.getId()) {
         this.setControllerHasAggro(true);
      }
   }

   /**
    * Applied damage input for this mob, enough damage taken implies an aggro
    * target update for the attacker shortly.
    */
   public void aggroMonsterDamage(MapleCharacter attacker, int damage) {
      MapleMonsterAggroCoordinator mapAggroCoordinator = this.getMapAggroCoordinator();
      mapAggroCoordinator.addAggroDamage(this, attacker.getId(), damage);

      MapleCharacter chrController = this.getController();
      if (chrController != attacker) {
         if (this.getMapAggroCoordinator().isLeadingCharacterAggro(this, attacker)) {
            this.aggroSwitchController(attacker, true);
         } else {
            this.setControllerHasAggro(true);
            this.aggroUpdatePuppetVisibility();
         }
            
            /*
            For some reason, some mobs loses aggro on controllers if other players also attacks them.
            Maybe it was intended by Nexon to interchange controllers at every attack...
            
            else if (chrController != null) {
                chrController.announce(MaplePacketCreator.stopControllingMonster(this.getObjectId()));
                aggroMonsterControl(chrController.getClient(), this, true);
            }
            */
      } else {
         this.setControllerHasAggro(true);
         this.aggroUpdatePuppetVisibility();
      }
   }

   private void aggroRefreshPuppetVisibility(MapleCharacter chrController, MapleSummon puppet) {
      // lame patch for client to redirect all aggro to the puppet

      List<MapleMonster> puppetControlled = new LinkedList<>();
      for (MapleMonster mob : chrController.getControlledMonsters()) {
         if (mob.isPuppetInVicinity(puppet)) {
            puppetControlled.add(mob);
         }
      }

      for (MapleMonster mob : puppetControlled) {
         PacketCreator.announce(chrController, new StopMonsterControl(mob.objectId()));
      }
      PacketCreator.announce(chrController, new RemoveSummon(puppet.getOwner().getId(), puppet.objectId(), false));

      MapleClient c = chrController.getClient();
      for (MapleMonster mob : puppetControlled) {
         aggroMonsterControl(c, mob, mob.isControllerKnowsAboutAggro());
      }
      PacketCreator.announce(chrController, new SpawnSummon(puppet.getOwner().getId(), puppet.objectId(),
            puppet.getSkill(), puppet.getSkillLevel(), puppet.position(), puppet.stance(),
            puppet.getMovementType().getValue(), puppet.isPuppet(), false));
   }

   public void aggroUpdatePuppetVisibility() {
      if (!availablePuppetUpdate) {
         return;
      }

      availablePuppetUpdate = false;
      Runnable r = () -> {
         try {
            MapleCharacter chrController = MapleMonster.this.getActiveController();
            if (chrController == null) {
               return;
            }

            MapleStatEffect puppetEffect = chrController.getBuffEffect(MapleBuffStat.PUPPET);
            if (puppetEffect != null) {
               MapleSummon puppet = chrController.getSummonByKey(puppetEffect.getSourceId());

               if (puppet != null && isPuppetInVicinity(puppet)) {
                  controllerHasPuppet = true;
                  aggroRefreshPuppetVisibility(chrController, puppet);
                  return;
               }
            }

            if (controllerHasPuppet) {
               controllerHasPuppet = false;
               PacketCreator.announce(chrController, new StopMonsterControl(MapleMonster.this.objectId()));
               aggroMonsterControl(chrController.getClient(), MapleMonster.this, MapleMonster.this.isControllerHasAggro());
            }
         } finally {
            availablePuppetUpdate = true;
         }
      };

      // had to schedule this since mob wouldn't stick to puppet aggro who knows why
      OverallService service = (OverallService) this.getMap().getChannelServer().getServiceAccess(ChannelServices.OVERALL);
      service.registerOverallAction(this.getMap().getId(), r, YamlConfig.config.server.UPDATE_INTERVAL);
   }

   /**
    * Clears all applied damage input for this mob, doesn't refresh target
    * aggro.
    */
   public void aggroClearDamages() {
      this.getMapAggroCoordinator().removeAggroEntries(this);
   }

   /**
    * Clears this mob aggro on the current controller.
    */
   public void aggroResetAggro() {
      aggroUpdateLock.lock();
      try {
         this.setControllerHasAggro(false);
         this.setControllerKnowsAboutAggro(false);
      } finally {
         aggroUpdateLock.unlock();
      }
   }

   public final int getRemoveAfter() {
      return stats.removeAfter();
   }

   public void dispose() {
      if (monsterItemDrop != null) {
         monsterItemDrop.cancel(false);
      }

      this.getMap().dismissRemoveAfter(this);
      disposeLocks();
   }

   private void disposeLocks() {
      LockCollector.getInstance().registerDisposeAction(this::emptyLocks);
   }

   private void emptyLocks() {
      externalLock = externalLock.dispose();
      monsterLock = monsterLock.dispose();
      statusLock = statusLock.dispose();
      animationLock = animationLock.dispose();
   }

   private final class DamageTask implements Runnable {

      private final int dealDamage;
      private final MapleCharacter chr;
      private final MonsterStatusEffect status;
      private final int type;
      private final MapleMap map;

      private DamageTask(int dealDamage, MapleCharacter chr, MonsterStatusEffect status, int type) {
         this.dealDamage = dealDamage;
         this.chr = chr;
         this.status = status;
         this.type = type;
         this.map = chr.getMap();
      }

      @Override
      public void run() {
         int curHp = hp.get();
         if (curHp <= 1) {
            MobStatusService service = (MobStatusService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_STATUS);
            service.interruptMobStatus(map.getId(), status);
            return;
         }

         int damage = dealDamage;
         if (damage >= curHp) {
            damage = curHp - 1;
            if (type == 1 || type == 2) {
               MobStatusService service = (MobStatusService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_STATUS);
               service.interruptMobStatus(map.getId(), status);
            }
         }
         if (damage > 0) {
            lockMonster();
            try {
               applyDamage(chr, damage, true, false);
            } finally {
               unlockMonster();
            }

            if (type == 1) {
               MasterBroadcaster.getInstance().sendToAllInMapRange(map, new DamageMonster(MapleMonster.this.objectId(), damage),
                     MapleMonster.this.position());
            } else if (type == 2) {
               if (damage < dealDamage) {    // ninja ambush (type 2) is already displaying DOT to the caster
                  MasterBroadcaster.getInstance().sendToAllInMapRange(map, new DamageMonster(MapleMonster.this.objectId(), damage),
                        MapleMonster.this.position());
               }
            }
         }
      }
   }
}