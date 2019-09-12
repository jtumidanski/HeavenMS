/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation version 3 as published by
the Free Software Foundation. You may not use, modify or distribute
this program under any other version of the GNU Affero General Public
License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.server.channel.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import client.autoban.AutobanFactory;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MapleWeaponType;
import client.status.MonsterStatusEffect;
import constants.skills.Outlaw;
import net.server.channel.packet.SummonDamagePacket;
import net.server.channel.packet.reader.SummonDamageReader;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.maps.MapleSummon;
import tools.FilePrinter;
import tools.MaplePacketCreator;

public final class SummonDamageHandler extends AbstractDealDamageHandler<SummonDamagePacket, SummonDamageReader> {
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
         if (sum.getObjectId() == oid) {
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
      player.getMap().broadcastMessage(player, MaplePacketCreator.summonAttack(player.getId(), summon.getObjectId(), packet.direction(), allDamage), summon.getPosition());

      if (player.getMap().isOwnershipRestricted(player)) {
         return;
      }

      boolean magic = summonEffect.getWatk() == 0;
      int maxDmg = calcMaxDamage(summonEffect, player, magic);    // thanks Darter (YungMoozi) for reporting unchecked max dmg
      for (SummonAttackEntry attackEntry : allDamage) {
         int damage = attackEntry.getDamage();
         MapleMonster target = player.getMap().getMonsterByOid(attackEntry.getMonsterOid());
         if (target != null) {
            if (damage > maxDmg) {
               AutobanFactory.DAMAGE_HACK.alert(client.getPlayer(), "Possible packet editing summon damage exploit.");

               FilePrinter.printError(FilePrinter.EXPLOITS + client.getPlayer().getName() + ".txt", client.getPlayer().getName() + " used a summon of skillid " + summon.getSkill() + " to attack " + MapleMonsterInformationProvider.getInstance().getMobNameFromId(target.getId()) + " with damage " + damage + " (max: " + maxDmg + ")");
               damage = maxDmg;
            }

            if (damage > 0 && summonEffect.getMonsterStati().size() > 0) {
               if (summonEffect.makeChanceResult()) {
                  target.applyStatus(player, new MonsterStatusEffect(summonEffect.getMonsterStati(), summonSkill.get(), null, false), summonEffect.isPoison(), 4000);
               }
            }
            player.getMap().damageMonster(player, target, damage);
         }
      }

      if (summon.getSkill() == Outlaw.GAVIOTA) {  // thanks Periwinks for noticing Gaviota not cancelling after grenade toss
         player.cancelEffect(summonEffect, false, -1);
      }
   }

   private int calcMaxDamage(MapleStatEffect summonEffect, MapleCharacter player, boolean magic) {
      double maxDamage;

      if (magic) {
         int matk = Math.max(player.getTotalMagic(), 14);
         maxDamage = player.calculateMaxBaseMagicDamage(matk) * (0.05 * summonEffect.getMatk());
      } else {
         int watk = Math.max(player.getTotalWatk(), 14);
         Item weapon_item = player.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);

         int maxBaseDmg;  // thanks Conrad, Atoot for detecting some summons legitimately hitting over the calculated limit
         if (weapon_item != null) {
            maxBaseDmg = player.calculateMaxBaseDamage(watk, MapleItemInformationProvider.getInstance().getWeaponType(weapon_item.getItemId()));
         } else {
            maxBaseDmg = player.calculateMaxBaseDamage(watk, MapleWeaponType.SWORD1H);
         }

         float summonDmgMod = (maxBaseDmg >= 438) ? 0.054f : 0.077f;
         maxDamage = maxBaseDmg * (summonDmgMod * summonEffect.getWatk());
      }

      return (int) maxDamage;
   }

   public final class SummonAttackEntry {

      private int monsterOid;
      private int damage;

      public SummonAttackEntry(int monsterOid, int damage) {
         this.monsterOid = monsterOid;
         this.damage = damage;
      }

      public int getMonsterOid() {
         return monsterOid;
      }

      public int getDamage() {
         return damage;
      }

   }
}
