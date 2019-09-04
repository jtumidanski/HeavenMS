package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.QuestData;

public class QuestStatusTransformer implements SqlTransformer<QuestData, ResultSet> {
   @Override
   public QuestData transform(ResultSet resultSet) throws SQLException {
      return new QuestData(resultSet.getShort("quest"),
            resultSet.getInt("status"),
            resultSet.getLong("time"),
            resultSet.getLong("expires"),
            resultSet.getInt("forfeited"),
            resultSet.getInt("completed"),
            resultSet.getInt("queststatusid"));
   }
}
