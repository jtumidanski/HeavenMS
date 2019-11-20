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

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.DamageSummonPacket;
import net.server.channel.packet.reader.DamageSummonReader;
import server.maps.MapleMapObject;
import server.maps.MapleSummon;
import tools.MasterBroadcaster;
import tools.packet.monster.DamageSummon;

public final class DamageSummonHandler extends AbstractPacketHandler<DamageSummonPacket> {
   @Override
   public Class<DamageSummonReader> getReaderClass() {
      return DamageSummonReader.class;
   }

   @Override
   public void handlePacket(DamageSummonPacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      MapleMapObject mmo = player.getMap().getMapObject(packet.objectId());

      if (mmo instanceof MapleSummon) {
         MapleSummon summon = (MapleSummon) mmo;

         summon.addHP(-packet.damage());
         if (summon.getHP() <= 0) {
            player.cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
         }
         MasterBroadcaster.getInstance().sendToAllInMapRange(player.getMap(), new DamageSummon(player.getId(), packet.objectId(), packet.damage(), packet.monsterIdFrom()), player, summon.position());
      }
   }
}
