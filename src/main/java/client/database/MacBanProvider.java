package client.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;

import tools.DatabaseConnection;

public class MacBanProvider {
   public static int getMacBanCount(Set<String> macs) {
      int i;
      int bans = 0;
      try {
         StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM macbans WHERE mac IN (");
         for (i = 0; i < macs.size(); i++) {
            sql.append("?");
            if (i != macs.size() - 1) {
               sql.append(", ");
            }
         }
         sql.append(")");

         Connection con = DatabaseConnection.getConnection();
         try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            i = 0;
            for (String mac : macs) {
               i++;
               ps.setString(i, mac);
            }
            try (ResultSet rs = ps.executeQuery()) {
               rs.next();
               bans = rs.getInt(1);
            }
         } finally {
            con.close();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return bans;
   }
}
