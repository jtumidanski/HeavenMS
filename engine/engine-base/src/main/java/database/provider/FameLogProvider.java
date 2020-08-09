package database.provider;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import accessor.AbstractQueryExecutor;
import tools.Pair;

public class FameLogProvider extends AbstractQueryExecutor {
   private static FameLogProvider instance;

   public static FameLogProvider getInstance() {
      if (instance == null) {
         instance = new FameLogProvider();
      }
      return instance;
   }

   private FameLogProvider() {
   }

   public List<Pair<Integer, Timestamp>> getForCharacter(EntityManager entityManager, int characterId) {
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.DAY_OF_MONTH, -30);
      Date then = calendar.getTime();

      Query query = entityManager.createQuery("SELECT f.characterIdTo, f.createDate FROM FameLog f WHERE f.characterId = :characterId AND f.createDate <= :then");
      query.setParameter("characterId", characterId);
      query.setParameter("then", then);
      List<Object[]> results = (List<Object[]>) query.getResultList();
      return results.stream().map(result -> new Pair<>((int) result[0], (Timestamp) result[1])).collect(Collectors.toList());
   }
}