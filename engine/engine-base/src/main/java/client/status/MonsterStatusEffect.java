package client.status;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import client.Skill;
import server.life.MobSkill;

public class MonsterStatusEffect {

   private Map<MonsterStatus, Integer> statuses;
   private Skill skill;
   private MobSkill mobskill;
   private boolean monsterSkill;

   public MonsterStatusEffect(Map<MonsterStatus, Integer> statuses, Skill skillId, MobSkill mobskill, boolean monsterSkill) {
      this.statuses = new ConcurrentHashMap<>(statuses);
      this.skill = skillId;
      this.monsterSkill = monsterSkill;
      this.mobskill = mobskill;
   }

   public Map<MonsterStatus, Integer> getStatuses() {
      return statuses;
   }

   public Integer setValue(MonsterStatus status, Integer newVal) {
      return statuses.put(status, newVal);
   }

   public Skill getSkill() {
      return skill;
   }

   public boolean isMonsterSkill() {
      return monsterSkill;
   }

   public void removeActiveStatus(MonsterStatus stat) {
      statuses.remove(stat);
   }

   public MobSkill getMobSkill() {
      return mobskill;
   }
}
