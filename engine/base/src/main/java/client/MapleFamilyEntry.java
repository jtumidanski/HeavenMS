/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2018 RonanLana

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;

import client.database.administrator.CharacterAdministrator;
import client.database.administrator.FamilyCharacterAdministrator;
import client.database.administrator.FamilyEntitlementAdministrator;
import client.processor.MapleFamilyProcessor;
import net.server.Server;
import tools.DatabaseConnection;
import tools.FilePrinter;
import tools.PacketCreator;
import tools.Pair;
import tools.packet.family.FamilyGainReputation;
import tools.packet.family.GetFamilyInfo;

/**
 * @author Ubaware
 */

public class MapleFamilyEntry {
   private final int characterID;
   private volatile MapleFamily family;
   private volatile MapleCharacter character;

   private volatile MapleFamilyEntry senior;
   private final MapleFamilyEntry[] juniors = new MapleFamilyEntry[2];
   private final int[] entitlements = new int[11];
   private volatile int reputation, totalReputation;
   private volatile int todaysRep, repsToSenior; //both are daily values
   private volatile int totalJuniors, totalSeniors;

   private volatile int generation;

   private volatile boolean repChanged; //used to ignore saving unchanged rep values

   // cached values for offline players
   private String charName;
   private int level;
   private MapleJob job;

   public MapleFamilyEntry(MapleFamily family, int characterID, String charName, int level, MapleJob job) {
      this.family = family;
      this.characterID = characterID;
      this.charName = charName;
      this.level = level;
      this.job = job;
   }

   public MapleCharacter getChr() {
      return character;
   }

   public void setCharacter(MapleCharacter newCharacter) {
      if (newCharacter == null) {
         cacheOffline(newCharacter);
      } else {
         newCharacter.setFamilyEntry(this);
      }
      this.character = newCharacter;
   }

   private void cacheOffline(MapleCharacter chr) {
      if (chr != null) {
         charName = chr.getName();
         level = chr.getLevel();
         job = chr.getJob();
      }
   }

   public synchronized void join(MapleFamilyEntry senior) {
      if (senior == null || getSenior() != null) {
         return;
      }
      MapleFamily oldFamily = getFamily();
      MapleFamily newFamily = senior.getFamily();
      setSenior(senior, false);
      addSeniorCount(newFamily.getTotalGenerations(), newFamily); //count will be overwritten by doFullCount()
      newFamily.getLeader().doFullCount(); //easier than keeping track of numbers
      MapleFamilyProcessor.getInstance().setMessage(oldFamily, null, true);
      newFamily.addEntryTree(this);
      Server.getInstance().getWorld(oldFamily.getWorld()).removeFamily(oldFamily.getID());

      //db
      DatabaseConnection.getInstance().withConnection(entityManager -> {
         entityManager.getTransaction().begin();
         boolean success = updateDBChangeFamily(entityManager, getChrId(), newFamily.getID(), senior.getChrId());
         for (MapleFamilyEntry junior : juniors) { // better to duplicate this than the SQL code
            if (junior != null) {
               success = junior.updateNewFamilyDB(entityManager); // recursively updates juniors in db
               if (!success) {
                  break;
               }
            }
         }
         if (!success) {
            entityManager.getTransaction().rollback();
            FilePrinter.printError(FilePrinter.FAMILY_ERROR, "Could not absorb " + oldFamily.getName() + " family into " + newFamily.getName() + " family. (SQL ERROR)");
         } else {
            entityManager.getTransaction().commit();
         }
      });
   }

   public synchronized void fork() {
      MapleFamily oldFamily = getFamily();
      MapleFamilyEntry oldSenior = getSenior();
      family = new MapleFamily(-1, oldFamily.getWorld());
      Server.getInstance().getWorld(family.getWorld()).addFamily(family.getID(), family);
      setSenior(null, false);
      family.setLeader(this);
      addSeniorCount(-getTotalSeniors(), family);
      setTotalSeniors(0);
      if (oldSenior != null) {
         oldSenior.addJuniorCount(-getTotalJuniors());
         oldSenior.removeJunior(this);
         oldFamily.getLeader().doFullCount();
      }
      oldFamily.removeEntryBranch(this);
      family.addEntryTree(this);
      this.repsToSenior = 0;
      this.repChanged = true;
      MapleFamilyProcessor.getInstance().setMessage(family, "", true);
      doFullCount(); //to make sure all counts are correct
      // update db

      DatabaseConnection.getInstance().withConnection(entityManager -> {
         entityManager.getTransaction().begin();
         boolean success = updateDBChangeFamily(entityManager, getChrId(), getFamily().getID(), 0);

         for (MapleFamilyEntry junior : juniors) { // better to duplicate this than the SQL code
            if (junior != null) {
               success = junior.updateNewFamilyDB(entityManager); // recursively updates juniors in db
               if (!success) {
                  break;
               }
            }
         }
         if (!success) {
            entityManager.getTransaction().rollback();
            FilePrinter.printError(FilePrinter.FAMILY_ERROR, "Could not fork family with new leader " + getName() + ". (Old senior : " + oldSenior.getName() + ", leader :" + oldFamily.getLeader().getName() + ")");
         } else {
            entityManager.getTransaction().commit();
         }
      });
   }

   private synchronized boolean updateNewFamilyDB(EntityManager con) {
      if (!updateFamilyEntryDB(con, getChrId(), getFamily().getID())) {
         return false;
      }
      if (!updateCharacterFamilyDB(con, getChrId(), getFamily().getID(), true)) {
         return false;
      }

      for (MapleFamilyEntry junior : juniors) {
         if (junior != null) {
            if (!junior.updateNewFamilyDB(con)) {
               return false;
            }
         }
      }
      return true;
   }

   private static boolean updateFamilyEntryDB(EntityManager con, int cid, int familyid) {
      FamilyCharacterAdministrator.getInstance().setFamilyForCharacter(con, cid, familyid);
      return true;
   }

   private synchronized void addSeniorCount(int seniorCount, MapleFamily newFamily) { // traverses tree and subtracts seniors and updates family
      if (newFamily != null) {
         this.family = newFamily;
      }
      setTotalSeniors(getTotalSeniors() + seniorCount);
      this.generation += seniorCount;
      for (MapleFamilyEntry junior : juniors) {
         if (junior != null) {
            junior.addSeniorCount(seniorCount, newFamily);
         }
      }
   }

   private synchronized void addJuniorCount(int juniorCount) { // climbs tree and adds junior count
      setTotalJuniors(getTotalJuniors() + juniorCount);
      MapleFamilyEntry senior = getSenior();
      if (senior != null) {
         senior.addJuniorCount(juniorCount);
      }
   }

   public MapleFamily getFamily() {
      return family;
   }

   public int getChrId() {
      return characterID;
   }

   public String getName() {
      MapleCharacter chr = character;
      if (chr != null) {
         return chr.getName();
      } else {
         return charName;
      }
   }

   public int getLevel() {
      MapleCharacter chr = character;
      if (chr != null) {
         return chr.getLevel();
      } else {
         return level;
      }
   }

   public MapleJob getJob() {
      MapleCharacter chr = character;
      if (chr != null) {
         return chr.getJob();
      } else {
         return job;
      }
   }

   public int getReputation() {
      return reputation;
   }

   public int getTodaysRep() {
      return todaysRep;
   }

   public void setReputation(int reputation) {
      if (reputation != this.reputation) {
         this.repChanged = true;
      }
      this.reputation = reputation;
   }

   public void setTodaysRep(int today) {
      if (today != todaysRep) {
         this.repChanged = true;
      }
      this.todaysRep = today;
   }

   public int getRepsToSenior() {
      return repsToSenior;
   }

   public void setRepsToSenior(int reputation) {
      if (reputation != this.repsToSenior) {
         this.repChanged = true;
      }
      this.repsToSenior = reputation;
   }

   public void gainReputation(int gain, boolean countTowardsTotal) {
      gainReputation(gain, countTowardsTotal, this);
   }

   private void gainReputation(int gain, boolean countTowardsTotal, MapleFamilyEntry from) {
      if (gain != 0) {
         repChanged = true;
      }
      this.reputation += gain;
      this.todaysRep += gain;
      if (gain > 0 && countTowardsTotal) {
         this.totalReputation += gain;
      }
      MapleCharacter chr = getChr();
      if (chr != null) {
         PacketCreator.announce(chr, new FamilyGainReputation(gain, from != null ? from.getName() : ""));
      }
   }

   public void giveReputationToSenior(int gain, boolean includeSuperSenior) {
      int actualGain = gain;
      MapleFamilyEntry senior = getSenior();
      if (senior != null && senior.getLevel() < getLevel() && gain > 0) {
         actualGain /= 2; //don't halve negative values
      }
      if (senior != null) {
         senior.gainReputation(actualGain, true, this);
         if (actualGain > 0) {
            this.repsToSenior += actualGain;
            this.repChanged = true;
         }
         if (includeSuperSenior) {
            senior = senior.getSenior();
            if (senior != null) {
               senior.gainReputation(actualGain, true, this);
            }
         }
      }
   }

   public int getTotalReputation() {
      return totalReputation;
   }

   public void setTotalReputation(int totalReputation) {
      if (totalReputation != this.totalReputation) {
         this.repChanged = true;
      }
      this.totalReputation = totalReputation;
   }

   public MapleFamilyEntry getSenior() {
      return senior;
   }

   public synchronized boolean setSenior(MapleFamilyEntry senior, boolean save) {
      if (this.senior == senior) {
         return false;
      }
      MapleFamilyEntry oldSenior = this.senior;
      this.senior = senior;
      if (senior != null) {
         if (senior.addJunior(this)) {
            if (save) {
               updateDBChangeFamily(getChrId(), senior.getFamily().getID(), senior.getChrId());
            }
            if (this.repsToSenior != 0) {
               this.repChanged = true;
            }
            this.repsToSenior = 0;
            this.addSeniorCount(1, null);
            this.setTotalSeniors(senior.getTotalSeniors() + 1);
            return true;
         }
      } else {
         if (oldSenior != null) {
            oldSenior.removeJunior(this);
         }
      }
      return false;
   }

   private boolean updateDBChangeFamily(int cid, int familyid, int seniorid) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> updateDBChangeFamily(connection, cid, familyid, seniorid)).orElse(false);
   }

   private boolean updateDBChangeFamily(EntityManager con, int cid, int familyid, int seniorid) {
      FamilyCharacterAdministrator.getInstance().changeFamily(con, cid, familyid, seniorid);
      return updateCharacterFamilyDB(con, cid, familyid, false);
   }

   private boolean updateCharacterFamilyDB(EntityManager con, int charid, int familyid, boolean fork) {
      CharacterAdministrator.getInstance().setFamilyId(con, charid, familyid);
      return true;
   }

   public List<MapleFamilyEntry> getJuniors() {
      return List.of(juniors);
   }

   public MapleFamilyEntry getOtherJunior(MapleFamilyEntry junior) {
      if (juniors[0] == junior) {
         return juniors[1];
      } else if (juniors[1] == junior) {
         return juniors[0];
      }
      return null;
   }

   public int getJuniorCount() { //close enough to be relatively consistent to multiple threads (and the result is not vital)
      int juniorCount = 0;
      if (juniors[0] != null) {
         juniorCount++;
      }
      if (juniors[1] != null) {
         juniorCount++;
      }
      return juniorCount;
   }

   public synchronized boolean addJunior(MapleFamilyEntry newJunior) {
      for (int i = 0; i < juniors.length; i++) {
         if (juniors[i] == null) { // successfully add new junior to family
            juniors[i] = newJunior;
            addJuniorCount(1);
            getFamily().addEntry(newJunior);
            return true;
         }
      }
      return false;
   }

   public synchronized boolean isJunior(MapleFamilyEntry entry) { //require locking since result accuracy is vital
      if (juniors[0] == entry) {
         return true;
      } else if (juniors[1] == entry) {
         return true;
      }
      return false;
   }

   public synchronized boolean removeJunior(MapleFamilyEntry junior) {
      for (int i = 0; i < juniors.length; i++) {
         if (juniors[i] == junior) {
            juniors[i] = null;
            return true;
         }
      }
      return false;
   }

   public int getTotalSeniors() {
      return totalSeniors;
   }

   public void setTotalSeniors(int totalSeniors) {
      this.totalSeniors = totalSeniors;
   }

   public int getTotalJuniors() {
      return totalJuniors;
   }

   public void setTotalJuniors(int totalJuniors) {
      this.totalJuniors = totalJuniors;
   }

   public Collection<MapleCharacter> getSeniors(boolean includeSuperSenior) {
      Collection<MapleCharacter> result = new ArrayList<>();
      MapleFamilyEntry senior = getSenior();
      if (senior != null) {
         MapleCharacter seniorChr = senior.getChr();
         if (seniorChr != null) {
            result.add(seniorChr);
         }
         senior = senior.getSenior();
         if (includeSuperSenior && senior != null) {
            seniorChr = senior.getChr();
            if (seniorChr != null) {
               result.add(seniorChr);
            }
         }
      }
      return result;
   }

   public void updateSeniorFamilyInfo(boolean includeSuperSenior) {
      MapleFamilyEntry senior = getSenior();
      if (senior != null) {
         MapleCharacter seniorChr = senior.getChr();
         if (seniorChr != null) {
            PacketCreator.announce(seniorChr, new GetFamilyInfo(senior));
         }
         senior = senior.getSenior();
         if (includeSuperSenior && senior != null) {
            seniorChr = senior.getChr();
            if (seniorChr != null) {
               PacketCreator.announce(seniorChr, new GetFamilyInfo(senior));
            }
         }
      }
   }

   /**
    * Traverses entire family tree to update senior/junior counts. Call on leader.
    */
   public synchronized void doFullCount() {
      Pair<Integer, Integer> counts = this.traverseAndUpdateCounts(0);
      getFamily().setTotalGenerations(counts.getLeft() + 1);
   }

   private Pair<Integer, Integer> traverseAndUpdateCounts(int seniors) { // recursion probably limits family size, but it should handle a depth of a few thousand
      setTotalSeniors(seniors);
      this.generation = seniors;
      int juniorCount = 0;
      int highestGeneration = this.generation;
      for (MapleFamilyEntry entry : juniors) {
         if (entry != null) {
            Pair<Integer, Integer> counts = entry.traverseAndUpdateCounts(seniors + 1);
            juniorCount += counts.getRight(); //total juniors
            if (counts.getLeft() > highestGeneration) {
               highestGeneration = counts.getLeft();
            }
         }
      }
      setTotalJuniors(juniorCount);
      return new Pair<>(highestGeneration, juniorCount); //creating new objects to return is a bit inefficient, but cleaner than packing into a long
   }

   public boolean useEntitlement(MapleFamilyEntitlement entitlement) {
      int id = entitlement.ordinal();
      if (entitlements[id] >= 1) {
         return false;
      }
      DatabaseConnection.getInstance().withConnection(connection -> FamilyEntitlementAdministrator.getInstance().create(connection, id, getChrId()));
      entitlements[id]++;
      return true;
   }

   public boolean refundEntitlement(MapleFamilyEntitlement entitlement) {
      int id = entitlement.ordinal();
      DatabaseConnection.getInstance().withConnection(connection -> FamilyEntitlementAdministrator.getInstance().deleteByCharacterAndId(connection, getChrId(), id));
      entitlements[id] = 0;
      return true;
   }

   public boolean isEntitlementUsed(MapleFamilyEntitlement entitlement) {
      return entitlements[entitlement.ordinal()] >= 1;
   }

   public int getEntitlementUsageCount(MapleFamilyEntitlement entitlement) {
      return entitlements[entitlement.ordinal()];
   }

   public void setEntitlementUsed(int id) {
      entitlements[id]++;
   }

   public void resetEntitlementUsages() {
      for (MapleFamilyEntitlement entitlement : MapleFamilyEntitlement.values()) {
         entitlements[entitlement.ordinal()] = 0;
      }
   }

   public boolean saveReputation() {
      if (!repChanged) {
         return true;
      }
      DatabaseConnection.getInstance().withConnection(this::saveReputation);
      return true;
   }

   public boolean saveReputation(EntityManager entityManager) {
      if (!repChanged) {
         return true;
      }
      FamilyCharacterAdministrator.getInstance().updateMember(entityManager, getChrId(), getReputation(), getTodaysRep(), getTotalReputation(), getRepsToSenior());
      return true;
   }

   public void savedSuccessfully() {
      this.repChanged = false;
   }
}
