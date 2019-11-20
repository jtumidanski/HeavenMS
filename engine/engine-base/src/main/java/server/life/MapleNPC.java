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
import server.life.AbstractLoadedMapleLife;
import server.maps.MapleMapObjectType;
import server.processor.MapleShopProcessor;

public class MapleNPC extends AbstractLoadedMapleLife {
   private MapleNPCStats stats;

   public MapleNPC(int id, MapleNPCStats stats) {
      super(id);
      this.stats = stats;
   }

   public boolean hasShop() {
      return MapleShopFactory.getInstance().getShopForNPC(id()) != null;
   }

   public void sendShop(MapleClient c) {
      MapleShopProcessor.getInstance().sendShop(MapleShopFactory.getInstance().getShopForNPC(id()), c);
   }

   @Override
   public MapleMapObjectType type() {
      return MapleMapObjectType.NPC;
   }

   public String getName() {
      return stats.name();
   }
}
