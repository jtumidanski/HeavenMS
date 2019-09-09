package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import server.life.MonsterGlobalDropEntry;

public class MonsterGlobalDropEntryTransformer implements SqlTransformer<MonsterGlobalDropEntry, ResultSet> {
   @Override
   public MonsterGlobalDropEntry transform(ResultSet resultSet) throws SQLException {
      return new MonsterGlobalDropEntry(
            resultSet.getInt("itemid"),
            resultSet.getInt("chance"),
            resultSet.getByte("continent"),
            resultSet.getInt("minimum_quantity"),
            resultSet.getInt("maximum_quantity"),
            resultSet.getShort("questid"));
   }
}
