package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import server.life.MonsterDropEntry;

public class MonsterDropEntryTransformer implements SqlTransformer<MonsterDropEntry, ResultSet> {
   @Override
   public MonsterDropEntry transform(ResultSet resultSet) throws SQLException {
      return new MonsterDropEntry(resultSet.getInt("itemid"),
            resultSet.getInt("chance"),
            resultSet.getInt("minimum_quantity"),
            resultSet.getInt("maximum_quantity"),
            resultSet.getShort("questid"));
   }
}
