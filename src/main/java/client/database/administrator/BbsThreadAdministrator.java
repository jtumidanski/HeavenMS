package client.database.administrator;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import net.server.Server;

public class BbsThreadAdministrator extends AbstractQueryExecutor {
   private static BbsThreadAdministrator instance;

   public static BbsThreadAdministrator getInstance() {
      if (instance == null) {
         instance = new BbsThreadAdministrator();
      }
      return instance;
   }

   private BbsThreadAdministrator() {
   }

   public void deleteThreadsFromCharacter(Connection connection, int characterId) {
      String getThreadSql = "SELECT threadid FROM bbs_threads WHERE postercid = ?";
      List<Integer> result = getListNew(connection, getThreadSql, ps -> ps.setInt(1, characterId),
            rs -> rs.getInt("threadid"));

      if (result.isEmpty()) {
         return;
      }

      String deleteRepliesSql = "DELETE FROM bbs_replies WHERE threadid = ?";
      result.forEach(threadId -> execute(connection, deleteRepliesSql, ps -> ps.setInt(1, threadId)));

      String deleteThreadSql = "DELETE FROM bbs_threads WHERE postercid = ?";
      execute(connection, deleteThreadSql, ps -> ps.setInt(1, characterId));
   }

   public void decrementReplyCount(Connection connection, int threadId) {
      String sql = "UPDATE bbs_threads SET replycount = replycount - 1 WHERE threadid = ?";
      execute(connection, sql, ps -> ps.setInt(1, threadId));
   }

   public void incrementReplyCount(Connection connection, int threadId) {
      String sql = "UPDATE bbs_threads SET replycount = replycount + 1 WHERE threadid = ?";
      execute(connection, sql, ps -> ps.setInt(1, threadId));
   }

   public void deleteById(Connection connection, int threadId) {
      String sql = "DELETE FROM bbs_threads WHERE threadid = ?";
      execute(connection, sql, ps -> ps.setInt(1, threadId));
   }

   public void editThread(Connection connection, int threadId, int guildId, int posterId, boolean privileged, String title, int icon, String text) {
      String sql = "UPDATE bbs_threads SET `name` = ?, `timestamp` = ?, " + "`icon` = ?, " + "`startpost` = ? WHERE guildid = ? AND localthreadid = ? AND (postercid = ? OR ?)";
      execute(connection, sql, ps -> {
         ps.setString(1, title);
         ps.setLong(2, Server.getInstance().getCurrentTime());
         ps.setInt(3, icon);
         ps.setString(4, text);
         ps.setInt(5, guildId);
         ps.setInt(6, threadId);
         ps.setInt(7, posterId);
         ps.setBoolean(8, privileged);
      });
   }

   public void create(Connection connection, int posterId, String title, int icon, String text, int guildId, int threadId) {
      String sql = "INSERT INTO bbs_threads " + "(`postercid`, `name`, `timestamp`, `icon`, `startpost`, " + "`guildid`, `localthreadid`) " + "VALUES(?, ?, ?, ?, ?, ?, ?)";
      execute(connection, sql, ps -> {
         ps.setInt(1, posterId);
         ps.setString(2, title);
         ps.setLong(3, Server.getInstance().getCurrentTime());
         ps.setInt(4, icon);
         ps.setString(5, text);
         ps.setInt(6, guildId);
         ps.setInt(7, threadId);
      });
   }
}
