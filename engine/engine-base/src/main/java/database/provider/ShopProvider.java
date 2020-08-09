package database.provider;

import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import entity.Shop;
import server.MapleShop;

public class ShopProvider extends AbstractQueryExecutor {
   private static ShopProvider instance;

   public static ShopProvider getInstance() {
      if (instance == null) {
         instance = new ShopProvider();
      }
      return instance;
   }

   private ShopProvider() {
   }

   public Optional<MapleShop> getById(EntityManager entityManager, int shopId) {
      TypedQuery<Shop> query = entityManager.createQuery("FROM Shop s WHERE s.shopId = :shopId", Shop.class);
      query.setParameter("shopId", shopId);
      try {
         Shop shop = query.getSingleResult();
         return Optional.of(processGetShopResultSet(shop));
      } catch (NoResultException exception) {
         return Optional.empty();
      }
   }

   public Optional<MapleShop> getByNPC(EntityManager entityManager, int npcId) {
      TypedQuery<Shop> query = entityManager.createQuery("FROM Shop s WHERE s.npcId = :npcId", Shop.class);
      query.setParameter("npcId", npcId);
      try {
         Shop shop = query.getSingleResult();
         return Optional.of(processGetShopResultSet(shop));
      } catch (NoResultException exception) {
         return Optional.empty();
      }
   }

   private MapleShop processGetShopResultSet(Shop shop) {
      return new MapleShop(shop.getShopId(), shop.getNpcId());
   }
}