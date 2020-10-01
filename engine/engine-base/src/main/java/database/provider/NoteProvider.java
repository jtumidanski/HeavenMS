package database.provider;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.NoteData;
import database.transformer.NoteTransformer;
import entity.Note;

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

   public List<NoteData> getFirstNote(EntityManager entityManager, String characterName) {
      TypedQuery<Note> query = entityManager.createQuery("FROM Note n WHERE n.noteTo = :to AND n.deleted = 0", Note.class);
      query.setParameter("to", characterName);
      return getResultList(query, new NoteTransformer());
   }

   public Optional<Integer> getFameForActiveNotes(EntityManager entityManager, int noteId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT n.fame FROM Note n WHERE n.id = :noteId AND n.deleted = 0", Integer.class);
      query.setParameter("noteId", noteId);
      return getSingleOptional(query);
   }
}
