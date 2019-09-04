package client.database.provider;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import client.database.data.NoteData;
import client.database.utility.NoteTransformer;

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
      NoteTransformer transformer = new NoteTransformer();
      return getListNew(connection, sql, ps -> ps.setString(1, characterName), transformer::transform);
   }

   public Optional<Integer> getFameForActiveNotes(Connection connection, int noteId) {
      String sql = "SELECT `fame` FROM notes WHERE id=? AND deleted=0";
      return getSingle(connection, sql, ps -> ps.setInt(1, noteId), "fame");
   }
}
