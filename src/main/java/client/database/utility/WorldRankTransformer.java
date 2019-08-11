package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.CharacterData;

public class WorldRankTransformer implements SqlTransformer<CharacterData, ResultSet> {
   @Override
   public CharacterData transform(ResultSet resultSet) throws SQLException {
      CharacterData characterData = new CharacterData();
      characterData.setWorld(resultSet.getInt("world"));
      characterData.setName(resultSet.getString("name"));
      characterData.setLevel(resultSet.getInt("level"));
      return characterData;
   }
}
