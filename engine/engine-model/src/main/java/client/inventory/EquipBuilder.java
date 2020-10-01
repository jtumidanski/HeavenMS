package client.inventory;

import java.util.Random;
import java.util.function.Function;

import config.YamlConfig;
import tools.Randomizer;

public class EquipBuilder<R extends Equip, B extends EquipBuilder<R, B>> extends ItemBuilder<R, B> {
   protected float itemExp;

   protected byte itemLevel;

   protected byte level;

   protected int str;

   protected int dex;

   protected int intelligence;

   protected int luk;

   protected int hp;

   protected int mp;

   protected int watk;

   protected int matk;

   protected int wdef;

   protected int mdef;

   protected int acc;

   protected int avoid;

   protected int hands;

   protected int speed;

   protected int jump;

   protected int vicious;

   protected int ringId;

   protected boolean wearing;

   protected boolean upgradeable;

   protected int slots;

   protected boolean elemental;

   public EquipBuilder(int id) {
      super(id);
      ringId = -1;
   }

   public EquipBuilder(R other) {
      super(other);
      this.itemExp = other.itemExp();
      this.itemLevel = other.itemLevel();
      this.level = other.level();
      this.str = other.str();
      this.dex = other.dex();
      this.intelligence = other.intelligence();
      this.luk = other.luk();
      this.hp = other.hp();
      this.mp = other.mp();
      this.watk = other.watk();
      this.matk = other.matk();
      this.wdef = other.wdef();
      this.mdef = other.mdef();
      this.acc = other.acc();
      this.avoid = other.avoid();
      this.hands = other.hands();
      this.speed = other.speed();
      this.jump = other.jump();
      this.vicious = other.vicious();
      this.ringId = other.ringId();
      this.wearing = other.wearing();
      this.upgradeable = other.upgradeable();
      this.slots = other.slots();
      this.elemental = other.elemental();
   }

   @Override
   public B getThis() {
      return (B) this;
   }

   @Override
   public R build() {
      //TODO handle short overlfow for
      return (R) new Equip(id, position, quantity, pet, petId, flag, sn, giftFrom, owner, expiration, itemExp, itemLevel,
            level, str, dex, intelligence, luk, hp, mp, watk, matk, wdef, mdef, acc, avoid, hands, speed, jump, vicious,
            ringId, wearing, upgradeable, slots, elemental);
   }

   public EquipBuilder<R, B> setItemExp(float itemExp) {
      this.itemExp = itemExp;
      return getThis();
   }

   public EquipBuilder<R, B> setItemLevel(byte itemLevel) {
      this.itemLevel = itemLevel;
      return getThis();
   }

   public EquipBuilder<R, B> setLevel(byte level) {
      this.level = level;
      return getThis();
   }

   public EquipBuilder<R, B> setStr(int str) {
      this.str = str;
      return getThis();
   }

   public EquipBuilder<R, B> setDex(int dex) {
      this.dex = dex;
      return getThis();
   }

   public EquipBuilder<R, B> setIntelligence(int intelligence) {
      this.intelligence = intelligence;
      return getThis();
   }

   public EquipBuilder<R, B> setLuk(int luk) {
      this.luk = luk;
      return getThis();
   }

   public EquipBuilder<R, B> setHp(int hp) {
      this.hp = hp;
      return getThis();
   }

   public EquipBuilder<R, B> setMp(int mp) {
      this.mp = mp;
      return getThis();
   }

   public EquipBuilder<R, B> setWatk(int watk) {
      this.watk = watk;
      return getThis();
   }

   public EquipBuilder<R, B> setMatk(int matk) {
      this.matk = matk;
      return getThis();
   }

   public EquipBuilder<R, B> setWdef(int wdef) {
      this.wdef = wdef;
      return getThis();
   }

   public EquipBuilder<R, B> setMdef(int mdef) {
      this.mdef = mdef;
      return getThis();
   }

   public EquipBuilder<R, B> setAcc(int acc) {
      this.acc = acc;
      return getThis();
   }

   public EquipBuilder<R, B> setAvoid(int avoid) {
      this.avoid = avoid;
      return getThis();
   }

   public EquipBuilder<R, B> setHands(int hands) {
      this.hands = hands;
      return getThis();
   }

   public EquipBuilder<R, B> setSpeed(int speed) {
      this.speed = speed;
      return getThis();
   }

   public EquipBuilder<R, B> setJump(int jump) {
      this.jump = jump;
      return getThis();
   }

   public EquipBuilder<R, B> setVicious(int vicious) {
      this.vicious = vicious;
      return getThis();
   }

   public EquipBuilder<R, B> setRingId(int ringId) {
      this.ringId = ringId;
      return getThis();
   }

   public EquipBuilder<R, B> setWearing(boolean wearing) {
      this.wearing = wearing;
      return getThis();
   }

   public EquipBuilder<R, B> setUpgradeable(boolean upgradeable) {
      this.upgradeable = upgradeable;
      return getThis();
   }

   public EquipBuilder<R, B> setSlots(int slots) {
      this.slots = slots;
      return getThis();
   }

   public EquipBuilder<R, B> setElemental(boolean elemental) {
      this.elemental = elemental;
      return getThis();
   }

   public EquipBuilder<R, B> increaseStr(int str) {
      this.str += str;
      return getThis();
   }

   public EquipBuilder<R, B> increaseDex(int dex) {
      this.dex += dex;
      return getThis();
   }

   public EquipBuilder<R, B> increaseIntelligence(int intelligence) {
      this.intelligence += intelligence;
      return getThis();
   }

   public EquipBuilder<R, B> increaseLuk(int luk) {
      this.luk += luk;
      return getThis();
   }

   public EquipBuilder<R, B> increaseHp(int hp) {
      this.hp += hp;
      return getThis();
   }

   public EquipBuilder<R, B> increaseMp(int mp) {
      this.mp += mp;
      return getThis();
   }

   public EquipBuilder<R, B> increaseWatk(int watk) {
      this.watk += watk;
      return getThis();
   }

   public EquipBuilder<R, B> increaseMatk(int matk) {
      this.matk += matk;
      return getThis();
   }

   public EquipBuilder<R, B> increaseWdef(int wdef) {
      this.wdef += wdef;
      return getThis();
   }

   public EquipBuilder<R, B> increaseMdef(int mdef) {
      this.mdef += mdef;
      return getThis();
   }

   public EquipBuilder<R, B> increaseAcc(int acc) {
      this.acc += acc;
      return getThis();
   }

   public EquipBuilder<R, B> increaseAvoid(int avoid) {
      this.avoid += avoid;
      return getThis();
   }

   public EquipBuilder<R, B> increaseSpeed(int value) {
      this.speed += value;
      return getThis();
   }

   public EquipBuilder<R, B> increaseJump(int value) {
      this.jump += value;
      return getThis();
   }

   public EquipBuilder<R, B> withChaos(int range, boolean option) {
      if (!option) {
         applyChaos(str, this::setStr, range);
         applyChaos(dex, this::setDex, range);
         applyChaos(intelligence, this::setIntelligence, range);
         applyChaos(luk, this::setLuk, range);
         applyChaos(acc, this::setAcc, range);
         applyChaos(avoid, this::setAvoid, range);
      } else {
         applyChaos(watk, this::setWatk, range);
         applyChaos(wdef, this::setWdef, range);
         applyChaos(matk, this::setMatk, range);
         applyChaos(mdef, this::setMdef, range);
         applyChaos(speed, this::setSpeed, range);
         applyChaos(jump, this::setJump, range);
         applyChaos(hp, this::setHp, range);
         applyChaos(mp, this::setMp, range);
      }
      return this;
   }

   public EquipBuilder<R, B> withChaos(int range) {
      final Equip before = this.build();

      if (YamlConfig.config.server.CHSCROLL_STAT_RATE > 0) {
         if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
         } else {
            str = Short.MIN_VALUE;
            dex = Short.MIN_VALUE;
            intelligence = Short.MIN_VALUE;
            luk = Short.MIN_VALUE;
            watk = Short.MIN_VALUE;
            wdef = Short.MIN_VALUE;
            matk = Short.MIN_VALUE;
            mdef = Short.MIN_VALUE;
            acc = Short.MIN_VALUE;
            avoid = Short.MIN_VALUE;
            speed = Short.MIN_VALUE;
            jump = Short.MIN_VALUE;
            hp = Short.MIN_VALUE;
            mp = Short.MIN_VALUE;
         }

         for (int i = 0; i < YamlConfig.config.server.CHSCROLL_STAT_RATE; i++) {
            str = evaluateChaos(before.str(), str, range);
            dex = evaluateChaos(before.dex(), dex, range);
            intelligence = evaluateChaos(before.intelligence(), intelligence, range);
            luk = evaluateChaos(before.luk(), luk, range);
            watk = evaluateChaos(before.watk(), watk, range);
            wdef = evaluateChaos(before.wdef(), wdef, range);
            matk = evaluateChaos(before.matk(), matk, range);
            mdef = evaluateChaos(before.mdef(), mdef, range);
            acc = evaluateChaos(before.acc(), acc, range);
            avoid = evaluateChaos(before.avoid(), avoid, range);
            speed = evaluateChaos(before.speed(), speed, range);
            jump = evaluateChaos(before.jump(), jump, range);
            hp = evaluateChaos(before.hp(), hp, range);
            mp = evaluateChaos(before.mp(), mp, range);
         }
         str = Math.max(0, str);
         dex = Math.max(0, dex);
         intelligence = Math.max(0, intelligence);
         luk = Math.max(0, luk);
         watk = Math.max(0, watk);
         wdef = Math.max(0, wdef);
         matk = Math.max(0, matk);
         mdef = Math.max(0, mdef);
         acc = Math.max(0, acc);
         avoid = Math.max(0, avoid);
         speed = Math.max(0, speed);
         jump = Math.max(0, jump);
         hp = Math.max(0, hp);
         mp = Math.max(0, mp);
      } else {
         str = evaluateChaos(before.str(), str, range);
         dex = evaluateChaos(before.dex(), dex, range);
         intelligence = evaluateChaos(before.intelligence(), intelligence, range);
         luk = evaluateChaos(before.luk(), luk, range);
         watk = evaluateChaos(before.watk(), watk, range);
         wdef = evaluateChaos(before.wdef(), wdef, range);
         matk = evaluateChaos(before.matk(), matk, range);
         mdef = evaluateChaos(before.mdef(), mdef, range);
         acc = evaluateChaos(before.acc(), acc, range);
         avoid = evaluateChaos(before.avoid(), avoid, range);
         speed = evaluateChaos(before.speed(), speed, range);
         jump = evaluateChaos(before.jump(), jump, range);
         hp = evaluateChaos(before.hp(), hp, range);
         mp = evaluateChaos(before.mp(), mp, range);
      }
      return this;
   }

   protected void applyChaos(Integer value, Function<Integer, EquipBuilder> setter, int range) {
      if (value > 0) {
         if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
            setter.apply((int) getMaximumShortMaxIfOverflow(value, (value + chaosScrollRandomizedStat(range))));
         } else {
            setter.apply((int) getMaximumShortMaxIfOverflow(0, (value + chaosScrollRandomizedStat(range))));
         }
      }
      throw new UnsupportedOperationException();
   }

   protected int evaluateChaos(int originalValue, int runningValue, int range) {
      int temp;
      if (originalValue > 0) {
         if (YamlConfig.config.server.USE_ENHANCED_CHSCROLL) {
            temp = runningValue + chaosScrollRandomizedStat(range);
         } else {
            temp = originalValue + chaosScrollRandomizedStat(range);
         }

         runningValue = getMaximumShortMaxIfOverflow(temp, runningValue);
      }
      return runningValue;
   }

   private short getMaximumShortMaxIfOverflow(int value1, int value2) {
      return (short) Math.min(Short.MAX_VALUE, Math.max(value1, value2));
   }

   private short getShortMaxIfOverflow(int value) {
      return (short) Math.min(Short.MAX_VALUE, value);
   }

   private short chaosScrollRandomizedStat(int range) {
      return (short) rand(-range, range);
   }

   private int rand(final int lbound, final int ubound) {
      Random rand = new Random();
      return (int) ((rand.nextDouble() * (ubound - lbound + 1)) + lbound);
   }


   public EquipBuilder<R, B> randomizeStats() {
      str = (getRandStat(str, 5));
      dex = (getRandStat(dex, 5));
      intelligence = (getRandStat(intelligence, 5));
      luk = (getRandStat(luk, 5));
      matk = (getRandStat(matk, 5));
      watk = (getRandStat(watk, 5));
      acc = (getRandStat(acc, 5));
      avoid = (getRandStat(avoid, 5));
      jump = (getRandStat(jump, 5));
      speed = (getRandStat(speed, 5));
      wdef = (getRandStat(wdef, 10));
      mdef = (getRandStat(mdef, 10));
      hp = (getRandStat(hp, 10));
      mp = (getRandStat(mp, 10));
      return this;
   }

   public EquipBuilder<R, B> randomizeUpgradeStats() {
      str = (getRandUpgradedStat(str, 2));
      dex = (getRandUpgradedStat(dex, 2));
      intelligence = (getRandUpgradedStat(intelligence, 2));
      luk = (getRandUpgradedStat(luk, 2));
      matk = (getRandUpgradedStat(matk, 2));
      watk = (getRandUpgradedStat(watk, 2));
      acc = (getRandUpgradedStat(acc, 2));
      avoid = (getRandUpgradedStat(avoid, 2));
      jump = (getRandUpgradedStat(jump, 2));
      wdef = (getRandUpgradedStat(wdef, 5));
      mdef = (getRandUpgradedStat(mdef, 5));
      hp = (getRandUpgradedStat(hp, 5));
      mp = (getRandUpgradedStat(mp, 5));
      return this;
   }

   private short getRandStat(int defaultValue, int maxRange) {
      if (defaultValue == 0) {
         return 0;
      }
      int lMaxRange = (int) Math.min(Math.ceil(defaultValue * 0.1), maxRange);
      return (short) ((defaultValue - lMaxRange) + Math.floor(Randomizer.nextDouble() * (lMaxRange * 2 + 1)));
   }

   private short getRandUpgradedStat(int defaultValue, int maxRange) {
      if (defaultValue == 0) {
         return 0;
      }
      return (short) (defaultValue + Math.floor(Randomizer.nextDouble() * (maxRange + 1)));
   }
}
