package client.database.utility;

import client.database.data.NxCodeData;
import entity.nx.NxCode;
import transformer.SqlTransformer;

public class NxCodeTransformer implements SqlTransformer<NxCodeData, NxCode> {
   @Override
   public NxCodeData transform(NxCode resultSet) {
      return new NxCodeData(resultSet.getRetriever(),
            resultSet.getExpiration(),
            resultSet.getId());
   }
}
