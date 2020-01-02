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
      switch (skillId) {
         case DarkKnight.BEHOLDER:
         case FirePoisonArchMage.ELQUINES:
         case IceLighteningArchMagician.IFRIT:
         case Priest.SUMMON_DRAGON:
         case Bishop.BAHAMUT:
         case Ranger.PUPPET:
         case Ranger.SILVER_HAWK:
         case Sniper.PUPPET:
         case Sniper.GOLDEN_EAGLE:
         case Hermit.SHADOW_PARTNER:
            return true;
         default:
            return false;
      }
   }
}
