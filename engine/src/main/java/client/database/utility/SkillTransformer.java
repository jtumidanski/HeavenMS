package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.SkillData;

public class SkillTransformer implements SqlTransformer<SkillData, ResultSet> {
   @Override
   public SkillData transform(ResultSet resultSet) throws SQLException {
      return new SkillData(resultSet.getInt("skillid"),
            resultSet.getByte("skilllevel"),
            resultSet.getInt("masterlevel"),
            resultSet.getLong("expiration"));
   }
}
