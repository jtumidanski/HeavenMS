package net.server.channel.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import client.autoban.AutoBanFactory;
import client.inventory.Item;
import client.inventory.MapleWeaponType;
import client.status.MonsterStatusEffect;
import constants.MapleInventoryType;
import constants.skills.Outlaw;
import net.server.channel.packet.SummonDamagePacket;
import net.server.channel.packet.reader.SummonDamageReader;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.maps.MapleSummon;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.MasterBroadcaster;
import tools.packet.PacketInput;
import tools.packet.attack.SummonAttack;

public final class SummonDamageHandler extends AbstractDealDamageHandler<SummonDamagePacket> {
   @Override
   public Class<SummonDamageReader> getReaderClass() {
      return SummonDamageReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      return client.getPlayer().isAlive();
   }

   @Override
   public void handlePacket(SummonDamagePacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      int oid = packet.objectId();
      MapleSummon summon = null;
      for (MapleSummon sum : player.getSummonsValues()) {
         if (sum.objectId() == oid) {
            summon = sum;
         }
      }
      if (summon == null) {
         return;
      }
      Optional<Skill> summonSkill = SkillFactory.getSkill(summon.getSkill());
      if (summonSkill.isEmpty()) {
         return;
      }

      MapleStatEffect summonEffect = summonSkill.get().getEffect(summon.getSkillLevel());
      List<SummonAttackEntry> allDamage = new ArrayList<>();
      for (int x = 0; x < packet.numAttacked(); x++) {
         allDamage.add(new SummonAttackEntry(packet.monsterObjectId()[x], packet.damage()[x]));
      }

      PacketInput attackPacket = new SummonAttack(player.getId(), summon.objectId(), packet.direction(), allDamage);
      MasterBroadcaster.getInstance().sendToAllInMapRange(player.getMap(), attackPacket, player, summon.position());

      if (player.getMap().isOwnershipRestricted(player)) {
         return;
      }

      boolean magic = summonEffect.getWeaponAttack() == 0;
      int maxDmg = calcMaxDamage(summonEffect, player, magic);
      for (SummonAttackEntry attackEntry : allDamage) {
         int damage = attackEntry.damage();
         MapleMonster target = player.getMap().getMonsterByOid(attackEntry.monsterObjectId());
         if (target != null) {
            if (damage > maxDmg) {
               AutoBanFactory.DAMAGE_HACK.alert(client.getPlayer(), "Possible packet editing summon damage exploit.");
               LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS,
                     client.getPlayer().getName() + " used a summon of skillId " + summon.getSkill() + " to attack "
                           + MapleMonsterInformationProvider.getInstance().getMobNameFromId(target.id()) + " with damage " + damage
                           + " (max: " + maxDmg + ")");
               damage = maxDmg;
            }

            if (damage > 0 && summonEffect.getMonsterStati().size() > 0) {
               if (summonEffect.makeChanceResult()) {
                  target.applyStatus(player,
                        new MonsterStatusEffect(summonEffect.getMonsterStati(), summonSkill.get(), null, false),
                        summonEffect.isPoison(), 4000);
               }
            }
            player.getMap().damageMonster(player, target, damage);
         }
      }

      if (summon.getSkill() == Outlaw.GAVIOTA) {
         player.cancelEffect(summonEffect, false, -1);
      }
   }

   private int calcMaxDamage(MapleStatEffect summonEffect, MapleCharacter player, boolean magic) {
      double maxDamage;

      if (magic) {
         int magicAttack = Math.max(player.getTotalMagic(), 14);
         maxDamage = player.calculateMaxBaseMagicDamage(magicAttack) * (0.05 * summonEffect.getMagicAttack());
      } else {
         int weaponAttack = Math.max(player.getTotalWeaponAttack(), 14);
         Item weapon_item = player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);

         int maxBaseDmg;
         if (weapon_item != null) {
            maxBaseDmg = player.calculateMaxBaseDamage(weaponAttack,
                  MapleItemInformationProvider.getInstance().getWeaponType(weapon_item.id()));
         } else {
            maxBaseDmg = player.calculateMaxBaseDamage(weaponAttack, MapleWeaponType.SWORD1H);
         }

         float summonDmgMod = (maxBaseDmg >= 438) ? 0.054f : 0.077f;
         maxDamage = maxBaseDmg * (summonDmgMod * summonEffect.getWeaponAttack());
      }

      return (int) maxDamage;
   }
}
