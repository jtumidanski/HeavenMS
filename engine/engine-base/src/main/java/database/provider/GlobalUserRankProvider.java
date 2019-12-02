package database.provider;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import database.AbstractQueryExecutor;
import client.database.data.CharacterData;
import client.database.data.GlobalUserRank;
import client.database.data.WorldRankData;

public class GlobalUserRankProvider extends AbstractQueryExecutor {
   private static GlobalUserRankProvider instance;

   public static GlobalUserRankProvider getInstance() {
      if (instance == null) {
         instance = new GlobalUserRankProvider();
      }
      return instance;
   }

   private GlobalUserRankProvider() {
   }

   public List<WorldRankData> getWorldRanks(EntityManager entityManager, int worldId) {
      TypedQuery<CharacterData> query = entityManager.createQuery("" +
            "SELECT NEW client.database.data.CharacterData(c.world, c.name, c.level) " +
            "FROM Character c LEFT JOIN Account a ON a.id = c.accountId " +
            "WHERE c.gm < 2 AND a.banned = false AND c.world = :world " +
            "ORDER BY c.world, c.level DESC, c.exp DESC, c.lastExpGainTime ASC", CharacterData.class);
      query.setParameter("world", worldId);
      query.setMaxResults(50);
      return parseMultiWorldRanks(query.getResultList());
   }

   public List<WorldRankData> getWorldRanksRange(EntityManager entityManager, int worldId) {
      TypedQuery<CharacterData> query = entityManager.createQuery("" +
            "SELECT NEW client.database.data.CharacterData(c.world, c.name, c.level) " +
            "FROM Character c LEFT JOIN Account a ON a.id = c.accountId " +
            "WHERE c.gm < 2 AND a.banned = false AND c.world >= 0 AND c.world <= :world " +
            "ORDER BY c.world, c.level DESC, c.exp DESC, c.lastExpGainTime ASC", CharacterData.class);
      query.setParameter("world", worldId);
      query.setMaxResults(50);
      return parseMultiWorldRanks(query.getResultList());
   }

   private List<WorldRankData> parseMultiWorldRanks(List<CharacterData> characterDataList) {
      List<WorldRankData> rankSystem = new ArrayList<>();
      int currentWorld = -1;
      for (CharacterData characterData : characterDataList) {
         if (currentWorld < characterData.world()) {
            currentWorld = characterData.world();
            rankSystem.add(new WorldRankData(characterData.world()));
         }
         rankSystem.get(characterData.world()).addUserRank(new GlobalUserRank(characterData.name(), characterData.level()));
      }
      return rankSystem;
   }

   public List<WorldRankData> getRanksWholeServer(EntityManager entityManager, int worldId) {
      TypedQuery<CharacterData> query = entityManager.createQuery("" +
            "SELECT NEW client.database.data.CharacterData(c.world, c.name, c.level) " +
            "FROM Character c LEFT JOIN Account a ON a.id = c.accountId " +
            "WHERE c.gm < 2 AND a.banned = false AND c.world >= 0 AND c.world <= :world " +
            "ORDER BY c.level DESC, c.exp DESC, c.lastExpGainTime ASC", CharacterData.class);
      query.setParameter("world", worldId);
      query.setMaxResults(50);

      List<CharacterData> characterDataList = query.getResultList();
      List<WorldRankData> rankSystem = new ArrayList<>();

      rankSystem.add(new WorldRankData(0));
      for (CharacterData characterData : characterDataList) {
         rankSystem.get(characterData.world()).addUserRank(new GlobalUserRank(characterData.name(), characterData.level()));
      }
      return rankSystem;
   }
}