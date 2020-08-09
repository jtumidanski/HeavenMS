package database.administrator;


import javax.persistence.EntityManager;
import javax.persistence.Query;

import accessor.AbstractQueryExecutor;
import entity.Note;
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

   public void sendNote(EntityManager entityManager, String to, String from, String msg, byte fame) {
      Note note = new Note();
      note.setNoteTo(to);
      note.setNoteFrom(from);
      note.setMessage(msg);
      note.setTimestamp(Server.getInstance().getCurrentTime());
      note.setFame((int) fame);
      insert(entityManager, note);
   }

   public void clearNote(EntityManager entityManager, int noteId) {
      Query query = entityManager.createQuery("UPDATE Note SET deleted = 1 WHERE id = :id");
      query.setParameter("id", noteId);
      execute(entityManager, query);
   }

   public void deleteWhereNamesLike(EntityManager entityManager, String fromName, String toName) {
      Query query = entityManager.createQuery("DELETE FROM Note WHERE noteFrom LIKE :from AND noteTo LIKE :to");
      query.setParameter("from", fromName);
      query.setParameter("to", toName);
      execute(entityManager, query);
   }
}
