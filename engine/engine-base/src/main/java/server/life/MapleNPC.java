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
