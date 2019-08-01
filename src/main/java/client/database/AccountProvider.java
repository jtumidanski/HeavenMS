package client.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import client.database.data.AccountLoginData;
import tools.DatabaseConnection;

public class AccountProvider {
   public static byte getGReason(int accountId) {
      Connection con = null;
      PreparedStatement ps = null;
      ResultSet rs = null;
      try {
         con = DatabaseConnection.getConnection();
         ps = con.prepareStatement("SELECT `greason` FROM `accounts` WHERE id = ?");
         ps.setInt(1, accountId);
         rs = ps.executeQuery();
         if (rs.next()) {
            return rs.getByte("greason");
         }
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }
            if (rs != null) {
               rs.close();
            }
            if (con != null) {
               con.close();
            }
         } catch (SQLException e) {
            e.printStackTrace();
         }
      }
      return 0;
   }

   public static int getVotePoints(int accountId) {
      try {
         Connection con = DatabaseConnection.getConnection();
         PreparedStatement ps = con.prepareStatement("SELECT `votepoints` FROM accounts WHERE id = ?");
         ps.setInt(1, accountId);
         ResultSet rs = ps.executeQuery();

         if (rs.next()) {
            return rs.getInt("votepoints");
         }
         ps.close();
         rs.close();

         con.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return 0;
   }

   public static byte getTosStatus(int accountId) {
      byte tosStatus = 0;
      try {
         Connection con = DatabaseConnection.getConnection();
         PreparedStatement ps = con.prepareStatement("SELECT `tos` FROM accounts WHERE id = ?");
         ps.setInt(1, accountId);
         ResultSet rs = ps.executeQuery();

         if (rs.next()) {
            tosStatus = rs.getByte("tos");
         }
         ps.close();
         rs.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return tosStatus;
   }

   public static AccountLoginData getLoginData(int accountId) {
      AccountLoginData loginData;
      try {
         Connection con = DatabaseConnection.getConnection();
         PreparedStatement ps = con.prepareStatement("SELECT loggedin, lastlogin, birthday FROM accounts WHERE id = ?");
         ps.setInt(1, accountId);
         ResultSet rs = ps.executeQuery();
         if (!rs.next()) {
            rs.close();
            ps.close();
            con.close();
            throw new RuntimeException("getLoginState - MapleClient AccID: " + accountId);
         }

         loginData = new AccountLoginData(rs.getInt("loggedin"), rs.getTimestamp("lastlogin"), rs.getDate("birthday"));
         rs.close();
         ps.close();
         con.close();
      } catch (SQLException e) {
         e.printStackTrace();
         throw new RuntimeException("login state");
      }
      return loginData;
   }

   public static Calendar getTempBanCalendar(int accountId) {
      Connection con = null;
      PreparedStatement ps = null;
      ResultSet rs = null;
      final Calendar lTempban = Calendar.getInstance();
      try {
         con = DatabaseConnection.getConnection();
         ps = con.prepareStatement("SELECT `tempban` FROM accounts WHERE id = ?");
         ps.setInt(1, accountId);
         rs = ps.executeQuery();
         if (!rs.next()) {
            return null;
         }
         long blubb = rs.getLong("tempban");
         if (blubb == 0) { // basically if timestamp in db is 0000-00-00
            return null;
         }
         lTempban.setTimeInMillis(rs.getTimestamp("tempban").getTime());
         return lTempban;
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         try {
            if (ps != null) {
               ps.close();
            }
            if (rs != null) {
               rs.close();
            }
            if (con != null && !con.isClosed()) {
               con.close();
            }
         } catch (SQLException e) {
            e.printStackTrace();
         }
      }
      return null;
   }

   public static Set<String> getMacs(int accountId) {
      Set<String> macs = new HashSet<>();
      try {
         Connection con = DatabaseConnection.getConnection();
         try (PreparedStatement ps = con.prepareStatement("SELECT macs FROM accounts WHERE id = ?")) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
               if (rs.next()) {
                  for (String mac : rs.getString("macs").split(", ")) {
                     if (!mac.equals("")) {
                        macs.add(mac);
                     }
                  }
               }
            }
         } finally {
            con.close();
         }
      } catch (SQLException exception) {
         exception.printStackTrace();
      }
      return macs;
   }

   public static String getHwid(int accountId) {
      String hwid = "";
      try {
         Connection con = DatabaseConnection.getConnection();
         try (PreparedStatement ps = con.prepareStatement("SELECT hwid FROM accounts WHERE id = ?")) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
               if (rs.next()) {
                  hwid = rs.getString("hwid");
               }
            }
         } finally {
            con.close();
         }
      } catch (SQLException exception) {
         exception.printStackTrace();
      }
      return hwid;
   }
}
