package net.server.channel.packet.reader;

import java.util.ArrayList;
import java.util.List;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleJob;
import client.Skill;
import client.SkillFactory;
import client.autoban.AutoBanFactory;
import constants.game.GameConstants;
import constants.skills.Aran;
import constants.skills.Beginner;
import constants.skills.Bishop;
import constants.skills.BlazeWizard;
import constants.skills.BowMaster;
import constants.skills.Brawler;
import constants.skills.Buccaneer;
import constants.skills.ChiefBandit;
import constants.skills.Cleric;
import constants.skills.Corsair;
import constants.skills.Crusader;
import constants.skills.DawnWarrior;
import constants.skills.DragonKnight;
import constants.skills.Evan;
import constants.skills.FPWizard;
import constants.skills.FirePoisonArchMage;
import constants.skills.FirePoisonMagician;
import constants.skills.Gunslinger;
import constants.skills.Hermit;
import constants.skills.Hero;
import constants.skills.IceLighteningArchMagician;
import constants.skills.IceLighteningMagician;
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
import net.server.PlayerBuffValueHolder;
import net.server.channel.builder.AttackPacketBuilder;
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
      AttackPacketBuilder builder = new AttackPacketBuilder();
      accessor.readByte();

      int numAttackedAndDamage = accessor.readByte();
      builder.setNumAttackedAndDamage(numAttackedAndDamage);

      int numAttacked = (numAttackedAndDamage >>> 4) & 0xF;
      builder.setNumAttacked(numAttacked);

      int numDamage = numAttackedAndDamage & 0xF;
      builder.setNumDamage(numDamage);

      //ret.clearAttacks();
      int skillId = accessor.readInt();
      builder.setSkill(skillId);
      builder.setRanged(ranged);
      builder.setMagic(magic);

      final int skillLevel;
      if (skillId > 0) {
         int rawSkillLevel = chr.getSkillLevel(skillId);
         if (rawSkillLevel == 0 && GameConstants.isPqSkillMap(chr.getMapId()) && GameConstants.isPqSkill(skillId)) {
            skillLevel = 1;
         } else {
            skillLevel = rawSkillLevel;
         }
      } else {
         skillLevel = 0;
      }
      builder.setSkillLevel(skillLevel);

      int charge = 0;
      if (skillId == Evan.ICE_BREATH || skillId == Evan.FIRE_BREATH || skillId == FirePoisonArchMage.BIG_BANG || skillId == IceLighteningArchMagician.BIG_BANG
            || skillId == Bishop.BIG_BANG || skillId == Gunslinger.GRENADE || skillId == Brawler.CORKSCREW_BLOW || skillId == ThunderBreaker.CORKSCREW_BLOW
            || skillId == NightWalker.POISON_BOMB) {
         charge = accessor.readInt();
      }
      builder.setCharge(charge);

      accessor.skip(8);
      int display = accessor.readByte();
      builder.setDisplay(display);
      int direction = accessor.readByte();
      builder.setDirection(direction);
      int stance = accessor.readByte();
      builder.setStance(stance);

      if (skillId == ChiefBandit.MESO_EXPLOSION) {
         if (numAttackedAndDamage == 0) {
            accessor.skip(10);
            int bullets = accessor.readByte();
            for (int j = 0; j < bullets; j++) {
               int mesoId = accessor.readInt();
               accessor.skip(1);
               builder.addDamage(mesoId, null);
            }
            return builder.build();
         } else {
            accessor.skip(6);
         }
         for (int i = 0; i < numAttacked + 1; i++) {
            int oid = accessor.readInt();
            if (i < numAttacked) {
               accessor.skip(12);
               int bullets = accessor.readByte();
               List<Integer> allDamageNumbers = new ArrayList<>();
               for (int j = 0; j < bullets; j++) {
                  int damage = accessor.readInt();
                  allDamageNumbers.add(damage);
               }
               builder.addDamage(oid, allDamageNumbers);
               accessor.skip(4);
            } else {
               int bullets = accessor.readByte();
               for (int j = 0; j < bullets; j++) {
                  int mesoId = accessor.readInt();
                  accessor.skip(1);
                  builder.addDamage(mesoId, null);
               }
            }
         }
         return builder.build();
      }
      int speed = 0;
      if (ranged) {
         accessor.readByte();
         speed = accessor.readByte();
         accessor.readByte();
         int rangedDirection = accessor.readByte();
         builder.setRangedDirection(rangedDirection);
         accessor.skip(7);
         if (skillId == BowMaster.HURRICANE || skillId == Marksman.PIERCING_ARROW || skillId == Corsair.RAPID_FIRE || skillId == WindArcher.HURRICANE) {
            accessor.skip(4);
         }
      } else {
         accessor.readByte();
         speed = accessor.readByte();
         accessor.skip(4);
      }
      builder.setSpeed(speed);

      // Find the base damage to base further calculations on.
      // Several skills have their own formula in this section.
      long calcDmgMax;

      if (magic && skillId != 0) {
         calcDmgMax = (long) (Math.ceil((chr.getTotalMagic() * Math.ceil(chr.getTotalMagic() / 1000.0) + chr.getTotalMagic()) / 30.0) + Math.ceil(chr.getTotalInt() / 200.0));
      } else if (skillId == 4001344 || skillId == NightWalker.LUCKY_SEVEN || skillId == NightLord.TRIPLE_THROW) {
         calcDmgMax = (long) ((chr.getTotalLuk() * 5) * Math.ceil(chr.getTotalWeaponAttack() / 100.0));
      } else if (skillId == DragonKnight.DRAGON_ROAR) {
         calcDmgMax = (long) ((chr.getTotalStr() * 4 + chr.getTotalDex()) * Math.ceil(chr.getTotalWeaponAttack() / 100.0));
      } else if (skillId == NightLord.VENOMOUS_STAR || skillId == Shadower.VENOMOUS_STAB) {
         calcDmgMax = (long) (Math.ceil((18.5 * (chr.getTotalStr() + chr.getTotalLuk()) + chr.getTotalDex() * 2) / 100.0) * chr.calculateMaxBaseDamage(chr.getTotalWeaponAttack()));
      } else {
         calcDmgMax = chr.calculateMaxBaseDamage(chr.getTotalWeaponAttack());
      }

      if (skillId != 0) {
         MapleStatEffect effect = SkillFactory.getSkill(skillId).map(skill -> skill.getEffect(skillLevel)).orElseThrow();

         if (magic) {
            // Since the skill is magic based, use the magic formula
            if (chr.getJob() == MapleJob.ICE_LIGHTENING_ARCH_MAGICIAN || chr.getJob() == MapleJob.ICE_LIGHTENING_MAGICIAN) {
               int skillLvl = chr.getSkillLevel(IceLighteningMagician.ELEMENT_AMPLIFICATION);
               if (skillLvl > 0) {
                  int y = SkillFactory.getSkill(IceLighteningMagician.ELEMENT_AMPLIFICATION).map(skill -> skill.getEffect(skillLvl)).map(MapleStatEffect::getY).orElse(100);
                  calcDmgMax = calcDmgMax * y / 100;
               }
            } else if (chr.getJob() == MapleJob.FIRE_POISON_ARCH_MAGICIAN || chr.getJob() == MapleJob.FIRE_POISON_MAGICIAN) {
               int skillLvl = chr.getSkillLevel(FirePoisonMagician.ELEMENT_AMPLIFICATION);
               if (skillLvl > 0) {
                  int y = SkillFactory.getSkill(FirePoisonMagician.ELEMENT_AMPLIFICATION).map(skill -> skill.getEffect(skillLvl)).map(MapleStatEffect::getY).orElse(100);
                  calcDmgMax = calcDmgMax * y / 100;
               }
            } else if (chr.getJob() == MapleJob.BLAZE_WIZARD_3 || chr.getJob() == MapleJob.BLAZE_WIZARD_4) {
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

            calcDmgMax *= effect.getMagicAttack();
            if (skillId == Cleric.HEAL) {
               // This formula is still a bit wonky, but it is fairly accurate.
               calcDmgMax = (int) Math.round((chr.getTotalInt() * 4.8 + chr.getTotalLuk() * 4) * chr.getTotalMagic() / 1000);
               calcDmgMax = calcDmgMax * effect.getHp() / 100;
               builder.setSpeed(7);
            }
         } else if (skillId == Hermit.SHADOW_MESO) {
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
         int advancedComboId = chr.isCygnus() ? DawnWarrior.ADVANCED_COMBO : Hero.ADVANCED_COMBO;

         if (comboBuff > 6) {
            // Advanced Combo
            int effectDamage = SkillFactory.getSkill(advancedComboId)
                  .map(skill -> skill.getEffect(chr.getSkillLevel(advancedComboId)))
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

         if (GameConstants.isFinisherSkill(skillId)) {
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

      int bonusDmgBuff = 100;
      for (PlayerBuffValueHolder buffValueHolder : chr.getAllBuffs()) {
         int bonusDmg = buffValueHolder.effect.getDamage() - 100;
         bonusDmgBuff += bonusDmg;
      }

      if (bonusDmgBuff != 100) {
         float dmgBuff = bonusDmgBuff / 100.0f;
         calcDmgMax = (long) Math.ceil(calcDmgMax * dmgBuff);
      }

      if (chr.getMapId() >= 914000000 && chr.getMapId() <= 914000500) {
         calcDmgMax += 80000; // Aran Tutorial.
      }

      boolean canCritical = false;
      if (chr.getJob().isA((MapleJob.BOWMAN)) || chr.getJob().isA(MapleJob.THIEF) || chr.getJob().isA(MapleJob.NIGHT_WALKER_1) || chr.getJob().isA(MapleJob.WIND_ARCHER_1) || chr.getJob() == MapleJob.ARAN3 || chr.getJob() == MapleJob.ARAN4 || chr.getJob() == MapleJob.MARAUDER || chr.getJob() == MapleJob.BUCCANEER) {
         canCritical = true;
      }

      if (chr.getBuffEffect(MapleBuffStat.SHARP_EYES) != null) {
         // Any class that has sharp eyes can critical. Also, since it stacks with normal critical go ahead
         // and calc it in.

         canCritical = true;
         calcDmgMax *= 1.4;
      }

      boolean shadowPartner = false;
      if (chr.getBuffEffect(MapleBuffStat.SHADOW_PARTNER) != null) {
         shadowPartner = true;
      }

      if (skillId != 0) {
         int fixed = SkillFactory.getSkill(skillId)
               .map(skill -> AbstractDealDamageHandler.getAttackEffect(builder.build(), chr, skill))
               .map(MapleStatEffect::getFixDamage)
               .orElse(0);
         if (fixed > 0) {
            calcDmgMax = fixed;
         }
      }
      for (int i = 0; i < numAttacked; i++) {
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

         if (skillId != 0) {
            Skill skill = SkillFactory.getSkill(skillId).orElseThrow();
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
            if (skillId == FPWizard.POISON_BREATH || skillId == FirePoisonMagician.POISON_MIST || skillId == FirePoisonArchMage.FIRE_DEMON
                  || skillId == IceLighteningArchMagician.ICE_DEMON) {
               if (monster != null) {
                  // Turns out poison is completely server side, so I don't know why I added this. >.<
                  //calcDmgMax = monster.getHp() / (70 - chr.getSkillLevel(skill));
               }
            } else if (skillId == Hermit.SHADOW_WEB) {
               if (monster != null) {
                  calcDmgMax = monster.getHp() / (50 - chr.getSkillLevel(skill));
               }
            } else if (skillId == Hermit.SHADOW_MESO) {
               if (monster != null) {
                  monster.removeMobStatus(Hermit.SHADOW_MESO);
               }
            } else if (skillId == Aran.BODY_PRESSURE) {
               if (monster != null) {
                  int skillDamage = SkillFactory.getSkill(Aran.BODY_PRESSURE)
                        .map(bodyPressure -> bodyPressure.getEffect(skillLevel))
                        .map(MapleStatEffect::getDamage)
                        .orElse(0);

                  int bodyPressureDmg = (int) Math.ceil((monster.getMaxHp() * skillDamage) / 100.0);
                  if (bodyPressureDmg > calcDmgMax) {
                     calcDmgMax = bodyPressureDmg;
                  }
               }
            }
         }

         for (int j = 0; j < numDamage; j++) {
            int damage = accessor.readInt();
            long hitDmgMax = calcDmgMax;
            if (skillId == Buccaneer.BARRAGE || skillId == ThunderBreaker.BARRAGE) {
               if (j > 3) {
                  hitDmgMax *= Math.pow(2, (j - 3));
               }
            }
            if (shadowPartner) {
               // For shadow partner, the second half of the hits only do 50% damage. So calc that
               // in for the critical effects.
               if (j >= numDamage / 2) {
                  hitDmgMax *= 0.5;
               }
            }

            if (skillId == Marksman.SNIPE) {
               damage = 195000 + Randomizer.nextInt(5000);
               hitDmgMax = 200000;
            } else if (skillId == Beginner.BAMBOO_RAIN || skillId == Noblesse.BAMBOO_RAIN || skillId == Evan.BAMBOO_THRUST || skillId == Legend.BAMBOO_THRUST) {
               hitDmgMax = 82569000; // 30% of Max HP of strongest Dojo boss
            }

            long maxWithCritical = hitDmgMax;
            if (canCritical) // They can critical, so up the max.
            {
               maxWithCritical *= 2;
            }

            // Warn if the damage is over 1.5x what we calculated above.
            if (damage > maxWithCritical * 1.5) {
               AutoBanFactory.DAMAGE_HACK.alert(chr, "DMG: " + damage + " MaxDMG: " + maxWithCritical + " SID: " + skillId + " MobID: " + (monster != null ? monster.id() : "null") + " Map: " + chr.getMap().getMapName() + " (" + chr.getMapId() + ")");
            }

            // Add a ab point if its over 5x what we calculated.
            if (damage > maxWithCritical * 5) {
               AutoBanFactory.DAMAGE_HACK.addPoint(chr.getAutoBanManager(), "DMG: " + damage + " MaxDMG: " + maxWithCritical + " SID: " + skillId + " MobID: " + (monster != null ? monster.id() : "null") + " Map: " + chr.getMap().getMapName() + " (" + chr.getMapId() + ")");
            }

            if (skillId == Marksman.SNIPE || (canCritical && damage > hitDmgMax)) {
               // If the skill is a critical, inverse the damage to make it show up on clients.
               damage = -Integer.MAX_VALUE + damage - 1;
            }

            allDamageNumbers.add(damage);
         }
         if (skillId != Corsair.RAPID_FIRE || skillId != Aran.HIDDEN_FULL_DOUBLE || skillId != Aran.HIDDEN_FULL_TRIPLE
               || skillId != Aran.HIDDEN_OVER_DOUBLE || skillId != Aran.HIDDEN_OVER_TRIPLE) {
            accessor.skip(4);
         }
         builder.addDamage(oid, allDamageNumbers);
      }
      if (skillId == NightWalker.POISON_BOMB) { // Poison Bomb
         accessor.skip(4);
         builder.setLocation(accessor.readShort(), accessor.readShort());
      }
      return builder.build();
   }

   @Override
   public AttackPacket read(SeekableLittleEndianAccessor accessor) {
      return null;
   }
}
