package server.life.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import server.life.BanishInfo;
import server.life.Element;
import server.life.ElementalEffectiveness;
import server.life.LoseItem;
import server.life.MapleMonsterStats;
import server.life.SelfDestruction;
import tools.Pair;

public class MapleMonsterStatsBuilder {
   private Integer hp;

   private Boolean isFriendly;

   private Integer paDamage;

   private Integer pdDamage;

   private Integer maDamage;

   private Integer mdDamage;

   private Integer mp;

   private Integer exp;

   private Integer level;

   private Integer removeAfter;

   private Boolean isBoss;

   private Boolean isExplosiveReward;

   private Boolean isFFALoot;

   private Boolean isUndead;

   private String name;

   private Integer buffToGive;

   private Integer cp;

   private Boolean removeOnMiss;

   private Pair<Integer, Integer> cool;

   private List<LoseItem> loseItemList;

   private SelfDestruction selfDestruction;

   private Boolean firstAttack;

   private Integer dropPeriod;

   private Map<String, Integer> animationTime;

   private Byte tagColor;

   private Byte tagBackgroundColor;

   private List<Integer> revives;

   private List<Pair<Integer, Integer>> skills;

   private BanishInfo banish;

   private Integer fixedStance;

   private Map<Element, ElementalEffectiveness> resistance;

   private Boolean changeable;

   public MapleMonsterStatsBuilder() {
      loseItemList = new ArrayList<>();
      animationTime = new HashMap<>();
      revives = new ArrayList<>();
      skills = new ArrayList<>();
      resistance = new HashMap<>();
   }

   public MapleMonsterStatsBuilder(MapleMonsterStats other) {
      this.hp = other.hp();
      this.isFriendly = other.isFriendly();
      this.paDamage = other.paDamage();
      this.pdDamage = other.pdDamage();
      this.maDamage = other.maDamage();
      this.mdDamage = other.mdDamage();
      this.mp = other.mp();
      this.exp = other.exp();
      this.level = other.level();
      this.removeAfter = other.removeAfter();
      this.isBoss = other.isBoss();
      this.isExplosiveReward = other.isExplosiveReward();
      this.isFFALoot = other.isFFALoot();
      this.isUndead = other.isUndead();
      this.name = other.name();
      this.buffToGive = other.buffToGive();
      this.cp = other.cp();
      this.removeOnMiss = other.removeOnMiss();
      this.cool = other.cool();
      this.loseItemList = other.loseItemList();
      this.selfDestruction = other.selfDestruction();
      this.firstAttack = other.firstAttack();
      this.dropPeriod = other.dropPeriod();
      this.animationTime = other.animationTimes();
      this.tagColor = other.tagColor();
      this.tagBackgroundColor = other.tagBackgroundColor();
      this.revives = other.revives();
      this.skills = other.skills();
      this.banish = other.banish();
      this.fixedStance = other.fixedStance();
      this.resistance = other.resistances();
      this.changeable = other.changeable();
   }

   public MapleMonsterStats build() {
      return new MapleMonsterStats(name, hp, mp, exp, level, paDamage, pdDamage, maDamage, mdDamage, isFriendly,
            removeAfter, isBoss, isExplosiveReward, isFFALoot, isUndead, buffToGive, cp, removeOnMiss, changeable,
            animationTime, resistance, loseItemList, skills, revives, tagColor, tagBackgroundColor, fixedStance,
            firstAttack, banish, dropPeriod, selfDestruction, cool);
   }

   public MapleMonsterStatsBuilder setHp(Integer hp) {
      this.hp = hp;
      return this;
   }

   public MapleMonsterStatsBuilder setFriendly(Boolean friendly) {
      isFriendly = friendly;
      return this;
   }

   public MapleMonsterStatsBuilder setPaDamage(Integer paDamage) {
      this.paDamage = paDamage;
      return this;
   }

   public MapleMonsterStatsBuilder setPdDamage(Integer pdDamage) {
      this.pdDamage = pdDamage;
      return this;
   }

   public MapleMonsterStatsBuilder setMaDamage(Integer maDamage) {
      this.maDamage = maDamage;
      return this;
   }

   public MapleMonsterStatsBuilder setMdDamage(Integer mdDamage) {
      this.mdDamage = mdDamage;
      return this;
   }

   public MapleMonsterStatsBuilder setMp(Integer mp) {
      this.mp = mp;
      return this;
   }

   public MapleMonsterStatsBuilder setExp(Integer exp) {
      this.exp = exp;
      return this;
   }

   public MapleMonsterStatsBuilder setLevel(Integer level) {
      this.level = level;
      return this;
   }

   public MapleMonsterStatsBuilder setRemoveAfter(Integer removeAfter) {
      this.removeAfter = removeAfter;
      return this;
   }

   public MapleMonsterStatsBuilder setBoss(Boolean boss) {
      isBoss = boss;
      return this;
   }

   public MapleMonsterStatsBuilder setExplosiveReward(Boolean explosiveReward) {
      isExplosiveReward = explosiveReward;
      return this;
   }

   public MapleMonsterStatsBuilder setFFALoot(Boolean FFALoot) {
      isFFALoot = FFALoot;
      return this;
   }

   public MapleMonsterStatsBuilder setUndead(Boolean undead) {
      isUndead = undead;
      return this;
   }

   public MapleMonsterStatsBuilder setName(String name) {
      this.name = name;
      return this;
   }

   public MapleMonsterStatsBuilder setBuffToGive(Integer buffToGive) {
      this.buffToGive = buffToGive;
      return this;
   }

   public MapleMonsterStatsBuilder setCp(Integer cp) {
      this.cp = cp;
      return this;
   }

   public MapleMonsterStatsBuilder setRemoveOnMiss(Boolean removeOnMiss) {
      this.removeOnMiss = removeOnMiss;
      return this;
   }

   public MapleMonsterStatsBuilder setCool(Integer damage, Integer probability) {
      this.cool = new Pair<>(damage, probability);
      return this;
   }

   public MapleMonsterStatsBuilder addLoseItem(LoseItem loseItem) {
      this.loseItemList.add(loseItem);
      return this;
   }

   public MapleMonsterStatsBuilder setSelfDestruction(SelfDestruction selfDestruction) {
      this.selfDestruction = selfDestruction;
      return this;
   }

   public MapleMonsterStatsBuilder setFirstAttack(Boolean firstAttack) {
      this.firstAttack = firstAttack;
      return this;
   }

   public MapleMonsterStatsBuilder setDropPeriod(Integer dropPeriod) {
      this.dropPeriod = dropPeriod;
      return this;
   }

   public MapleMonsterStatsBuilder setAnimationTime(String name, Integer delay) {
      this.animationTime.put(name, delay);
      return this;
   }

   public MapleMonsterStatsBuilder setTagColor(Byte tagColor) {
      this.tagColor = tagColor;
      return this;
   }

   public MapleMonsterStatsBuilder setTagBackgroundColor(Byte tagBackgroundColor) {
      this.tagBackgroundColor = tagBackgroundColor;
      return this;
   }

   public MapleMonsterStatsBuilder setRevives(List<Integer> revives) {
      this.revives = revives;
      return this;
   }

   public MapleMonsterStatsBuilder setSkills(List<Pair<Integer, Integer>> skills) {
      this.skills = skills;
      return this;
   }

   public MapleMonsterStatsBuilder setBanish(BanishInfo banish) {
      this.banish = banish;
      return this;
   }

   public MapleMonsterStatsBuilder setFixedStance(Integer fixedStance) {
      this.fixedStance = fixedStance;
      return this;
   }

   public MapleMonsterStatsBuilder setEffectiveness(Element element, ElementalEffectiveness effectiveness) {
      this.resistance.put(element, effectiveness);
      return this;
   }

   public MapleMonsterStatsBuilder removeEffectiveness(Element element) {
      this.resistance.remove(element);
      return this;
   }
}
