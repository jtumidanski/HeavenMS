package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.CharacterData;

public class WorldRankTransformer implements SqlTransformer<CharacterData, ResultSet> {
   @Override
   public CharacterData transform(ResultSet resultSet) throws SQLException {
      return new CharacterData(resultSet.getInt("world"),
            resultSet.getString("name"),
            resultSet.getInt("level"));
   }
}
