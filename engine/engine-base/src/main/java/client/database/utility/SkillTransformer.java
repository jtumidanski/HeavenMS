package client.database.utility;

import client.database.data.SkillData;
import entity.Skill;
import transformer.SqlTransformer;

public class SkillTransformer implements SqlTransformer<SkillData, Skill> {
   @Override
   public SkillData transform(Skill resultSet) {
      return new SkillData(resultSet.getSkillId(),
            resultSet.getSkillLevel().byteValue(),
            resultSet.getMasterLevel(),
            resultSet.getExpiration());
   }
}
