package client.database;

import java.sql.Connection;

public interface DeleteForCharacter {
   void deleteForCharacter(Connection connection, int characterId);
}
