/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2018 RonanLana

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
package client.inventory.manipulator;

import java.util.HashSet;
import java.util.Set;

import client.database.provider.PetProvider;
import client.database.provider.RingProvider;
import tools.DatabaseConnection;

/**
 * @author RonanLana
 */
public class MapleCashidGenerator {

   private final static Set<Integer> existentCashids = new HashSet<>(10000);
   private static Integer runningCashid = 0;

   public static synchronized void loadExistentCashIdsFromDb() {
      DatabaseConnection.withConnection(connection -> {
         existentCashids.addAll(RingProvider.getInstance().getAll(connection));
         existentCashids.addAll(PetProvider.getInstance().getAll(connection));
      });

      runningCashid = 0;
      do {
         runningCashid++;    // hopefully the id will never surpass the allotted amount for pets/rings?
      } while (existentCashids.contains(runningCashid));
   }

   private static void getNextAvailableCashId() {
      runningCashid++;
      if (runningCashid >= 777000000) {
         existentCashids.clear();
         loadExistentCashIdsFromDb();
      }
   }

   public static synchronized int generateCashId() {
      while (true) {
         if (!existentCashids.contains(runningCashid)) {
            int ret = runningCashid;
            getNextAvailableCashId();

            // existentCashids.add(ret)... no need to do this since the wrap over already refetches already used cashids from the DB
            return ret;
         }

         getNextAvailableCashId();
      }
   }

   public static synchronized void freeCashId(int cashId) {
      existentCashids.remove(cashId);
   }

}
