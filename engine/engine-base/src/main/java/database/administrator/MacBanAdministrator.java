package database.administrator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import database.AbstractQueryExecutor;
import entity.MacBan;

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

   public void addMacBan(EntityManager entityManager, int accountId, Set<String> macs, List<String> filtered) {
      List<MacBan> macBanList = macs.stream().filter(mac -> !filtered.contains(mac)).map(mac -> {
         MacBan macBan = new MacBan();
         macBan.setMac(mac);
         macBan.setAid(String.valueOf(accountId));
         return macBan;
      }).collect(Collectors.toList());
      insertBulk(entityManager, macBanList);
   }

   public void removeMacBan(EntityManager entityManager, int accountId) {
      Query query = entityManager.createQuery("DELETE FROM MacBan WHERE aid = :accountId");
      query.setParameter("accountId", accountId);
      execute(entityManager, query);
   }
}
