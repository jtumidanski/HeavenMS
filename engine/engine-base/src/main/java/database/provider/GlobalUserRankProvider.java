package database.provider;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.CharacterWorldNameLevel;
import client.database.data.CharacterData;
import client.database.data.GlobalUserRank;
import client.database.data.WorldRankData;
import database.transformer.CharacterWorldNameLevelTransformer;
import entity.Character;

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
      TypedQuery<Character> query = entityManager.createQuery("SELECT c FROM Character c LEFT JOIN Account a ON a.id = c.accountId WHERE c.gm < 2 AND a.banned = false AND c.world = :world ORDER BY c.world, c.level DESC, c.exp DESC, c.lastExpGainTime ASC", Character.class);
      query.setParameter("world", worldId);
      query.setMaxResults(50);
      List<CharacterWorldNameLevel> results = getResultList(query, new CharacterWorldNameLevelTransformer());
      return parseMultiWorldRanks(results);
   }

   public List<WorldRankData> getWorldRanksRange(EntityManager entityManager, int worldId) {
      TypedQuery<Character> query = entityManager.createQuery("SELECT c FROM Character c LEFT JOIN Account a ON a.id = c.accountId WHERE c.gm < 2 AND a.banned = false AND c.world >= 0 AND c.world <= :world ORDER BY c.world, c.level DESC, c.exp DESC, c.lastExpGainTime ASC", Character.class);
      query.setParameter("world", worldId);
      query.setMaxResults(50);
      List<CharacterWorldNameLevel> results = getResultList(query, new CharacterWorldNameLevelTransformer());
      return parseMultiWorldRanks(results);
   }

   private List<WorldRankData> parseMultiWorldRanks(List<CharacterWorldNameLevel> characterDataList) {
      List<WorldRankData> rankSystem = new ArrayList<>();
      int currentWorld = -1;
      for (CharacterWorldNameLevel characterData : characterDataList) {
         if (currentWorld < characterData.world()) {
            currentWorld = characterData.world();
            rankSystem.add(new WorldRankData(characterData.world()));
         }

         rankSystem.set(characterData.world(), rankSystem.get(characterData.world()).addUserRank(new GlobalUserRank(characterData.name(), characterData.level())));
      }
      return rankSystem;
   }

   public List<WorldRankData> getRanksWholeServer(EntityManager entityManager, int worldId) {
      TypedQuery<Character> query = entityManager.createQuery("SELECT c FROM Character c LEFT JOIN Account a ON a.id = c.accountId WHERE c.gm < 2 AND a.banned = false AND c.world >= 0 AND c.world <= :world ORDER BY c.level DESC, c.exp DESC, c.lastExpGainTime ASC", Character.class);
      query.setParameter("world", worldId);
      query.setMaxResults(50);

      List<CharacterWorldNameLevel> characterDataList = getResultList(query, new CharacterWorldNameLevelTransformer());
      List<WorldRankData> rankSystem = new ArrayList<>();

      rankSystem.add(new WorldRankData(0));
      for (CharacterWorldNameLevel characterData : characterDataList) {
         rankSystem.set(characterData.world(), rankSystem.get(characterData.world()).addUserRank(new GlobalUserRank(characterData.name(), characterData.level())));
      }
      return rankSystem;
   }
}