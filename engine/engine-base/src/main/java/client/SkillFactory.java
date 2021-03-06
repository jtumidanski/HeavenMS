package client;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import constants.skills.Aran;
import constants.skills.Archer;
import constants.skills.Assassin;
import constants.skills.Bandit;
import constants.skills.Beginner;
import constants.skills.Bishop;
import constants.skills.BlazeWizard;
import constants.skills.BowMaster;
import constants.skills.Buccaneer;
import constants.skills.ChiefBandit;
import constants.skills.Cleric;
import constants.skills.Corsair;
import constants.skills.Crossbowman;
import constants.skills.Crusader;
import constants.skills.DarkKnight;
import constants.skills.DawnWarrior;
import constants.skills.DragonKnight;
import constants.skills.Evan;
import constants.skills.FirePoisonArchMage;
import constants.skills.FirePoisonMagician;
import constants.skills.FPWizard;
import constants.skills.Fighter;
import constants.skills.GM;
import constants.skills.Gunslinger;
import constants.skills.Hermit;
import constants.skills.Hero;
import constants.skills.Hunter;
import constants.skills.IceLighteningArchMagician;
import constants.skills.IceLighteningMagician;
import constants.skills.ILWizard;
import constants.skills.Legend;
import constants.skills.Magician;
import constants.skills.Marauder;
import constants.skills.Marksman;
import constants.skills.NightLord;
import constants.skills.NightWalker;
import constants.skills.Noblesse;
import constants.skills.Page;
import constants.skills.Paladin;
import constants.skills.Pirate;
import constants.skills.Priest;
import constants.skills.Ranger;
import constants.skills.Rogue;
import constants.skills.Shadower;
import constants.skills.Sniper;
import constants.skills.Spearman;
import constants.skills.SuperGM;
import constants.skills.ThunderBreaker;
import constants.skills.Warrior;
import constants.skills.WhiteKnight;
import constants.skills.WindArcher;
import provider.MapleData;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataFileEntry;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.life.Element;
import server.processor.StatEffectProcessor;

public class SkillFactory {
   private static Map<Integer, Skill> skills = new HashMap<>();
   private static MapleDataProvider datasource = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Skill.wz"));

   public static Optional<Skill> getSkill(int id) {
      if (!skills.isEmpty()) {
         return Optional.ofNullable(skills.get(id));
      }
      return Optional.empty();
   }

   public static byte getSkillLevel(MapleCharacter character, int skillId) {
      Optional<Skill> skill = getSkill(skillId);
      return skill.map(character::getSkillLevel).orElse(Byte.valueOf("0"));
   }

   /**
    * Executes a function given a skill
    *
    * @param character the character
    * @param skillId   the skill identifier
    * @param function  the function to apply
    */
   public static void executeForSkill(MapleCharacter character, int skillId, BiConsumer<Skill, Integer> function) {
      Optional<Skill> skill = SkillFactory.getSkill(skillId);
      if (skill.isPresent()) {
         int skillLevel = character.getSkillLevel(skill.get());
         function.accept(skill.get(), skillLevel);
      }
   }

   /**
    * Executes a function given a skill
    *
    * @param character the character
    * @param skillId   the skill identifier
    * @param function  the function to apply
    */
   public static <T> T applyForSkill(MapleCharacter character, int skillId, BiFunction<Skill, Integer, T> function, T defaultValue) {
      Optional<Skill> skill = SkillFactory.getSkill(skillId);
      if (skill.isPresent()) {
         int skillLevel = character.getSkillLevel(skill.get());
         return function.apply(skill.get(), skillLevel);
      }
      return defaultValue;
   }

   /**
    * Executes a function if the user has a skill above level 0.
    *
    * @param character the character
    * @param skillId   the skill identifier
    * @param function  the function to execute
    */
   public static void executeIfHasSkill(MapleCharacter character, int skillId, BiConsumer<Skill, Integer> function) {
      executeIfSkillMeetsConditional(character, skillId, (skill, skillLevel) -> skillLevel > 0, function);
   }

   /**
    * Executes a function if the user has a skill that meets the conditional.
    *
    * @param character   the character
    * @param skillId     the skill identifier
    * @param conditional the condition to execute the function
    * @param function    the function to execute
    */
   public static void executeIfSkillMeetsConditional(MapleCharacter character, int skillId,
                                                     BiFunction<Skill, Integer, Boolean> conditional,
                                                     BiConsumer<Skill, Integer> function) {
      executeForSkill(character, skillId, (skill, skillLevel) -> {
         if (conditional.apply(skill, skillLevel)) {
            function.accept(skill, skillLevel);
         }
      });
   }

   /**
    * Executes a function if the user has a skill above level 0.
    *
    * @param character the character
    * @param skillId   the skill identifier
    * @param function  the function to execute
    */
   public static <T> T applyIfHasSkill(MapleCharacter character, int skillId, BiFunction<Skill, Integer, T> function, T defaultValue) {
      return applyForSkill(character, skillId, (skill, skillLevel) -> {
         if (skillLevel > 0) {
            return function.apply(skill, skillLevel);
         }
         return defaultValue;
      }, defaultValue);
   }

   public static void loadAllSkills() {
      final MapleDataDirectoryEntry root = datasource.getRoot();
      int skillId;
      for (MapleDataFileEntry topDir : root.getFiles()) { // Loop thru jobs
         if (topDir.getName().length() <= 8) {
            for (MapleData data : datasource.getData(topDir.getName())) { // Loop thru each jobs
               if (data.getName().equals("skill")) {
                  for (MapleData data2 : data) { // Loop thru each jobs
                     if (data2 != null) {
                        skillId = Integer.parseInt(data2.getName());
                        skills.put(skillId, loadFromData(skillId, data2));
                     }
                  }
               }
            }
         }
      }
   }

   private static Skill loadFromData(int id, MapleData data) {
      Skill ret = new Skill(id);
      boolean isBuff = false;
      int skillType = MapleDataTool.getInt("skillType", data, -1);
      String elem = MapleDataTool.getString("elemAttr", data, null);
      if (elem != null) {
         ret.setElement(Element.getFromChar(elem.charAt(0)));
      } else {
         ret.setElement(Element.NEUTRAL);
      }
      MapleData effect = data.getChildByPath("effect");
      if (skillType != -1) {
         if (skillType == 2) {
            isBuff = true;
         }
      } else {
         MapleData action_ = data.getChildByPath("action");
         boolean action = false;
         if (action_ == null) {
            if (data.getChildByPath("prepare/action") != null) {
               action = true;
            } else {
               action = switch (id) {
                  case Gunslinger.INVISIBLE_SHOT, Corsair.HYPNOTIZE -> true;
                  default -> action;
               };
            }
         } else {
            action = true;
         }
         ret.setAction(action);
         MapleData hit = data.getChildByPath("hit");
         MapleData ball = data.getChildByPath("ball");
         isBuff = effect != null && hit == null && ball == null;
         isBuff |= action_ != null && MapleDataTool.getString("0", action_, "").equals("alert2");
         switch (id) {
            case Hero.RUSH:
            case Paladin.RUSH:
            case DarkKnight.RUSH:
            case DragonKnight.SACRIFICE:
            case FirePoisonMagician.EXPLOSION:
            case FirePoisonMagician.POISON_MIST:
            case Cleric.HEAL:
            case Ranger.MORTAL_BLOW:
            case Sniper.MORTAL_BLOW:
            case Assassin.DRAIN:
            case Hermit.SHADOW_WEB:
            case Bandit.STEAL:
            case Shadower.SMOKE_SCREEN:
            case SuperGM.HEAL_PLUS_DISPEL:
            case Hero.MONSTER_MAGNET:
            case Paladin.MONSTER_MAGNET:
            case DarkKnight.MONSTER_MAGNET:
            case Evan.ICE_BREATH:
            case Evan.FIRE_BREATH:
            case Gunslinger.RECOIL_SHOT:
            case Marauder.ENERGY_DRAIN:
            case BlazeWizard.FLAME_GEAR:
            case NightWalker.SHADOW_WEB:
            case NightWalker.POISON_BOMB:
            case NightWalker.VAMPIRE:
            case ChiefBandit.CHAKRA:
            case Aran.COMBAT_STEP:
            case Evan.RECOVERY_AURA:
               isBuff = false;
               break;
            case Beginner.RECOVERY:
            case Beginner.NIMBLE_FEET:
            case Beginner.MONSTER_RIDER:
            case Beginner.ECHO_OF_HERO:
            case Beginner.MAP_CHAIR:
            case Warrior.IRON_BODY:
            case Fighter.AXE_BOOSTER:
            case Fighter.POWER_GUARD:
            case Fighter.RAGE:
            case Fighter.SWORD_BOOSTER:
            case Crusader.ARMOR_CRASH:
            case Crusader.COMBO:
            case Hero.ENRAGE:
            case Hero.HEROS_WILL:
            case Hero.MAPLE_WARRIOR:
            case Hero.STANCE:
            case Page.BW_BOOSTER:
            case Page.POWER_GUARD:
            case Page.SWORD_BOOSTER:
            case Page.THREATEN:
            case WhiteKnight.BW_FIRE_CHARGE:
            case WhiteKnight.BW_ICE_CHARGE:
            case WhiteKnight.BW_LIT_CHARGE:
            case WhiteKnight.MAGIC_CRASH:
            case WhiteKnight.SWORD_FIRE_CHARGE:
            case WhiteKnight.SWORD_ICE_CHARGE:
            case WhiteKnight.SWORD_LIT_CHARGE:
            case Paladin.BW_HOLY_CHARGE:
            case Paladin.HEROS_WILL:
            case Paladin.MAPLE_WARRIOR:
            case Paladin.STANCE:
            case Paladin.SWORD_HOLY_CHARGE:
            case Spearman.HYPER_BODY:
            case Spearman.IRON_WILL:
            case Spearman.POLEARM_BOOSTER:
            case Spearman.SPEAR_BOOSTER:
            case DragonKnight.DRAGON_BLOOD:
            case DragonKnight.POWER_CRASH:
            case DarkKnight.AURA_OF_BEHOLDER:
            case DarkKnight.BEHOLDER:
            case DarkKnight.HEROS_WILL:
            case DarkKnight.HEX_OF_BEHOLDER:
            case DarkKnight.MAPLE_WARRIOR:
            case DarkKnight.STANCE:
            case Magician.MAGIC_GUARD:
            case Magician.MAGIC_ARMOR:
            case FPWizard.MEDITATION:
            case FPWizard.SLOW:
            case FirePoisonMagician.SEAL:
            case FirePoisonMagician.SPELL_BOOSTER:
            case FirePoisonArchMage.HEROS_WILL:
            case FirePoisonArchMage.INFINITY:
            case FirePoisonArchMage.MANA_REFLECTION:
            case FirePoisonArchMage.MAPLE_WARRIOR:
            case ILWizard.MEDITATION:
            case IceLighteningMagician.SEAL:
            case ILWizard.SLOW:
            case IceLighteningMagician.SPELL_BOOSTER:
            case IceLighteningArchMagician.HEROS_WILL:
            case IceLighteningArchMagician.INFINITY:
            case IceLighteningArchMagician.MANA_REFLECTION:
            case IceLighteningArchMagician.MAPLE_WARRIOR:
            case Cleric.INVINCIBLE:
            case Cleric.BLESS:
            case Priest.DISPEL:
            case Priest.DOOM:
            case Priest.HOLY_SYMBOL:
            case Priest.MYSTIC_DOOR:
            case Bishop.HEROS_WILL:
            case Bishop.HOLY_SHIELD:
            case Bishop.INFINITY:
            case Bishop.MANA_REFLECTION:
            case Bishop.MAPLE_WARRIOR:
            case Archer.FOCUS:
            case Hunter.BOW_BOOSTER:
            case Hunter.SOUL_ARROW:
            case Ranger.PUPPET:
            case BowMaster.CONCENTRATE:
            case BowMaster.HEROS_WILL:
            case BowMaster.MAPLE_WARRIOR:
            case BowMaster.SHARP_EYES:
            case Crossbowman.CROSSBOW_BOOSTER:
            case Crossbowman.SOUL_ARROW:
            case Sniper.PUPPET:
            case Marksman.BLIND:
            case Marksman.HEROS_WILL:
            case Marksman.MAPLE_WARRIOR:
            case Marksman.SHARP_EYES:
            case Rogue.DARK_SIGHT:
            case Assassin.CLAW_BOOSTER:
            case Assassin.HASTE:
            case Hermit.MESO_UP:
            case Hermit.SHADOW_PARTNER:
            case NightLord.HEROS_WILL:
            case NightLord.MAPLE_WARRIOR:
            case NightLord.NINJA_AMBUSH:
            case NightLord.SHADOW_STARS:
            case Bandit.DAGGER_BOOSTER:
            case Bandit.HASTE:
            case ChiefBandit.MESO_GUARD:
            case ChiefBandit.PICKPOCKET:
            case Shadower.HEROS_WILL:
            case Shadower.MAPLE_WARRIOR:
            case Shadower.NINJA_AMBUSH:
            case Pirate.DASH:
            case Marauder.TRANSFORMATION:
            case Buccaneer.SUPER_TRANSFORMATION:
            case Corsair.BATTLE_SHIP:
            case GM.HIDE:
            case SuperGM.HASTE:
            case SuperGM.HOLY_SYMBOL:
            case SuperGM.BLESS:
            case SuperGM.HIDE:
            case SuperGM.HYPER_BODY:
            case Noblesse.BLESSING_OF_THE_FAIRY:
            case Noblesse.ECHO_OF_HERO:
            case Noblesse.MONSTER_RIDER:
            case Noblesse.NIMBLE_FEET:
            case Noblesse.RECOVERY:
            case Noblesse.MAP_CHAIR:
            case DawnWarrior.COMBO:
            case DawnWarrior.FINAL_ATTACK:
            case DawnWarrior.IRON_BODY:
            case DawnWarrior.RAGE:
            case DawnWarrior.SOUL:
            case DawnWarrior.SOUL_CHARGE:
            case DawnWarrior.SWORD_BOOSTER:
            case BlazeWizard.ELEMENTAL_RESET:
            case BlazeWizard.FLAME:
            case BlazeWizard.IFRIT:
            case BlazeWizard.MAGIC_ARMOR:
            case BlazeWizard.MAGIC_GUARD:
            case BlazeWizard.MEDITATION:
            case BlazeWizard.SEAL:
            case BlazeWizard.SLOW:
            case BlazeWizard.SPELL_BOOSTER:
            case WindArcher.BOW_BOOSTER:
            case WindArcher.EAGLE_EYE:
            case WindArcher.FINAL_ATTACK:
            case WindArcher.FOCUS:
            case WindArcher.PUPPET:
            case WindArcher.SOUL_ARROW:
            case WindArcher.STORM:
            case WindArcher.WIND_WALK:
            case NightWalker.CLAW_BOOSTER:
            case NightWalker.DARKNESS:
            case NightWalker.DARK_SIGHT:
            case NightWalker.HASTE:
            case NightWalker.SHADOW_PARTNER:
            case ThunderBreaker.DASH:
            case ThunderBreaker.ENERGY_CHARGE:
            case ThunderBreaker.ENERGY_DRAIN:
            case ThunderBreaker.KNUCKLER_BOOSTER:
            case ThunderBreaker.LIGHTNING:
            case ThunderBreaker.SPARK:
            case ThunderBreaker.LIGHTNING_CHARGE:
            case ThunderBreaker.SPEED_INFUSION:
            case ThunderBreaker.TRANSFORMATION:
            case Legend.BLESSING_OF_THE_FAIRY:
            case Legend.AGILE_BODY:
            case Legend.ECHO_OF_HERO:
            case Legend.RECOVERY:
            case Legend.MONSTER_RIDER:
            case Legend.MAP_CHAIR:
            case Aran.MAPLE_WARRIOR:
            case Aran.HEROS_WILL:
            case Aran.POLE_ARM_BOOSTER:
            case Aran.COMBO_DRAIN:
            case Aran.SNOW_CHARGE:
            case Aran.BODY_PRESSURE:
            case Aran.SMART_KNOCK_BACK:
            case Aran.COMBO_BARRIER:
            case Aran.COMBO_ABILITY:
            case Evan.BLESSING_OF_THE_FAIRY:
            case Evan.RECOVERY:
            case Evan.NIMBLE_FEET:
            case Evan.HEROS_WILL:
            case Evan.ECHO_OF_HERO:
            case Evan.MAGIC_BOOSTER:
            case Evan.MAGIC_GUARD:
            case Evan.ELEMENTAL_RESET:
            case Evan.MAPLE_WARRIOR:
            case Evan.MAGIC_RESISTANCE:
            case Evan.MAGIC_SHIELD:
            case Evan.SLOW:
               isBuff = true;
               break;
         }
      }

      for (MapleData level : data.getChildByPath("level")) {
         ret.addLevelEffect(StatEffectProcessor.getInstance().loadSkillEffectFromData(level, id, isBuff));
      }
      ret.setAnimationTime(0);
      if (effect != null) {
         for (MapleData effectEntry : effect) {
            ret.incAnimationTime(MapleDataTool.getIntConvert("delay", effectEntry, 0));
         }
      }
      return ret;
   }

   public static String getSkillName(int skillId) {
      MapleData data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/" + "String.wz")).getData("Skill.img");
      StringBuilder skill = new StringBuilder();
      skill.append(skillId);
      if (skill.length() == 4) {
         skill.delete(0, 4);
         skill.append("000").append(skillId);
      }
      if (data.getChildByPath(skill.toString()) != null) {
         for (MapleData skillData : data.getChildByPath(skill.toString()).getChildren()) {
            if (skillData.getName().equals("name")) {
               return MapleDataTool.getString(skillData, null);
            }
         }
      }

      return null;
   }
}
