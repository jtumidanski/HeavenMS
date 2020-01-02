package server;

import java.util.HashMap;
import java.util.Map;

import server.processor.MapleShopProcessor;

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
