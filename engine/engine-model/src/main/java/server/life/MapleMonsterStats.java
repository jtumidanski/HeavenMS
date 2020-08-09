package server.life;

import java.util.List;
import java.util.Map;

import server.life.builder.MapleMonsterStatsBuilder;
import tools.Pair;

public record MapleMonsterStats(String name, Integer hp, Integer mp, Integer exp, Integer level, Integer paDamage,
                                Integer pdDamage, Integer maDamage, Integer mdDamage, Boolean isFriendly,
                                Integer removeAfter, Boolean isBoss, Boolean isExplosiveReward, Boolean isFFALoot,
                                Boolean isUndead, Integer buffToGive, Integer cp, Boolean removeOnMiss,
                                Boolean changeable,
                                Map<String, Integer> animationTimes, Map<Element, ElementalEffectiveness> resistances,
                                List<LoseItem> loseItemList, List<Pair<Integer, Integer>> skills,
                                List<Integer> revives, Byte tagColor, Byte tagBackgroundColor, Integer fixedStance,
                                Boolean firstAttack, BanishInfo banish, Integer dropPeriod,
                                SelfDestruction selfDestruction, Pair<Integer, Integer> cool) {
   public Integer getAnimationTime(String name) {
      return animationTimes().getOrDefault(name, 500);
   }

   public Boolean isMobile() {
      return animationTimes().containsKey("move") || animationTimes().containsKey("fly");
   }

   public ElementalEffectiveness getEffectiveness(Element element) {
      return resistances().getOrDefault(element, ElementalEffectiveness.NORMAL);
   }

   public Boolean hasSkill(Integer skillId, Integer level) {
      return skills().stream().anyMatch(pair -> pair.getLeft().equals(skillId) && pair.getRight().equals(level));
   }

   public Integer getNoSkills() {
      return skills().size();
   }

   public MapleMonsterStats copy() {
      return new MapleMonsterStatsBuilder(this).build();
   }

   public MapleMonsterStats setHp(int hp) {
      return new MapleMonsterStatsBuilder(this).setHp(hp).build();
   }

   public MapleMonsterStats setBoss(Boolean isBoss) {
      return new MapleMonsterStatsBuilder(this).setBoss(isBoss).build();
   }

   public MapleMonsterStats setEffectiveness(Element element, ElementalEffectiveness effectiveness) {
      return new MapleMonsterStatsBuilder(this).setEffectiveness(element, effectiveness).build();
   }

   public MapleMonsterStats removeEffectiveness(Element element) {
      return new MapleMonsterStatsBuilder(this).removeEffectiveness(element).build();
   }
}
