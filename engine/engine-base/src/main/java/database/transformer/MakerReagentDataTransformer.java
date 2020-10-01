package database.transformer;

import client.database.data.MakerReagentData;
import transformer.SqlTransformer;

public class MakerReagentDataTransformer implements SqlTransformer<MakerReagentData, entity.maker.MakerReagentData> {
   @Override
   public MakerReagentData transform(entity.maker.MakerReagentData makerReagentData) {
      return new MakerReagentData(makerReagentData.getStat(), makerReagentData.getValue());
   }
}
