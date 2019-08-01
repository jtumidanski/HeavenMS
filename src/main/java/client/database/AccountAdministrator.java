package client.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.server.Server;
import tools.DatabaseConnection;

public class AccountAdministrator {
   public static void updateGender(int accountId, byte gender) {
      Connection con;
      try {
         con = DatabaseConnection.getConnection();
         try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET gender = ? WHERE id = ?")) {
            ps.setByte(1, gender);
            ps.setInt(2, accountId);
            ps.executeUpdate();
         }

         con.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   public static void updateSlotCount(int accountId, int count) {
      Connection con;
      try {
         con = DatabaseConnection.getConnection();

         try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET characterslots = ? WHERE id = ?")) {
            ps.setInt(1, count);
            ps.setInt(2, accountId);
            ps.executeUpdate();
         }

         con.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   public static void updateVotePoints(int accountId, int points) {
      try {
         Connection con = DatabaseConnection.getConnection();
         try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET votepoints = ? WHERE id = ?")) {
            ps.setInt(1, points);
            ps.setInt(2, accountId);
            ps.executeUpdate();
         }

         con.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   public static void acceptTos(int accountId) {
      try {
         Connection con = DatabaseConnection.getConnection();
         PreparedStatement ps = con.prepareStatement("UPDATE accounts SET tos = 1 WHERE id = ?");
         ps.setInt(1, accountId);
         ps.executeUpdate();
         ps.close();
         con.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   public static void setLoggedInStatus(int accountId, int status) {
      try {
         Connection con = DatabaseConnection.getConnection();
         PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = ?, lastlogin = ? WHERE id = ?");
         ps.setInt(1, status);
         ps.setTimestamp(2, new java.sql.Timestamp(Server.getInstance().getCurrentTime()));
         ps.setInt(3, accountId);
         ps.executeUpdate();
         ps.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   public static void setMacs(int accountId, String macData) {
      Connection con = null;
      PreparedStatement ps = null;
      try {
         con = DatabaseConnection.getConnection();
         ps = con.prepareStatement("UPDATE accounts SET macs = ? WHERE id = ?");
         ps.setString(1, macData);
         ps.setInt(2, accountId);
         ps.executeUpdate();
         ps.close();
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
         } catch (SQLException ex) {
            ex.printStackTrace();
         }
      }
   }

   public static void setHwid(int accountId, String hwid) {
      PreparedStatement ps = null;
      Connection con = null;
      try {
         con = DatabaseConnection.getConnection();
         ps = con.prepareStatement("UPDATE accounts SET hwid = ? WHERE id = ?");
         ps.setString(1, hwid);
         ps.setInt(2, accountId);
         ps.executeUpdate();
         ps.close();
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

   public static void setPic(int accountId, String pic) {
      try {
         Connection con = DatabaseConnection.getConnection();
         try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET pic = ? WHERE id = ?")) {
            ps.setString(1, pic);
            ps.setInt(2, accountId);
            ps.executeUpdate();
         } finally {
            con.close();
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   public static void setPin(int accountId, String pin) {
      try {
         Connection con = DatabaseConnection.getConnection();
         try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET pin = ? WHERE id = ?")) {
            ps.setString(1, pin);
            ps.setInt(2, accountId);
            ps.executeUpdate();
         } finally {
            con.close();
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }
}
