package client.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import tools.DatabaseConnection;

public class HwidBanAdministrator {
   public static void banHwid(String hwid) {
      PreparedStatement ps = null;
      Connection con = null;
      try {
         con = DatabaseConnection.getConnection();
         ps = con.prepareStatement("INSERT INTO hwidbans (hwid) VALUES (?)");
         ps.setString(1, hwid);
         ps.executeUpdate();
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         try {
            if (ps != null && !ps.isClosed()) {
               ps.close();
            }
            if (con != null && !con.isClosed()) {
               con.close();
            }
         } catch (SQLException e) {
            e.printStackTrace();
         }
      }
   }
}
