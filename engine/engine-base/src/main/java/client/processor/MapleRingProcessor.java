package client.processor;

import client.MapleCharacter;
import client.Ring;
import client.inventory.manipulator.MapleCashIdGenerator;
import database.DatabaseConnection;
import database.administrator.InventoryEquipmentAdministrator;
import database.administrator.RingAdministrator;
import database.provider.RingProvider;
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

   public Ring loadFromDb(int ringId) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> RingProvider.getInstance().getRingById(connection, ringId).orElse(null)).orElseThrow();
   }

   public void removeRing(final Ring ring) {
      if (ring == null) {
         return;
      }

      DatabaseConnection.getInstance().withConnection(connection -> {
         RingAdministrator.getInstance().deleteRing(connection, ring.ringId(), ring.partnerRingId());

         MapleCashIdGenerator.getInstance().freeCashId(ring.ringId());
         MapleCashIdGenerator.getInstance().freeCashId(ring.partnerRingId());

         InventoryEquipmentAdministrator.getInstance().updateRing(connection, ring.ringId(), ring.partnerRingId());
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
