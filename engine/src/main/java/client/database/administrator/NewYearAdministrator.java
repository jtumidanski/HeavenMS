package client.database.administrator;

import java.sql.Connection;

import client.database.AbstractQueryExecutor;

public class NewYearAdministrator extends AbstractQueryExecutor {
   private static NewYearAdministrator instance;

   public static NewYearAdministrator getInstance() {
      if (instance == null) {
         instance = new NewYearAdministrator();
      }
      return instance;
   }

   private NewYearAdministrator() {
   }

   public int create(Connection connection, int senderId, String senderName, int receiverId, String receiverName,
                     String content, boolean senderDiscardCard, boolean receiverDiscardCard,
                     boolean receiverReceivedCard, long dateSent, long dateReceived) {
      String sql = "INSERT INTO newyear VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
      return insertAndReturnKey(connection, sql, ps -> {
         ps.setInt(1, senderId);
         ps.setString(2, senderName);
         ps.setInt(3, receiverId);
         ps.setString(4, receiverName);
         ps.setString(5, content);
         ps.setBoolean(6, senderDiscardCard);
         ps.setBoolean(7, receiverDiscardCard);
         ps.setBoolean(8, receiverReceivedCard);
         ps.setLong(9, dateSent);
         ps.setLong(10, dateReceived);
      });
   }

   public void setReceived(Connection connection, int id, long dateReceived) {
      String sql = "UPDATE newyear SET received=1, timereceived=? WHERE id=?";
      execute(connection, sql, ps -> {
         ps.setLong(1, dateReceived);
         ps.setInt(2, id);
      });
   }

   public void deleteById(Connection connection, int id) {
      String sql = "DELETE FROM newyear WHERE id = ?";
      execute(connection, sql, ps -> ps.setInt(1, id));
   }
}