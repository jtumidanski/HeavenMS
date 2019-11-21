package client.database.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.database.AbstractQueryExecutor;
import constants.inventory.ItemConstants;
import server.MapleShopItem;

public class ShopItemProvider extends AbstractQueryExecutor {
   private static ShopItemProvider instance;

   public static ShopItemProvider getInstance() {
      if (instance == null) {
         instance = new ShopItemProvider();
      }
      return instance;
   }

   private ShopItemProvider() {
   }

   public List<MapleShopItem> getItemsForShop(EntityManager entityManager, int shopId, Set<Integer> rechargeableItems) {
      Query query = entityManager.createQuery("SELECT s.itemId, s.price, s.pitch FROM ShopItem s WHERE s.shopId = :shopId");
      query.setParameter("shopId", shopId);
      List<Object[]> results = (List<Object[]>) query.getResultList();

      List<MapleShopItem> shopItemData = new ArrayList<>();
      List<Integer> recharges = new ArrayList<>(rechargeableItems);
      for (Object[] result : results) {
         int itemId = (int) result[0];
         int price = (int) result[1];
         int pitch = (int) result[2];

         if (ItemConstants.isRechargeable(itemId)) {
            MapleShopItem starItem = new MapleShopItem((short) 1, itemId, price, pitch);
            shopItemData.add(starItem);
            if (rechargeableItems.contains(starItem.itemId())) {
               recharges.remove(Integer.valueOf(starItem.itemId()));
            }
         } else {
            shopItemData.add(new MapleShopItem((short) 1000, itemId, price, pitch));
         }
      }
      for (Integer recharge : recharges) {
         shopItemData.add(new MapleShopItem((short) 1000, recharge, 0, 0));
      }
      return shopItemData;
   }
}