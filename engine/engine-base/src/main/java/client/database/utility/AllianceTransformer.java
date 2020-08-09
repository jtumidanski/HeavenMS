package client.database.utility;

import client.database.data.AllianceData;
import entity.Alliance;
import transformer.SqlTransformer;

public class AllianceTransformer implements SqlTransformer<AllianceData, Alliance> {
   @Override
   public AllianceData transform(Alliance resultSet) {
      return new AllianceData(resultSet.getCapacity(), resultSet.getName(),
            resultSet.getNotice(),
            resultSet.getRank1(),
            resultSet.getRank2(),
            resultSet.getRank3(),
            resultSet.getRank4(),
            resultSet.getRank5());
   }
}
