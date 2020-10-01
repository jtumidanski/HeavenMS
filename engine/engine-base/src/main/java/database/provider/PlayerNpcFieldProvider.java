package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.PlayerNpcFieldData;
import database.transformer.PlayerNpcFieldTransformer;
import entity.PlayerNpcField;

public class PlayerNpcFieldProvider extends AbstractQueryExecutor {
   private static PlayerNpcFieldProvider instance;

   public static PlayerNpcFieldProvider getInstance() {
      if (instance == null) {
         instance = new PlayerNpcFieldProvider();
      }
      return instance;
   }

   private PlayerNpcFieldProvider() {
   }

   public List<PlayerNpcFieldData> get(EntityManager entityManager) {
      TypedQuery<PlayerNpcField> query = entityManager.createQuery("FROM PlayerNpcField", PlayerNpcField.class);
      return getResultList(query, new PlayerNpcFieldTransformer());
   }
}