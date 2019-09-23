package client.processor;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import client.MapleCharacter;
import client.MapleFamily;
import client.MapleFamilyEntry;
import client.MapleJob;
import client.database.administrator.CharacterAdministrator;
import client.database.administrator.FamilyCharacterAdministrator;
import client.database.data.CharacterData;
import client.database.provider.CharacterProvider;
import client.database.provider.FamilyCharacterProvider;
import client.database.provider.FamilyEntitlementProvider;
import constants.ServerConstants;
import net.server.Server;
import net.server.world.World;
import tools.DatabaseConnection;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.Pair;

public class MapleFamilyProcessor {
   private static MapleFamilyProcessor ourInstance = new MapleFamilyProcessor();

   public static MapleFamilyProcessor getInstance() {
      return ourInstance;
   }

   private MapleFamilyProcessor() {
   }

   public void broadcast(byte[] packet) {
      // family currently not developed
   }

   public void loadAllFamilies() {
      List<Pair<Pair<Integer, Integer>, MapleFamilyEntry>> unmatchedJuniors = new ArrayList<Pair<Pair<Integer, Integer>, MapleFamilyEntry>>(200); // <<world, seniorid> familyEntry>
      DatabaseConnection.getInstance().withConnection(connection -> {
         FamilyCharacterProvider.getInstance().getAllFamilies(connection).forEach(familyData -> {
            int cid = familyData.characterId();
            String name = null;
            int level = -1;
            int jobID = -1;
            int world = -1;

            Optional<CharacterData> characterData = CharacterProvider.getInstance().getById(connection, familyData.characterId());
            if (characterData.isPresent()) {
               name = characterData.get().name();
               level = characterData.get().level();
               jobID = characterData.get().job();
               world = characterData.get().world();
            }

            World wserv = Server.getInstance().getWorld(world);
            if (wserv == null) {
               return;
            }
            MapleFamily family = wserv.getFamily(familyData.familyId());
            if (family == null) {
               family = new MapleFamily(familyData.familyId(), world);
               Server.getInstance().getWorld(world).addFamily(familyData.familyId(), family);
            }

            MapleFamilyEntry familyEntry = new MapleFamilyEntry(family, cid, name, level, MapleJob.getById(jobID));
            family.addEntry(familyEntry);
            if (familyData.seniorId() <= 0) {
               family.setLeader(familyEntry);
               setMessage(family, familyData.precepts(), false);
            }

            MapleFamilyEntry senior = family.getEntryByID(familyData.seniorId());
            if (senior != null) {
               familyEntry.setSenior(family.getEntryByID(familyData.seniorId()), false);
            } else {
               if (familyData.seniorId() > 0) {
                  unmatchedJuniors.add(new Pair<>(new Pair<>(world, familyData.seniorId()), familyEntry));
               }
            }
            familyEntry.setReputation(familyData.reputation());
            familyEntry.setTodaysRep(familyData.todaysReputation());
            familyEntry.setTotalReputation(familyData.totalReputation());
            familyEntry.setRepsToSenior(familyData.reputationToSenior());

            FamilyEntitlementProvider.getInstance().getIdsByCharacter(connection, familyData.characterId()).forEach(familyEntry::setEntitlementUsed);
         });
      });

      for (Pair<Pair<Integer, Integer>, MapleFamilyEntry> unmatchedJunior : unmatchedJuniors) {
         int world = unmatchedJunior.getLeft().getLeft();
         int seniorid = unmatchedJunior.getLeft().getRight();
         MapleFamilyEntry junior = unmatchedJunior.getRight();
         MapleFamilyEntry senior = Server.getInstance().getWorld(world).getFamily(junior.getFamily().getID()).getEntryByID(seniorid);
         if (senior != null) {
            junior.setSenior(senior, false);
         } else {
            FilePrinter.printError(FilePrinter.FAMILY_ERROR, "Missing senior for character " + junior.getName() + " in world " + world);
         }
      }

      for (World world : Server.getInstance().getWorlds()) {
         for (MapleFamily family : world.getFamilies()) {
            family.getLeader().doFullCount();
         }
      }
   }

   public void broadcastFamilyInfoUpdate(MapleFamily mapleFamily) {
      for (MapleFamilyEntry entry : mapleFamily.getMembers()) {
         MapleCharacter chr = entry.getChr();
         if (chr != null) {
            chr.getClient().announce(MaplePacketCreator.getFamilyInfo(entry));
         }
      }
   }

   public boolean idInUse(int id) {
      for (World world : Server.getInstance().getWorlds()) {
         if (world.getFamily(id) != null) {
            return true;
         }
      }
      return false;
   }

   public void saveAllMembersRep(MapleFamily mapleFamily) { //was used for autosave worker, but character autosave should be enough
      DatabaseConnection.getInstance().withExplicitCommitConnection(connection -> {
         connection.setAutoCommit(false);
         boolean success = true;
         for (MapleFamilyEntry entry : mapleFamily.getMembers()) {
            success = entry.saveReputation(connection);
            if (!success) {
               break;
            }
         }
         if (!success) {
            connection.rollback();
            FilePrinter.printError(FilePrinter.FAMILY_ERROR, "Family rep autosave failed for family " + mapleFamily.getID() + " on " + Calendar.getInstance().getTime().toString() + ".");
         } else {
            connection.commit();
         }
         connection.setAutoCommit(true);
         //reset repChanged after successful save
         for (MapleFamilyEntry entry : mapleFamily.getMembers()) {
            entry.savedSuccessfully();
         }
      });
   }

   public void setMessage(MapleFamily mapleFamily, String message, boolean save) {
      mapleFamily.setMessage(message);
      if (save) {
         DatabaseConnection.getInstance().withConnection(connection -> FamilyCharacterAdministrator.getInstance().updatePrecepts(connection, mapleFamily.getLeader().getChrId(), message));
      }
   }

   public void saveCharactersFamilyReputation(Connection connection, MapleFamilyEntry familyEntry) {
      if (familyEntry != null) {
         if (familyEntry.saveReputation(connection)) {
            familyEntry.savedSuccessfully();
         }
         MapleFamilyEntry senior = familyEntry.getSenior();
         if (senior != null && senior.getChr() == null) { //only save for offline family members
            if (senior.saveReputation(connection)) {
               senior.savedSuccessfully();
            }
            senior = senior.getSenior(); //save one level up as well
            if (senior != null && senior.getChr() == null) {
               if (senior.saveReputation(connection)) {
                  senior.savedSuccessfully();
               }
            }
         }

      }
   }

   public void giveReputationToCharactersSenior(MapleFamilyEntry familyEntry, int level, String name) {
      if(familyEntry != null) {
         familyEntry.giveReputationToSenior(ServerConstants.FAMILY_REP_PER_LEVELUP, true);
         MapleFamilyEntry senior = familyEntry.getSenior();
         if(senior != null) { //only send the message to direct senior
            MapleCharacter seniorChr = senior.getChr();
            if(seniorChr != null) seniorChr.announce(MaplePacketCreator.levelUpMessage(1, level, name));
         }
      }
   }

   public void insertNewFamilyRecord(int characterID, int familyID, int seniorID, boolean updateChar) {
      DatabaseConnection.getInstance().withConnection(connection -> {
         FamilyCharacterAdministrator.getInstance().create(connection, characterID, familyID, seniorID);
         if (updateChar) {
            CharacterAdministrator.getInstance().setFamilyId(connection, characterID, familyID);
         }
      });
   }
}
