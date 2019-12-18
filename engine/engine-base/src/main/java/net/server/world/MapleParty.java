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
package net.server.world;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import net.server.audit.LockCollector;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantLock;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import server.maps.MapleDoor;

public class MapleParty {

   private int id;
   private int worldId;
   private MapleParty enemy = null;
   private int leaderId;
   private List<MaplePartyCharacter> members = new LinkedList<>();
   private List<MaplePartyCharacter> pqMembers = null;

   private Map<Integer, Integer> histMembers = new HashMap<>();
   private int nextEntry = 0;

   private Map<Integer, MapleDoor> doors = new HashMap<>();

   private MonitoredReentrantLock lock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.PARTY, true);

   public MapleParty(int id, int worldId, MaplePartyCharacter chrfor) {
      this.leaderId = chrfor.getId();
      this.id = id;
      this.worldId = worldId;
   }

   public boolean containsMembers(MaplePartyCharacter member) {
      lock.lock();
      try {
         return members.contains(member);
      } finally {
         lock.unlock();
      }
   }

   public void addMember(MaplePartyCharacter member) {
      lock.lock();
      try {
         histMembers.put(member.getId(), nextEntry);
         nextEntry++;
         members.add(member);
      } finally {
         lock.unlock();
      }
   }

   public void removeMember(MaplePartyCharacter member) {
      lock.lock();
      try {
         histMembers.remove(member.getId());
         members.remove(member);
      } finally {
         lock.unlock();
      }
   }

   public void updateMember(MaplePartyCharacter member) {
      lock.lock();
      try {
         for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getId() == member.getId()) {
               members.set(i, member);
            }
         }
      } finally {
         lock.unlock();
      }
   }

   public Optional<MaplePartyCharacter> getMemberById(int id) {
      lock.lock();
      try {
         return members.parallelStream().filter(maplePartyCharacter -> maplePartyCharacter.getId() == id).findFirst();
      } finally {
         lock.unlock();
      }
   }

   public boolean isMember(int id) {
      return getMemberById(id).isPresent();
   }

   public Collection<MaplePartyCharacter> getMembers() {
      lock.lock();
      try {
         return new LinkedList<>(members);
      } finally {
         lock.unlock();
      }
   }

   public List<MaplePartyCharacter> getPartyMembers() {
      lock.lock();
      try {
         return new LinkedList<>(members);
      } finally {
         lock.unlock();
      }
   }

   public List<MaplePartyCharacter> getPartyMembersOnline() {
      lock.lock();
      try {
         return members.stream().filter(MaplePartyCharacter::isOnline).collect(Collectors.toList());
      } finally {
         lock.unlock();
      }
   }

   // used whenever entering PQs: will draw every party member that can attempt a target PQ while ingnoring those unfit.
   public Collection<MaplePartyCharacter> getEligibleMembers() {
      return Collections.unmodifiableList(pqMembers);
   }

   public void setEligibleMembers(List<MaplePartyCharacter> eliParty) {
      pqMembers = eliParty;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public int getLeaderId() {
      return leaderId;
   }

   public MaplePartyCharacter getLeader() {
      lock.lock();
      try {
         return members.parallelStream()
               .filter(maplePartyCharacter -> maplePartyCharacter.getId() == leaderId)
               .findFirst()
               .orElseThrow();
      } finally {
         lock.unlock();
      }
   }

   public void setLeader(MaplePartyCharacter victim) {
      this.leaderId = victim.getId();
   }

   public MapleParty getEnemy() {
      return enemy;
   }

   public void setEnemy(MapleParty enemy) {
      this.enemy = enemy;
   }

   public List<Integer> getMembersSortedByHistory() {
      List<Entry<Integer, Integer>> histList;

      lock.lock();
      try {
         histList = new LinkedList<>(histMembers.entrySet());
      } finally {
         lock.unlock();
      }

      histList.sort(Entry.comparingByValue());

      List<Integer> histSort = new LinkedList<>();
      for (Entry<Integer, Integer> e : histList) {
         histSort.add(e.getKey());
      }

      return histSort;
   }

   public byte getPartyDoor(int cid) {
      List<Integer> histList = getMembersSortedByHistory();
      byte slot = 0;
      for (Integer e : histList) {
         if (e == cid) {
            break;
         }
         slot++;
      }

      return slot;
   }

   public void addDoor(Integer owner, MapleDoor door) {
      lock.lock();
      try {
         this.doors.put(owner, door);
      } finally {
         lock.unlock();
      }
   }

   public void removeDoor(Integer owner) {
      lock.lock();
      try {
         this.doors.remove(owner);
      } finally {
         lock.unlock();
      }
   }

   public Map<Integer, MapleDoor> getDoors() {
      lock.lock();
      try {
         return Collections.unmodifiableMap(doors);
      } finally {
         lock.unlock();
      }
   }

   public Optional<MaplePartyCharacter> assignNewLeader() {
      lock.lock();
      try {
         return members.stream().filter(maplePartyCharacter -> maplePartyCharacter.getId() != leaderId).max(Comparator.comparingInt(MaplePartyCharacter::getLevel));
      } finally {
         lock.unlock();
      }
   }

   public void disposeLocks() {
      LockCollector.getInstance().registerDisposeAction(this::emptyLocks);
   }

   private void emptyLocks() {
      lock = lock.dispose();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + id;
      return result;
   }

   public MaplePartyCharacter getMemberByPos(int pos) {
      int i = 0;
      for (MaplePartyCharacter chr : members) {
         if (pos == i) {
            return chr;
         }
         i++;
      }
      return null;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final MapleParty other = (MapleParty) obj;
      return id == other.id;
   }

   public int getWorldId() {
      return worldId;
   }
}
