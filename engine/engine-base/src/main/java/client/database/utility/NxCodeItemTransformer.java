package client.database.utility;

import client.database.data.NxCodeItemData;
import entity.nx.NxCodeItem;
import transformer.SqlTransformer;

public class NxCodeItemTransformer implements SqlTransformer<NxCodeItemData, NxCodeItem> {
   @Override
   public NxCodeItemData transform(NxCodeItem resultSet) {
      return new NxCodeItemData(resultSet.getType(),
            resultSet.getQuantity(),
            resultSet.getItem());
   }
}
