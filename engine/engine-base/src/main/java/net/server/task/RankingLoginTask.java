package net.server.task;

import java.util.List;
import java.util.function.Supplier;

import javax.persistence.EntityManager;

import client.MapleJob;
import database.DatabaseConnection;
import database.administrator.CharacterAdministrator;
import client.database.data.CharacterRankData;
import database.provider.CharacterProvider;
import config.YamlConfig;
import net.server.Server;
import tools.TriConsumer;

public class RankingLoginTask implements Runnable {
   private long lastUpdate = System.currentTimeMillis();

   private void performRanking(Supplier<List<CharacterRankData>> rankSupplier, TriConsumer<Integer, Integer, Integer> consumer) {
      List<CharacterRankData> rankData = rankSupplier.get();
      int rank = 0;
      for (CharacterRankData characterRankData : rankData) {
         int rankMove = 0;
         rank++;
         if (characterRankData.lastLogin().getTime() < lastUpdate || characterRankData.loggedIn() > 0) {
            rankMove = characterRankData.move();
         }
         rankMove += characterRankData.rank() - rank;
         consumer.apply(characterRankData.characterId(), rank, rankMove);
      }
   }

   private void updateRanking(EntityManager entityManager, int job, int world) {
      if (job != -1) {
         performRanking(
               () -> CharacterProvider.getInstance().getRankByJob(entityManager, world, job),
               (characterId, rank, rankMove) -> CharacterAdministrator.getInstance().updateJobRank(entityManager, characterId, rank, rankMove)
         );
      } else {
         performRanking(
               () -> CharacterProvider.getInstance().getRank(entityManager, world),
               (characterId, rank, rankMove) -> CharacterAdministrator.getInstance().updateRank(entityManager, characterId, rank, rankMove)
         );
      }
   }

   @Override
   public void run() {
      DatabaseConnection.getInstance().withConnection(entityManager -> {
         entityManager.getTransaction().begin();
         if (YamlConfig.config.server.USE_REFRESH_RANK_MOVE) {
            CharacterAdministrator.getInstance().resetAllJobRankMove(entityManager);
            CharacterAdministrator.getInstance().resetAllRankMove(entityManager);
         }
         for (int j = 0; j < Server.getInstance().getWorldsSize(); j++) {
            updateRanking(entityManager, -1, j);    //overall ranking
            for (int i = 0; i <= MapleJob.getMax(); i++) {
               updateRanking(entityManager, i, j);
            }
            entityManager.getTransaction().commit();
         }
      });
      lastUpdate = System.currentTimeMillis();
   }
}
