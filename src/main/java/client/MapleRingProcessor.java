package client;

import client.database.administrator.InventoryEquipmentAdministrator;
import client.database.administrator.RingAdministrator;
import client.database.provider.RingProvider;
import client.inventory.manipulator.MapleCashidGenerator;
import tools.DatabaseConnection;
import tools.Pair;

public class MapleRingProcessor {
   private static MapleRingProcessor instance;

   private MapleRingProcessor() {
   }

   public static MapleRingProcessor getInstance() {
      if (instance == null) {
         instance = new MapleRingProcessor();
      }
      return instance;
   }

   public MapleRing loadFromDb(int ringId) {
      return DatabaseConnection.withConnectionResultOpt(connection -> RingProvider.getInstance().getRingById(connection, ringId)).orElseThrow();
   }

   public void removeRing(final MapleRing ring) {
      if (ring == null) {
         return;
      }

      DatabaseConnection.withConnection(connection -> {
         RingAdministrator.getInstance().deleteRing(connection, ring.getRingId(), ring.getPartnerRingId());

         MapleCashidGenerator.freeCashId(ring.getRingId());
         MapleCashidGenerator.freeCashId(ring.getPartnerRingId());

         InventoryEquipmentAdministrator.getInstance().updateRing(connection, ring.getRingId(), ring.getPartnerRingId());
      });
   }

   public Pair<Integer, Integer> createRing(int itemId, final MapleCharacter partner1, final MapleCharacter partner2) {
      if (partner1 == null) {
         return new Pair<>(-3, -3);
      } else if (partner2 == null) {
         return new Pair<>(-2, -2);
      }

      int[] ringID = new int[2];
      ringID[0] = MapleCashidGenerator.generateCashId();
      ringID[1] = MapleCashidGenerator.generateCashId();

      DatabaseConnection.withConnection(connection -> {
         RingAdministrator.getInstance().addRing(connection, ringID[0], itemId, ringID[1], partner2.getId(), partner2.getName());
         RingAdministrator.getInstance().addRing(connection, ringID[1], itemId, ringID[0], partner1.getId(), partner1.getName());
      });

      return new Pair<>(ringID[0], ringID[1]);
   }
}
