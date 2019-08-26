package client.processor;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import client.database.administrator.AccountAdministrator;
import client.database.provider.AccountProvider;
import client.database.provider.CharacterProvider;
import tools.DatabaseConnection;
import tools.DatabaseTestBase;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DatabaseConnection.class, AccountProvider.class, CharacterProvider.class, AccountAdministrator.class})
public class BanProcessorUnitTest extends DatabaseTestBase {
   protected BanProcessor banProcessor;

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
      super.setup();

      PowerMockito.mockStatic(AccountProvider.class);
      Mockito.when(AccountProvider.getInstance()).thenReturn(accountProvider);

      PowerMockito.mockStatic(CharacterProvider.class);
      Mockito.when(CharacterProvider.getInstance()).thenReturn(characterProvider);

      PowerMockito.mockStatic(AccountAdministrator.class);
      Mockito.when(AccountAdministrator.getInstance()).thenReturn(accountAdministrator);

      banProcessor = new BanProcessor();
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
