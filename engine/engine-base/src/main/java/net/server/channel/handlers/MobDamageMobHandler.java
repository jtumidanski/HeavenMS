package net.server.channel.handlers;

import java.util.Map;

import client.MapleCharacter;
import client.MapleClient;
import client.autoban.AutoBanFactory;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.MobDamageMobPacket;
import net.server.channel.packet.reader.MobDamageMobReader;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.maps.MapleMap;
import tools.FilePrinter;
import tools.MasterBroadcaster;
import tools.packet.monster.DamageMonster;

public final class MobDamageMobHandler extends AbstractPacketHandler<MobDamageMobPacket> {
   @Override
   public Class<MobDamageMobReader> getReaderClass() {
      return MobDamageMobReader.class;
   }

   private int calcMaxDamage(MapleMonster attacker, MapleMonster damaged, boolean magic) {
      int attackerAtk, damagedDef, attackerLevel = attacker.getLevel();
      double maxDamage;
      if (magic) {
         int atkRate = calcModifier(attacker, MonsterStatus.MAGIC_ATTACK_UP, MonsterStatus.MAGIC_ATTACK);
         attackerAtk = (attacker.getStats().maDamage() * atkRate) / 100;

         int defRate = calcModifier(damaged, MonsterStatus.MAGIC_DEFENSE_UP, MonsterStatus.MAGIC_DEFENSE);
         damagedDef = (damaged.getStats().mdDamage() * defRate) / 100;

         maxDamage = ((attackerAtk * (1.15 + (0.025 * attackerLevel))) - (0.75 * damagedDef)) * (Math.log(Math.abs(damagedDef - attackerAtk)) / Math.log(12));
      } else {
         int atkRate = calcModifier(attacker, MonsterStatus.WEAPON_ATTACK_UP, MonsterStatus.WEAPON_ATTACK);
         attackerAtk = (attacker.getStats().paDamage() * atkRate) / 100;

         int defRate = calcModifier(damaged, MonsterStatus.WEAPON_DEFENSE_UP, MonsterStatus.WEAPON_DEFENSE);
         damagedDef = (damaged.getStats().pdDamage() * defRate) / 100;

         maxDamage = ((attackerAtk * (1.15 + (0.025 * attackerLevel))) - (0.75 * damagedDef)) * (Math.log(Math.abs(damagedDef - attackerAtk)) / Math.log(17));
      }

      return (int) maxDamage;
   }

   private int calcModifier(MapleMonster monster, MonsterStatus buff, MonsterStatus nerf) {
      int atkModifier;
      final Map<MonsterStatus, MonsterStatusEffect> monsterStatuses = monster.getMonsterStatuses();

      MonsterStatusEffect atkBuff = monsterStatuses.get(buff);
      if (atkBuff != null) {
         atkModifier = atkBuff.getStatuses().get(buff);
      } else {
         atkModifier = 100;
      }

      MonsterStatusEffect atkNerf = monsterStatuses.get(nerf);
      if (atkNerf != null) {
         atkModifier -= atkNerf.getStatuses().get(nerf);
      }

      return atkModifier;
   }

   @Override
   public void handlePacket(MobDamageMobPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      MapleMap map = chr.getMap();
      MapleMonster attacker = map.getMonsterByOid(packet.from());
      MapleMonster damaged = map.getMonsterByOid(packet.to());

      if (attacker != null && damaged != null) {
         int maxDmg = calcMaxDamage(attacker, damaged, packet.magic());

         int dmg = packet.damage();
         if (dmg > maxDmg) {
            AutoBanFactory.DAMAGE_HACK.alert(client.getPlayer(), "Possible packet editing hypnotize damage exploit.");

            FilePrinter.printError(FilePrinter.EXPLOITS + client.getPlayer().getName() + ".txt", client.getPlayer().getName() + " had hypnotized " + MapleMonsterInformationProvider.getInstance().getMobNameFromId(attacker.id()) + " to attack " + MapleMonsterInformationProvider.getInstance().getMobNameFromId(damaged.id()) + " with damage " + dmg + " (max: " + maxDmg + ")");
            dmg = maxDmg;
         }

         map.damageMonster(chr, damaged, dmg);
         int finalDmg = dmg;
         MasterBroadcaster.getInstance().sendToAllInMap(map, new DamageMonster(packet.to(), finalDmg), false, chr);
      }
   }
}
