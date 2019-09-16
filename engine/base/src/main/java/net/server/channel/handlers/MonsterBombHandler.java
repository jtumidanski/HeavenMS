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
import net.server.channel.packet.MonsterBombPacket;
import net.server.channel.packet.reader.MonsterBombReader;
import server.life.MapleMonster;
import tools.MaplePacketCreator;

public final class MonsterBombHandler extends AbstractPacketHandler<MonsterBombPacket> {
   @Override
   public Class<MonsterBombReader> getReaderClass() {
      return MonsterBombReader.class;
   }

   @Override
   public void handlePacket(MonsterBombPacket packet, MapleClient client) {
      MapleMonster monster = client.getPlayer().getMap().getMonsterByOid(packet.objectId());
      if (!client.getPlayer().isAlive() || monster == null) {
         return;
      }
      if (monster.getId() == 8500003 || monster.getId() == 8500004) {
         monster.getMap().broadcastMessage(MaplePacketCreator.killMonster(monster.getObjectId(), 4));
         client.getPlayer().getMap().removeMapObject(packet.objectId());
      }
   }
}
