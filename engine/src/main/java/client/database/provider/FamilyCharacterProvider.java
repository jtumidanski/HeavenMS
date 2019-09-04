package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.FamilyData;
import client.database.utility.FamilyDataFromResultSetTransformer;

public class FamilyCharacterProvider extends AbstractQueryExecutor {
   private static FamilyCharacterProvider instance;

   public static FamilyCharacterProvider getInstance() {
      if (instance == null) {
         instance = new FamilyCharacterProvider();
      }
      return instance;
   }

   private FamilyCharacterProvider() {
   }

   public List<FamilyData> getAllFamilies(Connection connection) {
      String sql = "SELECT * FROM family_character";
      FamilyDataFromResultSetTransformer transformer = new FamilyDataFromResultSetTransformer();
      return getListNew(connection, sql, transformer::transform);
   }
}