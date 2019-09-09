package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.newyear.NewYearCardRecord;

public class NewYearCardRecordFromResultSetTransformer implements SqlTransformer<NewYearCardRecord, ResultSet> {
   @Override
   public NewYearCardRecord transform(ResultSet resultSet) throws SQLException {
      NewYearCardRecord newYearCard = new NewYearCardRecord(resultSet.getInt("senderid"), resultSet.getString("sendername"), resultSet.getInt("receiverid"), resultSet.getString("receivername"), resultSet.getString("message"));
      newYearCard.setExtraNewYearCardRecord(resultSet.getInt("id"), resultSet.getBoolean("senderdiscard"), resultSet.getBoolean("receiverdiscard"), resultSet.getBoolean("received"), resultSet.getLong("timesent"), resultSet.getLong("timereceived"));
      newYearCard.startNewYearCardTask();
      return newYearCard;
   }
}
