package client.database.administrator;

import java.awt.Point;
import java.sql.Connection;

import client.MapleCharacter;
import client.database.AbstractQueryExecutor;
import client.inventory.Item;
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

   public void setPodium(Connection connection, int value, int worldId, int mapId) {
      String sql = "UPDATE playernpcs_field SET podium = ? WHERE world = ? AND map = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, value);
         ps.setInt(2, worldId);
         ps.setInt(3, mapId);
      });
   }

   public void setStep(Connection connection, int value, int worldId, int mapId) {
      String sql = "UPDATE playernpcs_field SET step = ? WHERE world = ? AND map = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, value);
         ps.setInt(2, worldId);
         ps.setInt(3, mapId);
      });
   }

   public void addPodium(Connection connection, int value, int worldId, int mapId) {
      String sql = "INSERT INTO playernpcs_field (podium, world, map) VALUES (?, ?, ?)";
      execute(connection, sql, ps -> {
         ps.setInt(1, value);
         ps.setInt(2, worldId);
         ps.setInt(3, mapId);
      });
   }

   public void addStep(Connection connection, int value, int worldId, int mapId) {
      String sql = "INSERT INTO playernpcs_field (step, world, map) VALUES (?, ?, ?)";
      execute(connection, sql, ps -> {
         ps.setInt(1, value);
         ps.setInt(2, worldId);
         ps.setInt(3, mapId);
      });
   }

   public int create(Connection connection, MapleCharacter character, MapleMap map, Point position, int scriptId, int worldRank, int overallRank, int worldJobRank) {
      String sql = "INSERT INTO playernpcs (name, hair, face, skin, gender, x, cy, world, map, scriptid, dir, fh, rx0, rx1, worldrank, overallrank, worldjobrank, job) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
      return insertAndReturnKey(connection, sql, ps -> {
         int jobId = (character.getJob().getId() / 100) * 100;
         ps.setString(1, character.getName());
         ps.setInt(2, character.getHair());
         ps.setInt(3, character.getFace());
         ps.setInt(4, character.getSkinColor().getId());
         ps.setInt(5, character.getGender());
         ps.setInt(6, position.x);
         ps.setInt(7, position.y);
         ps.setInt(8, character.getWorld());
         ps.setInt(9, map.getId());
         ps.setInt(10, scriptId);
         ps.setInt(11, 1);    // default direction
         ps.setInt(12, map.getFootholds().findBelow(position).id());
         ps.setInt(13, position.x + 50);
         ps.setInt(14, position.x - 50);
         ps.setInt(15, worldRank);
         ps.setInt(16, overallRank);
         ps.setInt(17, worldJobRank);
         ps.setInt(18, jobId);
      });
   }

   public void createEquips(Connection connection, int npcId, Iterable<Item> items) {
      String sql = "INSERT INTO playernpcs_equip (npcid, equipid, equippos) VALUES (?, ?, ?)";
      batch(connection, sql, (ps, data) -> {
         ps.setInt(1, npcId);
         ps.setInt(2, data.id());
         ps.setInt(3, data.position());
      }, items);
   }

   public void deleteById(Connection connection, int npcId) {
      String sql = "DELETE FROM playernpcs WHERE id = ?";
      execute(connection, sql, ps -> ps.setInt(1, npcId));
   }

   public void deleteEquipById(Connection connection, int npcId) {
      String sql = "DELETE FROM playernpcs_equip WHERE npcid = ?";
      execute(connection, sql, ps -> ps.setInt(1, npcId));
   }

   public void deleteAllNpcs(Connection connection) {
      String deleteNpcSql = "DELETE FROM playernpcs";
      executeNoParam(connection, deleteNpcSql);

      String deleteNpcEquipSql = "DELETE FROM playernpcs_equip";
      executeNoParam(connection, deleteNpcEquipSql);

      String deleteNpcFieldSql = "DELETE FROM playernpcs_field";
      executeNoParam(connection, deleteNpcFieldSql);
   }

   public void updatePosition(Connection connection, int npcId, int x, int cy, int fh, int rx0, int rx1) {
      String sql = "UPDATE playernpcs SET x = ?, cy = ?, fh = ?, rx0 = ?, rx1 = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, x);
         ps.setInt(2, cy);
         ps.setInt(3, fh);
         ps.setInt(4, rx0);
         ps.setInt(5, rx1);
         ps.setInt(6, npcId);
      });
   }
}