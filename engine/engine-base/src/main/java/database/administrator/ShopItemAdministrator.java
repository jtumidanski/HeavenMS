package database.administrator;

import javax.persistence.EntityManager;

import database.AbstractQueryExecutor;
import entity.ShopItem;

public class ShopItemAdministrator extends AbstractQueryExecutor {
   private static ShopItemAdministrator instance;

   public static ShopItemAdministrator getInstance() {
      if (instance == null) {
         instance = new ShopItemAdministrator();
      }
      return instance;
   }

   private ShopItemAdministrator() {
   }

   public int create(EntityManager entityManager, int shopId, int itemId, int price, int pitch, int position) {
      ShopItem shopItem = new ShopItem();
      shopItem.setShopId(shopId);
      shopItem.setItemId(itemId);
      shopItem.setPrice(price);
      shopItem.setPitch(pitch);
      shopItem.setPosition(position);
      insert(entityManager, shopItem);
      return shopItem.getShopItemId();
   }
}