package client.processor;

import client.MapleCharacter;
import client.MapleRing;
import client.database.administrator.InventoryEquipmentAdministrator;
import client.database.administrator.RingAdministrator;
import client.database.provider.RingProvider;
import client.inventory.manipulator.MapleCashIdGenerator;
import tools.DatabaseConnection;
import tools.Pair;

public class MapleRingProcessor {
   private static MapleRingProcessor instance;

   protected MapleRingProcessor() {
   }

   public static MapleRingProcessor getInstance() {
      if (instance == null) {
         instance = new MapleRingProcessor();
      }
      return instance;
   }

   public MapleRing loadFromDb(int ringId) {
      return DatabaseConnection.getInstance().withConnectionResultOpt(connection -> RingProvider.getInstance().getRingById(connection, ringId)).orElseThrow();
   }

   public void removeRing(final MapleRing ring) {
      if (ring == null) {
         return;
      }

      DatabaseConnection.getInstance().withConnection(connection -> {
         RingAdministrator.getInstance().deleteRing(connection, ring.getRingId(), ring.getPartnerRingId());

         MapleCashIdGenerator.getInstance().freeCashId(ring.getRingId());
         MapleCashIdGenerator.getInstance().freeCashId(ring.getPartnerRingId());

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
      ringID[0] = MapleCashIdGenerator.getInstance().generateCashId();
      ringID[1] = MapleCashIdGenerator.getInstance().generateCashId();

      DatabaseConnection.getInstance().withConnection(connection -> {
         RingAdministrator.getInstance().addRing(connection, ringID[0], itemId, ringID[1], partner2.getId(), partner2.getName());
         RingAdministrator.getInstance().addRing(connection, ringID[1], itemId, ringID[0], partner1.getId(), partner1.getName());
      });

      return new Pair<>(ringID[0], ringID[1]);
   }
}
