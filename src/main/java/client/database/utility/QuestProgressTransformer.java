package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.QuestProgressData;

public class QuestProgressTransformer implements SqlTransformer<QuestProgressData, ResultSet> {
   @Override
   public QuestProgressData transform(ResultSet resultSet) throws SQLException {
      return new QuestProgressData(resultSet.getInt("queststatusid"),
            resultSet.getInt("progressid"),
            resultSet.getString("progress"));
   }
}
