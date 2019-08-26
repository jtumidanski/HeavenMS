package client.processor;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import client.database.administrator.AccountAdministrator;
import client.database.provider.AccountProvider;
import client.database.provider.CharacterProvider;
import tools.DatabaseConnection;

public class BanProcessorUnitTest {
   protected BanProcessor banProcessor;

   @Mock
   protected Connection connection;

   @Spy
   protected DatabaseConnection databaseConnection;

   @Mock
   protected AccountProvider accountProvider;

   @Mock
   protected CharacterProvider characterProvider;

   @Mock
   protected AccountAdministrator accountAdministrator;

   private String NAME = "test";

   private int ID = 1;

   private String REASON = "autoban";

   @Before
   public void setup() throws SQLException {
      MockitoAnnotations.initMocks(this);
      Mockito.doNothing().when(databaseConnection).initHikariDataSource();
      Mockito.doReturn(connection).when(databaseConnection).getConnection();
      Mockito.doCallRealMethod().when(databaseConnection).withConnectionResult(Mockito.any());

      banProcessor = new BanProcessor(databaseConnection, accountProvider, characterProvider, accountAdministrator);
   }

   @Test
   public void banViaAccountId() {
      //Setup
      Mockito.doReturn(ID).when(accountProvider).getAccountIdForName(Mockito.any(Connection.class), Mockito.eq(NAME));

      //Do
      boolean result = banProcessor.ban(NAME, REASON, true);

      //Assert
      Assert.assertTrue(result);
      Mockito.verify(accountProvider, Mockito.times(1)).getAccountIdForName(connection, NAME);
      Mockito.verify(characterProvider, Mockito.times(0)).getAccountIdForName(connection, NAME);
      Mockito.verify(accountAdministrator, Mockito.times(1)).setPermaBan(connection, ID, REASON);
   }

   @Test
   public void banViaAccountId_accountUnfound() {
      //Setup
      Mockito.doReturn(null).when(accountProvider).getAccountIdForName(Mockito.any(Connection.class), Mockito.eq(NAME));

      //Do
      boolean result = banProcessor.ban(NAME, REASON, true);

      //Assert
      Assert.assertFalse(result);
      Mockito.verify(accountProvider, Mockito.times(1)).getAccountIdForName(connection, NAME);
      Mockito.verify(characterProvider, Mockito.times(0)).getAccountIdForName(connection, NAME);
      Mockito.verify(accountAdministrator, Mockito.times(0)).setPermaBan(connection, ID, REASON);
   }

   @Test
   public void banViaCharacterId() {
      //Setup
      Mockito.doReturn(ID).when(characterProvider).getAccountIdForName(Mockito.any(Connection.class), Mockito.eq(NAME));

      //Do
      boolean result = banProcessor.ban(NAME, REASON, false);

      //Assert
      Assert.assertTrue(result);
      Mockito.verify(accountProvider, Mockito.times(0)).getAccountIdForName(connection, NAME);
      Mockito.verify(characterProvider, Mockito.times(1)).getAccountIdForName(connection, NAME);
      Mockito.verify(accountAdministrator, Mockito.times(1)).setPermaBan(connection, ID, REASON);
   }

   @Test
   public void banViaCharacterIdUnfound() {
      //Setup
      Mockito.doReturn(null).when(characterProvider).getAccountIdForName(Mockito.any(Connection.class), Mockito.eq(NAME));

      //Do
      boolean result = banProcessor.ban(NAME, REASON, false);

      //Assert
      Assert.assertFalse(result);
      Mockito.verify(accountProvider, Mockito.times(0)).getAccountIdForName(connection, NAME);
      Mockito.verify(characterProvider, Mockito.times(1)).getAccountIdForName(connection, NAME);
      Mockito.verify(accountAdministrator, Mockito.times(0)).setPermaBan(connection, ID, REASON);
   }
}
