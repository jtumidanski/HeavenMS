package net.server.channel.packet.reader;

import java.util.ArrayList;
import java.util.List;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleJob;
import client.Skill;
import client.SkillFactory;
import client.autoban.AutobanFactory;
import constants.game.GameConstants;
import constants.skills.Aran;
import constants.skills.Beginner;
import constants.skills.Bishop;
import constants.skills.BlazeWizard;
import constants.skills.Bowmaster;
import constants.skills.Brawler;
import constants.skills.Buccaneer;
import constants.skills.ChiefBandit;
import constants.skills.Cleric;
import constants.skills.Corsair;
import constants.skills.Crusader;
import constants.skills.DawnWarrior;
import constants.skills.DragonKnight;
import constants.skills.Evan;
import constants.skills.FPArchMage;
import constants.skills.FPMage;
import constants.skills.FPWizard;
import constants.skills.Gunslinger;
import constants.skills.Hermit;
import constants.skills.Hero;
import constants.skills.ILArchMage;
import constants.skills.ILMage;
import constants.skills.Legend;
import constants.skills.Marauder;
import constants.skills.Marksman;
import constants.skills.NightLord;
import constants.skills.NightWalker;
import constants.skills.Noblesse;
import constants.skills.Paladin;
import constants.skills.Shadower;
import constants.skills.ThunderBreaker;
import constants.skills.WhiteKnight;
import constants.skills.WindArcher;
import net.server.PacketReader;
import net.server.channel.handlers.AbstractDealDamageHandler;
import net.server.channel.packet.AttackPacket;
import server.MapleStatEffect;
import server.life.Element;
import server.life.ElementalEffectiveness;
import server.life.MapleMonster;
import tools.Randomizer;
import tools.data.input.SeekableLittleEndianAccessor;

public class DamageReader implements PacketReader<AttackPacket> {


   public AttackPacket read(SeekableLittleEndianAccessor accessor, MapleCharacter chr, boolean ranged, boolean magic) {
      //2C 00 00 01 91 A1 12 00 A5 57 62 FC E2 75 99 10 00 47 80 01 04 01 C6 CC 02 DD FF 5F 00
      AttackPacket ret = new AttackPacket();
      accessor.readByte();
      ret.numAttackedAndDamage_$eq(accessor.readByte());
      ret.numAttacked_$eq((ret.numAttackedAndDamage() >>> 4) & 0xF);
      ret.numDamage_$eq(ret.numAttackedAndDamage() & 0xF);
      ret.clearAttacks();
      ret.skill_$eq(accessor.readInt());
      ret.ranged_$eq(ranged);
      ret.magic_$eq(magic);

      if (ret.skill() > 0) {
         ret.skillLevel_$eq(chr.getSkillLevel(ret.skill()));
         if (ret.skillLevel() == 0 && GameConstants.isPqSkillMap(chr.getMapId()) && GameConstants.isPqSkill(ret.skill())) {
            ret.skillLevel_$eq(1);
         }
      }

      if (ret.skill() == Evan.ICE_BREATH || ret.skill() == Evan.FIRE_BREATH || ret.skill() == FPArchMage.BIG_BANG || ret.skill() == ILArchMage.BIG_BANG || ret.skill() == Bishop.BIG_BANG || ret.skill() == Gunslinger.GRENADE || ret.skill() == Brawler.CORKSCREW_BLOW || ret.skill() == ThunderBreaker.CORKSCREW_BLOW || ret.skill() == NightWalker.POISON_BOMB) {
         ret.charge_$eq(accessor.readInt());
      } else {
         ret.charge_$eq(0);
      }

      accessor.skip(8);
      ret.display_$eq(accessor.readByte());
      ret.direction_$eq(accessor.readByte());
      ret.stance_$eq(accessor.readByte());
      if (ret.skill() == ChiefBandit.MESO_EXPLOSION) {
         if (ret.numAttackedAndDamage() == 0) {
            accessor.skip(10);
            int bullets = accessor.readByte();
            for (int j = 0; j < bullets; j++) {
               int mesoid = accessor.readInt();
               accessor.skip(1);
               ret.addDamage(mesoid, null);
            }
            return ret;
         } else {
            accessor.skip(6);
         }
         for (int i = 0; i < ret.numAttacked() + 1; i++) {
            int oid = accessor.readInt();
            if (i < ret.numAttacked()) {
               accessor.skip(12);
               int bullets = accessor.readByte();
               List<Integer> allDamageNumbers = new ArrayList<>();
               for (int j = 0; j < bullets; j++) {
                  int damage = accessor.readInt();
                  allDamageNumbers.add(damage);
               }
               ret.addDamage(oid, allDamageNumbers);
               accessor.skip(4);
            } else {
               int bullets = accessor.readByte();
               for (int j = 0; j < bullets; j++) {
                  int mesoid = accessor.readInt();
                  accessor.skip(1);
                  ret.addDamage(mesoid, null);
               }
            }
         }
         return ret;
      }
      if (ranged) {
         accessor.readByte();
         ret.speed_$eq(accessor.readByte());
         accessor.readByte();
         ret.rangedDirection_$eq(accessor.readByte());
         accessor.skip(7);
         if (ret.skill() == Bowmaster.HURRICANE || ret.skill() == Marksman.PIERCING_ARROW || ret.skill() == Corsair.RAPID_FIRE || ret.skill() == WindArcher.HURRICANE) {
            accessor.skip(4);
         }
      } else {
         accessor.readByte();
         ret.speed_$eq(accessor.readByte());
         accessor.skip(4);
      }

      // Find the base damage to base futher calculations on.
      // Several skills have their own formula in this section.
      long calcDmgMax;

      if (magic && ret.skill() != 0) {
         calcDmgMax = (long) (Math.ceil((chr.getTotalMagic() * Math.ceil(chr.getTotalMagic() / 1000.0) + chr.getTotalMagic()) / 30.0) + Math.ceil(chr.getTotalInt() / 200.0));
      } else if (ret.skill() == 4001344 || ret.skill() == NightWalker.LUCKY_SEVEN || ret.skill() == NightLord.TRIPLE_THROW) {
         calcDmgMax = (long) ((chr.getTotalLuk() * 5) * Math.ceil(chr.getTotalWatk() / 100.0));
      } else if (ret.skill() == DragonKnight.DRAGON_ROAR) {
         calcDmgMax = (long) ((chr.getTotalStr() * 4 + chr.getTotalDex()) * Math.ceil(chr.getTotalWatk() / 100.0));
      } else if (ret.skill() == NightLord.VENOMOUS_STAR || ret.skill() == Shadower.VENOMOUS_STAB) {
         calcDmgMax = (long) (Math.ceil((18.5 * (chr.getTotalStr() + chr.getTotalLuk()) + chr.getTotalDex() * 2) / 100.0) * chr.calculateMaxBaseDamage(chr.getTotalWatk()));
      } else {
         calcDmgMax = chr.calculateMaxBaseDamage(chr.getTotalWatk());
      }

      if (ret.skill() != 0) {
         MapleStatEffect effect = SkillFactory.getSkill(ret.skill()).map(skill -> skill.getEffect(ret.skillLevel())).orElseThrow();

         if (magic) {
            // Since the skill is magic based, use the magic formula
            if (chr.getJob() == MapleJob.IL_ARCHMAGE || chr.getJob() == MapleJob.IL_MAGE) {
               int skillLvl = chr.getSkillLevel(ILMage.ELEMENT_AMPLIFICATION);
               if (skillLvl > 0) {
                  int y = SkillFactory.getSkill(ILMage.ELEMENT_AMPLIFICATION).map(skill -> skill.getEffect(skillLvl)).map(MapleStatEffect::getY).orElse(100);
                  calcDmgMax = calcDmgMax * y / 100;
               }
            } else if (chr.getJob() == MapleJob.FP_ARCHMAGE || chr.getJob() == MapleJob.FP_MAGE) {
               int skillLvl = chr.getSkillLevel(FPMage.ELEMENT_AMPLIFICATION);
               if (skillLvl > 0) {
                  int y = SkillFactory.getSkill(FPMage.ELEMENT_AMPLIFICATION).map(skill -> skill.getEffect(skillLvl)).map(MapleStatEffect::getY).orElse(100);
                  calcDmgMax = calcDmgMax * y / 100;
               }
            } else if (chr.getJob() == MapleJob.BLAZEWIZARD3 || chr.getJob() == MapleJob.BLAZEWIZARD4) {
               int skillLvl = chr.getSkillLevel(BlazeWizard.ELEMENT_AMPLIFICATION);
               if (skillLvl > 0) {
                  int y = SkillFactory.getSkill(BlazeWizard.ELEMENT_AMPLIFICATION).map(skill -> skill.getEffect(skillLvl)).map(MapleStatEffect::getY).orElse(100);
                  calcDmgMax = calcDmgMax * y / 100;
               }
            } else if (chr.getJob() == MapleJob.EVAN7 || chr.getJob() == MapleJob.EVAN8 || chr.getJob() == MapleJob.EVAN9 || chr.getJob() == MapleJob.EVAN10) {
               int skillLvl = chr.getSkillLevel(Evan.MAGIC_AMPLIFICATION);
               if (skillLvl > 0) {
                  int y = SkillFactory.getSkill(Evan.MAGIC_AMPLIFICATION).map(skill -> skill.getEffect(skillLvl)).map(MapleStatEffect::getY).orElse(100);
                  calcDmgMax = calcDmgMax * y / 100;
               }
            }

            calcDmgMax *= effect.getMatk();
            if (ret.skill() == Cleric.HEAL) {
               // This formula is still a bit wonky, but it is fairly accurate.
               calcDmgMax = (int) Math.round((chr.getTotalInt() * 4.8 + chr.getTotalLuk() * 4) * chr.getTotalMagic() / 1000);
               calcDmgMax = calcDmgMax * effect.getHp() / 100;

               ret.speed_$eq(7);
            }
         } else if (ret.skill() == Hermit.SHADOW_MESO) {
            // Shadow Meso also has its own formula
            calcDmgMax = effect.getMoneyCon() * 10;
            calcDmgMax = (int) Math.floor(calcDmgMax * 1.5);
         } else {
            // Normal damage formula for skills
            calcDmgMax = calcDmgMax * effect.getDamage() / 100;
         }
      }

      Integer comboBuff = chr.getBuffedValue(MapleBuffStat.COMBO);
      if (comboBuff != null && comboBuff > 0) {
         int oid = chr.isCygnus() ? DawnWarrior.COMBO : Crusader.COMBO;
         int advcomboid = chr.isCygnus() ? DawnWarrior.ADVANCED_COMBO : Hero.ADVANCED_COMBO;

         if (comboBuff > 6) {
            // Advanced Combo
            int effectDamage = SkillFactory.getSkill(advcomboid)
                  .map(skill -> skill.getEffect(chr.getSkillLevel(advcomboid)))
                  .map(MapleStatEffect::getDamage)
                  .orElse(0);
            calcDmgMax = (long) Math.floor(calcDmgMax * (effectDamage + 50.0) / 100 + 0.20 + (comboBuff - 5) * 0.04);
         } else {
            // Normal Combo
            int skillLv = chr.getSkillLevel(oid);
            int maxLv = SkillFactory.getSkill(oid).map(Skill::getMaxLevel).orElse(skillLv);
            final int adjustedLv;

            if (skillLv <= 0 || chr.isGM()) {
               adjustedLv = maxLv;
            } else {
               adjustedLv = skillLv;
            }

            if (skillLv > 0) {
               int effectDamage = SkillFactory.getSkill(oid)
                     .map(skill -> skill.getEffect(adjustedLv))
                     .map(MapleStatEffect::getDamage)
                     .orElse(0);
               calcDmgMax = (long) Math.floor(calcDmgMax * (effectDamage + 50.0) / 100 + Math.floor((comboBuff - 1) * (skillLv / 6)) / 100);
            }
         }

         if (GameConstants.isFinisherSkill(ret.skill())) {
            // Finisher skills do more damage based on how many orbs the player has.
            int orbs = comboBuff - 1;
            if (orbs == 2) {
               calcDmgMax *= 1.2;
            } else if (orbs == 3) {
               calcDmgMax *= 1.54;
            } else if (orbs == 4) {
               calcDmgMax *= 2;
            } else if (orbs >= 5) {
               calcDmgMax *= 2.5;
            }
         }
      }

      if (chr.getEnergyBar() == 15000) {
         int energyChargeId = chr.isCygnus() ? ThunderBreaker.ENERGY_CHARGE : Marauder.ENERGY_CHARGE;
         int effectDamage = SkillFactory.getSkill(energyChargeId)
               .map(skill -> skill.getEffect(chr.getSkillLevel(energyChargeId)))
               .map(MapleStatEffect::getDamage)
               .orElse(0);
         calcDmgMax *= (100 + effectDamage) / 100;
      }

      if (chr.getMapId() >= 914000000 && chr.getMapId() <= 914000500) {
         calcDmgMax += 80000; // Aran Tutorial.
      }

      boolean canCrit = false;
      if (chr.getJob().isA((MapleJob.BOWMAN)) || chr.getJob().isA(MapleJob.THIEF) || chr.getJob().isA(MapleJob.NIGHTWALKER1) || chr.getJob().isA(MapleJob.WINDARCHER1) || chr.getJob() == MapleJob.ARAN3 || chr.getJob() == MapleJob.ARAN4 || chr.getJob() == MapleJob.MARAUDER || chr.getJob() == MapleJob.BUCCANEER) {
         canCrit = true;
      }

      if (chr.getBuffEffect(MapleBuffStat.SHARP_EYES) != null) {
         // Any class that has sharp eyes can crit. Also, since it stacks with normal crit go ahead
         // and calc it in.

         canCrit = true;
         calcDmgMax *= 1.4;
      }

      boolean shadowPartner = false;
      if (chr.getBuffEffect(MapleBuffStat.SHADOWPARTNER) != null) {
         shadowPartner = true;
      }

      if (ret.skill() != 0) {
         int fixed = SkillFactory.getSkill(ret.skill())
               .map(skill -> AbstractDealDamageHandler.getAttackEffect(ret, chr, skill))
               .map(MapleStatEffect::getFixDamage)
               .orElse(0);
         if (fixed > 0) {
            calcDmgMax = fixed;
         }
      }
      for (int i = 0; i < ret.numAttacked(); i++) {
         int oid = accessor.readInt();
         accessor.skip(14);
         List<Integer> allDamageNumbers = new ArrayList<>();
         MapleMonster monster = chr.getMap().getMonsterByOid(oid);

         if (chr.getBuffEffect(MapleBuffStat.WK_CHARGE) != null) {
            // Charge, so now we need to check elemental effectiveness
            int sourceID = chr.getBuffSource(MapleBuffStat.WK_CHARGE);
            int level = chr.getBuffedValue(MapleBuffStat.WK_CHARGE);
            if (monster != null) {
               if (sourceID == WhiteKnight.BW_FIRE_CHARGE || sourceID == WhiteKnight.SWORD_FIRE_CHARGE) {
                  if (monster.getStats().getEffectiveness(Element.FIRE) == ElementalEffectiveness.WEAK) {
                     calcDmgMax *= 1.05 + level * 0.015;
                  }
               } else if (sourceID == WhiteKnight.BW_ICE_CHARGE || sourceID == WhiteKnight.SWORD_ICE_CHARGE) {
                  if (monster.getStats().getEffectiveness(Element.ICE) == ElementalEffectiveness.WEAK) {
                     calcDmgMax *= 1.05 + level * 0.015;
                  }
               } else if (sourceID == WhiteKnight.BW_LIT_CHARGE || sourceID == WhiteKnight.SWORD_LIT_CHARGE) {
                  if (monster.getStats().getEffectiveness(Element.LIGHTING) == ElementalEffectiveness.WEAK) {
                     calcDmgMax *= 1.05 + level * 0.015;
                  }
               } else if (sourceID == Paladin.BW_HOLY_CHARGE || sourceID == Paladin.SWORD_HOLY_CHARGE) {
                  if (monster.getStats().getEffectiveness(Element.HOLY) == ElementalEffectiveness.WEAK) {
                     calcDmgMax *= 1.2 + level * 0.015;
                  }
               }
            } else {
               // Since we already know the skill has an elemental attribute, but we dont know if the monster is weak or not, lets
               // take the safe approach and just assume they are weak.
               calcDmgMax *= 1.5;
            }
         }

         if (ret.skill() != 0) {
            Skill skill = SkillFactory.getSkill(ret.skill()).orElseThrow();
            if (skill.getElement() != Element.NEUTRAL && chr.getBuffedValue(MapleBuffStat.ELEMENTAL_RESET) == null) {
               // The skill has an element effect, so we need to factor that in.
               if (monster != null) {
                  ElementalEffectiveness eff = monster.getElementalEffectiveness(skill.getElement());
                  if (eff == ElementalEffectiveness.WEAK) {
                     calcDmgMax *= 1.5;
                  } else if (eff == ElementalEffectiveness.STRONG) {
                     //calcDmgMax *= 0.5;
                  }
               } else {
                  // Since we already know the skill has an elemental attribute, but we dont know if the monster is weak or not, lets
                  // take the safe approach and just assume they are weak.
                  calcDmgMax *= 1.5;
               }
            }
            if (ret.skill() == FPWizard.POISON_BREATH || ret.skill() == FPMage.POISON_MIST || ret.skill() == FPArchMage.FIRE_DEMON || ret.skill() == ILArchMage.ICE_DEMON) {
               if (monster != null) {
                  // Turns out poison is completely server side, so I don't know why I added this. >.<
                  //calcDmgMax = monster.getHp() / (70 - chr.getSkillLevel(skill));
               }
            } else if (ret.skill() == Hermit.SHADOW_WEB) {
               if (monster != null) {
                  calcDmgMax = monster.getHp() / (50 - chr.getSkillLevel(skill));
               }
            } else if (ret.skill() == Hermit.SHADOW_MESO) {
               if (monster != null) {
                  monster.debuffMob(Hermit.SHADOW_MESO);
               }
            } else if (ret.skill() == Aran.BODY_PRESSURE) {
               if (monster != null) {
                  int skillDamage = SkillFactory.getSkill(Aran.BODY_PRESSURE)
                        .map(bodyPressure -> bodyPressure.getEffect(ret.skillLevel()))
                        .map(MapleStatEffect::getDamage)
                        .orElse(0);

                  int bodyPressureDmg = monster.getMaxHp() * skillDamage / 100;
                  if (bodyPressureDmg > calcDmgMax) {
                     calcDmgMax = bodyPressureDmg;
                  }
               }
            }
         }

         for (int j = 0; j < ret.numDamage(); j++) {
            int damage = accessor.readInt();
            long hitDmgMax = calcDmgMax;
            if (ret.skill() == Buccaneer.BARRAGE || ret.skill() == ThunderBreaker.BARRAGE) {
               if (j > 3) {
                  hitDmgMax *= Math.pow(2, (j - 3));
               }
            }
            if (shadowPartner) {
               // For shadow partner, the second half of the hits only do 50% damage. So calc that
               // in for the crit effects.
               if (j >= ret.numDamage() / 2) {
                  hitDmgMax *= 0.5;
               }
            }

            if (ret.skill() == Marksman.SNIPE) {
               damage = 195000 + Randomizer.nextInt(5000);
               hitDmgMax = 200000;
            } else if (ret.skill() == Beginner.BAMBOO_RAIN || ret.skill() == Noblesse.BAMBOO_RAIN || ret.skill() == Evan.BAMBOO_THRUST || ret.skill() == Legend.BAMBOO_THRUST) {
               hitDmgMax = 82569000; // 30% of Max HP of strongest Dojo boss
            }

            long maxWithCrit = hitDmgMax;
            if (canCrit) // They can crit, so up the max.
            {
               maxWithCrit *= 2;
            }

            // Warn if the damage is over 1.5x what we calculated above.
            if (damage > maxWithCrit * 1.5) {
               AutobanFactory.DAMAGE_HACK.alert(chr, "DMG: " + damage + " MaxDMG: " + maxWithCrit + " SID: " + ret.skill() + " MobID: " + (monster != null ? monster.id() : "null") + " Map: " + chr.getMap().getMapName() + " (" + chr.getMapId() + ")");
            }

            // Add a ab point if its over 5x what we calculated.
            if (damage > maxWithCrit * 5) {
               AutobanFactory.DAMAGE_HACK.addPoint(chr.getAutobanManager(), "DMG: " + damage + " MaxDMG: " + maxWithCrit + " SID: " + ret.skill() + " MobID: " + (monster != null ? monster.id() : "null") + " Map: " + chr.getMap().getMapName() + " (" + chr.getMapId() + ")");
            }

            if (ret.skill() == Marksman.SNIPE || (canCrit && damage > hitDmgMax)) {
               // If the skill is a crit, inverse the damage to make it show up on clients.
               damage = -Integer.MAX_VALUE + damage - 1;
            }

            allDamageNumbers.add(damage);
         }
         if (ret.skill() != Corsair.RAPID_FIRE || ret.skill() != Aran.HIDDEN_FULL_DOUBLE || ret.skill() != Aran.HIDDEN_FULL_TRIPLE || ret.skill() != Aran.HIDDEN_OVER_DOUBLE || ret.skill() != Aran.HIDDEN_OVER_TRIPLE) {
            accessor.skip(4);
         }
         ret.addDamage(oid, allDamageNumbers);
      }
      if (ret.skill() == NightWalker.POISON_BOMB) { // Poison Bomb
         accessor.skip(4);
         ret.position().setLocation(accessor.readShort(), accessor.readShort());
      }
      return ret;
   }

   @Override
   public AttackPacket read(SeekableLittleEndianAccessor accessor) {
      return null;
   }
}
