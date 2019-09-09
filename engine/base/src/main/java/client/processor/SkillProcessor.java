package client.processor;

import constants.skills.Bishop;
import constants.skills.DarkKnight;
import constants.skills.FPArchMage;
import constants.skills.Hermit;
import constants.skills.ILArchMage;
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

   public boolean dispelSkills(int skillid) {
      switch (skillid) {
         case DarkKnight.BEHOLDER:
         case FPArchMage.ELQUINES:
         case ILArchMage.IFRIT:
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
