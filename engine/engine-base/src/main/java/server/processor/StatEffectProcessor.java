package server.processor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleAbnormalStatus;
import client.SkillFactory;
import client.status.MonsterStatus;
import config.YamlConfig;
import constants.skills.Aran;
import constants.skills.Assassin;
import constants.skills.Bandit;
import constants.skills.Beginner;
import constants.skills.Bishop;
import constants.skills.BlazeWizard;
import constants.skills.BowMaster;
import constants.skills.Brawler;
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
import constants.skills.Marksman;
import constants.skills.NightLord;
import constants.skills.NightWalker;
import constants.skills.Noblesse;
import constants.skills.Outlaw;
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
import constants.skills.WhiteKnight;
import constants.skills.WindArcher;
import provider.MapleData;
import provider.MapleDataTool;
import server.CardItemUpStats;
import server.MapleStatEffect;
import tools.ArrayMap;
import tools.Pair;

public class StatEffectProcessor {
   private static StatEffectProcessor ourInstance = new StatEffectProcessor();

   public static StatEffectProcessor getInstance() {
      return ourInstance;
   }

   private StatEffectProcessor() {
   }

   public MapleStatEffect loadSkillEffectFromData(MapleData source, int skillId, boolean overtime) {
      return loadFromData(source, skillId, true, overtime);
   }

   public MapleStatEffect loadItemEffectFromData(MapleData source, int itemId) {
      return loadFromData(source, itemId, false, false);
   }

   private void addBuffStatPairToListIfNotZero(List<Pair<MapleBuffStat, Integer>> list, MapleBuffStat buffStat, Integer val) {
      if (val != 0) {
         list.add(new Pair<>(buffStat, val));
      }
   }

   private byte mapProtection(int sourceId) {
      if (sourceId == 2022001 || sourceId == 2022186) {
         return 1;   //elnath cold
      } else if (sourceId == 2022040) {
         return 2;   //aqua road underwater
      } else {
         return 0;
      }
   }

   public boolean isMapChair(int sourceId) {
      return sourceId == Beginner.MAP_CHAIR || sourceId == Noblesse.MAP_CHAIR || sourceId == Legend.MAP_CHAIR;
   }

   public boolean isDojoBuff(int sourceId) {
      return sourceId >= 2022359 && sourceId <= 2022421;
   }

   public boolean isHpMpRecovery(int sourceId) {
      return sourceId == 2022198 || sourceId == 2022337;
   }

   public boolean isPyramidBuff(int sourceId) {
      return sourceId >= 2022585 && sourceId <= 2022617;
   }

   public boolean isRateCoupon(int sourceId) {
      int itemType = sourceId / 1000;
      return itemType == 5211 || itemType == 5360;
   }

   public boolean isExpIncrease(int sourceId) {
      return sourceId >= 2022450 && sourceId <= 2022452;
   }

   public boolean isAriantShield(int sourceId) {
      return sourceId == 2022269;
   }

   public boolean isMonsterCard(int sourceId) {
      int itemType = sourceId / 10000;
      return itemType == 238;
   }

   public boolean isHerosWill(int skillId) {
      switch (skillId) {
         case Hero.HEROS_WILL:
         case Paladin.HEROS_WILL:
         case DarkKnight.HEROS_WILL:
         case FirePoisonArchMage.HEROS_WILL:
         case IceLighteningArchMagician.HEROS_WILL:
         case Bishop.HEROS_WILL:
         case BowMaster.HEROS_WILL:
         case Marksman.HEROS_WILL:
         case NightLord.HEROS_WILL:
         case Shadower.HEROS_WILL:
         case Buccaneer.PIRATES_RAGE:
         case Aran.HEROS_WILL:
            return true;

         default:
            return false;
      }
   }

   private MapleStatEffect loadFromData(MapleData source, int sourceId, boolean skill, boolean overTime) {
      MapleStatEffect ret = new MapleStatEffect();
      ret.setDuration(MapleDataTool.getIntConvert("time", source, -1));
      ret.setHp((short) MapleDataTool.getInt("hp", source, 0));
      ret.setHpR(MapleDataTool.getInt("hpR", source, 0) / 100.0);
      ret.setMp((short) MapleDataTool.getInt("mp", source, 0));
      ret.setMpR(MapleDataTool.getInt("mpR", source, 0) / 100.0);
      ret.setMpCon((short) MapleDataTool.getInt("mpCon", source, 0));
      ret.setHpCon((short) MapleDataTool.getInt("hpCon", source, 0));
      int iprop = MapleDataTool.getInt("prop", source, 100);
      ret.setProp(iprop / 100.0);

      ret.setCp(MapleDataTool.getInt("cp", source, 0));
      List<MapleAbnormalStatus> cure = new ArrayList<>(5);
      if (MapleDataTool.getInt("poison", source, 0) > 0) {
         cure.add(MapleAbnormalStatus.POISON);
      }
      if (MapleDataTool.getInt("seal", source, 0) > 0) {
         cure.add(MapleAbnormalStatus.SEAL);
      }
      if (MapleDataTool.getInt("darkness", source, 0) > 0) {
         cure.add(MapleAbnormalStatus.DARKNESS);
      }
      if (MapleDataTool.getInt("weakness", source, 0) > 0) {
         cure.add(MapleAbnormalStatus.WEAKEN);
         cure.add(MapleAbnormalStatus.SLOW);
      }
      if (MapleDataTool.getInt("curse", source, 0) > 0) {
         cure.add(MapleAbnormalStatus.CURSE);
      }
      ret.setCureAbnormalStatuses(cure);
      ret.setNuffSkill(MapleDataTool.getInt("nuffSkill", source, 0));
      ret.setMobCount(MapleDataTool.getInt("mobCount", source, 1));
      ret.setCoolDown(MapleDataTool.getInt("cooltime", source, 0));
      ret.setMorphId(MapleDataTool.getInt("morph", source, 0));
      ret.setGhost(MapleDataTool.getInt("ghost", source, 0));
      ret.setFatigue(MapleDataTool.getInt("incFatigue", source, 0));
      ret.setRepeatEffect(MapleDataTool.getInt("repeatEffect", source, 0) > 0);

      MapleData mdd = source.getChildByPath("0");
      if (mdd != null && mdd.getChildren().size() > 0) {
         ret.setMobSkill((short) MapleDataTool.getInt("mobSkill", mdd, 0));
         ret.setMobSkillLevel((short) MapleDataTool.getInt("level", mdd, 0));
         ret.setTarget(MapleDataTool.getInt("target", mdd, 0));
      } else {
         ret.setMobSkill((short) 0);
         ret.setMobSkillLevel((short) 0);
         ret.setTarget(0);
      }

      MapleData mdds = source.getChildByPath("mob");
      if (mdds != null) {
         if (mdds.getChildren() != null && mdds.getChildren().size() > 0) {
            ret.setMob(MapleDataTool.getInt("mob", mdds, 0));
         }
      }
      ret.setSourceId(sourceId);
      ret.setSkill(skill);
      if (!ret.isSkill() && ret.getDuration() > -1) {
         ret.setOverTime(true);
      } else {
         ret.setDuration(ret.getDuration() * 1000); // items have their times stored in ms, of course
         ret.setOverTime(overTime);
      }

      ArrayList<Pair<MapleBuffStat, Integer>> statups = new ArrayList<>();
      ret.setWeaponAttack((short) MapleDataTool.getInt("pad", source, 0));
      ret.setWeaponDefense((short) MapleDataTool.getInt("pdd", source, 0));
      ret.setMagicAttack((short) MapleDataTool.getInt("mad", source, 0));
      ret.setMagicDefense((short) MapleDataTool.getInt("mdd", source, 0));
      ret.setAcc((short) MapleDataTool.getIntConvert("acc", source, 0));
      ret.setAvoid((short) MapleDataTool.getInt("eva", source, 0));

      ret.setSpeed((short) MapleDataTool.getInt("speed", source, 0));
      ret.setJump((short) MapleDataTool.getInt("jump", source, 0));

      ret.setBarrier(MapleDataTool.getInt("barrier", source, 0));
      addBuffStatPairToListIfNotZero(statups, MapleBuffStat.AURA, ret.getBarrier());

      ret.setMapProtection(mapProtection(sourceId));
      addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MAP_PROTECTION, (int) ret.getMapProtection());

      if (ret.isOverTime() && ret.getSummonMovementType() == null) {
         if (!skill) {
            if (isPyramidBuff(sourceId)) {
               ret.setBerserk(MapleDataTool.getInt("berserk", source, 0));
               ret.setBooster(MapleDataTool.getInt("booster", source, 0));

               addBuffStatPairToListIfNotZero(statups, MapleBuffStat.BERSERK, ret.getBerserk());
               addBuffStatPairToListIfNotZero(statups, MapleBuffStat.BOOSTER, ret.getBooster());

            } else if (isDojoBuff(sourceId) || isHpMpRecovery(sourceId)) {
               ret.setMhpR((byte) MapleDataTool.getInt("mhpR", source, 0));
               ret.setMhpRRate((short) (MapleDataTool.getInt("mhpRRate", source, 0) * 100));
               ret.setMmpR((byte) MapleDataTool.getInt("mmpR", source, 0));
               ret.setMmpRRate((short) (MapleDataTool.getInt("mmpRRate", source, 0) * 100));

               addBuffStatPairToListIfNotZero(statups, MapleBuffStat.HP_RECOVERY, (int) ret.getMhpR());
               addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MP_RECOVERY, (int) ret.getMmpR());

            } else if (isRateCoupon(sourceId)) {
               switch (MapleDataTool.getInt("expR", source, 0)) {
                  case 1:
                     addBuffStatPairToListIfNotZero(statups, MapleBuffStat.COUPON_EXP1, 1);
                     break;

                  case 2:
                     addBuffStatPairToListIfNotZero(statups, MapleBuffStat.COUPON_EXP2, 1);
                     break;

                  case 3:
                     addBuffStatPairToListIfNotZero(statups, MapleBuffStat.COUPON_EXP3, 1);
                     break;

                  case 4:
                     addBuffStatPairToListIfNotZero(statups, MapleBuffStat.COUPON_EXP4, 1);
                     break;
               }

               switch (MapleDataTool.getInt("drpR", source, 0)) {
                  case 1:
                     addBuffStatPairToListIfNotZero(statups, MapleBuffStat.COUPON_DRP1, 1);
                     break;

                  case 2:
                     addBuffStatPairToListIfNotZero(statups, MapleBuffStat.COUPON_DRP2, 1);
                     break;

                  case 3:
                     addBuffStatPairToListIfNotZero(statups, MapleBuffStat.COUPON_DRP3, 1);
                     break;
               }
            } else if (isMonsterCard(sourceId)) {
               int prob = 0, itemUpCode = Integer.MAX_VALUE;
               List<Pair<Integer, Integer>> areas = null;
               boolean inParty = false;

               MapleData con = source.getChildByPath("con");
               if (con != null) {
                  areas = new ArrayList<>(3);

                  for (MapleData conData : con.getChildren()) {
                     int type = MapleDataTool.getInt("type", conData, -1);

                     if (type == 0) {
                        int startMap = MapleDataTool.getInt("sMap", conData, 0);
                        int endMap = MapleDataTool.getInt("eMap", conData, 0);

                        areas.add(new Pair<>(startMap, endMap));
                     } else if (type == 2) {
                        inParty = true;
                     }
                  }

                  if (areas.isEmpty()) {
                     areas = null;
                  }
               }

               if (MapleDataTool.getInt("mesoupbyitem", source, 0) != 0) {
                  addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MESO_UP_BY_ITEM, 4);
                  prob = MapleDataTool.getInt("prob", source, 1);
               }

               int itemUpType = MapleDataTool.getInt("itemupbyitem", source, 0);
               if (itemUpType != 0) {
                  addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ITEM_UP_BY_ITEM, 4);
                  prob = MapleDataTool.getInt("prob", source, 1);

                  switch (itemUpType) {
                     case 2:
                        itemUpCode = MapleDataTool.getInt("itemCode", source, 1);
                        break;

                     case 3:
                        itemUpCode = MapleDataTool.getInt("itemRange", source, 1);    // 3 digits
                        break;
                  }
               }

               if (MapleDataTool.getInt("respectPimmune", source, 0) != 0) {
                  addBuffStatPairToListIfNotZero(statups, MapleBuffStat.RESPECT_PLAYER_IMMUNE, 4);
               }

               if (MapleDataTool.getInt("respectMimmune", source, 0) != 0) {
                  addBuffStatPairToListIfNotZero(statups, MapleBuffStat.RESPECT_MONSTER_IMMUNE, 4);
               }

               if (MapleDataTool.getString("defenseAtt", source, null) != null) {
                  addBuffStatPairToListIfNotZero(statups, MapleBuffStat.DEFENSE_ATT, 4);
               }

               if (MapleDataTool.getString("defenseState", source, null) != null) {
                  addBuffStatPairToListIfNotZero(statups, MapleBuffStat.DEFENSE_STATE, 4);
               }

               int thaw = MapleDataTool.getInt("thaw", source, 0);
               if (thaw != 0) {
                  addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MAP_PROTECTION, thaw > 0 ? 1 : 2);
               }

               ret.setCardStats(new CardItemUpStats(itemUpCode, prob, areas, inParty));
            } else if (isExpIncrease(sourceId)) {
               addBuffStatPairToListIfNotZero(statups, MapleBuffStat.EXP_INCREASE, MapleDataTool.getInt("expinc", source, 0));
            }
         } else {
            if (isMapChair(sourceId)) {
               addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MAP_CHAIR, 1);
            } else if ((sourceId == Beginner.NIMBLE_FEET || sourceId == Noblesse.NIMBLE_FEET || sourceId == Evan.NIMBLE_FEET || sourceId == Legend.AGILE_BODY) && YamlConfig.config.server.USE_ULTRA_NIMBLE_FEET) {
               ret.setJump((short) (ret.getSpeed() * 4));
               ret.setSpeed((short) (ret.getSpeed() * 15));
            }
         }

         addBuffStatPairToListIfNotZero(statups, MapleBuffStat.WEAPON_ATTACK, (int) ret.getWeaponAttack());
         addBuffStatPairToListIfNotZero(statups, MapleBuffStat.WEAPON_DEFENSE, (int) ret.getWeaponDefense());
         addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MAGIC_ATTACK, (int) ret.getMagicAttack());
         addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MAGIC_DEFENSE, (int) ret.getMagicDefense());
         addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ACC, (int) ret.getAcc());
         addBuffStatPairToListIfNotZero(statups, MapleBuffStat.AVOID, (int) ret.getAvoid());
         addBuffStatPairToListIfNotZero(statups, MapleBuffStat.SPEED, (int) ret.getSpeed());
         addBuffStatPairToListIfNotZero(statups, MapleBuffStat.JUMP, (int) ret.getJump());
      }

      MapleData ltd = source.getChildByPath("lt");
      if (ltd != null) {
         ret.setLt((Point) ltd.getData());
         ret.setRb((Point) source.getChildByPath("rb").getData());

         if (YamlConfig.config.server.USE_MAXRANGE_ECHO_OF_HERO && (sourceId == Beginner.ECHO_OF_HERO || sourceId == Noblesse.ECHO_OF_HERO || sourceId == Legend.ECHO_OF_HERO || sourceId == Evan.ECHO_OF_HERO)) {
            ret.setLt(new Point(Integer.MIN_VALUE, Integer.MIN_VALUE));
            ret.setRb(new Point(Integer.MAX_VALUE, Integer.MAX_VALUE));
         }
      }

      int x = MapleDataTool.getInt("x", source, 0);

      if ((sourceId == Beginner.RECOVERY || sourceId == Noblesse.RECOVERY || sourceId == Evan.RECOVERY || sourceId == Legend.RECOVERY) && YamlConfig.config.server.USE_ULTRA_RECOVERY) {
         x *= 10;
      }
      ret.setX(x);
      ret.setY(MapleDataTool.getInt("y", source, 0));

      ret.setDamage(MapleDataTool.getIntConvert("damage", source, 100));
      ret.setFixDamage(MapleDataTool.getIntConvert("fixdamage", source, -1));
      ret.setAttackCount(MapleDataTool.getIntConvert("attackCount", source, 1));
      ret.setBulletCount((short) MapleDataTool.getIntConvert("bulletCount", source, 1));
      ret.setBulletConsume((short) MapleDataTool.getIntConvert("bulletConsume", source, 0));
      ret.setMoneyCon(MapleDataTool.getIntConvert("moneyCon", source, 0));
      ret.setItemCon(MapleDataTool.getInt("itemCon", source, 0));
      ret.setItemConNo(MapleDataTool.getInt("itemConNo", source, 0));
      ret.setMoveTo(MapleDataTool.getInt("moveTo", source, -1));
      Map<MonsterStatus, Integer> monsterStatus = new ArrayMap<>();
      if (skill) {
         switch (sourceId) {
            // BEGINNER
            case Beginner.RECOVERY:
            case Noblesse.RECOVERY:
            case Legend.RECOVERY:
            case Evan.RECOVERY:
               statups.add(new Pair<>(MapleBuffStat.RECOVERY, x));
               break;
            case Beginner.ECHO_OF_HERO:
            case Noblesse.ECHO_OF_HERO:
            case Legend.ECHO_OF_HERO:
            case Evan.ECHO_OF_HERO:
               statups.add(new Pair<>(MapleBuffStat.ECHO_OF_HERO, ret.getX()));
               break;
            case Beginner.MONSTER_RIDER:
            case Noblesse.MONSTER_RIDER:
            case Legend.MONSTER_RIDER:
            case Corsair.BATTLE_SHIP:
            case Beginner.SPACESHIP:
            case Noblesse.SPACESHIP:
            case Beginner.YETI_MOUNT1:
            case Beginner.YETI_MOUNT2:
            case Noblesse.YETI_MOUNT1:
            case Noblesse.YETI_MOUNT2:
            case Legend.YETI_MOUNT1:
            case Legend.YETI_MOUNT2:
            case Beginner.WITCH_BROOMSTICK:
            case Noblesse.WITCH_BROOMSTICK:
            case Legend.WITCH_BROOMSTICK:
            case Beginner.BALROG_MOUNT:
            case Noblesse.BALROG_MOUNT:
            case Legend.BALROG_MOUNT:
               statups.add(new Pair<>(MapleBuffStat.MONSTER_RIDING, sourceId));
               break;
            case Beginner.INVINCIBLE_BARRIER:
            case Noblesse.INVINCIBLE_BARRIER:
            case Legend.INVISIBLE_BARRIER:
            case Evan.INVINCIBLE_BARRIER:
               statups.add(new Pair<>(MapleBuffStat.DIVINE_BODY, 1));
               break;
            case Fighter.POWER_GUARD:
            case Page.POWER_GUARD:
               statups.add(new Pair<>(MapleBuffStat.POWER_GUARD, x));
               break;
            case Spearman.HYPER_BODY:
            case GM.HYPER_BODY:
            case SuperGM.HYPER_BODY:
               statups.add(new Pair<>(MapleBuffStat.HYPER_BODY_HP, x));
               statups.add(new Pair<>(MapleBuffStat.HYPER_BODY_MP, ret.getY()));
               break;
            case Crusader.COMBO:
            case DawnWarrior.COMBO:
               statups.add(new Pair<>(MapleBuffStat.COMBO, 1));
               break;
            case WhiteKnight.BW_FIRE_CHARGE:
            case WhiteKnight.BW_ICE_CHARGE:
            case WhiteKnight.BW_LIT_CHARGE:
            case WhiteKnight.SWORD_FIRE_CHARGE:
            case WhiteKnight.SWORD_ICE_CHARGE:
            case WhiteKnight.SWORD_LIT_CHARGE:
            case Paladin.BW_HOLY_CHARGE:
            case Paladin.SWORD_HOLY_CHARGE:
            case DawnWarrior.SOUL_CHARGE:
            case ThunderBreaker.LIGHTNING_CHARGE:
               statups.add(new Pair<>(MapleBuffStat.WK_CHARGE, x));
               break;
            case DragonKnight.DRAGON_BLOOD:
               statups.add(new Pair<>(MapleBuffStat.DRAGON_BLOOD, ret.getX()));
               break;
            case Hero.STANCE:
            case Paladin.STANCE:
            case DarkKnight.STANCE:
            case Aran.FREEZE_STANDING:
               statups.add(new Pair<>(MapleBuffStat.STANCE, iprop));
               break;
            case DawnWarrior.FINAL_ATTACK:
            case WindArcher.FINAL_ATTACK:
               statups.add(new Pair<>(MapleBuffStat.FINAL_ATTACK, x));
               break;
            // MAGICIAN
            case Magician.MAGIC_GUARD:
            case BlazeWizard.MAGIC_GUARD:
            case Evan.MAGIC_GUARD:
               statups.add(new Pair<>(MapleBuffStat.MAGIC_GUARD, x));
               break;
            case Cleric.INVINCIBLE:
               statups.add(new Pair<>(MapleBuffStat.INVINCIBLE, x));
               break;
            case Priest.HOLY_SYMBOL:
            case SuperGM.HOLY_SYMBOL:
               statups.add(new Pair<>(MapleBuffStat.HOLY_SYMBOL, x));
               break;
            case FirePoisonArchMage.INFINITY:
            case IceLighteningArchMagician.INFINITY:
            case Bishop.INFINITY:
               statups.add(new Pair<>(MapleBuffStat.INFINITY, x));
               break;
            case FirePoisonArchMage.MANA_REFLECTION:
            case IceLighteningArchMagician.MANA_REFLECTION:
            case Bishop.MANA_REFLECTION:
               statups.add(new Pair<>(MapleBuffStat.MANA_REFLECTION, 1));
               break;
            case Bishop.HOLY_SHIELD:
               statups.add(new Pair<>(MapleBuffStat.HOLY_SHIELD, x));
               break;
            case BlazeWizard.ELEMENTAL_RESET:
            case Evan.ELEMENTAL_RESET:
               statups.add(new Pair<>(MapleBuffStat.ELEMENTAL_RESET, x));
               break;
            case Evan.MAGIC_SHIELD:
               statups.add(new Pair<>(MapleBuffStat.MAGIC_SHIELD, x));
               break;
            case Evan.MAGIC_RESISTANCE:
               statups.add(new Pair<>(MapleBuffStat.MAGIC_RESISTANCE, x));
               break;
            case Evan.SLOW:
               statups.add(new Pair<>(MapleBuffStat.SLOW, x));
               // BOWMAN
            case Priest.MYSTIC_DOOR:
            case Hunter.SOUL_ARROW:
            case Crossbowman.SOUL_ARROW:
            case WindArcher.SOUL_ARROW:
               statups.add(new Pair<>(MapleBuffStat.SOUL_ARROW, x));
               break;
            case Ranger.PUPPET:
            case Sniper.PUPPET:
            case WindArcher.PUPPET:
            case Outlaw.OCTOPUS:
            case Corsair.WRATH_OF_THE_OCTOPI:
               statups.add(new Pair<>(MapleBuffStat.PUPPET, 1));
               break;
            case BowMaster.CONCENTRATE:
               statups.add(new Pair<>(MapleBuffStat.CONCENTRATE, x));
               break;
            case BowMaster.HAMSTRING:
               statups.add(new Pair<>(MapleBuffStat.HAMSTRING, x));
               monsterStatus.put(MonsterStatus.SPEED, x);
               break;
            case Marksman.BLIND:
               statups.add(new Pair<>(MapleBuffStat.BLIND, x));
               monsterStatus.put(MonsterStatus.ACC, x);
               break;
            case BowMaster.SHARP_EYES:
            case Marksman.SHARP_EYES:
               statups.add(new Pair<>(MapleBuffStat.SHARP_EYES, ret.getX() << 8 | ret.getY()));
               break;
            case WindArcher.WIND_WALK:
               statups.add(new Pair<>(MapleBuffStat.WIND_WALK, x));
               break;
            case Rogue.DARK_SIGHT:
            case NightWalker.DARK_SIGHT:
               statups.add(new Pair<>(MapleBuffStat.DARK_SIGHT, x));
               break;
            case Hermit.MESO_UP:
               statups.add(new Pair<>(MapleBuffStat.MESOUP, x));
               break;
            case Hermit.SHADOW_PARTNER:
            case NightWalker.SHADOW_PARTNER:
               statups.add(new Pair<>(MapleBuffStat.SHADOW_PARTNER, x));
               break;
            case ChiefBandit.MESO_GUARD:
               statups.add(new Pair<>(MapleBuffStat.MESO_GUARD, x));
               break;
            case ChiefBandit.PICKPOCKET:
               statups.add(new Pair<>(MapleBuffStat.PICKPOCKET, x));
               break;
            case NightLord.SHADOW_STARS:
               statups.add(new Pair<>(MapleBuffStat.SHADOW_CLAW, 0));
               break;
            // PIRATE
            case Pirate.DASH:
            case ThunderBreaker.DASH:
            case Beginner.SPACE_DASH:
            case Noblesse.SPACE_DASH:
               statups.add(new Pair<>(MapleBuffStat.DASH2, ret.getX()));
               statups.add(new Pair<>(MapleBuffStat.DASH, ret.getY()));
               break;
            case Corsair.SPEED_INFUSION:
            case Buccaneer.SPEED_INFUSION:
            case ThunderBreaker.SPEED_INFUSION:
               statups.add(new Pair<>(MapleBuffStat.SPEED_INFUSION, x));
               break;
            case Outlaw.HOMING_BEACON:
            case Corsair.BULLS_EYE:
               statups.add(new Pair<>(MapleBuffStat.HOMING_BEACON, x));
               break;
            case ThunderBreaker.SPARK:
               statups.add(new Pair<>(MapleBuffStat.SPARK, x));
               break;
            // MULTIPLE
            case Aran.POLE_ARM_BOOSTER:
            case Fighter.AXE_BOOSTER:
            case Fighter.SWORD_BOOSTER:
            case Page.BW_BOOSTER:
            case Page.SWORD_BOOSTER:
            case Spearman.POLEARM_BOOSTER:
            case Spearman.SPEAR_BOOSTER:
            case Hunter.BOW_BOOSTER:
            case Crossbowman.CROSSBOW_BOOSTER:
            case Assassin.CLAW_BOOSTER:
            case Bandit.DAGGER_BOOSTER:
            case FirePoisonMagician.SPELL_BOOSTER:
            case IceLighteningMagician.SPELL_BOOSTER:
            case Brawler.KNUCKLER_BOOSTER:
            case Gunslinger.GUN_BOOSTER:
            case DawnWarrior.SWORD_BOOSTER:
            case BlazeWizard.SPELL_BOOSTER:
            case WindArcher.BOW_BOOSTER:
            case NightWalker.CLAW_BOOSTER:
            case ThunderBreaker.KNUCKLER_BOOSTER:
            case Evan.MAGIC_BOOSTER:
            case Beginner.POWER_EXPLOSION:
            case Noblesse.POWER_EXPLOSION:
            case Legend.POWER_EXPLOSION:
               statups.add(new Pair<>(MapleBuffStat.BOOSTER, x));
               break;
            case Hero.MAPLE_WARRIOR:
            case Paladin.MAPLE_WARRIOR:
            case DarkKnight.MAPLE_WARRIOR:
            case FirePoisonArchMage.MAPLE_WARRIOR:
            case IceLighteningArchMagician.MAPLE_WARRIOR:
            case Bishop.MAPLE_WARRIOR:
            case BowMaster.MAPLE_WARRIOR:
            case Marksman.MAPLE_WARRIOR:
            case NightLord.MAPLE_WARRIOR:
            case Shadower.MAPLE_WARRIOR:
            case Corsair.MAPLE_WARRIOR:
            case Buccaneer.MAPLE_WARRIOR:
            case Aran.MAPLE_WARRIOR:
            case Evan.MAPLE_WARRIOR:
               statups.add(new Pair<>(MapleBuffStat.MAPLE_WARRIOR, ret.getX()));
               break;
            // SUMMON
            case Ranger.SILVER_HAWK:
            case Sniper.GOLDEN_EAGLE:
               statups.add(new Pair<>(MapleBuffStat.SUMMON, 1));
               monsterStatus.put(MonsterStatus.STUN, 1);
               break;
            case FirePoisonArchMage.ELQUINES:
            case Marksman.FROST_PREY:
               statups.add(new Pair<>(MapleBuffStat.SUMMON, 1));
               monsterStatus.put(MonsterStatus.FREEZE, 1);
               break;
            case Priest.SUMMON_DRAGON:
            case BowMaster.PHOENIX:
            case IceLighteningArchMagician.IFRIT:
            case Bishop.BAHAMUT:
            case DarkKnight.BEHOLDER:
            case Outlaw.GAVIOTA:
            case DawnWarrior.SOUL:
            case BlazeWizard.FLAME:
            case WindArcher.STORM:
            case NightWalker.DARKNESS:
            case ThunderBreaker.LIGHTNING:
            case BlazeWizard.IFRIT:
               statups.add(new Pair<>(MapleBuffStat.SUMMON, 1));
               break;
            // ----------------------------- MONSTER STATUS ---------------------------------- //
            case Crusader.ARMOR_CRASH:
            case DragonKnight.POWER_CRASH:
            case WhiteKnight.MAGIC_CRASH:
               monsterStatus.put(MonsterStatus.SEAL_SKILL, 1);
               break;
            case Rogue.DISORDER:
               monsterStatus.put(MonsterStatus.WEAPON_ATTACK, ret.getX());
               monsterStatus.put(MonsterStatus.WEAPON_DEFENSE, ret.getY());
               break;
            case Corsair.HYPNOTIZE:
               monsterStatus.put(MonsterStatus.INERT_MOB, 1);
               break;
            case NightLord.NINJA_AMBUSH:
            case Shadower.NINJA_AMBUSH:
               monsterStatus.put(MonsterStatus.NINJA_AMBUSH, ret.getDamage());
               break;
            case Page.THREATEN:
               monsterStatus.put(MonsterStatus.WEAPON_ATTACK, ret.getX());
               monsterStatus.put(MonsterStatus.WEAPON_DEFENSE, ret.getY());
               break;
            case DragonKnight.DRAGON_ROAR:
               ret.setHpR(-x / 100.0);
               monsterStatus.put(MonsterStatus.STUN, 1);
               break;
            case Crusader.AXE_COMA:
            case Crusader.SWORD_COMA:
            case Crusader.SHOUT:
            case WhiteKnight.CHARGE_BLOW:
            case Hunter.ARROW_BOMB:
            case ChiefBandit.ASSAULTER:
            case Shadower.BOOMERANG_STEP:
            case Brawler.BACK_SPIN_BLOW:
            case Brawler.DOUBLE_UPPERCUT:
            case Buccaneer.DEMOLITION:
            case Buccaneer.SNATCH:
            case Buccaneer.BARRAGE:
            case Gunslinger.BLANK_SHOT:
            case DawnWarrior.COMA:
            case ThunderBreaker.BARRAGE:
            case Aran.ROLLING_SPIN:
            case Evan.FIRE_BREATH:
            case Evan.BLAZE:
               monsterStatus.put(MonsterStatus.STUN, 1);
               break;
            case NightLord.TAUNT:
            case Shadower.TAUNT:
               monsterStatus.put(MonsterStatus.SHOWDOWN, ret.getX());
               monsterStatus.put(MonsterStatus.MAGIC_DEFENSE, ret.getX());
               monsterStatus.put(MonsterStatus.WEAPON_DEFENSE, ret.getX());
               break;
            case ILWizard.COLD_BEAM:
            case IceLighteningMagician.ICE_STRIKE:
            case IceLighteningArchMagician.BLIZZARD:
            case IceLighteningMagician.ELEMENT_COMPOSITION:
            case Sniper.BLIZZARD:
            case Outlaw.ICE_SPLITTER:
            case FirePoisonArchMage.PARALYZE:
            case Aran.COMBO_TEMPEST:
            case Evan.ICE_BREATH:
               monsterStatus.put(MonsterStatus.FREEZE, 1);
               ret.setDuration(ret.getDuration() * 2); // freezing skills are a little strange
               break;
            case FPWizard.SLOW:
            case ILWizard.SLOW:
            case BlazeWizard.SLOW:
               monsterStatus.put(MonsterStatus.SPEED, ret.getX());
               break;
            case FPWizard.POISON_BREATH:
            case FirePoisonMagician.ELEMENT_COMPOSITION:
               monsterStatus.put(MonsterStatus.POISON, 1);
               break;
            case Priest.DOOM:
               monsterStatus.put(MonsterStatus.DOOM, 1);
               break;
            case IceLighteningMagician.SEAL:
            case FirePoisonMagician.SEAL:
            case BlazeWizard.SEAL:
               monsterStatus.put(MonsterStatus.SEAL, 1);
               break;
            case Hermit.SHADOW_WEB: // shadow web
            case NightWalker.SHADOW_WEB:
               monsterStatus.put(MonsterStatus.SHADOW_WEB, 1);
               break;
            case FirePoisonArchMage.FIRE_DEMON:
            case IceLighteningArchMagician.ICE_DEMON:
               monsterStatus.put(MonsterStatus.POISON, 1);
               monsterStatus.put(MonsterStatus.FREEZE, 1);
               break;
            case Evan.PHANTOM_IMPRINT:
               monsterStatus.put(MonsterStatus.PHANTOM_IMPRINT, x);
            case Aran.COMBO_ABILITY:
               statups.add(new Pair<>(MapleBuffStat.ARAN_COMBO, 100));
               break;
            case Aran.COMBO_BARRIER:
               statups.add(new Pair<>(MapleBuffStat.COMBO_BARRIER, ret.getX()));
               break;
            case Aran.COMBO_DRAIN:
               statups.add(new Pair<>(MapleBuffStat.COMBO_DRAIN, ret.getX()));
               break;
            case Aran.SMART_KNOCK_BACK:
               statups.add(new Pair<>(MapleBuffStat.SMART_KNOCK_BACK, ret.getX()));
               break;
            case Aran.BODY_PRESSURE:
               statups.add(new Pair<>(MapleBuffStat.BODY_PRESSURE, ret.getX()));
               break;
            case Aran.SNOW_CHARGE:
               statups.add(new Pair<>(MapleBuffStat.WK_CHARGE, ret.getDuration()));
               break;
            default:
               break;
         }
      }
      if (ret.isMorph()) {
         statups.add(new Pair<>(MapleBuffStat.MORPH, ret.getMorph()));
      }
      if (ret.getGhost() > 0 && !skill) {
         statups.add(new Pair<>(MapleBuffStat.GHOST_MORPH, ret.getGhost()));
      }
      ret.setMonsterStatus(monsterStatus);
      statups.trimToSize();
      ret.setStatups(statups);
      return ret;
   }


   public MapleStatEffect getAlchemistEffect(MapleCharacter chr) {
      int id = Hermit.ALCHEMIST;
      if (chr.isCygnus()) {
         id = NightWalker.ALCHEMIST;
      }

      return SkillFactory.applyForSkill(chr, id, (skill, skillLevel) -> {
         if (skillLevel != 0) {
            return skill.getEffect(skillLevel);
         }
         return null;
      }, null);
   }
}
