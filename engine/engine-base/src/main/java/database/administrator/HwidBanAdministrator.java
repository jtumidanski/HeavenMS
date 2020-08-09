package database.administrator;


import javax.persistence.EntityManager;

import accessor.AbstractQueryExecutor;
import entity.HwidBan;

public class HwidBanAdministrator extends AbstractQueryExecutor {
   private static HwidBanAdministrator instance;

   public static HwidBanAdministrator getInstance() {
      if (instance == null) {
         instance = new HwidBanAdministrator();
      }
      return instance;
   }

   private HwidBanAdministrator() {
   }

   public void banHwid(EntityManager entityManager, String hwid) {
      HwidBan hwidBan = new HwidBan();
      hwidBan.setHwid(hwid);
      insert(entityManager, hwidBan);
   }
}
