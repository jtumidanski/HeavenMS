package database.administrator;

import java.awt.Point;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.MapleCharacter;
import database.AbstractQueryExecutor;
import client.inventory.Item;
import entity.PlayerNpc;
import entity.PlayerNpcEquip;
import entity.PlayerNpcField;
import server.maps.MapleMap;

public class PlayerNpcAdministrator extends AbstractQueryExecutor {
   private static PlayerNpcAdministrator instance;

   public static PlayerNpcAdministrator getInstance() {
      if (instance == null) {
         instance = new PlayerNpcAdministrator();
      }
      return instance;
   }

   private PlayerNpcAdministrator() {
   }

   public void setPodium(EntityManager entityManager, int value, int worldId, int mapId) {
      Query query = entityManager.createQuery("UPDATE PlayerNpcField SET podium = :podium WHERE world = :world AND map = :map");
      query.setParameter("podium", value);
      query.setParameter("world", worldId);
      query.setParameter("map", mapId);
      execute(entityManager, query);
   }

   public void setStep(EntityManager entityManager, int value, int worldId, int mapId) {
      Query query = entityManager.createQuery("UPDATE PlayerNpcField SET step = :step WHERE world = :world AND map = :map");
      query.setParameter("step", value);
      query.setParameter("world", worldId);
      query.setParameter("map", mapId);
      execute(entityManager, query);
   }

   public void addPodium(EntityManager entityManager, int value, int worldId, int mapId) {
      PlayerNpcField playerNpcField = new PlayerNpcField();
      playerNpcField.setPodium(value);
      playerNpcField.setWorld(worldId);
      playerNpcField.setMap(mapId);
      insert(entityManager, playerNpcField);
   }

   public void addStep(EntityManager entityManager, int value, int worldId, int mapId) {
      PlayerNpcField playerNpcField = new PlayerNpcField();
      playerNpcField.setStep(value);
      playerNpcField.setWorld(worldId);
      playerNpcField.setMap(mapId);
      insert(entityManager, playerNpcField);
   }

   public int create(EntityManager entityManager, MapleCharacter character, MapleMap map, Point position, int scriptId, int worldRank, int overallRank, int worldJobRank) {
      PlayerNpc playerNpc = new PlayerNpc();
      playerNpc.setName(character.getName());
      playerNpc.setHair(character.getHair());
      playerNpc.setFace(character.getFace());
      playerNpc.setSkin(character.getSkinColor().getId());
      playerNpc.setGender(character.getGender());
      playerNpc.setX(position.x);
      playerNpc.setCy(position.y);
      playerNpc.setWorld(character.getWorld());
      playerNpc.setMap(map.getId());
      playerNpc.setScriptId(scriptId);
      playerNpc.setDir(1);
      playerNpc.setFh(map.getFootholds().findBelow(position).id());
      playerNpc.setRx0(position.x + 50);
      playerNpc.setRx1(position.x - 50);
      playerNpc.setWorldRank(worldRank);
      playerNpc.setOverallRank(overallRank);
      playerNpc.setWorldJobRank(worldJobRank);
      playerNpc.setJob((character.getJob().getId() / 100) * 100);
      insert(entityManager, playerNpc);
      return playerNpc.getId();
   }

   public void createEquips(EntityManager entityManager, int npcId, Iterable<Item> items) {
      List<PlayerNpcEquip> playerNpcEquipList = StreamSupport.stream(items.spliterator(), false).map(item -> {
         PlayerNpcEquip playerNpcEquip = new PlayerNpcEquip();
         playerNpcEquip.setNpcId(npcId);
         playerNpcEquip.setEquipId(item.id());
         playerNpcEquip.setEquipPosition((int) item.position());
         return playerNpcEquip;
      }).collect(Collectors.toList());
      insertBulk(entityManager, playerNpcEquipList);
   }

   public void deleteById(EntityManager entityManager, int npcId) {
      Query query = entityManager.createQuery("DELETE FROM PlayerNpc WHERE id = :id");
      query.setParameter("id", npcId);
      execute(entityManager, query);
   }

   public void deleteEquipById(EntityManager entityManager, int npcId) {
      Query query = entityManager.createQuery("DELETE FROM PlayerNpcEquip WHERE npcId = :npcId");
      query.setParameter("npcId", npcId);
      execute(entityManager, query);
   }

   public void deleteAllNpcs(EntityManager entityManager) {
      entityManager.getTransaction().begin();
      Query deleteFromPlayerNpc = entityManager.createQuery("DELETE FROM PlayerNpc");
      deleteFromPlayerNpc.executeUpdate();

      Query deleteFromPlayerNpcEquip = entityManager.createQuery("DELETE FROM PlayerNpcEquip");
      deleteFromPlayerNpcEquip.executeUpdate();

      Query deleteFromPlayerNpcField = entityManager.createQuery("DELETE FROM PlayerNpcField");
      deleteFromPlayerNpcField.executeUpdate();
      entityManager.getTransaction().commit();
   }

   public void updatePosition(EntityManager entityManager, int npcId, int x, int cy, int fh, int rx0, int rx1) {
      Query query = entityManager.createQuery("UPDATE PlayerNpc SET x = :x, cy = :cy, fh = :fh, rx0 = :rx0, rx1 = :rx1 WHERE id = :id");
      query.setParameter("x", x);
      query.setParameter("cy", cy);
      query.setParameter("fh", fh);
      query.setParameter("rx0", rx0);
      query.setParameter("rx1", rx1);
      query.setParameter("id", npcId);
      execute(entityManager, query);
   }
}