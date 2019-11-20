package client.database.provider;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import client.database.AbstractQueryExecutor;
import client.database.data.BbsThreadReplyData;
import client.database.utility.BbsThreadReplyTransformer;
import entity.bbs.BBSReply;

public class BbsThreadReplyProvider extends AbstractQueryExecutor {
   private static BbsThreadReplyProvider instance;

   public static BbsThreadReplyProvider getInstance() {
      if (instance == null) {
         instance = new BbsThreadReplyProvider();
      }
      return instance;
   }

   private BbsThreadReplyProvider() {
   }

   public List<BbsThreadReplyData> getByThreadId(EntityManager entityManager, int threadId) {
      TypedQuery<BBSReply> query = entityManager.createQuery("FROM BBSReply b WHERE b.threadId = :threadId", BBSReply.class);
      query.setParameter("threadId", threadId);
      BbsThreadReplyTransformer transformer = new BbsThreadReplyTransformer();
      return query.getResultList().stream().map(transformer::transform).collect(Collectors.toList());
   }

   public Optional<BbsThreadReplyData> getByReplyId(EntityManager entityManager, int replyId) {
      TypedQuery<BBSReply> query = entityManager.createQuery("FROM BBSReply b WHERE b.replyId = :replyId", BBSReply.class);
      query.setParameter("replyId", replyId);
      return getSingleOptional(query, new BbsThreadReplyTransformer());
   }
}