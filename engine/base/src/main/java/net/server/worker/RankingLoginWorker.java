/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package net.server.worker;

import java.util.List;
import java.util.function.Supplier;

import javax.persistence.EntityManager;

import client.MapleJob;
import client.database.administrator.CharacterAdministrator;
import client.database.data.CharacterRankData;
import client.database.provider.CharacterProvider;
import constants.ServerConstants;
import net.server.Server;
import tools.DatabaseConnection;
import tools.TriConsumer;

/**
 * @author Matze
 * @author Quit
 * @author Ronan
 */
public class RankingLoginWorker implements Runnable {
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
         if (ServerConstants.USE_REFRESH_RANK_MOVE) {
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
