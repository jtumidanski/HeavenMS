package client.database.administrator;

import java.sql.Connection;

import client.database.AbstractQueryExecutor;
import net.server.Server;

public class NoteAdministrator extends AbstractQueryExecutor {
   private static NoteAdministrator instance;

   public static NoteAdministrator getInstance() {
      if (instance == null) {
         instance = new NoteAdministrator();
      }
      return instance;
   }

   private NoteAdministrator() {
   }

   public void sendNote(Connection connection, String to, String from, String msg, byte fame) {
      String sql = "INSERT INTO notes (`to`, `from`, `message`, `timestamp`, `fame`) VALUES (?, ?, ?, ?, ?)";
      execute(connection, sql, ps -> {
         ps.setString(1, to);
         ps.setString(2, from);
         ps.setString(3, msg);
         ps.setLong(4, Server.getInstance().getCurrentTime());
         ps.setByte(5, fame);
      });
   }

   public void clearNote(Connection connection, int noteId) {
      String sql = "UPDATE notes SET `deleted` = 1 WHERE id = ?";
      execute(connection, sql, ps -> ps.setInt(1, noteId));
   }

   public void deleteWhereNamesLike(Connection connection, String fromName, String toName) {
      String sql = "DELETE FROM `notes` WHERE `from` LIKE ? AND `to` LIKE ?";
      execute(connection, sql, ps -> {
         ps.setString(1, fromName);
         ps.setString(2, toName);
      });
   }
}
