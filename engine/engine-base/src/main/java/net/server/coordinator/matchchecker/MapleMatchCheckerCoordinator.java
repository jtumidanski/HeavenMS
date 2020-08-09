package net.server.coordinator.matchchecker;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

import client.MapleCharacter;
import net.server.Server;
import net.server.coordinator.matchchecker.MatchCheckerListenerFactory.MatchCheckerType;
import net.server.world.World;

public class MapleMatchCheckerCoordinator {

   private final Map<Integer, MapleMatchCheckingElement> matchEntries = new HashMap<>();

   private final Set<Integer> pooledCharacterIds = new HashSet<>();
   private final Semaphore semaphorePool = new Semaphore(7);

   private void unpoolMatchPlayer(Integer characterId) {
      unpoolMatchPlayers(Collections.singleton(characterId));
   }

   private void unpoolMatchPlayers(Set<Integer> matchPlayers) {
      matchPlayers.forEach(pooledCharacterIds::remove);
   }

   private boolean poolMatchPlayer(Integer cid) {
      return poolMatchPlayers(Collections.singleton(cid));
   }

   private boolean poolMatchPlayers(Set<Integer> matchPlayers) {
      Set<Integer> pooledPlayers = new HashSet<>();

      for (Integer cid : matchPlayers) {
         if (!pooledCharacterIds.add(cid)) {
            unpoolMatchPlayers(pooledPlayers);
            return false;
         } else {
            pooledPlayers.add(cid);
         }
      }

      return true;
   }

   private boolean isMatchingAvailable(Set<Integer> matchPlayers) {
      return matchPlayers.stream().noneMatch(matchEntries::containsKey);
   }

   private void reenablePlayerMatching(Set<Integer> matchPlayers) {
      for (Integer cid : matchPlayers) {
         MapleMatchCheckingElement matchCheckingElement = matchEntries.get(cid);

         if (matchCheckingElement != null) {
            synchronized (matchCheckingElement) {
               if (!matchCheckingElement.isMatchActive()) {
                  matchEntries.remove(cid);
               }
            }
         }
      }
   }

   public int getMatchConfirmationLeaderId(int cid) {
      MapleMatchCheckingElement matchCheckingElement = matchEntries.get(cid);
      if (matchCheckingElement != null) {
         return matchCheckingElement.leaderCid;
      } else {
         return -1;
      }
   }

   public MatchCheckerType getMatchConfirmationType(int cid) {
      MapleMatchCheckingElement matchCheckingElement = matchEntries.get(cid);
      if (matchCheckingElement != null) {
         return matchCheckingElement.matchType;
      } else {
         return null;
      }
   }

   public boolean isMatchConfirmationActive(int cid) {
      MapleMatchCheckingElement matchCheckingElement = matchEntries.get(cid);
      if (matchCheckingElement != null) {
         return matchCheckingElement.active;
      } else {
         return false;
      }
   }

   private MapleMatchCheckingElement createMatchConfirmationInternal(MatchCheckerType matchType, int world, int leaderCid, AbstractMatchCheckerListener leaderListener, Set<Integer> players, String message) {
      MapleMatchCheckingElement matchCheckingElement = new MapleMatchCheckingElement(matchType, leaderCid, world, leaderListener, players, message);
      players.forEach(id -> matchEntries.put(id, matchCheckingElement));
      acceptMatchElement(matchCheckingElement, leaderCid);
      return matchCheckingElement;
   }

   public boolean createMatchConfirmation(MatchCheckerType matchType, int world, int leaderCid, Set<Integer> players, String message) {
      MapleMatchCheckingElement matchCheckingElement = null;
      try {
         semaphorePool.acquire();
         try {
            if (poolMatchPlayers(players)) {
               try {
                  if (isMatchingAvailable(players)) {
                     AbstractMatchCheckerListener leaderListener = matchType.getListener();
                     matchCheckingElement = createMatchConfirmationInternal(matchType, world, leaderCid, leaderListener, players, message);
                  } else {
                     reenablePlayerMatching(players);
                  }
               } finally {
                  unpoolMatchPlayers(players);
               }
            }
         } finally {
            semaphorePool.release();
         }
      } catch (InterruptedException ie) {
         ie.printStackTrace();
      }

      if (matchCheckingElement != null) {
         matchCheckingElement.dispatchMatchCreated();
         return true;
      } else {
         return false;
      }
   }

   private void disposeMatchElement(MapleMatchCheckingElement matchCheckingElement) {
      Set<Integer> matchPlayers = matchCheckingElement.getMatchPlayers();
      while (!poolMatchPlayers(matchPlayers)) {
         try {
            Thread.sleep(1000);
         } catch (InterruptedException ignored) {
         }
      }

      try {
         matchPlayers.forEach(matchEntries::remove);
      } finally {
         unpoolMatchPlayers(matchPlayers);
      }
   }

   private boolean acceptMatchElement(MapleMatchCheckingElement matchCheckingElement, int cid) {
      if (matchCheckingElement.acceptEntry(cid)) {
         unpoolMatchPlayer(cid);
         disposeMatchElement(matchCheckingElement);
         return true;
      }
      return false;
   }

   private void denyMatchElement(MapleMatchCheckingElement matchCheckingElement, int cid) {
      unpoolMatchPlayer(cid);
      disposeMatchElement(matchCheckingElement);
   }

   private void dismissMatchElement(MapleMatchCheckingElement matchCheckingElement, int cid) {
      matchCheckingElement.setMatchActive(false);

      unpoolMatchPlayer(cid);
      disposeMatchElement(matchCheckingElement);
   }

   public boolean answerMatchConfirmation(int cid, boolean accept) {
      MapleMatchCheckingElement matchCheckingElement = null;
      try {
         semaphorePool.acquire();
         try {
            while (matchEntries.containsKey(cid)) {
               if (poolMatchPlayer(cid)) {
                  try {
                     matchCheckingElement = matchEntries.get(cid);

                     if (matchCheckingElement != null) {
                        synchronized (matchCheckingElement) {
                           if (!matchCheckingElement.isMatchActive()) {
                              matchEntries.remove(cid);
                              matchCheckingElement = null;
                           } else {
                              if (accept) {
                                 if (!acceptMatchElement(matchCheckingElement, cid)) {
                                    matchCheckingElement = null;
                                 }
                                 break;
                              } else {
                                 denyMatchElement(matchCheckingElement, cid);
                                 matchEntries.remove(cid);
                              }
                           }
                        }
                     }
                  } finally {
                     unpoolMatchPlayer(cid);
                  }
               }
            }
         } finally {
            semaphorePool.release();
         }
      } catch (InterruptedException ie) {
         ie.printStackTrace();
      }

      if (matchCheckingElement != null) {
         matchCheckingElement.dispatchMatchResult(accept);
      }

      return false;
   }

   public boolean dismissMatchConfirmation(int cid) {
      MapleMatchCheckingElement matchCheckingElement = null;
      try {
         semaphorePool.acquire();
         try {
            while (matchEntries.containsKey(cid)) {
               if (poolMatchPlayer(cid)) {
                  try {
                     matchCheckingElement = matchEntries.get(cid);

                     if (matchCheckingElement != null) {
                        synchronized (matchCheckingElement) {
                           if (!matchCheckingElement.isMatchActive()) {
                              matchCheckingElement = null;
                           } else {
                              dismissMatchElement(matchCheckingElement, cid);
                           }
                        }
                     }
                  } finally {
                     unpoolMatchPlayer(cid);
                  }
               }
            }
         } finally {
            semaphorePool.release();
         }
      } catch (InterruptedException ie) {
         ie.printStackTrace();
      }

      if (matchCheckingElement != null) {
         matchCheckingElement.dispatchMatchDismissed();
         return true;
      } else {
         return false;
      }
   }

   private class MapleMatchCheckingElement {
      private int leaderCid;
      private int world;

      private MatchCheckerType matchType;
      private AbstractMatchCheckerListener listener;

      private Map<Integer, MapleMatchCheckingEntry> confirmingMembers = new HashMap<>();
      private int confirmCount;
      private boolean active = true;

      private String message;

      private MapleMatchCheckingElement(MatchCheckerType matchType, int leaderCid, int world, AbstractMatchCheckerListener leaderListener, Set<Integer> matchPlayers, String message) {
         this.leaderCid = leaderCid;
         this.world = world;
         this.listener = leaderListener;
         this.confirmCount = 0;
         this.message = message;
         this.matchType = matchType;
         matchPlayers.forEach(id -> confirmingMembers.put(id, new MapleMatchCheckingEntry(id)));
      }

      private boolean acceptEntry(int cid) {
         MapleMatchCheckingEntry mmcEntry = confirmingMembers.get(cid);
         if (mmcEntry != null) {
            if (mmcEntry.accept()) {
               this.confirmCount++;
               return this.confirmCount == this.confirmingMembers.size();
            }
         }

         return false;
      }

      private boolean isMatchActive() {
         return active;
      }

      private void setMatchActive(boolean a) {
         active = a;
      }

      private Set<Integer> getMatchPlayers() {
         return confirmingMembers.keySet();
      }

      private Set<Integer> getAcceptedMatchPlayers() {
         return confirmingMembers.entrySet().stream()
               .filter(entry -> entry.getValue().accepted())
               .map(Entry::getKey)
               .collect(Collectors.toSet());
      }

      private Set<MapleCharacter> getMatchCharacters() {
         Set<MapleCharacter> players = new HashSet<>();
         World world = Server.getInstance().getWorld(this.world);
         if (world != null) {
            players = getMatchPlayers().stream()
                  .map(id -> world.getPlayerStorage().getCharacterById(id))
                  .flatMap(Optional::stream)
                  .collect(Collectors.toSet());
         }
         return players;
      }

      private void dispatchMatchCreated() {
         Set<MapleCharacter> nonLeaderMatchPlayers = getMatchCharacters();
         MapleCharacter leader = nonLeaderMatchPlayers.stream()
               .filter(character -> character.getId() == leaderCid)
               .findFirst()
               .orElse(null);
         nonLeaderMatchPlayers.remove(leader);
         listener.onMatchCreated(leader, nonLeaderMatchPlayers, message);
      }

      private void dispatchMatchResult(boolean accept) {
         if (accept) {
            listener.onMatchAccepted(leaderCid, getMatchCharacters(), message);
         } else {
            listener.onMatchDeclined(leaderCid, getMatchCharacters(), message);
         }
      }

      private void dispatchMatchDismissed() {
         listener.onMatchDismissed(leaderCid, getMatchCharacters(), message);
      }
   }
}