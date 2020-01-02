package net.server.coordinator.matchchecker;

import net.server.coordinator.matchchecker.listener.MatchCheckerCPQChallenge;
import net.server.coordinator.matchchecker.listener.MatchCheckerGuildCreation;

public class MatchCheckerListenerFactory {

   public enum MatchCheckerType {
      GUILD_CREATION(MatchCheckerGuildCreation.loadListener()),
      CPQ_CHALLENGE(MatchCheckerCPQChallenge.loadListener());

      private final AbstractMatchCheckerListener listener;

      MatchCheckerType(AbstractMatchCheckerListener listener) {
         this.listener = listener;
      }

      public AbstractMatchCheckerListener getListener() {
         return this.listener;
      }
   }

}
