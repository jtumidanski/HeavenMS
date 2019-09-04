package client.database.provider;

import java.sql.Connection;
import java.util.Optional;

import client.database.AbstractQueryExecutor;

public class BitVotingRecordProvider extends AbstractQueryExecutor {
   private static BitVotingRecordProvider instance;

   public static BitVotingRecordProvider getInstance() {
      if (instance == null) {
         instance = new BitVotingRecordProvider();
      }
      return instance;
   }

   private BitVotingRecordProvider() {
   }

   public int getVoteDate(Connection connection, String accountName) {
      String sql = "SELECT date FROM bit_votingrecords WHERE UPPER(account) = UPPER(?)";
      Optional<Integer> result = getSingle(connection, sql, ps -> ps.setString(1, accountName), "date");
      return result.orElse(-1);
   }
}
