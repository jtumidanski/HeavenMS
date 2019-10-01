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
package server.life;

import client.MapleClient;
import server.MapleShopFactory;
import server.maps.MapleMapObjectType;
import server.processor.MapleShopProcessor;
import tools.MaplePacketCreator;
import tools.PacketCreator;
import tools.packet.spawn.RemoveNPCController;
import tools.packet.spawn.SpawnNPC;
import tools.packet.spawn.SpawnNPCRequestController;

public class MapleNPC extends AbstractLoadedMapleLife {
   private MapleNPCStats stats;

   public MapleNPC(int id, MapleNPCStats stats) {
      super(id);
      this.stats = stats;
   }

   public boolean hasShop() {
      return MapleShopFactory.getInstance().getShopForNPC(getId()) != null;
   }

   public void sendShop(MapleClient c) {
      MapleShopProcessor.getInstance().sendShop(MapleShopFactory.getInstance().getShopForNPC(getId()), c);
   }

   @Override
   public void sendSpawnData(MapleClient client) {
      PacketCreator.announce(client, new SpawnNPC(this));
      PacketCreator.announce(client, new SpawnNPCRequestController(this, true));
   }

   @Override
   public void sendDestroyData(MapleClient client) {
      PacketCreator.announce(client, new RemoveNPCController(getObjectId()));
      client.announce(MaplePacketCreator.removeNPC(getObjectId()));
   }

   @Override
   public MapleMapObjectType getType() {
      return MapleMapObjectType.NPC;
   }

   public String getName() {
      return stats.name();
   }
}
