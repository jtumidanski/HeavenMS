package tools;

import java.sql.Connection;
import java.sql.SQLException;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

public abstract class DatabaseTestBase {

   protected DatabaseConnection databaseConnection;

   @Mock
   protected Connection connection;

   public void setup() throws SQLException {
      databaseConnection = PowerMockito.spy(DatabaseConnection.getInstance());
      PowerMockito.mockStatic(DatabaseConnection.class);
      Mockito.when(DatabaseConnection.getInstance()).thenReturn(databaseConnection);
//      Mockito.doNothing().when(databaseConnection).initHikariDataSource();
//      Mockito.doReturn(connection).when(databaseConnection).getConnection();
      Mockito.doCallRealMethod().when(databaseConnection).withConnectionResult(Mockito.any());
   }

}
