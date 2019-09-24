package client.database.administrator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
      List<Integer> result = getListNew(connection, getCartsSql, ps -> ps.setInt(1, characterId), rs -> rs.getInt("id"));

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

   public void createItem(Connection connection, int tab, int type, int itemId, int quantity, long expiration, String giftFrom, int characterId, int price, String owner, String characterName, String date) {
      String sql = "INSERT INTO mts_items (tab, type, itemid, quantity, seller, expiration, giftFrom, price, owner, sellername, sell_ends) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
      execute(connection, sql, ps -> {
         baseInsertParam(tab, type, itemId, quantity, expiration, giftFrom, characterId, price, ps);
         ps.setString(9, owner);
         ps.setString(10, characterName);
         ps.setString(11, date);
      });
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

   public void createEquip(Connection connection, int tab, int type, int itemId, int quantity, long expiration, String giftFrom, int characterId,
                           int price, int upgradeSlots, int level, int strength, int dexterity, int intelligence,
                           int luck, int hp, int mp, int weaponAttack, int magicAttack, int weaponDefense,
                           int magicDefense, int accuracy, int avoidability, int hands, int speed, int jump,
                           int locked, String owner, String characterName, String date, int vicious, int flag,
                           float itemExp, byte itemLevel, int ringId) {
      String sql = "INSERT INTO mts_items (tab, type, itemid, quantity, seller, price, upgradeslots, level, str, dex, `int`, luk, hp, mp, watk, matk, wdef, mdef, acc, avoid, hands, speed, jump, locked, owner, sellername, sell_ends, vicious, flag, itemexp, itemlevel, ringid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
      execute(connection, sql, ps -> {
         baseInsertParam(tab, type, itemId, quantity, expiration, giftFrom, characterId, price, ps);
         ps.setInt(9, upgradeSlots);
         ps.setInt(10, level);
         ps.setInt(11, strength);
         ps.setInt(12, dexterity);
         ps.setInt(13, intelligence);
         ps.setInt(14, luck);
         ps.setInt(15, hp);
         ps.setInt(16, mp);
         ps.setInt(17, weaponAttack);
         ps.setInt(18, magicAttack);
         ps.setInt(19, weaponDefense);
         ps.setInt(20, magicDefense);
         ps.setInt(21, accuracy);
         ps.setInt(22, avoidability);
         ps.setInt(23, hands);
         ps.setInt(24, speed);
         ps.setInt(25, jump);
         ps.setInt(26, locked);
         ps.setString(27, owner);
         ps.setString(28, characterName);
         ps.setString(29, date);
         ps.setInt(30, vicious);
         ps.setInt(31, flag);
         ps.setFloat(32, itemExp);
         ps.setByte(33, itemLevel);    // thanks Jefe for noticing missing itemlevel labels
         ps.setInt(34, ringId);
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