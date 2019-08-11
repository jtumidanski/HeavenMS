package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.SkillMacroData;

public class SkillMacroTransformer implements SqlTransformer<SkillMacroData, ResultSet> {
   @Override
   public SkillMacroData transform(ResultSet resultSet) throws SQLException {
      return new SkillMacroData(resultSet.getInt("position"),
            resultSet.getInt("skill1"),
            resultSet.getInt("skill2"),
            resultSet.getInt("skill3"),
            resultSet.getString("name"),
            resultSet.getInt("shout"));
   }
}
