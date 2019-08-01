package client.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tools.DatabaseConnection;

public class IpBanProvider {
   public static int getIpBanCount(String ipAddress) {
      int banCount = 0;
      try {
         Connection con = DatabaseConnection.getConnection();
         try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM ipbans WHERE ? LIKE CONCAT(ip, '%')")) {
            ps.setString(1, ipAddress);
            try (ResultSet rs = ps.executeQuery()) {
               rs.next();
               banCount = rs.getInt(1);
            }
         }
         con.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return banCount;
   }
}
