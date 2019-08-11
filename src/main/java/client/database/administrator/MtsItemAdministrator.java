package client.database.administrator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;

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
   public void deleteForCharacter(Connection connection, int characterId) {
      String getCartsSql = "SELECT id FROM mts_cart WHERE cid = ?";
      List<Integer> result = getList(connection, getCartsSql, ps -> ps.setInt(1, characterId), rs -> {
         List<Integer> cartIds = new ArrayList<>();
         while (rs != null && rs.next()) {
            cartIds.add(rs.getInt("id"));
         }
         return cartIds;
      });

      if (result.isEmpty()) {
         return;
      }

      String deleteItemsSql = "DELETE FROM mts_items WHERE id = ?";
      result.forEach(cartId -> execute(connection, deleteItemsSql, ps -> ps.setInt(1, cartId)));
   }

   public void deleteTransferItem(Connection connection, int itemId, int sellerId) {
      String sql = "DELETE FROM mts_items WHERE id = ? AND seller = ? AND transfer = 1";
      execute(connection, sql, ps -> {
         ps.setInt(1, itemId);
         ps.setInt(2, sellerId);
      });
   }

   public void createItem(Connection connection, int tab, int type, int itemId, int quantity, int characterId, int price, String owner, String characterName, String date) {
      String sql = "INSERT INTO mts_items (tab, type, itemid, quantity, seller, price, owner, sellername, sell_ends) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
      execute(connection, sql, ps -> {
         baseInsertParam(tab, type, itemId, quantity, characterId, price, ps);
         ps.setString(7, owner);
         ps.setString(8, characterName);
         ps.setString(9, date);
      });
   }

   private void baseInsertParam(int tab, int type, int itemId, int quantity, int characterId, int price, PreparedStatement ps) throws SQLException {
      ps.setInt(1, tab);
      ps.setInt(2, type);
      ps.setInt(3, itemId);
      ps.setInt(4, quantity);
      ps.setInt(5, characterId);
      ps.setInt(6, price);
   }

   public void createEquip(Connection connection, int tab, int type, int itemId, int quantity, int characterId,
                           int price, int upgradeSlots, int level, int strength, int dexterity, int intelligence,
                           int luck, int hp, int mp, int weaponAttack, int magicAttack, int weaponDefense,
                           int magicDefense, int accuracy, int avoidability, int hands, int speed, int jump,
                           int locked, String owner, String characterName, String date, int vicious, int flag) {
      String sql = "INSERT INTO mts_items (tab, type, itemid, quantity, seller, price, upgradeslots, level, str, dex, `int`, luk, hp, mp, watk, matk, wdef, mdef, acc, avoid, hands, speed, jump, locked, owner, sellername, sell_ends, vicious, flag) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
      execute(connection, sql, ps -> {
         baseInsertParam(tab, type, itemId, quantity, characterId, price, ps);
         ps.setInt(7, upgradeSlots);
         ps.setInt(8, level);
         ps.setInt(9, strength);
         ps.setInt(10, dexterity);
         ps.setInt(11, intelligence);
         ps.setInt(12, luck);
         ps.setInt(13, hp);
         ps.setInt(14, mp);
         ps.setInt(15, weaponAttack);
         ps.setInt(16, magicAttack);
         ps.setInt(17, weaponDefense);
         ps.setInt(18, magicDefense);
         ps.setInt(19, accuracy);
         ps.setInt(20, avoidability);
         ps.setInt(21, hands);
         ps.setInt(22, speed);
         ps.setInt(23, jump);
         ps.setInt(24, locked);
         ps.setString(25, owner);
         ps.setString(26, characterName);
         ps.setString(27, date);
         ps.setInt(28, vicious);
         ps.setInt(29, flag);
      });
   }

   public void cancelSale(Connection connection, int characterId, int itemId) {
      String sql = "UPDATE mts_items SET transfer = 1 WHERE id = ? AND seller = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, itemId);
         ps.setInt(2, characterId);
      });
   }

   public void transfer(Connection connection, int itemId, int sellerId) {
      String sql = "UPDATE mts_items SET seller = ?, transfer = 1 WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, sellerId);
         ps.setInt(2, itemId);
      });
   }
}