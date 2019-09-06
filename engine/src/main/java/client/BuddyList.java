/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package client;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class BuddyList {
   private Map<Integer, BuddylistEntry> buddies = new LinkedHashMap<>();
   private int capacity;
   private Deque<CharacterNameAndId> pendingRequests = new LinkedList<>();

   public BuddyList(int capacity) {
      this.capacity = capacity;
   }

   public boolean contains(int characterId) {
      synchronized (buddies) {
         return buddies.containsKey(characterId);
      }
   }

   public boolean containsVisible(int characterId) {
      BuddylistEntry ble;
      synchronized (buddies) {
         ble = buddies.get(characterId);
      }

      if (ble == null) {
         return false;
      }
      return ble.isVisible();

   }

   public int getCapacity() {
      return capacity;
   }

   public void setCapacity(int capacity) {
      this.capacity = capacity;
   }

   public BuddylistEntry get(int characterId) {
      synchronized (buddies) {
         return buddies.get(characterId);
      }
   }

   public BuddylistEntry get(String characterName) {
      String lowerCaseName = characterName.toLowerCase();
      for (BuddylistEntry ble : getBuddies()) {
         if (ble.getName().toLowerCase().equals(lowerCaseName)) {
            return ble;
         }
      }

      return null;
   }

   public void put(BuddylistEntry entry) {
      synchronized (buddies) {
         buddies.put(entry.getCharacterId(), entry);
      }
   }

   public void remove(int characterId) {
      synchronized (buddies) {
         buddies.remove(characterId);
      }
   }

   public Collection<BuddylistEntry> getBuddies() {
      synchronized (buddies) {
         return Collections.unmodifiableCollection(buddies.values());
      }
   }

   public boolean isFull() {
      synchronized (buddies) {
         return buddies.size() >= capacity;
      }
   }

   public int[] getBuddyIds() {
      synchronized (buddies) {
         int[] buddyIds = new int[buddies.size()];
         int i = 0;
         for (BuddylistEntry ble : buddies.values()) {
            buddyIds[i++] = ble.getCharacterId();
         }
         return buddyIds;
      }
   }

   public void addRequest(CharacterNameAndId buddy) {
      pendingRequests.push(buddy);
   }

   public CharacterNameAndId pollPendingRequest() {
      return pendingRequests.pollLast();
   }

   public boolean hasPendingRequest() {
      return !pendingRequests.isEmpty();
   }

   public enum BuddyOperation {
      ADDED, DELETED
   }

   public enum BuddyAddResult {
      BUDDYLIST_FULL, ALREADY_ON_LIST, OK
   }
}