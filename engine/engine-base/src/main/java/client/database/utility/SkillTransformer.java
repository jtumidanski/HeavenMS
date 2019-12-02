package client.database.utility;

import client.database.data.SkillData;
import database.SqlTransformer;
import entity.Skill;

public class SkillTransformer implements SqlTransformer<SkillData, Skill> {
   @Override
   public SkillData transform(Skill resultSet) {
      return new SkillData(resultSet.getSkillId(),
            resultSet.getSkillLevel().byteValue(),
            resultSet.getMasterLevel(),
            resultSet.getExpiration());
   }
}
