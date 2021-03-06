package net.server.channel.handlers;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleWeaponType;
import client.inventory.manipulator.MapleInventoryManipulator;
import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import config.YamlConfig;
import constants.ItemConstants;
import constants.MapleInventoryType;
import constants.game.GameConstants;
import constants.skills.Aran;
import constants.skills.Buccaneer;
import constants.skills.NightLord;
import constants.skills.NightWalker;
import constants.skills.Shadower;
import constants.skills.ThunderBreaker;
import constants.skills.WindArcher;
import net.server.channel.packet.AttackPacket;
import net.server.channel.packet.reader.DamageReader;
import net.server.channel.packet.PacketReaderFactory;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.Randomizer;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.GetEnergy;
import tools.packet.PacketInput;
import tools.packet.attack.RangedAttack;
import tools.packet.character.SkillCoolDown;

public final class RangedAttackHandler extends AbstractDealDamageHandler<AttackPacket> {
   @Override
   public Class<DamageReader> getReaderClass() {
      return DamageReader.class;
   }

   @Override
   public void handlePacket(SeekableLittleEndianAccessor accessor, MapleClient client) {
      DamageReader damageReader = (DamageReader) PacketReaderFactory.getInstance().get(getReaderClass());
      handlePacket(damageReader.read(accessor, client.getPlayer(), false, false), client);
   }

   @Override
   public final void handlePacket(AttackPacket attack, MapleClient c) {
      MapleCharacter chr = c.getPlayer();

      if (chr.getBuffEffect(MapleBuffStat.MORPH) != null) {
         if (chr.getBuffEffect(MapleBuffStat.MORPH).isMorphWithoutAttack()) {
            // How are they attacking when the client won't let them?
            chr.getClient().disconnect(false, false);
            return;
         }
      }

      if (GameConstants.isDojo(chr.getMap().getId()) && attack.numAttacked() > 0) {
         chr.setDojoEnergy(chr.getDojoEnergy() + YamlConfig.config.server.DOJO_ENERGY_ATK);
         PacketCreator.announce(c, new GetEnergy("energy", chr.getDojoEnergy()));
      }

      if (attack.skill() == Buccaneer.ENERGY_ORB || attack.skill() == ThunderBreaker.SPARK || attack.skill() == Shadower.TAUNT || attack.skill() == NightLord.TAUNT) {
         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new RangedAttack(chr.getId(), attack.skill(), attack.skillLevel(), attack.stance(), attack.numAttackedAndDamage(), 0, attack.allDamage(), attack.speed(), attack.direction(), attack.display()), false, chr);
         applyAttack(attack, chr, 1);
      } else if (attack.skill() == ThunderBreaker.SHARK_WAVE && chr.getSkillLevel(ThunderBreaker.SHARK_WAVE) > 0) {
         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new RangedAttack(chr.getId(), attack.skill(), attack.skillLevel(), attack.stance(), attack.numAttackedAndDamage(), 0, attack.allDamage(), attack.speed(), attack.direction(), attack.display()), false, chr);
         applyAttack(attack, chr, 1);

         for (int i = 0; i < attack.numAttacked(); i++) {
            chr.handleEnergyChargeGain();
         }
      } else if (attack.skill() == Aran.COMBO_SMASH || attack.skill() == Aran.COMBO_FENRIR || attack.skill() == Aran.COMBO_TEMPEST) {
         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new RangedAttack(chr.getId(), attack.skill(), attack.skillLevel(), attack.stance(), attack.numAttackedAndDamage(), 0, attack.allDamage(), attack.speed(), attack.direction(), attack.display()), false, chr);
         if (attack.skill() == Aran.COMBO_SMASH && chr.getCombo() >= 30) {
            chr.setCombo((short) 0);
            applyAttack(attack, chr, 1);
         } else if (attack.skill() == Aran.COMBO_FENRIR && chr.getCombo() >= 100) {
            chr.setCombo((short) 0);
            applyAttack(attack, chr, 2);
         } else if (attack.skill() == Aran.COMBO_TEMPEST && chr.getCombo() >= 200) {
            chr.setCombo((short) 0);
            applyAttack(attack, chr, 4);
         }
      } else {
         Item weapon = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
         MapleWeaponType type = MapleItemInformationProvider.getInstance().getWeaponType(weapon.id());
         if (type == MapleWeaponType.NOT_A_WEAPON) {
            return;
         }
         short slot = -1;
         int projectile = 0;
         short bulletCount = 1;
         MapleStatEffect effect = null;
         if (attack.skill() != 0) {
            effect = getAttackEffect(attack, chr, null);
            bulletCount = effect.getBulletCount();
            if (effect.getCoolDown() > 0) {
               PacketCreator.announce(c, new SkillCoolDown(attack.skill(), effect.getCoolDown()));
            }

            if (attack.skill() == 4111004) {   // shadow meso
               bulletCount = 0;

               int money = effect.getMoneyCon();
               if (money != 0) {
                  int moneyMod = money / 2;
                  money += Randomizer.nextInt(moneyMod);
                  if (money > chr.getMeso()) {
                     money = chr.getMeso();
                  }
                  chr.gainMeso(-money, false);
               }
            }
         }
         boolean hasShadowPartner = chr.getBuffedValue(MapleBuffStat.SHADOW_PARTNER) != null;
         if (hasShadowPartner) {
            bulletCount *= 2;
         }
         MapleInventory inv = chr.getInventory(MapleInventoryType.USE);
         for (short i = 1; i <= inv.getSlotLimit(); i++) {
            Item item = inv.getItem(i);
            if (item != null) {
               int id = item.id();
               slot = item.position();

               boolean bow = ItemConstants.isArrowForBow(id);
               boolean crossBow = ItemConstants.isArrowForCrossBow(id);
               if (item.quantity() >= bulletCount) { //Fixes the bug where you can't use your last arrow.
                  if (type == MapleWeaponType.CLAW && ItemConstants.isThrowingStar(id) && weapon.id() != 1472063) {
                     if (((id == 2070007 || id == 2070018) && chr.getLevel() < 70) || (id == 2070016 && chr.getLevel() < 50)) {
                     } else {
                        projectile = id;
                        break;
                     }
                  } else if ((type == MapleWeaponType.GUN && ItemConstants.isBullet(id))) {
                     if (id == 2331000 && id == 2332000) {
                        if (chr.getLevel() > 69) {
                           projectile = id;
                           break;
                        }
                     } else if (chr.getLevel() > (id % 10) * 20 + 9) {
                        projectile = id;
                        break;
                     }
                  } else if ((type == MapleWeaponType.BOW && bow) || (type == MapleWeaponType.CROSSBOW && crossBow) || (weapon.id() == 1472063 && (bow || crossBow))) {
                     projectile = id;
                     break;
                  }
               }
            }
         }
         boolean soulArrow = chr.getBuffedValue(MapleBuffStat.SOUL_ARROW) != null;
         boolean shadowClaw = chr.getBuffedValue(MapleBuffStat.SHADOW_CLAW) != null;
         if (projectile != 0) {
            if (!soulArrow && !shadowClaw && attack.skill() != 11101004 && attack.skill() != 15111007 && attack.skill() != 14101006) {
               short bulletConsume = bulletCount;

               if (effect != null && effect.getBulletConsume() != 0) {
                  bulletConsume = (byte) (effect.getBulletConsume() * (hasShadowPartner ? 2 : 1));
               }

               if (slot < 0) {
                  LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, "Projectile to use was unable to be found.");
               } else {
                  MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, bulletConsume, false, true);
               }
            }
         }

         if (projectile != 0 || soulArrow || attack.skill() == 11101004 || attack.skill() == 15111007 || attack.skill() == 14101006 || attack.skill() == 4111004 || attack.skill() == 13101005) {
            int visProjectile = projectile; //visible projectile sent to players
            if (ItemConstants.isThrowingStar(projectile)) {
               MapleInventory cash = chr.getInventory(MapleInventoryType.CASH);
               for (int i = 1; i <= cash.getSlotLimit(); i++) { // impose order...
                  Item item = cash.getItem((short) i);
                  if (item != null) {
                     if (item.id() / 1000 == 5021) {
                        visProjectile = item.id();
                        break;
                     }
                  }
               }
            } else if (soulArrow || attack.skill() == 3111004 || attack.skill() == 3211004 || attack.skill() == 11101004 || attack.skill() == 15111007 || attack.skill() == 14101006 || attack.skill() == 13101005) {
               visProjectile = 0;
            }

            PacketInput packet;
            switch (attack.skill()) {
               case 3121004: // Hurricane
               case 3221001: // Pierce
               case 5221004: // Rapid Fire
               case 13111002: // KoC Hurricane
                  packet = new RangedAttack(chr.getId(), attack.skill(), attack.skillLevel(), attack.rangedDirection(), attack.numAttackedAndDamage(), visProjectile, attack.allDamage(), attack.speed(), attack.direction(), attack.display());
                  break;
               default:
                  packet = new RangedAttack(chr.getId(), attack.skill(), attack.skillLevel(), attack.stance(), attack.numAttackedAndDamage(), visProjectile, attack.allDamage(), attack.speed(), attack.direction(), attack.display());
                  break;
            }
            MasterBroadcaster.getInstance().sendToAllInMapRange(chr.getMap(), packet, false, chr, true);

            if (attack.skill() != 0) {
               int coolDown = SkillFactory.getSkill(attack.skill())
                     .map(skill -> skill.getEffect(chr.getSkillLevel(skill)))
                     .map(MapleStatEffect::getCoolDown)
                     .orElse(0);
               if (coolDown > 0) {
                  if (chr.skillIsCooling(attack.skill())) {
                     return;
                  } else {
                     PacketCreator.announce(c, new SkillCoolDown(attack.skill(), coolDown));
                     chr.addCoolDown(attack.skill(), currentServerTime(), coolDown * 1000);
                  }
               }
            }

            //TODO - Investigate why this is inconsistent with the CloseRangeAttackHandler
            if (chr.getBuffedValue(MapleBuffStat.DARK_SIGHT) != null && attack.numAttacked() > 0 && chr.getBuffSource(MapleBuffStat.DARK_SIGHT) != 9101004) {
               SkillFactory.executeIfHasSkill(chr, NightWalker.VANISH, (skill, skillLevel) -> cancelDarkSight(chr));
            } else if (chr.getBuffedValue(MapleBuffStat.WIND_WALK) != null && attack.numAttacked() > 0) {
               SkillFactory.executeIfHasSkill(chr, WindArcher.WIND_WALK, (skill, skillLevel) -> cancelWindWalk(chr));
            }

            applyAttack(attack, chr, bulletCount);
         }
      }
   }

   private void cancelWindWalk(MapleCharacter chr) {
      chr.cancelEffectFromBuffStat(MapleBuffStat.WIND_WALK);
      chr.cancelBuffStats(MapleBuffStat.WIND_WALK);
   }

   private void cancelDarkSight(MapleCharacter chr) {
      chr.cancelEffectFromBuffStat(MapleBuffStat.DARK_SIGHT);
      chr.cancelBuffStats(MapleBuffStat.DARK_SIGHT);
   }
}