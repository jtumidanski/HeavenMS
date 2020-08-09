package client.inventory.manipulator;

import java.util.HashSet;
import java.util.Set;

import database.DatabaseConnection;
import database.provider.PetProvider;
import database.provider.RingProvider;

public class MapleCashIdGenerator {
   private static MapleCashIdGenerator instance;

   private MapleCashIdGenerator() {
   }

   public static MapleCashIdGenerator getInstance() {
      if (instance == null) {
         instance = new MapleCashIdGenerator();
      }
      return instance;
   }

   private final static Set<Integer> existentCashIds = new HashSet<>(10000);
   private static Integer runningCashId = 0;

   public synchronized void loadExistentCashIdsFromDb() {
      DatabaseConnection.getInstance().withConnection(connection -> {
         existentCashIds.addAll(RingProvider.getInstance().getAll(connection));
         existentCashIds.addAll(PetProvider.getInstance().getAll(connection));
      });

      runningCashId = 0;
      do {
         runningCashId++;    // hopefully the id will never surpass the allotted amount for pets/rings?
      } while (existentCashIds.contains(runningCashId));
   }

   private void getNextAvailableCashId() {
      runningCashId++;
      if (runningCashId >= 777000000) {
         existentCashIds.clear();
         loadExistentCashIdsFromDb();
      }
   }

   public synchronized int generateCashId() {
      while (true) {
         if (!existentCashIds.contains(runningCashId)) {
            int ret = runningCashId;
            getNextAvailableCashId();
            return ret;
         }

         getNextAvailableCashId();
      }
   }

   public synchronized void freeCashId(int cashId) {
      existentCashIds.remove(cashId);
   }

}
