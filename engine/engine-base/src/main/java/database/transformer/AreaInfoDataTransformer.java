package database.transformer;

import client.database.data.AreaInfoData;
import entity.AreaInfo;
import transformer.SqlTransformer;

public class AreaInfoDataTransformer implements SqlTransformer<AreaInfoData, AreaInfo> {
   @Override
   public AreaInfoData transform(AreaInfo areaInfo) {
      return new AreaInfoData(areaInfo.getArea(), areaInfo.getInfo());
   }
}
