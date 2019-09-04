package net.server.worker;

import java.util.Calendar;

import client.MapleFamily;
import client.database.administrator.FamilyCharacterAdministrator;
import client.database.administrator.FamilyEntitlementAdministrator;
import net.server.world.World;
import tools.DatabaseConnection;

public class FamilyDailyResetWorker implements Runnable {

   private final World world;

   public FamilyDailyResetWorker(World world) {
      this.world = world;
   }

   @Override
   public void run() {
      resetEntitlementUsage(world);
      for (MapleFamily family : world.getFamilies()) {
         family.resetDailyReps();
      }
   }

   public static void resetEntitlementUsage(World world) {
      Calendar resetTime = Calendar.getInstance();
      resetTime.add(Calendar.MINUTE, 1); // to make sure that we're in the "next day", since this is called at midnight
      resetTime.set(Calendar.HOUR, 0);
      resetTime.set(Calendar.MINUTE, 0);
      resetTime.set(Calendar.SECOND, 0);
      resetTime.set(Calendar.MILLISECOND, 0);
      DatabaseConnection.getInstance().withConnection(connection -> {
         FamilyCharacterAdministrator.getInstance().resetReputationOlderThan(connection, resetTime.getTimeInMillis());
         FamilyEntitlementAdministrator.getInstance().deleteOlderThan(connection, resetTime.getTimeInMillis());
      });
   }
}
