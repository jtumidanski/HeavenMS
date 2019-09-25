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
package server;

import java.util.HashMap;
import java.util.Map;

import server.processor.MapleShopProcessor;

/**
 * @author Matze
 */
public class MapleShopFactory {

   private static MapleShopFactory instance = new MapleShopFactory();
   private Map<Integer, MapleShop> shops = new HashMap<>();
   private Map<Integer, MapleShop> npcShops = new HashMap<>();

   public static MapleShopFactory getInstance() {
      return instance;
   }

   private MapleShop loadShop(int id, boolean isShopId) {
      MapleShop ret = MapleShopProcessor.getInstance().createFromDB(id, isShopId);
      if (ret != null) {
         shops.put(ret.id(), ret);
         npcShops.put(ret.npcId(), ret);
      } else if (isShopId) {
         shops.put(id, null);
      } else {
         npcShops.put(id, null);
      }
      return ret;
   }

   public MapleShop getShop(int shopId) {
      if (shops.containsKey(shopId)) {
         return shops.get(shopId);
      }
      return loadShop(shopId, true);
   }

   public MapleShop getShopForNPC(int npcId) {
      if (npcShops.containsKey(npcId)) {
         return npcShops.get(npcId);
      }
      return loadShop(npcId, false);
   }

   public void reloadShops() {
      shops.clear();
      npcShops.clear();
   }
}
