package client.database.administrator;


import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.database.AbstractQueryExecutor;
import entity.Alliance;

public class AllianceAdministrator extends AbstractQueryExecutor {
   private static AllianceAdministrator instance;

   public static AllianceAdministrator getInstance() {
      if (instance == null) {
         instance = new AllianceAdministrator();
      }
      return instance;
   }

   private AllianceAdministrator() {
   }

   protected void update(EntityManager entityManager, int id, Consumer<Alliance> consumer) {
      super.update(entityManager, Alliance.class, id, consumer);
   }

   public int createAlliance(EntityManager entityManager, String name) {
      Alliance alliance = new Alliance();
      alliance.setName(name);
      insert(entityManager, alliance);
      return alliance.getId();
   }

   public void deleteAlliance(EntityManager entityManager, int allianceId) {
      Query query = entityManager.createQuery("DELETE FROM Alliance WHERE id = :id");
      query.setParameter("id", allianceId);
      execute(entityManager, query);
   }

   public void updateAlliance(EntityManager entityManager, int allianceId, int capacity, String notice, String rank1, String rank2, String rank3, String rank4, String rank5) {
      update(entityManager, allianceId, alliance -> {
         alliance.setCapacity(capacity);
         alliance.setNotice(notice);
         alliance.setRank1(rank1);
         alliance.setRank2(rank2);
         alliance.setRank3(rank3);
         alliance.setRank4(rank4);
         alliance.setRank5(rank5);
      });
   }
}