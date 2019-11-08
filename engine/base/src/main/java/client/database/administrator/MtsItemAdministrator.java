package client.database.administrator;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;
import entity.mts.MtsItem;

public class MtsItemAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static MtsItemAdministrator instance;

   public static MtsItemAdministrator getInstance() {
      if (instance == null) {
         instance = new MtsItemAdministrator();
      }
      return instance;
   }

   private MtsItemAdministrator() {
   }

   @Override
   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT m.id FROM MtsCart m WHERE m.characterId = :characterId", Integer.class);
      query.setParameter("characterId", characterId);
      List<Integer> result = query.getResultList();
      if (result.isEmpty()) {
         return;
      }

      Query deleteQuery = entityManager.createQuery("DELETE FROM MtsItem WHERE id IN :ids");
      deleteQuery.setParameter("ids", result);
      execute(entityManager, deleteQuery);
   }

   public void deleteTransferItem(EntityManager entityManager, int itemId, int sellerId) {
      Query query = entityManager.createQuery("DELETE FROM MtsItem WHERE id = :id AND seller = :seller AND transfer = 1");
      query.setParameter("id", itemId);
      query.setParameter("seller", sellerId);
      execute(entityManager, query);
   }

   public void createItem(EntityManager entityManager, int tab, int type, int itemId, int quantity, long expiration, String giftFrom, int characterId, int price, String owner, String characterName, String date) {
      MtsItem mtsItem = new MtsItem();
      mtsItem.setTab(tab);
      mtsItem.setType(type);
      mtsItem.setItemId(itemId);
      mtsItem.setQuantity(quantity);
      mtsItem.setSeller(characterId);
      mtsItem.setExpiration(expiration);
      mtsItem.setGiftFrom(giftFrom);
      mtsItem.setPrice(price);
      mtsItem.setOwner(owner);
      mtsItem.setSellerName(characterName);
      mtsItem.setSellEnds(date);
      insert(entityManager, mtsItem);
   }

   private void baseInsertParam(int tab, int type, int itemId, int quantity, long expiration, String giftFrom, int characterId, int price, PreparedStatement ps) throws SQLException {
      ps.setInt(1, tab);
      ps.setInt(2, type);
      ps.setInt(3, itemId);
      ps.setInt(4, quantity);
      ps.setLong(5, expiration);
      ps.setString(6, giftFrom);
      ps.setInt(7, characterId);
      ps.setInt(8, price);
   }

   public void createEquip(EntityManager entityManager, int tab, int type, int itemId, int quantity, long expiration, String giftFrom, int characterId,
                           int price, int upgradeSlots, int level, int strength, int dexterity, int intelligence,
                           int luck, int hp, int mp, int weaponAttack, int magicAttack, int weaponDefense,
                           int magicDefense, int accuracy, int avoidability, int hands, int speed, int jump,
                           int locked, String owner, String characterName, String date, int vicious, int flag,
                           float itemExp, byte itemLevel, int ringId) {
      MtsItem mtsItem = new MtsItem();
      mtsItem.setTab(tab);
      mtsItem.setType(type);
      mtsItem.setItemId(itemId);
      mtsItem.setQuantity(quantity);
      mtsItem.setSeller(characterId);
      mtsItem.setPrice(price);
      mtsItem.setUpgradeSlots(upgradeSlots);
      mtsItem.setLevel(level);
      mtsItem.setStr(strength);
      mtsItem.setDex(dexterity);
      mtsItem.setIntelligence(intelligence);
      mtsItem.setLuk(luck);
      mtsItem.setHp(hp);
      mtsItem.setMp(mp);
      mtsItem.setWatk(weaponAttack);
      mtsItem.setMatk(magicAttack);
      mtsItem.setWdef(weaponDefense);
      mtsItem.setMdef(magicDefense);
      mtsItem.setAcc(accuracy);
      mtsItem.setAvoid(avoidability);
      mtsItem.setHands(hands);
      mtsItem.setSpeed(speed);
      mtsItem.setJump(jump);
      mtsItem.setLocked(locked);
      mtsItem.setOwner(owner);
      mtsItem.setSellerName(characterName);
      mtsItem.setSellEnds(date);
      mtsItem.setVicious(vicious);
      mtsItem.setFlag(flag);
      mtsItem.setItemExp(itemExp);
      mtsItem.setItemLevel((int) itemLevel);
      mtsItem.setRingId(ringId);
      insert(entityManager, mtsItem);
   }

   public void cancelSale(EntityManager entityManager, int characterId, int itemId) {
      Query query = entityManager.createQuery("UPDATE MtsItem  SET transfer = 1 WHERE id = :itemId AND seller = :characterId");
      query.setParameter("itemId", itemId);
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void transfer(EntityManager entityManager, int itemId, int sellerId) {
      Query query = entityManager.createQuery("UPDATE MtsItem SET seller = :sellerId, transfer = 1 WHERE id = :itemId");
      query.setParameter("sellerId", sellerId);
      query.setParameter("itemId", itemId);
      execute(entityManager, query);
   }
}