package client.database.provider;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import client.database.utility.DueyPackageFromResultSetTransformer;
import server.DueyPackage;
import tools.Pair;

public class DueyPackageProvider extends AbstractQueryExecutor {
   private static DueyPackageProvider instance;

   public static DueyPackageProvider getInstance() {
      if (instance == null) {
         instance = new DueyPackageProvider();
      }
      return instance;
   }

   private DueyPackageProvider() {
   }

   public Optional<Integer> getPackageTypeForCharacter(Connection connection, int characterId) {
      String sql = "SELECT Type FROM dueypackages WHERE ReceiverId = ? AND Checked = 1 ORDER BY Type DESC";
      return getSingle(connection, sql, ps -> ps.setInt(1, characterId), "Type");
   }

   public Optional<Pair<String, Integer>> get(Connection connection, int characterId) {
      String sql = "SELECT SenderName, Type FROM dueypackages WHERE ReceiverId = ? AND Checked = 1 ORDER BY Type DESC";
      return getNew(connection, sql, ps -> ps.setInt(1, characterId),
            rs -> new Pair<>(rs.getString("SenderName"), rs.getInt("Type")));
   }

   public List<DueyPackage> getPackagesForReceiver(Connection connection, int characterId) {
      DueyPackageFromResultSetTransformer resultSetTransformer = new DueyPackageFromResultSetTransformer();
      String sql = "SELECT * FROM dueypackages dp WHERE ReceiverId = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId), resultSetTransformer::transform);
   }

   public Optional<DueyPackage> getById(Connection connection, int packageId) {
      String sql = "SELECT * FROM dueypackages dp WHERE PackageId = ?";
      DueyPackageFromResultSetTransformer resultSetTransformer = new DueyPackageFromResultSetTransformer();
      return getNew(connection, sql, ps -> ps.setInt(1, packageId), resultSetTransformer::transform);
   }

   public List<Integer> getPackagesAfter(Connection connection, Timestamp timestamp) {
      String sql = "SELECT `PackageId` FROM dueypackages WHERE `TimeStamp` < ?";
      return getListNew(connection, sql, ps -> ps.setTimestamp(1, timestamp), rs -> rs.getInt("PackageId"));
   }
}