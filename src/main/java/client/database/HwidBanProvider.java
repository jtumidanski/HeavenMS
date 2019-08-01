package client.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tools.DatabaseConnection;

public class HwidBanProvider {
   public static int getHwidBanCount(String hwid) {
      int bans = 0;
      PreparedStatement ps = null;
      Connection con = null;
      try {
         con = DatabaseConnection.getConnection();
         ps = con.prepareStatement("SELECT COUNT(*) FROM hwidbans WHERE hwid LIKE ?");
         ps.setString(1, hwid);
         ResultSet rs = ps.executeQuery();
         if (rs != null && rs.next()) {
            bans = rs.getInt(1);
         }
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
      return bans;
   }
}
