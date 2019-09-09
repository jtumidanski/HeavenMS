package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.CharacterIdNameAccountId;

public class CharacterIdNameAccountIdTransformer implements SqlTransformer<CharacterIdNameAccountId, ResultSet> {
   @Override
   public CharacterIdNameAccountId transform(ResultSet resultSet) throws SQLException {
      return new CharacterIdNameAccountId(resultSet.getInt("id"),
            resultSet.getInt("accountid"),
            resultSet.getString("name"));
   }
}
