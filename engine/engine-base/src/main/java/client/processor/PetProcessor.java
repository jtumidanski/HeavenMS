package client.processor;

import client.MapleCharacter;
import database.administrator.PetAdministrator;
import database.provider.PetProvider;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.PetFlag;
import client.inventory.manipulator.MapleCashIdGenerator;
import constants.game.ExpTable;
import server.MapleItemInformationProvider;
import database.DatabaseConnection;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.packet.foreigneffect.ShowPetLevelUp;
import tools.packet.pet.PetFoodResponse;
import tools.packet.showitemgaininchat.ShowOwnPetLevelUp;

public class PetProcessor {
   private static PetProcessor instance;

   public static PetProcessor getInstance() {
      if (instance == null) {
         instance = new PetProcessor();
      }
      return instance;
   }

   private PetProcessor() {
   }

   public MaplePet loadFromDb(int itemId, short position, int petId) {
      MaplePet ret = new MaplePet(itemId, position, petId);
      DatabaseConnection.getInstance().withConnectionResult(connection -> PetProvider.getInstance().loadPet(connection, petId)).ifPresent(petData -> {
         ret.name_$eq(petData.name());
         ret.closeness_$eq(petData.closeness());
         ret.level_$eq(petData.level());
         ret.fullness_$eq(petData.fullness());
         ret.summoned_$eq(petData.summoned());
         ret.petFlag_$eq(petData.flag());
      });

      return ret;
   }

   public void deleteFromDb(MapleCharacter owner, int petId) {
      DatabaseConnection.getInstance().withConnection(connection -> PetAdministrator.getInstance().deleteAllPetData(connection, petId));
      owner.resetExcluded(petId);
      MapleCashIdGenerator.getInstance().freeCashId(petId);
   }

   public int createPet(int itemId) {
      return createPet(itemId, Byte.parseByte("1"), 0, 100);
   }

   public int createPet(int itemId, byte level, int closeness, int fullness) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> PetAdministrator.getInstance().createPet(connection, itemId, level, closeness, fullness)).orElse(-1);
   }

   public void saveToDb(MaplePet pet) {
      DatabaseConnection.getInstance().withConnection(connection -> PetAdministrator.getInstance().updatePet(connection, pet.name(), pet.level(), pet.closeness(), pet.fullness(), pet.summoned(), pet.petFlag(), pet.uniqueId()));
   }

   public void gainClosenessFullness(MaplePet pet, MapleCharacter owner, int incCloseness, int incFullness, int type) {
      byte slot = owner.getPetIndex(pet);
      boolean enjoyed;

      //will NOT increase pet's closeness if tried to feed pet with 100% fullness
      if (pet.fullness() < 100 || incFullness == 0) {   //incFullness == 0: command given
         int newFullness = pet.fullness() + incFullness;
         if (newFullness > 100) {
            newFullness = 100;
         }
         pet.fullness_$eq(newFullness);

         if (incCloseness > 0 && pet.closeness() < 30000) {
            int newCloseness = pet.closeness() + incCloseness;
            if (newCloseness > 30000) {
               newCloseness = 30000;
            }

            pet.closeness_$eq(newCloseness);
            while (newCloseness >= ExpTable.getClosenessNeededForLevel(pet.level())) {
               pet.level_$eq((byte) (pet.level() + 1));
               PacketCreator.announce(owner, new ShowOwnPetLevelUp(slot));
               MasterBroadcaster.getInstance().sendToAllInMap(owner.getMap(), new ShowPetLevelUp(owner.getId(), slot));
            }
         }

         enjoyed = true;
      } else {
         int newCloseness = pet.closeness() - 1;
         if (newCloseness < 0) {
            newCloseness = 0;
         }

         pet.closeness_$eq(newCloseness);
         if (pet.level() > 1 && newCloseness < ExpTable.getClosenessNeededForLevel(pet.level() - 1)) {
            pet.level_$eq((byte) (pet.level() - 1));
         }

         enjoyed = false;
      }

      MasterBroadcaster.getInstance().sendToAllInMap(owner.getMap(), new PetFoodResponse(owner.getId(), slot, enjoyed, false));
      PetProcessor.getInstance().saveToDb(pet);

      Item petItem = owner.getInventory(MapleInventoryType.CASH).getItem(pet.position());
      if (petItem != null) {
         owner.forceUpdateItem(petItem);
      }
   }

   public void addPetFlag(MaplePet pet, MapleCharacter owner, PetFlag flag) {
      pet.petFlag_$eq(pet.petFlag() | flag.getValue());
      PetProcessor.getInstance().saveToDb(pet);

      Item petItem = owner.getInventory(MapleInventoryType.CASH).getItem(pet.position());
      if (petItem != null) {
         owner.forceUpdateItem(petItem);
      }
   }

   public void removePetFlag(MaplePet pet, MapleCharacter owner, PetFlag flag) {
      pet.petFlag_$eq(pet.petFlag() ^ flag.getValue());
      PetProcessor.getInstance().saveToDb(pet);

      Item petItem = owner.getInventory(MapleInventoryType.CASH).getItem(pet.position());
      if (petItem != null) {
         owner.forceUpdateItem(petItem);
      }
   }

   public Pair<Integer, Boolean> canConsume(MaplePet pet, int itemId) {
      return MapleItemInformationProvider.getInstance().canPetConsume(pet.id(), itemId);
   }
}