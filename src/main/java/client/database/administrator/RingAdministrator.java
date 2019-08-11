package client.database.administrator;

import java.sql.Connection;
import java.util.Arrays;

import client.database.AbstractQueryExecutor;

public class RingAdministrator extends AbstractQueryExecutor {
   private static RingAdministrator instance;

   public static RingAdministrator getInstance() {
      if (instance == null) {
         instance = new RingAdministrator();
      }
      return instance;
   }

   private RingAdministrator() {
   }

   public void deleteRing(Connection connection, int ringId, int partnerRingId) {
      String sql = "DELETE FROM rings WHERE id=?";
      batch(connection, sql, (ps, data) -> ps.setInt(1, data), Arrays.asList(ringId, partnerRingId));
   }

   public void addRing(Connection connection, int ringId, int itemId, int parnterRingId, int partnerCharacterId, String partnerName) {
      String sql = "INSERT INTO rings (id, itemid, partnerRingId, partnerChrId, partnername) VALUES (?, ?, ?, ?, ?)";
      execute(connection, sql, ps -> {
         ps.setInt(1, ringId);
         ps.setInt(2, itemId);
         ps.setInt(3, parnterRingId);
         ps.setInt(4, partnerCharacterId);
         ps.setString(5, partnerName);
      });
   }
}