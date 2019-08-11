package client.database.provider;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import client.database.AbstractQueryExecutor;
import constants.ItemConstants;
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

   public List<MapleShopItem> getItemsForShop(Connection connection, int shopId, Set<Integer> rechargeableItems) {
      String sql = "SELECT itemid, price, pitch FROM shopitems WHERE shopid = ? ORDER BY position DESC";
      return getList(connection, sql, ps -> ps.setInt(1, shopId), rs -> {
         List<MapleShopItem> shopItemData = new ArrayList<>();
         List<Integer> recharges = new ArrayList<>(rechargeableItems);
         while (rs != null && rs.next()) {
            if (ItemConstants.isRechargeable(rs.getInt("itemid"))) {
               MapleShopItem starItem = new MapleShopItem((short) 1, rs.getInt("itemid"), rs.getInt("price"), rs.getInt("pitch"));
               shopItemData.add(starItem);
               if (rechargeableItems.contains(starItem.getItemId())) {
                  recharges.remove(Integer.valueOf(starItem.getItemId()));
               }
            } else {
               shopItemData.add(new MapleShopItem((short) 1000, rs.getInt("itemid"), rs.getInt("price"), rs.getInt("pitch")));
            }
         }
         for (Integer recharge : recharges) {
            shopItemData.add(new MapleShopItem((short) 1000, recharge, 0, 0));
         }
         return shopItemData;
      });
   }
}