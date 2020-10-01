package database.provider;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.GetInventoryItems;
import database.transformer.InventoryEquipTransformer;
import database.transformer.InventoryItemTransformer;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import tools.Pair;

public class InventoryItemProvider extends AbstractQueryExecutor {
   private static InventoryItemProvider instance;

   public static InventoryItemProvider getInstance() {
      if (instance == null) {
         instance = new InventoryItemProvider();
      }
      return instance;
   }

   private InventoryItemProvider() {
   }

   public List<Pair<Item, MapleInventoryType>> getItemsByCharacterAndType(EntityManager entityManager, int characterId, int type, boolean loggedIn) {
      TypedQuery<GetInventoryItems> query;
      if (loggedIn) {
         query = entityManager.createQuery(
               "SELECT NEW client.database.data.GetInventoryItems(ii.inventoryType, ii.itemId, ii.position, ii.quantity, " +
                     "ii.petId, ii.owner, ii.expiration, ii.giftFrom, ii.flag, coalesce(ie.acc, 0), coalesce(ie.avoid, 0), coalesce(ie.dex, 0), coalesce(ie.hands, 0), coalesce(ie.hp, 0), " +
                     "coalesce(ie.intelligence, 0), coalesce(ie.jump, 0), coalesce(ie.vicious, 0), coalesce(ie.luk, 0), coalesce(ie.matk, 0), coalesce(ie.mdef, 0), coalesce(ie.mp, 0), coalesce(ie.speed, 0), coalesce(ie.str, 0), coalesce(ie.watk, 0), " +
                     "coalesce(ie.wdef, 0), coalesce(ie.upgradeSlots, 0), coalesce(ie.level, 0), coalesce(ie.itemExp, 0), coalesce(ie.itemLevel, 0), coalesce(ie.ringId, 0), ii.characterId) " +
                     "FROM InventoryItem ii LEFT JOIN InventoryEquipment ie ON ii.inventoryItemId = ie.inventoryItemId " +
                     "WHERE ii.type = :type AND ii.characterId = :characterId AND ii.inventoryType = :inventoryType", GetInventoryItems.class);
         query.setParameter("type", type);
         query.setParameter("characterId", characterId);
         query.setParameter("inventoryType", (int) MapleInventoryType.EQUIPPED.getType());
      } else {
         query = entityManager.createQuery(
               "SELECT NEW client.database.data.GetInventoryItems(ii.inventoryType, ii.itemId, ii.position, ii.quantity, " +
                     "ii.petId, ii.owner, ii.expiration, ii.giftFrom, ii.flag, coalesce(ie.acc, 0), coalesce(ie.avoid, 0), " +
                     "coalesce(ie.dex, 0), coalesce(ie.hands, 0), coalesce(ie.hp, 0), coalesce(ie.intelligence, 0), " +
                     "coalesce(ie.jump, 0), coalesce(ie.vicious, 0), coalesce(ie.luk, 0), coalesce(ie.matk, 0), " +
                     "coalesce(ie.mdef, 0), coalesce(ie.mp, 0), coalesce(ie.speed, 0), coalesce(ie.str, 0), " +
                     "coalesce(ie.watk, 0), coalesce(ie.wdef, 0), coalesce(ie.upgradeSlots, 0), coalesce(ie.level, 0), " +
                     "coalesce(ie.itemExp, 0), coalesce(ie.itemLevel, 0), coalesce(ie.ringId, 0), ii.characterId) " +
                     "FROM InventoryItem ii LEFT JOIN InventoryEquipment ie ON ii.inventoryItemId = ie.inventoryItemId " +
                     "WHERE ii.type = :type AND ii.characterId = :characterId", GetInventoryItems.class);
         query.setParameter("type", type);
         query.setParameter("characterId", characterId);
      }
      return getResultList(query, new InventoryItemTransformer());
   }

   public List<Pair<Item, MapleInventoryType>> getItemsByAccountAndType(EntityManager entityManager, int accountId, int type, boolean loggedIn) {
      TypedQuery<GetInventoryItems> query;
      if (loggedIn) {
         query = entityManager.createQuery(
               "SELECT NEW client.database.data.GetInventoryItems(ii.inventoryType, ii.itemId, ii.position, ii.quantity, " +
                     "ii.petId, ii.owner, ii.expiration, ii.giftFrom, ii.flag, coalesce(ie.acc, 0), coalesce(ie.avoid, 0), coalesce(ie.dex, 0), coalesce(ie.hands, 0), coalesce(ie.hp, 0), " +
                     "coalesce(ie.intelligence, 0), coalesce(ie.jump, 0), coalesce(ie.vicious, 0), coalesce(ie.luk, 0), coalesce(ie.matk, 0), coalesce(ie.mdef, 0), coalesce(ie.mp, 0), coalesce(ie.speed, 0), coalesce(ie.str, 0), coalesce(ie.watk, 0), " +
                     "coalesce(ie.wdef, 0), coalesce(ie.upgradeSlots, 0), coalesce(ie.level, 0), coalesce(ie.itemExp, 0), coalesce(ie.itemLevel, 0), coalesce(ie.ringId, 0), ii.characterId) " +
                     "FROM InventoryItem ii LEFT JOIN InventoryEquipment ie ON ii.inventoryItemId = ie.inventoryItemId " +
                     "WHERE ii.type = :type AND ii.accountId = :accountId AND ii.inventoryType = :inventoryType", GetInventoryItems.class);
         query.setParameter("type", type);
         query.setParameter("accountId", accountId);
         query.setParameter("inventoryType", (int) MapleInventoryType.EQUIPPED.getType());
      } else {
         query = entityManager.createQuery(
               "SELECT NEW client.database.data.GetInventoryItems(ii.inventoryType, ii.itemId, ii.position, ii.quantity, " +
                     "ii.petId, ii.owner, ii.expiration, ii.giftFrom, ii.flag, coalesce(ie.acc, 0), coalesce(ie.avoid, 0), coalesce(ie.dex, 0), coalesce(ie.hands, 0), coalesce(ie.hp, 0), " +
                     "coalesce(ie.intelligence, 0), coalesce(ie.jump, 0), coalesce(ie.vicious, 0), coalesce(ie.luk, 0), coalesce(ie.matk, 0), coalesce(ie.mdef, 0), coalesce(ie.mp, 0), coalesce(ie.speed, 0), coalesce(ie.str, 0), coalesce(ie.watk, 0), " +
                     "coalesce(ie.wdef, 0), coalesce(ie.upgradeSlots, 0), coalesce(ie.level, 0), coalesce(ie.itemExp, 0), coalesce(ie.itemLevel, 0), coalesce(ie.ringId, 0), ii.characterId) " +
                     "FROM InventoryItem ii LEFT JOIN InventoryEquipment ie ON ii.inventoryItemId = ie.inventoryItemId " +
                     "WHERE ii.type = :type AND ii.accountId = :accountId", GetInventoryItems.class);
         query.setParameter("type", type);
         query.setParameter("accountId", accountId);
      }
      return getResultList(query, new InventoryItemTransformer());
   }

   public List<Pair<Item, Integer>> getEquipsByCharacter(EntityManager entityManager, int characterId, boolean loggedIn) {
      TypedQuery<GetInventoryItems> query = constructGetEquipsByQuery(entityManager, false, loggedIn, characterId);
      return getResultList(query, new InventoryEquipTransformer());
   }

   public List<Pair<Item, Integer>> getEquipsByAccount(EntityManager entityManager, int accountId, boolean loggedIn) {
      TypedQuery<GetInventoryItems> query = constructGetEquipsByQuery(entityManager, true, loggedIn, accountId);
      return getResultList(query, new InventoryEquipTransformer());
   }

   private TypedQuery<GetInventoryItems> constructGetEquipsByQuery(EntityManager entityManager, boolean account, boolean loggedIn, int id) {
      TypedQuery<GetInventoryItems> query;
      if (account) {
         if (loggedIn) {
            query = entityManager.createQuery(
                  "SELECT NEW client.database.data.GetInventoryItems(ii.inventoryType, ii.itemId, ii.position, ii.quantity, " +
                        "ii.petId, ii.owner, ii.expiration, ii.giftFrom, ii.flag, coalesce(ie.acc, 0), coalesce(ie.avoid, 0), coalesce(ie.dex, 0), coalesce(ie.hands, 0), coalesce(ie.hp, 0), " +
                        "coalesce(ie.intelligence, 0), coalesce(ie.jump, 0), coalesce(ie.vicious, 0), coalesce(ie.luk, 0), coalesce(ie.matk, 0), coalesce(ie.mdef, 0), coalesce(ie.mp, 0), coalesce(ie.speed, 0), coalesce(ie.str, 0), coalesce(ie.watk, 0), " +
                        "coalesce(ie.wdef, 0), coalesce(ie.upgradeSlots, 0), coalesce(ie.level, 0), coalesce(ie.itemExp, 0), coalesce(ie.itemLevel, 0), coalesce(ie.ringId, 0), ii.characterId) " +
                        "FROM InventoryItem ii " +
                        "LEFT JOIN InventoryEquipment ie ON ii.inventoryItemId = ie.inventoryItemId " +
                        "LEFT JOIN Character c ON c.id = ii.characterId " +
                        "WHERE c.accountId = :accountId AND ii.inventoryType = :type", GetInventoryItems.class);
            query.setParameter("accountId", id);
            query.setParameter("type", (int) MapleInventoryType.EQUIPPED.getType());
         } else {
            query = entityManager.createQuery(
                  "SELECT NEW client.database.data.GetInventoryItems(ii.inventoryType, ii.itemId, ii.position, ii.quantity, " +
                        "ii.petId, ii.owner, ii.expiration, ii.giftFrom, ii.flag, coalesce(ie.acc, 0), coalesce(ie.avoid, 0), coalesce(ie.dex, 0), coalesce(ie.hands, 0), coalesce(ie.hp, 0), " +
                        "coalesce(ie.intelligence, 0), coalesce(ie.jump, 0), coalesce(ie.vicious, 0), coalesce(ie.luk, 0), coalesce(ie.matk, 0), coalesce(ie.mdef, 0), coalesce(ie.mp, 0), coalesce(ie.speed, 0), coalesce(ie.str, 0), coalesce(ie.watk, 0), " +
                        "coalesce(ie.wdef, 0), coalesce(ie.upgradeSlots, 0), coalesce(ie.level, 0), coalesce(ie.itemExp, 0), coalesce(ie.itemLevel, 0), coalesce(ie.ringId, 0), ii.characterId) " +
                        "FROM InventoryItem ii " +
                        "LEFT JOIN InventoryEquipment ie ON ii.inventoryItemId = ie.inventoryItemId " +
                        "LEFT JOIN Character c ON c.id = ii.characterId " +
                        "WHERE c.accountId = :accountId", GetInventoryItems.class);
            query.setParameter("accountId", id);
         }
      } else {
         if (loggedIn) {
            query = entityManager.createQuery(
                  "SELECT NEW client.database.data.GetInventoryItems(ii.inventoryType, ii.itemId, ii.position, ii.quantity, " +
                        "ii.petId, ii.owner, ii.expiration, ii.giftFrom, ii.flag, coalesce(ie.acc, 0), coalesce(ie.avoid, 0), coalesce(ie.dex, 0), coalesce(ie.hands, 0), coalesce(ie.hp, 0), " +
                        "coalesce(ie.intelligence, 0), coalesce(ie.jump, 0), coalesce(ie.vicious, 0), coalesce(ie.luk, 0), coalesce(ie.matk, 0), coalesce(ie.mdef, 0), coalesce(ie.mp, 0), coalesce(ie.speed, 0), coalesce(ie.str, 0), coalesce(ie.watk, 0), " +
                        "coalesce(ie.wdef, 0), coalesce(ie.upgradeSlots, 0), coalesce(ie.level, 0), coalesce(ie.itemExp, 0), coalesce(ie.itemLevel, 0), coalesce(ie.ringId, 0), ii.characterId) " +
                        "FROM InventoryItem ii " +
                        "LEFT JOIN InventoryEquipment ie ON ii.inventoryItemId = ie.inventoryItemId " +
                        "LEFT JOIN Character c ON c.id = ii.characterId " +
                        "WHERE c.id = :characterId AND ii.inventoryType = :type", GetInventoryItems.class);
            query.setParameter("characterId", id);
            query.setParameter("type", (int) MapleInventoryType.EQUIPPED.getType());
         } else {
            query = entityManager.createQuery(
                  "SELECT NEW client.database.data.GetInventoryItems(ii.inventoryType, ii.itemId, ii.position, ii.quantity, " +
                        "ii.petId, ii.owner, ii.expiration, ii.giftFrom, ii.flag, coalesce(ie.acc, 0), coalesce(ie.avoid, 0), coalesce(ie.dex, 0), coalesce(ie.hands, 0), coalesce(ie.hp, 0), " +
                        "coalesce(ie.intelligence, 0), coalesce(ie.jump, 0), coalesce(ie.vicious, 0), coalesce(ie.luk, 0), coalesce(ie.matk, 0), coalesce(ie.mdef, 0), coalesce(ie.mp, 0), coalesce(ie.speed, 0), coalesce(ie.str, 0), coalesce(ie.watk, 0), " +
                        "coalesce(ie.wdef, 0), coalesce(ie.upgradeSlots, 0), coalesce(ie.level, 0), coalesce(ie.itemExp, 0), coalesce(ie.itemLevel, 0), coalesce(ie.ringId, 0), ii.characterId) " +
                        "FROM InventoryItem ii " +
                        "LEFT JOIN InventoryEquipment ie ON ii.inventoryItemId = ie.inventoryItemId " +
                        "LEFT JOIN Character c ON c.id = ii.characterId " +
                        "WHERE c.id = :characterId", GetInventoryItems.class);
            query.setParameter("characterId", id);
         }
      }
      return query;
   }

   public List<Integer> getPetsForCharacter(EntityManager entityManager, int characterId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT i.petId FROM InventoryItem i WHERE i.characterId = :characterId AND i.petId > -1", Integer.class);
      query.setParameter("characterId", characterId);
      return query.getResultList();
   }

   public List<Pair<Integer, Integer>> get(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("SELECT i.inventoryItemId, i.petId FROM InventoryItem i WHERE i.characterId = :characterId");
      query.setParameter("characterId", characterId);
      List<Object[]> results = (List<Object[]>) query.getResultList();
      return results.stream().map(result -> new Pair<>((int) result[0], (int) result[1])).collect(Collectors.toList());
   }
}