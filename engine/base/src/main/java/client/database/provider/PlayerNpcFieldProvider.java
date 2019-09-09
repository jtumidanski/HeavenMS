package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.PlayerNpcFieldData;
import client.database.utility.PlayerNpcFieldTransformer;

public class PlayerNpcFieldProvider extends AbstractQueryExecutor {
   private static PlayerNpcFieldProvider instance;

   public static PlayerNpcFieldProvider getInstance() {
      if (instance == null) {
         instance = new PlayerNpcFieldProvider();
      }
      return instance;
   }

   private PlayerNpcFieldProvider() {
   }

   public List<PlayerNpcFieldData> get(Connection connection) {
      String sql = "SELECT * FROM playernpcs_field";
      PlayerNpcFieldTransformer transformer = new PlayerNpcFieldTransformer();
      return getListNew(connection, sql, transformer::transform);
   }
}