package client.database.administrator;

import java.sql.Connection;

import client.database.AbstractQueryExecutor;

public class GiftAdministrator extends AbstractQueryExecutor {
   private static GiftAdministrator instance;

   public static GiftAdministrator getInstance() {
      if (instance == null) {
         instance = new GiftAdministrator();
      }
      return instance;
   }

   private GiftAdministrator() {
   }

   public void createGift(Connection connection, int recipient, String from, String message, int sn, int ringid) {
      String sql = "INSERT INTO `gifts` VALUES (DEFAULT, ?, ?, ?, ?, ?)";
      execute(connection, sql, ps -> {
         ps.setInt(1, recipient);
         ps.setString(2, from);
         ps.setString(3, message);
         ps.setInt(4, sn);
         ps.setInt(5, ringid);
      });
   }

   public void deleteAllGiftsForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM `gifts` WHERE `to` = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }
}