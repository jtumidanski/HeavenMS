package database.transformer;

import client.database.data.CoolDownData;
import entity.CoolDown;
import transformer.SqlTransformer;

public class CoolDownDataTransformer implements SqlTransformer<CoolDownData, CoolDown> {
   @Override
   public CoolDownData transform(CoolDown coolDown) {
      return new CoolDownData(coolDown.getSkillId(), coolDown.getStartTime(), coolDown.getLength());
   }
}
