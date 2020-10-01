package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.NxCodeItemData;
import database.transformer.NxCodeItemTransformer;
import entity.nx.NxCodeItem;

public class NxCodeItemProvider extends AbstractQueryExecutor {
   private static NxCodeItemProvider instance;

   public static NxCodeItemProvider getInstance() {
      if (instance == null) {
         instance = new NxCodeItemProvider();
      }
      return instance;
   }

   private NxCodeItemProvider() {
   }

   public List<NxCodeItemData> get(EntityManager entityManager, int codeId) {
      TypedQuery<NxCodeItem> query = entityManager.createQuery("FROM NxCodeItem n WHERE n.codeId = :codeId", NxCodeItem.class);
      query.setParameter("codeId", codeId);
      return getResultList(query, new NxCodeItemTransformer());
   }
}