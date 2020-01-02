package net.server.coordinator.matchchecker;

import java.util.Set;

import client.MapleCharacter;

public interface AbstractMatchCheckerListener {
   void onMatchCreated(MapleCharacter leader, Set<MapleCharacter> nonLeaderMatchPlayers, String message);

   void onMatchAccepted(int leaderId, Set<MapleCharacter> matchPlayers, String message);

   void onMatchDeclined(int leaderId, Set<MapleCharacter> matchPlayers, String message);

   void onMatchDismissed(int leaderId, Set<MapleCharacter> matchPlayers, String message);
}
