package database.provider;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.CharacterIdAndAccount;
import client.CharacterNameAndLevel;
import client.database.data.CharNameAndIdData;
import client.database.data.CharacterData;
import client.database.data.CharacterGuildData;
import client.database.data.CharacterGuildFamilyData;
import client.database.data.CharacterIdNameAccountId;
import client.database.data.CharacterRankData;
import database.transformer.CharNameAndIdDataTransformer;
import database.transformer.CharacterFromResultSetTransformer;
import database.transformer.CharacterGuildDataTransformer;
import database.transformer.CharacterGuildFamilyDataTransformer;
import database.transformer.CharacterIdAndAccountTransformer;
import database.transformer.CharacterIdNameAccountIdTransformer;
import database.transformer.CharacterNameAndLevelTransformer;
import entity.Character;
import tools.Pair;

public class CharacterProvider extends AbstractQueryExecutor {
   private static CharacterProvider instance;

   public static CharacterProvider getInstance() {
      if (instance == null) {
         instance = new CharacterProvider();
      }
      return instance;
   }

   private CharacterProvider() {
   }

   public List<CharacterIdNameAccountId> getAllCharacters(EntityManager entityManager) {
      TypedQuery<Character> query = entityManager.createQuery("SELECT c FROM Character c", Character.class);
      return getResultList(query, new CharacterIdNameAccountIdTransformer());
   }

   public Optional<Integer> getCharacterForNameAndWorld(EntityManager entityManager, String name, int worldId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT c.id FROM Character c WHERE c.name LIKE :name AND c.world = :world", Integer.class);
      query.setParameter("name", name);
      query.setParameter("world", worldId);
      return getSingleOptional(query);
   }

   public List<CharNameAndIdData> getCharacterInfoForWorld(EntityManager entityManager, int accountId, int worldId) {
      TypedQuery<Character> query = entityManager.createQuery("SELECT c FROM Character c WHERE c.accountId = :accountId AND c.world = :worldId", Character.class);
      query.setParameter("accountId", accountId);
      query.setParameter("worldId", worldId);
      return getResultList(query, new CharNameAndIdDataTransformer());
   }

   public Optional<CharNameAndIdData> getCharacterInfoForName(EntityManager entityManager, String name) {
      TypedQuery<Character> query = entityManager.createQuery("SELECT c FROM Character c WHERE c.name LIKE :name", Character.class);
      query.setParameter("name", name);
      return getSingleOptional(query, new CharNameAndIdDataTransformer());
   }

   public int countReborns(EntityManager entityManager, int characterId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT c.reborns FROM Character c WHERE c.id = :id", Integer.class);
      query.setParameter("id", characterId);
      return getSingleWithDefault(query, 0);
   }

   public int getWorldId(EntityManager entityManager, int characterId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT c.world FROM Character c WHERE c.id = :id", Integer.class);
      query.setParameter("id", characterId);
      return getSingleWithDefault(query, 0);
   }

   public Optional<CharacterGuildData> getGuildDataForCharacter(EntityManager entityManager, int characterId, int accountId) {
      TypedQuery<Character> query = entityManager.createQuery("SELECT c FROM Character c WHERE c.id = :id AND c.accountId = :accountId AND c.guildId > 0", Character.class);
      query.setParameter("id", characterId);
      query.setParameter("accountId", accountId);
      return getSingleOptional(query, new CharacterGuildDataTransformer());
   }


   public List<CharacterGuildData> getGuildCharacterData(EntityManager entityManager, int guildId) {
      TypedQuery<Character> query = entityManager.createQuery("SELECT c FROM Character c WHERE c.guildId = :guildId ORDER BY c.guildRank ASC, c.name ASC", Character.class);
      query.setParameter("guildId", guildId);
      return getResultList(query, new CharacterGuildDataTransformer());
   }

   public Optional<CharacterIdAndAccount> getIdAndAccountIdForName(EntityManager entityManager, String name) {
      TypedQuery<Character> query = entityManager.createQuery("SELECT c FROM Character c WHERE c.name = :name", Character.class);
      query.setParameter("name", name);
      return getSingleOptional(query, new CharacterIdAndAccountTransformer());
   }

   public Integer getAccountIdForName(EntityManager entityManager, String name) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT c.accountId FROM Character c WHERE c.name = :name", Integer.class);
      query.setParameter("name", name);
      return query.getSingleResult();
   }

   public int getAccountIdForCharacterId(EntityManager entityManager, int characterId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT c.accountId FROM Character c WHERE c.id = :id", Integer.class);
      query.setParameter("id", characterId);
      return query.getSingleResult();
   }

   public int getIdForName(EntityManager entityManager, String name) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT c.id FROM Character c WHERE c.name = :name", Integer.class);
      query.setParameter("name", name);
      return getSingleWithDefault(query, -1);
   }

   public String getNameForId(EntityManager entityManager, int characterId) {
      TypedQuery<String> query = entityManager.createQuery("SELECT c.name FROM Character c WHERE c.id = :id", String.class);
      query.setParameter("id", characterId);
      return query.getSingleResult();
   }

   public long getMerchantMesos(EntityManager entityManager, int characterId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT c.merchantMesos FROM Character c WHERE c.id = :id", Integer.class);
      query.setParameter("id", characterId);
      return (long) query.getSingleResult();
   }

   public List<Integer> getCharacterLevels(EntityManager entityManager, int accountId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT c.level FROM Character c WHERE c.accountId = :accountId", Integer.class);
      query.setParameter("accountId", accountId);
      return query.getResultList();
   }

   public Optional<Integer> getGmLevel(EntityManager entityManager, String name) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT c.gm FROM Character c WHERE c.name = :name", Integer.class);
      query.setParameter("name", name);
      return Optional.of(query.getSingleResult());
   }

   public Optional<Integer> getMarriageItem(EntityManager entityManager, int characterId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT c.marriageItemId FROM Character c WHERE c.id = :id", Integer.class);
      query.setParameter("id", characterId);
      return Optional.of(query.getSingleResult());
   }

   public Optional<CharacterIdNameAccountId> getByName(EntityManager entityManager, String name) {
      TypedQuery<Character> query = entityManager.createQuery("SELECT c FROM Character c WHERE c.name = :name", Character.class);
      query.setParameter("name", name);
      return getSingleOptional(query, new CharacterIdNameAccountIdTransformer());
   }

   public List<CharacterRankData> getRankByJob(EntityManager entityManager, int worldId, int jobId) {
      TypedQuery<CharacterRankData> query = entityManager.createQuery(
            "SELECT NEW client.database.data.CharacterRankData(a.lastLogin, a.loggedIn, c.jobRankMove, c.jobRank, c.id) " +
                  "FROM Character c LEFT JOIN Account a ON c.accountId = a.id " +
                  "WHERE c.gm < 2 AND c.world = :world AND c.job / 100 = :job " +
                  "ORDER BY c.level DESC, c.exp DESC, c.lastExpGainTime ASC, c.fame DESC, c.meso DESC", CharacterRankData.class);
      query.setParameter("world", worldId);
      query.setParameter("job", jobId);
      return query.getResultList();
   }

   public List<CharacterRankData> getRank(EntityManager entityManager, int worldId) {
      TypedQuery<CharacterRankData> query = entityManager.createQuery(
            "SELECT NEW client.database.data.CharacterRankData(a.lastLogin, a.loggedIn, c.rankMove, c.rank, c.id) " +
                  "FROM Character  c LEFT JOIN Account a ON c.accountId = a.id " +
                  "WHERE c.gm < 2 AND c.world = :world " +
                  "ORDER BY c.level DESC, c.exp DESC, c.lastExpGainTime ASC, c.fame DESC, c.meso DESC", CharacterRankData.class);
      query.setParameter("world", worldId);
      return query.getResultList();
   }

   public Optional<CharacterData> getById(EntityManager entityManager, int characterId) {
      TypedQuery<Character> query = entityManager.createQuery("FROM Character c WHERE c.id = :id", Character.class);
      query.setParameter("id", characterId);
      return getSingleOptional(query, new CharacterFromResultSetTransformer());
   }

   public List<CharacterData> getByAccountId(EntityManager entityManager, int accountId) {
      TypedQuery<Character> query = entityManager.createQuery("FROM Character c WHERE c.accountId = :accountId ORDER BY c.world, c.id", Character.class);
      query.setParameter("accountId", accountId);
      return getResultList(query, new CharacterFromResultSetTransformer());
   }

   public Optional<CharacterNameAndLevel> getHighestLevelOtherCharacterData(EntityManager entityManager, int accountId, int characterId) {
      TypedQuery<Character> query = entityManager.createQuery(
            "SELECT c FROM Character c WHERE c.accountId = :accountId AND c.id != :id ORDER BY c.level DESC", Character.class);
      query.setParameter("accountId", accountId);
      query.setParameter("id", characterId);
      query.setMaxResults(1);
      return getSingleOptional(query, new CharacterNameAndLevelTransformer());
   }

   public int getCharactersInWorld(EntityManager entityManager, int accountId, int worldId) {
      TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(*) FROM Character c WHERE c.accountId = :accountId AND c.world = :world", Long.class);
      query.setParameter("accountId", accountId);
      query.setParameter("world", worldId);
      try {
         return query.getSingleResult().intValue();
      } catch (NoResultException exception) {
         return 0;
      }
   }

   public int getMesosForCharacter(EntityManager entityManager, int characterId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT c.meso FROM Character c WHERE c.id = :id", Integer.class);
      query.setParameter("id", characterId);
      return query.getSingleResult();
   }

   public Optional<CharacterGuildFamilyData> getGuildFamilyInformation(EntityManager entityManager, int characterId) {
      TypedQuery<Character> query = entityManager.createQuery("SELECT c FROM Character c WHERE c.id = :id", Character.class);
      query.setParameter("id", characterId);
      return getSingleOptional(query, new CharacterGuildFamilyDataTransformer());
   }
}
