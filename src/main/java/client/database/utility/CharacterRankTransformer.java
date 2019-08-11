package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.CharacterRankData;

public class CharacterRankTransformer implements SqlTransformer<CharacterRankData, ResultSet> {
   @Override
   public CharacterRankData transform(ResultSet resultSet) throws SQLException {
      return new CharacterRankData(resultSet.getLong("lastlogin"),
            resultSet.getInt("loggedin"),
            resultSet.getInt("jobRankMove"),
            resultSet.getInt("jobRank"),
            resultSet.getInt("id"));
   }
}
