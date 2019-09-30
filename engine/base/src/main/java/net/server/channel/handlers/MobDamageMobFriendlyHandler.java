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

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.MobDamageMobFriendlyPacket;
import net.server.channel.packet.reader.MobDamageMobFriendlyReader;
import scripting.event.EventInstanceManager;
import server.life.MapleMonster;
import server.maps.MapleMap;
import tools.MaplePacketCreator;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Randomizer;
import tools.ServerNoticeType;
import tools.packet.stat.EnableActions;

/**
 * @author Xotic & BubblesDev
 */

public final class MobDamageMobFriendlyHandler extends AbstractPacketHandler<MobDamageMobFriendlyPacket> {
   @Override
   public Class<MobDamageMobFriendlyReader> getReaderClass() {
      return MobDamageMobFriendlyReader.class;
   }

   @Override
   public void handlePacket(MobDamageMobFriendlyPacket packet, MapleClient client) {
      MapleMap map = client.getPlayer().getMap();
      MapleMonster monster = map.getMonsterByOid(packet.damage());

      if (monster == null || map.getMonsterByOid(packet.attacker()) == null) {
         return;
      }

      int damage = Randomizer.nextInt(((monster.getMaxHp() / 13 + monster.getPADamage() * 10)) * 2 + 500) / 10; //Beng's formula.

      if (monster.getHp() - damage < 1) {     // friendly dies
         if (monster.getId() == 9300102) {
            MessageBroadcaster.getInstance().sendMapServerNotice(map, ServerNoticeType.LIGHT_BLUE, "The Watch Hog has been injured by the aliens. Better luck next time...");
         } else if (monster.getId() == 9300061) {  //moon bunny
            MessageBroadcaster.getInstance().sendMapServerNotice(map, ServerNoticeType.LIGHT_BLUE, "The Moon Bunny went home because he was sick.");
         } else if (monster.getId() == 9300093) {   //tylus
            MessageBroadcaster.getInstance().sendMapServerNotice(map, ServerNoticeType.LIGHT_BLUE, "Tylus has fallen by the overwhelming forces of the ambush.");
         } else if (monster.getId() == 9300137) {   //juliet
            MessageBroadcaster.getInstance().sendMapServerNotice(map, ServerNoticeType.LIGHT_BLUE, "Juliet has fainted in the middle of the combat.");
         } else if (monster.getId() == 9300138) {   //romeo
            MessageBroadcaster.getInstance().sendMapServerNotice(map, ServerNoticeType.LIGHT_BLUE, "Romeo has fainted in the middle of the combat.");
         } else if (monster.getId() == 9400322 || monster.getId() == 9400327 || monster.getId() == 9400332) { //snowman
            MessageBroadcaster.getInstance().sendMapServerNotice(map, ServerNoticeType.LIGHT_BLUE, "The Snowman has melted on the heat of the battle.");
         } else if (monster.getId() == 9300162) {   //delli
            MessageBroadcaster.getInstance().sendMapServerNotice(map, ServerNoticeType.LIGHT_BLUE, "Delli vanished after the ambush, sheets still laying on the ground...");
         }

         map.killFriendlies(monster);
      } else {
         EventInstanceManager eim = map.getEventInstance();
         if (eim != null) {
            eim.friendlyDamaged(monster);
         }
      }

      monster.applyAndGetHpDamage(damage, false);
      int remainingHp = monster.getHp();
      if (remainingHp <= 0) {
         remainingHp = 0;
         map.removeMapObject(monster);
      }

      int finalRemainingHp = remainingHp;
      MasterBroadcaster.getInstance().sendToAllInMapRange(map, character -> MaplePacketCreator.MobDamageMobFriendly(monster, damage, finalRemainingHp), monster.getPosition());
      PacketCreator.announce(client, new EnableActions());
   }
}