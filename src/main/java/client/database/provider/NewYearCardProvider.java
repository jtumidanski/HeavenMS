package client.database.provider;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import client.database.utility.NewYearCardRecordFromResultSetTransformer;
import client.newyear.NewYearCardRecord;

public class NewYearCardProvider extends AbstractQueryExecutor {
   private static NewYearCardProvider instance;

   public static NewYearCardProvider getInstance() {
      if (instance == null) {
         instance = new NewYearCardProvider();
      }
      return instance;
   }

   private NewYearCardProvider() {
   }

   public List<NewYearCardRecord> getNotReceived(Connection connection) {
      NewYearCardRecordFromResultSetTransformer resultSetTransformer = new NewYearCardRecordFromResultSetTransformer();
      String sql = "SELECT * FROM newyear WHERE timereceived = 0 AND senderdiscard = 0";
      return getListNew(connection, sql, ps -> {
      }, resultSetTransformer::transform);
   }

   public Optional<NewYearCardRecord> getById(Connection connection, int cardId) {
      NewYearCardRecordFromResultSetTransformer resultSetTransformer = new NewYearCardRecordFromResultSetTransformer();
      String sql = "SELECT * FROM newyear WHERE id = ?";
      return get(connection, sql, ps -> ps.setInt(1, cardId), rs -> {
         if (rs != null && rs.next()) {
            return Optional.of(resultSetTransformer.transform(rs));
         }
         return Optional.empty();
      });
   }

   public List<NewYearCardRecord> getBySenderOrReceiver(Connection connection, int senderId, int receiverId) {
      NewYearCardRecordFromResultSetTransformer resultSetTransformer = new NewYearCardRecordFromResultSetTransformer();
      String sql = "SELECT * FROM newyear WHERE senderid = ? OR receiverid = ?";
      return getListNew(connection, sql, ps -> {
         ps.setInt(1, senderId);
         ps.setInt(2, receiverId);
      }, resultSetTransformer::transform);
   }
}