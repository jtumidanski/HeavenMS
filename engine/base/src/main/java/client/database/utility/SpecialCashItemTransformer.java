package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import server.CashShop;

public class SpecialCashItemTransformer implements SqlTransformer<CashShop.SpecialCashItem, ResultSet> {
   @Override
   public CashShop.SpecialCashItem transform(ResultSet resultSet) throws SQLException {
      return new CashShop.SpecialCashItem(resultSet.getInt("sn"),
            resultSet.getInt("modifier"),
            resultSet.getByte("info"));
   }
}
