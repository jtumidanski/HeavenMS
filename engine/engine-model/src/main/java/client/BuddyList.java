package client;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import tools.Pair;

public record BuddyList(Integer capacity, Map<Integer, BuddyListEntry> buddies,
                        Deque<CharacterNameAndId> pendingRequests) {
   public BuddyList(Integer capacity) {
      this(capacity, new HashMap<>(), new ArrayDeque<>());
   }

   public Boolean contains(Integer characterId) {
      return buddies.containsKey(characterId);
   }

   public Boolean containsVisible(Integer characterId) {
      BuddyListEntry buddyListEntry = buddies.get(characterId);
      if (buddyListEntry == null) {
         return false;
      }
      return buddyListEntry.visible();
   }

   public BuddyListEntry get(Integer characterId) {
      return buddies.get(characterId);
   }

   public BuddyListEntry get(String characterName) {
      return buddies.values().stream().filter(buddy -> buddy.name().equals(characterName)).findFirst().orElse(null);
   }

   public BuddyList put(BuddyListEntry entry) {
      Map<Integer, BuddyListEntry> newBuddies = new HashMap<>(buddies);
      newBuddies.put(entry.characterId(), entry);
      return new BuddyList(capacity, Collections.unmodifiableMap(newBuddies), pendingRequests);
   }

   public BuddyList remove(int characterId) {
      Map<Integer, BuddyListEntry> newBuddies = new HashMap<>(buddies);
      newBuddies.remove(characterId);
      return new BuddyList(capacity, Collections.unmodifiableMap(newBuddies), pendingRequests);
   }

   public Boolean isFull() {
      return buddies.size() >= capacity;
   }

   public int[] buddyIds() {
      return buddies.values().stream().map(BuddyListEntry::characterId).mapToInt(i -> i).toArray();
   }

   public BuddyList addRequest(CharacterNameAndId buddy) {
      Deque<CharacterNameAndId> newRequests = new ArrayDeque<>(pendingRequests);
      newRequests.add(buddy);
      return new BuddyList(capacity, buddies, newRequests);
   }

   public Optional<Pair<BuddyList, CharacterNameAndId>> pollPendingRequest() {
      if (!hasPendingRequest()) {
         return Optional.empty();
      } else {
         CharacterNameAndId request = pendingRequests.removeLast();
         return Optional.of(new Pair<>(new BuddyList(capacity, buddies, pendingRequests), request));
      }
   }

   public Boolean hasPendingRequest() {
      return !pendingRequests.isEmpty();
   }

   public BuddyList updateCapacity(int capacity) {
      return new BuddyList(capacity, buddies, pendingRequests);
   }
}
