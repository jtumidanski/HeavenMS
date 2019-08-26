package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.database.data.FamilyData;

public class FamilyDataFromResultSetTransformer implements SqlTransformer<FamilyData, ResultSet> {
   @Override
   public FamilyData transform(ResultSet resultSet) throws SQLException {
      return new FamilyData(
            resultSet.getInt("cid"),
            resultSet.getInt("familyid"),
            resultSet.getInt("seniorid"),
            resultSet.getInt("reputation"),
            resultSet.getInt("todaysrep"),
            resultSet.getInt("totalreputation"),
            resultSet.getInt("reptosenior"),
            resultSet.getString("precepts")
      );
   }
}
