package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.CoolDownData;

public class CoolDownTransformer implements SqlTransformer<CoolDownData, ResultSet> {
   @Override
   public CoolDownData transform(ResultSet resultSet) throws SQLException {
      return new CoolDownData(resultSet.getInt("SkillID"), resultSet.getLong("length"), resultSet.getLong("StartTime"));
   }
}
