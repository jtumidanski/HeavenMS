package database.administrator;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import accessor.AbstractQueryExecutor;
import entity.InventoryEquipment;

public class InventoryEquipmentAdministrator extends AbstractQueryExecutor {
   private static InventoryEquipmentAdministrator instance;

   public static InventoryEquipmentAdministrator getInstance() {
      if (instance == null) {
         instance = new InventoryEquipmentAdministrator();
      }
      return instance;
   }

   private InventoryEquipmentAdministrator() {
   }

   public void create(EntityManager entityManager, int inventoryItemId, int upgradeSlots, int level, int strength,
                      int dexterity, int intelligence, int luck, int hp, int mp, int weaponAttack, int magicAttack,
                      int weaponDefense, int magicDefense, int accuracy, int avoidability, int hands, int speed,
                      int jump, int locked, int vicious, int itemLevel, float itemExp, int ringId) {
      InventoryEquipment inventoryEquipment = new InventoryEquipment();
      inventoryEquipment.setInventoryItemId(inventoryItemId);
      inventoryEquipment.setUpgradeSlots(upgradeSlots);
      inventoryEquipment.setLevel(level);
      inventoryEquipment.setStr(strength);
      inventoryEquipment.setDex(dexterity);
      inventoryEquipment.setIntelligence(intelligence);
      inventoryEquipment.setLuk(luck);
      inventoryEquipment.setHp(hp);
      inventoryEquipment.setMp(mp);
      inventoryEquipment.setWatk(weaponAttack);
      inventoryEquipment.setMatk(magicAttack);
      inventoryEquipment.setWdef(weaponDefense);
      inventoryEquipment.setMdef(magicDefense);
      inventoryEquipment.setAcc(accuracy);
      inventoryEquipment.setAvoid(avoidability);
      inventoryEquipment.setHands(hands);
      inventoryEquipment.setSpeed(speed);
      inventoryEquipment.setJump(jump);
      inventoryEquipment.setLocked(locked);
      inventoryEquipment.setVicious(vicious);
      inventoryEquipment.setItemLevel(itemLevel);
      inventoryEquipment.setItemExp(itemExp);
      inventoryEquipment.setRingId(ringId);
      insert(entityManager, inventoryEquipment);
   }

   public void updateRing(EntityManager entityManager, int ringId, int partnerRingId) {
      entityManager.getTransaction().begin();

      Query ringQuery = entityManager.createQuery("UPDATE InventoryEquipment SET ringId = -1 WHERE ringId = :ringId");
      ringQuery.setParameter("ringId", ringId);
      ringQuery.executeUpdate();

      Query partnerRingQuery = entityManager.createQuery("UPDATE InventoryEquipment SET ringId = -1 WHERE ringId = :ringId");
      partnerRingQuery.setParameter("ringId", partnerRingId);
      partnerRingQuery.executeUpdate();

      entityManager.getTransaction().commit();
   }

   public void deleteById(EntityManager entityManager, int inventoryItemId) {
      Query query = entityManager.createQuery("DELETE FROM InventoryEquipment WHERE inventoryItemId = :inventoryItemId");
      query.setParameter("inventoryItemId", inventoryItemId);
      execute(entityManager, query);
   }
}