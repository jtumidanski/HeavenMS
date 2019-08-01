package client.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import client.database.data.CharNameAndIdData;
import tools.DatabaseConnection;

public class CharacterProvider {
   public static List<CharNameAndIdData> getCharacterInfoForWorld(int accountId, int worldId) {
      List<CharNameAndIdData> chars = new ArrayList<>(15);
      try {
         Connection con = DatabaseConnection.getConnection();
         PreparedStatement ps = con.prepareStatement("SELECT id, name FROM characters WHERE accountid = ? AND world = ?");
         ps.setInt(1, accountId);
         ps.setInt(2, worldId);
         try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
               chars.add(new CharNameAndIdData(rs.getString("name"), rs.getInt("id")));
            }
         }
         ps.close();
         con.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return chars;
   }
}
