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

import java.sql.Connection;
import java.util.List;
import java.util.function.Supplier;

import client.MapleJob;
import client.database.administrator.CharacterAdministrator;
import client.database.data.CharacterRankData;
import client.database.provider.CharacterProvider;
import constants.ServerConstants;
import net.server.Server;
import tools.DatabaseConnection;

/**
 * @author Matze
 * @author Quit
 * @author Ronan
 */
public class RankingLoginWorker implements Runnable {
   private long lastUpdate = System.currentTimeMillis();

   private interface TriConsumer<T, U, V> {
      void apply(T t, U u, V v);
   }

   private void performRanking(Supplier<List<CharacterRankData>> rankSupplier, TriConsumer<Integer, Integer, Integer> consumer) {
      List<CharacterRankData> rankData = rankSupplier.get();
      int rank = 0;
      for (CharacterRankData characterRankData : rankData) {
         int rankMove = 0;
         rank++;
         if (characterRankData.getLastLogin() < lastUpdate || characterRankData.getLoggedIn() > 0) {
            rankMove = characterRankData.getMove();
         }
         rankMove += characterRankData.getRank() - rank;
         consumer.apply(characterRankData.getCharacterId(), rank, rankMove);
      }
   }

   private void updateRanking(Connection connection, int job, int world) {
      if (job != -1) {
         performRanking(
               () -> CharacterProvider.getInstance().getRankByJob(connection, world, job),
               (characterId, rank, rankMove) -> CharacterAdministrator.getInstance().updateJobRank(connection, characterId, rank, rankMove)
         );
      } else {
         performRanking(
               () -> CharacterProvider.getInstance().getRank(connection, world),
               (characterId, rank, rankMove) -> CharacterAdministrator.getInstance().updateRank(connection, characterId, rank, rankMove)
         );
      }
   }

   @Override
   public void run() {
      DatabaseConnection.withExplicitCommitConnection(connection -> {
         if (ServerConstants.USE_REFRESH_RANK_MOVE) {
            CharacterAdministrator.getInstance().resetAllJobRankMove(connection);
            CharacterAdministrator.getInstance().resetAllRankMove(connection);
         }
         for (int j = 0; j < Server.getInstance().getWorldsSize(); j++) {
            updateRanking(connection, -1, j);    //overall ranking
            for (int i = 0; i <= MapleJob.getMax(); i++) {
               updateRanking(connection, i, j);
            }
            connection.commit();
         }
      });
      lastUpdate = System.currentTimeMillis();
   }
}
