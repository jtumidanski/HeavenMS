package database.transformer;

import client.database.data.MarriageData;
import entity.Marriage;
import transformer.SqlTransformer;

public class MarriageDataTransformer implements SqlTransformer<MarriageData, Marriage> {
   @Override
   public MarriageData transform(Marriage marriage) {
      return new MarriageData(marriage.getMarriageId(), marriage.getHusbandId(), marriage.getWifeId());
   }
}
