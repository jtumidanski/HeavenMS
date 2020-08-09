package server.life;

import constants.MobConstants;

public class ChangeableStats extends OverrideMonsterStats {
   private Integer watk;

   private Integer matk;

   private Integer wdef;

   private Integer mdef;

   private Integer level;

   public ChangeableStats() {
   }

   public ChangeableStats(MapleMonsterStats mapleMonsterStats, OverrideMonsterStats overrideMonsterStats) {
      super(overrideMonsterStats.hp(), overrideMonsterStats.mp(), overrideMonsterStats.exp());
      watk = mapleMonsterStats.paDamage();
      matk = mapleMonsterStats.maDamage();
      wdef = mapleMonsterStats.pdDamage();
      mdef = mapleMonsterStats.mdDamage();
      level = mapleMonsterStats.level();
   }

   public ChangeableStats(MapleMonsterStats mapleMonsterStats, Integer newLevel, Boolean pqMob) {
      super(getHp(mapleMonsterStats, newLevel), getMp(mapleMonsterStats, newLevel, pqMob), getExp(mapleMonsterStats, newLevel));

      double mod = newLevel / (double) mapleMonsterStats.level();
      watk = Math.min((int) Math.round(mapleMonsterStats.paDamage() * mod), Integer.MAX_VALUE);
      matk = Math.min((int) Math.round(mapleMonsterStats.maDamage() * mod), Integer.MAX_VALUE);
      wdef = Math.min(Math.min(mapleMonsterStats.isBoss() ? 30 : 20, (int) Math.round(mapleMonsterStats.pdDamage() * mod)), Integer.MAX_VALUE);
      mdef = Math.min(Math.min(mapleMonsterStats.isBoss() ? 30 : 20, (int) Math.round(mapleMonsterStats.mdDamage() * mod)), Integer.MAX_VALUE);
      level = newLevel;
   }

   public ChangeableStats(MapleMonsterStats mapleMonsterStats, Float statModifier, Boolean pqMob) {
      this(mapleMonsterStats, (int) (statModifier * mapleMonsterStats.level()), pqMob);
   }

   private static int getMp(MapleMonsterStats mapleMonsterStats, Integer newLevel, Boolean pqMob) {
      double mod = newLevel / (double) mapleMonsterStats.level();
      double pqMod = pqMob ? 1.5 : 1.0;
      int mpRound = (int) Math.round(mapleMonsterStats.mp() * mod * pqMod);
      return Math.min(mpRound, Integer.MAX_VALUE);
   }

   private static int getHp(MapleMonsterStats mapleMonsterStats, Integer newLevel) {
      double mod = newLevel / (double) mapleMonsterStats.level();
      int hpRound = (int) Math.round(mapleMonsterStats.isBoss() ? mapleMonsterStats.hp() * mod : MobConstants.getMonsterHP(newLevel));
      return Math.min(hpRound, Integer.MAX_VALUE);
   }

   private static int getExp(MapleMonsterStats mapleMonsterStats, Integer newLevel) {
      double hpRatio = mapleMonsterStats.hp() / (double) mapleMonsterStats.exp();
      int expRound = (int) Math.round(mapleMonsterStats.isBoss() ? mapleMonsterStats.exp() : MobConstants.getMonsterHP(newLevel) / hpRatio);
      return Math.min(expRound, Integer.MAX_VALUE);
   }

   public Integer watk() {
      return watk;
   }

   public Integer matk() {
      return matk;
   }

   public Integer wdef() {
      return wdef;
   }

   public Integer mdef() {
      return mdef;
   }

   public Integer level() {
      return level;
   }
}
