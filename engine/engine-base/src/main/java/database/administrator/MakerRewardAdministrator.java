package database.administrator;

import javax.persistence.EntityManager;

import database.AbstractQueryExecutor;
import entity.maker.MakerRewardData;

public class MakerRewardAdministrator extends AbstractQueryExecutor {
   private static MakerRewardAdministrator instance;

   public static MakerRewardAdministrator getInstance() {
      if (instance == null) {
         instance = new MakerRewardAdministrator();
      }
      return instance;
   }

   private MakerRewardAdministrator() {
   }

   public void create(EntityManager entityManager, int itemId, int rewardId, int quantity, int probability) {
      MakerRewardData makerRewardData = new MakerRewardData();
      makerRewardData.setItemId(itemId);
      makerRewardData.setRewardId(rewardId);
      makerRewardData.setQuantity(quantity);
      makerRewardData.setProb(probability);
      insert(entityManager, makerRewardData);
   }
}