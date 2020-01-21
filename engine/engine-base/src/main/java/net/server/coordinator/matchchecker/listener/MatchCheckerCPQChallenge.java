package net.server.coordinator.matchchecker.listener;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import client.MapleCharacter;
import net.server.coordinator.matchchecker.AbstractMatchCheckerListener;
import net.server.coordinator.matchchecker.MatchCheckerListenerRecipe;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import scripting.npc.NPCConversationManager;
import scripting.npc.NPCScriptManager;
import tools.I18nMessage;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public class MatchCheckerCPQChallenge implements MatchCheckerListenerRecipe {

   public static AbstractMatchCheckerListener loadListener() {
      return (new MatchCheckerCPQChallenge()).getListener();
   }

   private static MapleCharacter getChallenger(int leaderId, Set<MapleCharacter> matchPlayers) {
      MapleCharacter leader = null;
      for (MapleCharacter chr : matchPlayers) {
         if (chr.getId() == leaderId && chr.getClient() != null) {
            leader = chr;
            break;
         }
      }

      return leader;
   }

   @Override
   public AbstractMatchCheckerListener getListener() {
      return new AbstractMatchCheckerListener() {

         @Override
         public void onMatchCreated(MapleCharacter leader, Set<MapleCharacter> nonLeaderMatchPlayers, String message) {
            NPCConversationManager cm = leader.getClient().getCM();
            int npcId = cm.getNpc();

            MapleCharacter ldr = null;
            for (MapleCharacter chr : nonLeaderMatchPlayers) {
               ldr = chr;
               break;
            }

            List<MaplePartyCharacter> chrMembers = leader.getParty()
                  .map(MapleParty::getMembers).orElse(Collections.emptyList()).stream()
                  .filter(MaplePartyCharacter::isOnline)
                  .collect(Collectors.toList());

            if (message.contentEquals("cpq1")) {
               NPCScriptManager.getInstance().start("cpqchallenge", ldr.getClient(), npcId, chrMembers);
            } else {
               NPCScriptManager.getInstance().start("cpqchallenge2", ldr.getClient(), npcId, chrMembers);
            }

            cm.sendOk(I18nMessage.from("CPQ_CHALLENGE_ROOM_SENT"));
         }

         @Override
         public void onMatchAccepted(int leaderId, Set<MapleCharacter> matchPlayers, String message) {
            MapleCharacter chr = getChallenger(leaderId, matchPlayers);

            MapleCharacter ldr = null;
            for (MapleCharacter ch : matchPlayers) {
               if (ch != chr) {
                  ldr = ch;
                  break;
               }
            }

            if (message.contentEquals("cpq1")) {
               ldr.getClient().getCM().startCPQ(chr, ldr.getMapId() + 1);
            } else {
               ldr.getClient().getCM().startCPQ2(chr, ldr.getMapId() + 1);
            }

            if (ldr.getParty().isPresent() && chr.getParty().isPresent()) {
               ldr.getParty().get().setEnemy(chr.getParty().get());
               chr.getParty().get().setEnemy(ldr.getParty().get());
            }
            chr.setChallenged(false);
         }

         @Override
         public void onMatchDeclined(int leaderId, Set<MapleCharacter> matchPlayers, String message) {
            MapleCharacter chr = getChallenger(leaderId, matchPlayers);
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("CPQ_CHALLENGE_ROOM_DENIED"));
         }

         @Override
         public void onMatchDismissed(int leaderId, Set<MapleCharacter> matchPlayers, String message) {
         }
      };
   }
}
