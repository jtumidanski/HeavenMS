package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.CharacterGuildData;

public class CharacterGuildTransformer implements SqlTransformer<CharacterGuildData, ResultSet> {
   @Override
   public CharacterGuildData transform(ResultSet resultSet) throws SQLException {
      return new CharacterGuildData(
            resultSet.getInt("id"),
            resultSet.getInt("guildid"),
            resultSet.getInt("guildrank"),
            resultSet.getString("name"),
            resultSet.getInt("allianceRank"),
            resultSet.getInt("level"),
            resultSet.getInt("job"));
   }
}
