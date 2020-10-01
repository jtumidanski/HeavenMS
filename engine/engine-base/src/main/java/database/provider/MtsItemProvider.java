package database.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import database.transformer.MtsItemInfoTransformer;
import database.transformer.TransferItemTransformer;
import client.inventory.Item;
import entity.mts.MtsItem;
import server.MTSItemInfo;
import tools.Pair;

public class MtsItemProvider extends AbstractQueryExecutor {
   private static MtsItemProvider instance;

   public static MtsItemProvider getInstance() {
      if (instance == null) {
         instance = new MtsItemProvider();
      }
      return instance;
   }

   private MtsItemProvider() {
   }

   public List<MTSItemInfo> getByTabAndType(EntityManager entityManager, int tab, int type, int limit) {
      TypedQuery<MtsItem> query = entityManager.createQuery("FROM MtsItem m WHERE m.tab = :tab AND m.type = :type AND m.transfer = 0 ORDER BY m.id DESC", MtsItem.class);
      query.setParameter("tab", tab);
      query.setParameter("type", type);
      query.setMaxResults(limit);
      return getResultList(query, new MtsItemInfoTransformer());
   }

   public long countByTabAndType(EntityManager entityManager, int tab, int type) {
      TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(*) FROM MtsItem m WHERE m.tab = :tab AND m.type = :type and m.transfer = 0", Long.class);
      query.setParameter("tab", tab);
      query.setParameter("type", type);
      return getSingleWithDefault(query, 0L);
   }

   public List<MTSItemInfo> getByTab(EntityManager entityManager, int tab, int limit) {
      TypedQuery<MtsItem> query = entityManager.createQuery("FROM MtsItem m WHERE m.tab = :tab AND m.transfer = 0 ORDER BY m.id DESC", MtsItem.class);
      query.setParameter("tab", tab);
      query.setMaxResults(limit);
      return getResultList(query, new MtsItemInfoTransformer());
   }

   public long countByTab(EntityManager entityManager, int tab) {
      TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(*) FROM MtsItem m WHERE m.tab = :tab AND m.transfer = 0", Long.class);
      query.setParameter("tab", tab);
      return getSingleWithDefault(query, 0L);
   }

   public long countBySeller(EntityManager entityManager, int characterId) {
      TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(*) FROM MtsItem m WHERE m.seller = :seller", Long.class);
      query.setParameter("seller", characterId);
      return getSingleWithDefault(query, 0L);
   }

   public Optional<MTSItemInfo> getById(EntityManager entityManager, int id) {
      TypedQuery<MtsItem> query = entityManager.createQuery("FROM MtsItem m WHERE m.id = :id", MtsItem.class);
      query.setParameter("id", id);
      return getSingleOptional(query, new MtsItemInfoTransformer());
   }

   public Optional<Pair<Integer, Integer>> getSaleInfoById(EntityManager entityManager, int id) {
      Query query = entityManager.createQuery("SELECT m.seller, m.price FROM MtsItem m WHERE m.id = :id ORDER BY m.id DESC");
      query.setParameter("id", id);
      query.setMaxResults(1);
      try {
         Object[] result = (Object[]) query.getSingleResult();
         return Optional.of(new Pair<>((int) result[0], (int) result[1]));
      } catch (NoResultException exception) {
         return Optional.empty();
      }
   }

   public List<MTSItemInfo> getTransferItems(EntityManager entityManager, int sellerId) {
      TypedQuery<MtsItem> query = entityManager.createQuery("FROM MtsItem m WHERE m.transfer = 1 AND m.seller = :seller ORDER BY m.id DESC", MtsItem.class);
      query.setParameter("seller", sellerId);
      return getResultList(query, new MtsItemInfoTransformer());
   }

   public Optional<Item> getTransferItem(EntityManager entityManager, int characterId, int itemId) {
      TypedQuery<MtsItem> query = entityManager.createQuery("FROM MtsItem m WHERE m.seller = :seller AND m.transfer = 1 AND m.id = :id ORDER BY m.id DESC", MtsItem.class);
      query.setParameter("seller", characterId);
      query.setParameter("id", itemId);
      return getSingleOptional(query, new TransferItemTransformer());
   }

   public boolean isItemForSaleBySomeoneElse(EntityManager entityManager, int itemId, int characterId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT m.id FROM MtsItem m WHERE m.id = :id AND m.seller <> :seller", Integer.class);
      query.setParameter("id", itemId);
      query.setParameter("seller", characterId);
      return resultExists(query);
   }

   public List<MTSItemInfo> getUnsoldItems(EntityManager entityManager, int sellerId) {
      TypedQuery<MtsItem> query = entityManager.createQuery("FROM MtsItem m WHERE m.seller = :seller AND m.transfer = 0 ORDER BY m.id DESC", MtsItem.class);
      query.setParameter("seller", sellerId);
      return getResultList(query, new MtsItemInfoTransformer());
   }

   public long countSearchItems(EntityManager entityManager, int tab, int type, int characterId, String search, List<Pair<Integer, String>> items) {
      StringBuilder clause = constructSearchClause(characterId, search, items);
      TypedQuery<Long> query;
      if (type != 0) {
         query = entityManager.createQuery("SELECT COUNT(*) FROM MtsItem m WHERE m.tab = ? " + clause + " AND m.type = ? AND m.transfer = 0", Long.class);
         query.setParameter("tab", tab);
         query.setParameter("type", type);
      } else {
         query = entityManager.createQuery("SELECT COUNT(*) FROM MtsItem m WHERE m.tab = ? " + clause + " AND m.transfer = 0", Long.class);
         query.setParameter("tab", tab);
      }
      return getSingleWithDefault(query, 0L);
   }

   public List<MTSItemInfo> getSearchItems(EntityManager entityManager, int tab, int type, int characterId, String search, int page, List<Pair<Integer, String>> items) {
      StringBuilder clause = constructSearchClause(characterId, search, items);
      TypedQuery<MtsItem> query;
      if (type != 0) {
         query = entityManager.createQuery("FROM MtsItem m WHERE m.tab = ? " + clause + " AND m.type = ? AND m.transfer = 0 ORDER BY m.id DESC", MtsItem.class);
         query.setParameter("tab", tab);
         query.setParameter("type", type);
      } else {
         query = entityManager.createQuery("FROM MtsItem m WHERE m.tab = ? " + clause + " AND m.transfer = 0 ORDER BY m.id DESC", MtsItem.class);
         query.setParameter("tab", tab);
      }
      query.setMaxResults(page * 16);
      return getResultList(query, new MtsItemInfoTransformer());
   }

   private StringBuilder constructSearchClause(int characterId, String search, List<Pair<Integer, String>> items) {
      StringBuilder listOfItems = new StringBuilder();
      if (characterId != 0) {
         List<String> retItems = new ArrayList<>();
         for (Pair<Integer, String> itemPair : items) {
            if (itemPair.getRight().toLowerCase().contains(search.toLowerCase())) {
               retItems.add(" m.itemId = " + itemPair.getLeft() + " OR ");
            }
         }
         listOfItems.append(" AND (");
         if (retItems.size() > 0) {
            for (String singleRetItem : retItems) {
               listOfItems.append(singleRetItem);
            }
            listOfItems.append(" m.itemId = 0 )");
         }
      } else {
         listOfItems = new StringBuilder(" AND m.sellerName LIKE CONCAT('%','" + search + "', '%')");
      }
      return listOfItems;
   }
}