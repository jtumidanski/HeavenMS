package client.database.administrator;

import java.sql.Connection;

import client.database.AbstractQueryExecutor;
import net.server.Server;

public class BbsThreadReplyAdministrator extends AbstractQueryExecutor {
   private static BbsThreadReplyAdministrator instance;

   public static BbsThreadReplyAdministrator getInstance() {
      if (instance == null) {
         instance = new BbsThreadReplyAdministrator();
      }
      return instance;
   }

   private BbsThreadReplyAdministrator() {
   }

   public void deleteById(Connection connection, int replyId) {
      String sql = "DELETE FROM bbs_replies WHERE replyid = ?";
      execute(connection, sql, ps -> ps.setInt(1, replyId));
   }

   public void deleteByThreadId(Connection connection, int threadId) {
      String sql = "DELETE FROM bbs_replies WHERE threadid = ?";
      execute(connection, sql, ps -> ps.setInt(1, threadId));
   }

   public void create(Connection connection, int threadId, int playerId, String text) {
      String sql = "INSERT INTO bbs_replies (`threadid`, `postercid`, `timestamp`, `content`) VALUES (?, ?, ?, ?)";
      execute(connection, sql, ps -> {
         ps.setInt(1, threadId);
         ps.setInt(2, playerId);
         ps.setLong(3, Server.getInstance().getCurrentTime());
         ps.setString(4, text);
      });
   }
}