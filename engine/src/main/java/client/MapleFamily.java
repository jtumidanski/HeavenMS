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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import client.processor.MapleFamilyProcessor;

/**
 * @author Jay Estrella - Mr.Trash :3
 * @author Ubaware
 */
public class MapleFamily {

   private static final AtomicInteger familyIDCounter = new AtomicInteger();

   private final int id, world;
   private final Map<Integer, MapleFamilyEntry> members = new ConcurrentHashMap<>();
   private MapleFamilyEntry leader;
   private String name;
   private String preceptsMessage = "";
   private int totalGenerations;

   public MapleFamily(int id, int world) {
      int newId = id;
      if (id == -1) {
         // get next available family id
         while (MapleFamilyProcessor.getInstance().idInUse(newId = familyIDCounter.incrementAndGet())) {
         }
      }
      this.id = newId;
      this.world = world;
   }

   public int getID() {
      return id;
   }

   public int getWorld() {
      return world;
   }

   public void setLeader(MapleFamilyEntry leader) {
      this.leader = leader;
      setName(leader.getName());
   }

   public MapleFamilyEntry getLeader() {
      return leader;
   }

   private void setName(String name) {
      this.name = name;
   }

   public int getTotalMembers() {
      return members.size();
   }

   public int getTotalGenerations() {
      return totalGenerations;
   }

   public void setTotalGenerations(int generations) {
      this.totalGenerations = generations;
   }

   public String getName() {
      return this.name;
   }

   public void setMessage(String message) {
      this.preceptsMessage = message;
   }

   public String getMessage() {
      return preceptsMessage;
   }

   public void addEntry(MapleFamilyEntry entry) {
      members.put(entry.getChrId(), entry);
   }

   public void removeEntryBranch(MapleFamilyEntry root) {
      members.remove(root.getChrId());
      for (MapleFamilyEntry junior : root.getJuniors()) {
         if (junior != null) {
            removeEntryBranch(junior);
         }
      }
   }

   public void addEntryTree(MapleFamilyEntry root) {
      members.put(root.getChrId(), root);
      for (MapleFamilyEntry junior : root.getJuniors()) {
         if (junior != null) {
            addEntryTree(junior);
         }
      }
   }

   public MapleFamilyEntry getEntryByID(int cid) {
      return members.get(cid);
   }

   public void broadcast(byte[] packet) {
      broadcast(packet, -1);
   }

   public void broadcast(byte[] packet, int ignoreID) {
      for (MapleFamilyEntry entry : members.values()) {
         MapleCharacter chr = entry.getChr();
         if (chr != null) {
            if (chr.getId() == ignoreID) {
               continue;
            }
            chr.getClient().announce(packet);
         }
      }
   }

   public void resetDailyReps() {
      for (MapleFamilyEntry entry : members.values()) {
         entry.setTodaysRep(0);
         entry.setRepsToSenior(0);
         entry.resetEntitlementUsages();
      }
   }

   public Collection<MapleFamilyEntry> getMembers() {
      return members.values();
   }
}
