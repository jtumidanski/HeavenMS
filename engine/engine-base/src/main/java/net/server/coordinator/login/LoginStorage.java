package net.server.coordinator.login;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import config.YamlConfig;
import net.server.Server;

public class LoginStorage {

   private ConcurrentHashMap<Integer, List<Long>> loginHistory = new ConcurrentHashMap<>();

   public boolean registerLogin(int accountId) {
      List<Long> accHist = loginHistory.computeIfAbsent(accountId, k -> new LinkedList<>());
      synchronized (accHist) {
         if (accHist.size() > YamlConfig.config.server.MAX_ACCOUNT_LOGIN_ATTEMPT) {
            long blockExpiration = Server.getInstance().getCurrentTime() + YamlConfig.config.server.LOGIN_ATTEMPT_DURATION;
            Collections.fill(accHist, blockExpiration);

            return false;
         }

         accHist.add(Server.getInstance().getCurrentTime() + YamlConfig.config.server.LOGIN_ATTEMPT_DURATION);
         return true;
      }
   }

   public void updateLoginHistory() {
      long timeNow = Server.getInstance().getCurrentTime();
      List<Integer> toRemove = new LinkedList<>();
      List<Long> toRemoveAttempt = new LinkedList<>();

      for (Entry<Integer, List<Long>> loginEntries : loginHistory.entrySet()) {
         toRemoveAttempt.clear();

         List<Long> accAttempts = loginEntries.getValue();
         synchronized (accAttempts) {
            for (Long loginAttempt : accAttempts) {
               if (loginAttempt < timeNow) {
                  toRemoveAttempt.add(loginAttempt);
               }
            }

            if (!toRemoveAttempt.isEmpty()) {
               for (Long trAttempt : toRemoveAttempt) {
                  accAttempts.remove(trAttempt);
               }

               if (accAttempts.isEmpty()) {
                  toRemove.add(loginEntries.getKey());
               }
            }
         }
      }

      for (Integer tr : toRemove) {
         loginHistory.remove(tr);
      }
   }
}
