package client.database.administrator;

import java.sql.Connection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import client.database.AbstractQueryExecutor;

public class MacBanAdministrator extends AbstractQueryExecutor {
   private static MacBanAdministrator instance;

   public static MacBanAdministrator getInstance() {
      if (instance == null) {
         instance = new MacBanAdministrator();
      }
      return instance;
   }

   private MacBanAdministrator() {
   }

   public void addMacBan(Connection connection, int accountId, Set<String> macs, List<String> filtered) {
      String sql = "INSERT INTO macbans (mac, aid) VALUES (?, ?)";
      batch(connection, sql, (ps, data) -> {
         ps.setString(1, data);
         ps.setString(2, String.valueOf(accountId));
      }, macs.stream().filter(mac -> !filtered.contains(mac)).collect(Collectors.toList()));
   }

   public void removeMacBan(Connection connection, int accountId) {
      String sql = "DELETE FROM macbans WHERE aid = ?";
      execute(connection, sql, ps -> ps.setInt(1, accountId));
   }
}
