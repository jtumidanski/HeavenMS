package database.transformer;

import client.database.data.SkillMacroData;
import entity.SkillMacro;
import transformer.SqlTransformer;

public class SkillMacroTransformer implements SqlTransformer<SkillMacroData, SkillMacro> {
   @Override
   public SkillMacroData transform(SkillMacro resultSet) {
      return new SkillMacroData(resultSet.getPosition(),
            resultSet.getSkill1(),
            resultSet.getSkill2(),
            resultSet.getSkill3(),
            resultSet.getName(),
            resultSet.getShout());
   }
}
