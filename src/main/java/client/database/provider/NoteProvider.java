package client.database.provider;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import client.database.data.NoteData;

public class NoteProvider extends AbstractQueryExecutor {
   private static NoteProvider instance;

   public static NoteProvider getInstance() {
      if (instance == null) {
         instance = new NoteProvider();
      }
      return instance;
   }

   private NoteProvider() {
   }

   public List<NoteData> getFirstNote(Connection connection, String characterName) {
      String sql = "SELECT * FROM notes WHERE `to` = ? AND `deleted` = 0";
      return getList(connection, sql, ps -> ps.setString(1, characterName), rs -> {
         List<NoteData> noteData = new ArrayList<>();
         while (rs != null && rs.next()) {
            noteData.add(new NoteData(
                  rs.getInt("id"),
                  rs.getString("from"),
                  rs.getString("message"),
                  rs.getLong("timestamp"),
                  rs.getByte("fame")
            ));
         }
         return noteData;
      });
   }

   public Optional<Integer> getFameForActiveNotes(Connection connection, int noteId) {
      String sql = "SELECT `fame` FROM notes WHERE id=? AND deleted=0";
      return getSingle(connection, sql, ps -> ps.setInt(1, noteId), "fame");
   }
}
