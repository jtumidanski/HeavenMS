package client.database.utility;

import database.SqlTransformer;
import entity.SpecialCashItem;
import server.CashShop;

public class SpecialCashItemTransformer implements SqlTransformer<CashShop.SpecialCashItem, SpecialCashItem> {
   @Override
   public CashShop.SpecialCashItem transform(SpecialCashItem resultSet) {
      return new CashShop.SpecialCashItem(resultSet.getSn(),
            resultSet.getModifier(),
            resultSet.getInfo().byteValue());
   }
}
