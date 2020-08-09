package net.server;

public record SkillMacro(String name, Integer shout, Integer skill1, Integer skill2, Integer skill3, Integer position) {
   public SkillMacro setSkill1(Integer skill1) {
      return new SkillMacro(name, shout, skill1, skill2, skill3, position);
   }

   public SkillMacro setSkill2(Integer skill2) {
      return new SkillMacro(name, shout, skill1, skill2, skill3, position);
   }

   public SkillMacro setSkill3(Integer skill2) {
      return new SkillMacro(name, shout, skill1, skill2, skill3, position);
   }
}
