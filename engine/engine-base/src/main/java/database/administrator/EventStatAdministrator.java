package database.administrator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import accessor.AbstractQueryExecutor;
import database.DeleteForCharacter;
import entity.EventStat;
import server.events.MapleEvents;

public class EventStatAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static EventStatAdministrator instance;

   public static EventStatAdministrator getInstance() {
      if (instance == null) {
         instance = new EventStatAdministrator();
      }
      return instance;
   }

   private EventStatAdministrator() {
   }

   @Override
   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM EventStat WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void create(EntityManager entityManager, int characterId, Set<Map.Entry<String, MapleEvents>> events) {
      List<EventStat> eventStatList = events.stream().map(entry -> {
         EventStat eventStat = new EventStat();
         eventStat.setCharacterId(characterId);
         eventStat.setName(entry.getKey());
         eventStat.setInfo(entry.getValue().getInfo());
         return eventStat;
      }).collect(Collectors.toList());
      insertBulk(entityManager, eventStatList);
   }
}