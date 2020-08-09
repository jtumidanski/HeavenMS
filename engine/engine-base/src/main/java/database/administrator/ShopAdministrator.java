package database.administrator;

import javax.persistence.EntityManager;

import accessor.AbstractQueryExecutor;
import entity.Shop;

public class ShopAdministrator extends AbstractQueryExecutor {
   private static ShopAdministrator instance;

   public static ShopAdministrator getInstance() {
      if (instance == null) {
         instance = new ShopAdministrator();
      }
      return instance;
   }

   private ShopAdministrator() {
   }

   public void create(EntityManager entityManager, int shopId, int npcId) {
      Shop shop = new Shop();
      shop.setShopId(shopId);
      shop.setNpcId(npcId);
      insert(entityManager, shop);
   }
}