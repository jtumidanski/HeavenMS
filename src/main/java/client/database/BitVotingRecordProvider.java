package client.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tools.DatabaseConnection;
import tools.FilePrinter;

public class BitVotingRecordProvider {
   public static int getVoteDate(String accountName) {
      int voteTime = -1;
      try {
         Connection con = DatabaseConnection.getConnection();
         try (PreparedStatement ps = con.prepareStatement("SELECT date FROM bit_votingrecords WHERE UPPER(account) = UPPER(?)")) {
            ps.setString(1, accountName);
            try (ResultSet rs = ps.executeQuery()) {
               if (!rs.next()) {
                  voteTime = -1;
               }
               voteTime = rs.getInt("date");
            }
         }
         con.close();
      } catch (SQLException e) {
         FilePrinter.printError("hasVotedAlready.txt", e);
         voteTime = -1;
      }
      return voteTime;
   }
}
