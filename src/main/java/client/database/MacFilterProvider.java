package client.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import tools.DatabaseConnection;

public class MacFilterProvider {
   public static List<String> getMacFilters() {
      List<String> filtered = new LinkedList<>();
      try {
         Connection con = DatabaseConnection.getConnection();
         try (PreparedStatement ps = con.prepareStatement("SELECT filter FROM macfilters"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
               filtered.add(rs.getString("filter"));
            }
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return filtered;
   }
}
