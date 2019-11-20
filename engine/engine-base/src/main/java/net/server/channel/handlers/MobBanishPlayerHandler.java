/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2018 RonanLana

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

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.MobBanishPlayerPacket;
import net.server.channel.packet.reader.MobBanishPlayerReader;
import server.life.BanishInfo;
import server.life.MapleMonster;

public final class MobBanishPlayerHandler extends AbstractPacketHandler<MobBanishPlayerPacket> {
   @Override
   public Class<MobBanishPlayerReader> getReaderClass() {
      return MobBanishPlayerReader.class;
   }

   @Override
   public void handlePacket(MobBanishPlayerPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      MapleMonster mob = chr.getMap().getMonsterById(packet.mobId());

      if (mob != null) {
         BanishInfo banishInfo = mob.getBanish();
         if (banishInfo != null) {
            chr.changeMapBanish(banishInfo.map(), banishInfo.portal(), banishInfo.msg());
         }
      }
   }
}