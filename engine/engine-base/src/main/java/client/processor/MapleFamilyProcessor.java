package client.processor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;

import client.MapleCharacter;
import client.MapleFamily;
import client.MapleFamilyEntry;
import client.database.data.CharacterData;
import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import config.YamlConfig;
import constants.MapleJob;
import database.DatabaseConnection;
import database.administrator.CharacterAdministrator;
import database.administrator.FamilyCharacterAdministrator;
import database.provider.CharacterProvider;
import database.provider.FamilyCharacterProvider;
import database.provider.FamilyEntitlementProvider;
import net.server.Server;
import net.server.world.World;
import tools.PacketCreator;
import tools.Pair;
import tools.packet.family.GetFamilyInfo;
import tools.packet.message.NotifyLevelUp;

public class MapleFamilyProcessor {
   private static MapleFamilyProcessor ourInstance = new MapleFamilyProcessor();

   public static MapleFamilyProcessor getInstance() {
      return ourInstance;
   }

   private MapleFamilyProcessor() {
   }

   public void loadAllFamilies() {
      List<Pair<Pair<Integer, Integer>, MapleFamilyEntry>> unmatchedJuniors = new ArrayList<>(200);
      DatabaseConnection.getInstance()
            .withConnection(connection -> FamilyCharacterProvider.getInstance().getAllFamilies(connection).forEach(familyData -> {
               int cid = familyData.characterId();
               String name = null;
               int level = -1;
               int jobID = -1;
               int worldId = -1;

               Optional<CharacterData> characterData =
                     CharacterProvider.getInstance().getById(connection, familyData.characterId());
               if (characterData.isPresent()) {
                  name = characterData.get().name();
                  level = characterData.get().level();
                  jobID = characterData.get().job();
                  worldId = characterData.get().world();
               }

               World world = Server.getInstance().getWorld(worldId);
               if (world == null) {
                  return;
               }
               MapleFamily family = world.getFamily(familyData.familyId());
               if (family == null) {
                  family = new MapleFamily(familyData.familyId(), worldId);
                  Server.getInstance().getWorld(worldId).addFamily(familyData.familyId(), family);
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
                     unmatchedJuniors.add(new Pair<>(new Pair<>(worldId, familyData.seniorId()), familyEntry));
                  }
               }
               familyEntry.setReputation(familyData.reputation());
               familyEntry.setTodaysRep(familyData.todaysReputation());
               familyEntry.setTotalReputation(familyData.totalReputation());
               familyEntry.setRepsToSenior(familyData.reputationToSenior());

               FamilyEntitlementProvider.getInstance().getIdsByCharacter(connection, familyData.characterId())
                     .forEach(familyEntry::setEntitlementUsed);
            }));

      for (Pair<Pair<Integer, Integer>, MapleFamilyEntry> unmatchedJunior : unmatchedJuniors) {
         int world = unmatchedJunior.getLeft().getLeft();
         int seniorId = unmatchedJunior.getLeft().getRight();
         MapleFamilyEntry junior = unmatchedJunior.getRight();
         MapleFamilyEntry senior =
               Server.getInstance().getWorld(world).getFamily(junior.getFamily().getID()).getEntryByID(seniorId);
         if (senior != null) {
            junior.setSenior(senior, false);
         } else {
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.FAMILY_ERROR,
                  "Missing senior for character " + junior.getName() + " in world " + world);
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
            PacketCreator.announce(chr, new GetFamilyInfo(entry));
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

   public void saveAllMembersRep(MapleFamily mapleFamily) {
      DatabaseConnection.getInstance().withConnection(entityManager -> {
         entityManager.getTransaction().begin();
         boolean success = true;
         for (MapleFamilyEntry entry : mapleFamily.getMembers()) {
            success = entry.saveReputation(entityManager);
            if (!success) {
               break;
            }
         }
         if (!success) {
            entityManager.getTransaction().rollback();
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.FAMILY_ERROR,
                  "Family rep auto save failed for family " + mapleFamily.getID() + " on " + Calendar.getInstance().getTime()
                        .toString() + ".");
         } else {
            entityManager.getTransaction().commit();
         }
         //reset repChanged after successful save
         for (MapleFamilyEntry entry : mapleFamily.getMembers()) {
            entry.savedSuccessfully();
         }
      });
   }

   public void setMessage(MapleFamily mapleFamily, String message, boolean save) {
      mapleFamily.setMessage(message);
      if (save) {
         DatabaseConnection.getInstance().withConnection(connection -> FamilyCharacterAdministrator.getInstance()
               .updatePrecepts(connection, mapleFamily.getLeader().getChrId(), message));
      }
   }

   public void saveCharactersFamilyReputation(EntityManager entityManager, MapleFamilyEntry familyEntry) {
      if (familyEntry != null) {
         if (familyEntry.saveReputation(entityManager)) {
            familyEntry.savedSuccessfully();
         }
         MapleFamilyEntry senior = familyEntry.getSenior();
         if (senior != null && senior.getChr() == null) { //only save for offline family members
            if (senior.saveReputation(entityManager)) {
               senior.savedSuccessfully();
            }
            senior = senior.getSenior(); //save one level up as well
            if (senior != null && senior.getChr() == null) {
               if (senior.saveReputation(entityManager)) {
                  senior.savedSuccessfully();
               }
            }
         }
      }
   }

   public void giveReputationToCharactersSenior(MapleFamilyEntry familyEntry, int level, String name) {
      if (familyEntry != null) {
         familyEntry.giveReputationToSenior(YamlConfig.config.server.FAMILY_REP_PER_LEVELUP, true);
         MapleFamilyEntry senior = familyEntry.getSenior();
         if (senior != null) { //only send the message to direct senior
            MapleCharacter seniorChr = senior.getChr();
            if (seniorChr != null) {
               PacketCreator.announce(seniorChr, new NotifyLevelUp(1, level, name));
            }
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
