package client.processor;

import constants.skills.Bishop;
import constants.skills.DarkKnight;
import constants.skills.FirePoisonArchMage;
import constants.skills.Hermit;
import constants.skills.IceLighteningArchMagician;
import constants.skills.Priest;
import constants.skills.Ranger;
import constants.skills.Sniper;

public class SkillProcessor {
   private static SkillProcessor ourInstance = new SkillProcessor();

   public static SkillProcessor getInstance() {
      return ourInstance;
   }

   private SkillProcessor() {
   }

   public boolean dispelSkills(int skillId) {
      return switch (skillId) {
         case DarkKnight.BEHOLDER, FirePoisonArchMage.ELQUINES, IceLighteningArchMagician.IFRIT, Priest.SUMMON_DRAGON,
               Bishop.BAHAMUT, Ranger.PUPPET, Ranger.SILVER_HAWK, Sniper.PUPPET, Sniper.GOLDEN_EAGLE,
               Hermit.SHADOW_PARTNER -> true;
         default -> false;
      };
   }
}
