package client.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import tools.DatabaseConnection;

public class MacBanAdministrator {
   public static void addMacBan(int accountId, Set<String> macs, List<String> filtered) {
      try {
         Connection con = DatabaseConnection.getConnection();
         try (PreparedStatement ps = con.prepareStatement("INSERT INTO macbans (mac, aid) VALUES (?, ?)")) {
            for (String mac : macs) {
               boolean matched = false;
               for (String filter : filtered) {
                  if (mac.matches(filter)) {
                     matched = true;
                     break;
                  }
               }
               if (!matched) {
                  ps.setString(1, mac);
                  ps.setString(2, String.valueOf(accountId));
                  ps.executeUpdate();
               }
            }
         }

         con.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }
}
