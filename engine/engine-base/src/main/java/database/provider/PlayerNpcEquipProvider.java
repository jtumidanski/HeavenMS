package database.provider;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import database.AbstractQueryExecutor;
import entity.PlayerNpcEquip;
import tools.Pair;

public class PlayerNpcEquipProvider extends AbstractQueryExecutor {
   private static PlayerNpcEquipProvider instance;

   public static PlayerNpcEquipProvider getInstance() {
      if (instance == null) {
         instance = new PlayerNpcEquipProvider();
      }
      return instance;
   }

   private PlayerNpcEquipProvider() {
   }

   public List<Pair<Short, Integer>> getEquips(EntityManager entityManager, int npcId) {
      TypedQuery<PlayerNpcEquip> query = entityManager.createQuery("FROM PlayerNpcEquip p WHERE p.npcId = :npcId", PlayerNpcEquip.class);
      query.setParameter("npcId", npcId);
      return query.getResultStream().map(result -> new Pair<>(result.getEquipPosition().shortValue(), result.getEquipId())).collect(Collectors.toList());
   }
}