package database.provider;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.BbsThreadData;
import database.transformer.BbsThreadTransformer;
import entity.bbs.BBSThread;

public class BbsThreadProvider extends AbstractQueryExecutor {
   private static BbsThreadProvider instance;

   public static BbsThreadProvider getInstance() {
      if (instance == null) {
         instance = new BbsThreadProvider();
      }
      return instance;
   }

   private BbsThreadProvider() {
   }

   public Optional<BbsThreadData> getByThreadAndGuildId(EntityManager entityManager, int threadId, int guildId, boolean localThread) {
      String threadColumn = localThread ? "localThreadId" : "threadId";
      TypedQuery<BBSThread> query = entityManager.createQuery("FROM BBSThread b WHERE b.guildId = :guildId AND b." + threadColumn + " = :threadId", BBSThread.class);
      query.setParameter("guildId", guildId);
      query.setParameter("threadId", threadId);

      try {
         BBSThread bbsThread = query.getSingleResult();
         BbsThreadTransformer transformer = new BbsThreadTransformer();
         BbsThreadData threadData = transformer.transform(bbsThread);
         threadData = BbsThreadReplyProvider.getInstance().getByThreadId(entityManager, !localThread ? threadId : threadData.threadId())
               .stream()
               .reduce(threadData, BbsThreadData::addReply, (a, b) -> b);
         return Optional.of(threadData);
      } catch (NoResultException exception) {
         return Optional.empty();
      }
   }

   public List<BbsThreadData> getThreadsForGuild(EntityManager entityManager, int guildId) {
      TypedQuery<BBSThread> query = entityManager.createQuery("FROM BBSThread  b WHERE b.guildId = :guildId ORDER BY b.localThreadId DESC", BBSThread.class);
      query.setParameter("guildId", guildId);
      return getResultList(query, new BbsThreadTransformer());
   }

   public int getNextLocalThreadId(EntityManager entityManager, int guildId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT MAX(b.localThreadId) FROM BBSThread b WHERE b.guildId = :guildId", Integer.class);
      query.setParameter("guildId", guildId);
      return query.getSingleResult();
   }
}