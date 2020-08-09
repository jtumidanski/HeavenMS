package server.life;

public class OverrideMonsterStats {
   private final Integer hp;

   private final Integer mp;

   private final Integer exp;

   private final Boolean change;

   public OverrideMonsterStats(Integer hp, Integer mp, Integer exp, Boolean change) {
      this.hp = hp;
      this.mp = mp;
      this.exp = exp;
      this.change = change;
   }

   public OverrideMonsterStats(Integer hp, Integer mp, Integer exp) {
      this(hp, mp, exp, true);
   }

   public OverrideMonsterStats() {
      this(1, 0, 0, true);
   }

   public Integer hp() {
      return hp;
   }

   public Integer mp() {
      return mp;
   }

   public Integer exp() {
      return exp;
   }

   public Boolean change() {
      return change;
   }
}
