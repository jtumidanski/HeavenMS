package client.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.YamlConfig;
import tools.Pair;

public class Equip extends Item {
   private final float itemExp;

   private final byte itemLevel;

   private final byte level;

   private final int str;

   private final int dex;

   private final int intelligence;

   private final int luk;

   private final int hp;

   private final int mp;

   private final int watk;

   private final int matk;

   private final int wdef;

   private final int mdef;

   private final int acc;

   private final int avoid;

   private final int hands;

   private final int speed;

   private final int jump;

   private final int vicious;

   private final int ringId;

   private final boolean wearing;

   private final boolean upgradeable;

   private final int slots;

   private final boolean elemental;

   public Equip(int id, short position, short quantity, MaplePet pet, int petId, short flag, int sn, String giftFrom,
                String owner, long expiration, float itemExp, byte itemLevel, byte level, int str, int dex,
                int intelligence, int luk, int hp, int mp, int watk, int matk, int wdef, int mdef, int acc, int avoid,
                int hands, int speed, int jump, int vicious, int ringId, boolean wearing, boolean upgradeable,
                int slots, boolean elemental) {
      super(id, position, quantity, pet, petId, flag, sn, giftFrom, owner, expiration);
      this.itemExp = itemExp;
      this.itemLevel = itemLevel;
      this.level = level;
      this.str = str;
      this.dex = dex;
      this.intelligence = intelligence;
      this.luk = luk;
      this.hp = hp;
      this.mp = mp;
      this.watk = watk;
      this.matk = matk;
      this.wdef = wdef;
      this.mdef = mdef;
      this.acc = acc;
      this.avoid = avoid;
      this.hands = hands;
      this.speed = speed;
      this.jump = jump;
      this.vicious = vicious;
      this.ringId = ringId;
      this.wearing = wearing;
      this.upgradeable = upgradeable;
      this.slots = slots;
      this.elemental = elemental;
   }

   public Equip(int id, short position, int slots, boolean elemental) {
      super(id, position, (short) 1);
      this.itemExp = 0;
      this.itemLevel = 0;
      this.level = 0;
      this.str = 0;
      this.dex = 0;
      this.intelligence = 0;
      this.luk = 0;
      this.hp = 0;
      this.mp = 0;
      this.watk = 0;
      this.matk = 0;
      this.wdef = 0;
      this.mdef = 0;
      this.acc = 0;
      this.avoid = 0;
      this.hands = 0;
      this.speed = 0;
      this.jump = 0;
      this.vicious = 0;
      this.ringId = 0;
      this.wearing = false;
      this.upgradeable = false;
      this.slots = slots;
      this.elemental = elemental;
   }

   public Equip(int id, short position, boolean elemental) {
      this(id, position, 0, elemental);
   }

   public float itemExp() {
      return itemExp;
   }

   public byte itemLevel() {
      return itemLevel;
   }

   public byte level() {
      return level;
   }

   public int str() {
      return str;
   }

   public int dex() {
      return dex;
   }

   public int intelligence() {
      return intelligence;
   }

   public int luk() {
      return luk;
   }

   public int hp() {
      return hp;
   }

   public int mp() {
      return mp;
   }

   public int watk() {
      return watk;
   }

   public int matk() {
      return matk;
   }

   public int wdef() {
      return wdef;
   }

   public int mdef() {
      return mdef;
   }

   public int acc() {
      return acc;
   }

   public int avoid() {
      return avoid;
   }

   public int hands() {
      return hands;
   }

   public int speed() {
      return speed;
   }

   public int jump() {
      return jump;
   }

   public int vicious() {
      return vicious;
   }

   public int ringId() {
      return ringId;
   }

   public boolean wearing() {
      return wearing;
   }

   public boolean upgradeable() {
      return upgradeable;
   }

   public int slots() {
      return slots;
   }

   public boolean elemental() {
      return elemental;
   }

   public Map<StatUpgrade, Short> getStats() {
      Map<StatUpgrade, Short> stats = new HashMap<>();
      if (dex > 0) {
         stats.put(StatUpgrade.incDEX, (short) dex);
      }
      if (str > 0) {
         stats.put(StatUpgrade.incSTR, (short) str);
      }
      if (intelligence > 0) {
         stats.put(StatUpgrade.incINT, (short) intelligence);
      }
      if (luk > 0) {
         stats.put(StatUpgrade.incLUK, (short) luk);
      }
      if (hp > 0) {
         stats.put(StatUpgrade.incMHP, (short) hp);
      }
      if (mp > 0) {
         stats.put(StatUpgrade.incMMP, (short) mp);
      }
      if (watk > 0) {
         stats.put(StatUpgrade.incPAD, (short) watk);
      }
      if (matk > 0) {
         stats.put(StatUpgrade.incMAD, (short) matk);
      }
      if (wdef > 0) {
         stats.put(StatUpgrade.incPDD, (short) wdef);
      }
      if (mdef > 0) {
         stats.put(StatUpgrade.incMDD, (short) mdef);
      }
      if (avoid > 0) {
         stats.put(StatUpgrade.incEVA, (short) avoid);
      }
      if (acc > 0) {
         stats.put(StatUpgrade.incACC, (short) acc);
      }
      if (speed > 0) {
         stats.put(StatUpgrade.incSpeed, (short) speed);
      }
      if (jump > 0) {
         stats.put(StatUpgrade.incJump, (short) jump);
      }
      return stats;
   }

   public Pair<Equip, Pair<StringBuilder, Pair<Boolean, Boolean>>> gainStats(List<Pair<StatUpgrade, Integer>> stats) {
      return stats.stream().reduce(
            new Pair<>(this, new Pair<>(new StringBuilder(), new Pair<>(false, false))),
            (lastResult, stat) -> {
               Equip newEquip = lastResult.getLeft();
               boolean gotSlot = lastResult.getRight().getRight().getLeft();
               boolean gotVicious = lastResult.getRight().getRight().getRight();
               int statUp;
               int maxStat = YamlConfig.config.server.MAX_EQUIPMNT_STAT;
               StringBuilder runningString = lastResult.getRight().getLeft();

               switch (stat.getLeft()) {
                  case incDEX -> {
                     statUp = Math.min(stat.getRight(), maxStat - newEquip.dex());
                     newEquip = Equip.newBuilder(newEquip).setDex(newEquip.dex() + statUp).build();
                     runningString.append("+").append(statUp).append("DEX ");
                  }
                  case incSTR -> {
                     statUp = Math.min(stat.getRight(), maxStat - newEquip.str());
                     newEquip = Equip.newBuilder(newEquip).setStr(newEquip.str() + statUp).build();
                     runningString.append("+").append(statUp).append("STR ");
                  }
                  case incINT -> {
                     statUp = Math.min(stat.getRight(), maxStat - newEquip.intelligence());
                     newEquip = Equip.newBuilder(newEquip).setIntelligence(newEquip.intelligence() + statUp).build();
                     runningString.append("+").append(statUp).append("INT ");
                  }
                  case incLUK -> {
                     statUp = Math.min(stat.getRight(), maxStat - newEquip.luk());
                     newEquip = Equip.newBuilder(newEquip).setLuk(newEquip.luk() + statUp).build();
                     runningString.append("+").append(statUp).append("LUK ");
                  }
                  case incMHP -> {
                     statUp = Math.min(stat.getRight(), maxStat - newEquip.hp());
                     newEquip = Equip.newBuilder(newEquip).setHp(newEquip.hp() + statUp).build();
                     runningString.append("+").append(statUp).append("HP ");
                  }
                  case incMMP -> {
                     statUp = Math.min(stat.getRight(), maxStat - newEquip.mp());
                     newEquip = Equip.newBuilder(newEquip).setMp(newEquip.mp() + statUp).build();
                     runningString.append("+").append(statUp).append("MP ");
                  }
                  case incPAD -> {
                     statUp = Math.min(stat.getRight(), maxStat - newEquip.watk());
                     newEquip = Equip.newBuilder(newEquip).setWatk(newEquip.watk() + statUp).build();
                     runningString.append("+").append(statUp).append("WATK ");
                  }
                  case incMAD -> {
                     statUp = Math.min(stat.getRight(), maxStat - newEquip.matk());
                     newEquip = Equip.newBuilder(newEquip).setMatk(newEquip.matk() + statUp).build();
                     runningString.append("+").append(statUp).append("MATK ");
                  }
                  case incPDD -> {
                     statUp = Math.min(stat.getRight(), maxStat - newEquip.wdef());
                     newEquip = Equip.newBuilder(newEquip).setWdef(newEquip.wdef() + statUp).build();
                     runningString.append("+").append(statUp).append("WDEF ");
                  }
                  case incMDD -> {
                     statUp = Math.min(stat.getRight(), maxStat - newEquip.mdef());
                     newEquip = Equip.newBuilder(newEquip).setMdef(newEquip.mdef() + statUp).build();
                     runningString.append("+").append(statUp).append("MDEF ");
                  }
                  case incEVA -> {
                     statUp = Math.min(stat.getRight(), maxStat - newEquip.avoid());
                     newEquip = Equip.newBuilder(newEquip).setAvoid(newEquip.avoid() + statUp).build();
                     runningString.append("+").append(statUp).append("AVOID ");
                  }
                  case incACC -> {
                     statUp = Math.min(stat.getRight(), maxStat - newEquip.acc());
                     newEquip = Equip.newBuilder(newEquip).setAcc(newEquip.acc() + statUp).build();
                     runningString.append("+").append(statUp).append("ACC ");
                  }
                  case incSpeed -> {
                     statUp = Math.min(stat.getRight(), maxStat - newEquip.speed());
                     newEquip = Equip.newBuilder(newEquip).setSpeed(newEquip.speed() + statUp).build();
                     runningString.append("+").append(statUp).append("SPEED ");
                  }
                  case incJump -> {
                     statUp = Math.min(stat.getRight(), maxStat - newEquip.jump());
                     newEquip = Equip.newBuilder(newEquip).setJump(newEquip.jump() + statUp).build();
                     runningString.append("+").append(statUp).append("JUMP ");
                  }
                  case incVicious -> {
                     newEquip = Equip.newBuilder(newEquip).setVicious(newEquip.vicious() - stat.getRight()).build();
                     gotVicious = true;
                  }
                  case incSlot -> {
                     newEquip = Equip.newBuilder(newEquip).setSlots(newEquip.slots() + stat.getRight()).build();
                     gotSlot = true;
                  }
               }

               return new Pair<>(newEquip, new Pair<>(runningString, new Pair<>(gotSlot, gotVicious)));
            }, (a, b) -> b
      );
   }

   @Override
   public Byte itemType() {
      return 1;
   }

   public static EquipBuilder<? extends Equip, ? extends EquipBuilder> newBuilder(int id) {
      return new EquipBuilder<>(id);
   }

   public static EquipBuilder<? extends Equip, ? extends EquipBuilder> newBuilder(Equip other) {
      return new EquipBuilder<>(other);
   }

   @Override
   protected EquipBuilder<? extends Equip, ? extends EquipBuilder> getBuilder() {
      return new EquipBuilder<>(this);
   }

   public Equip setSlots(int slots) {
      return getBuilder().setSlots(slots).build();
   }
}
