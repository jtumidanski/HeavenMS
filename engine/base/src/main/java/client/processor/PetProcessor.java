package client.processor;

import client.MapleCharacter;
import client.database.administrator.PetAdministrator;
import client.database.provider.PetProvider;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.PetFlag;
import client.inventory.manipulator.MapleCashIdGenerator;
import constants.ExpTable;
import server.MapleItemInformationProvider;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;
import tools.MasterBroadcaster;
import tools.Pair;

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

   public MaplePet loadFromDb(int itemid, short position, int petid) {
      MaplePet ret = new MaplePet(itemid, position, petid);
      DatabaseConnection.getInstance().withConnectionResult(connection -> PetProvider.getInstance().loadPet(connection, petid)).ifPresent(petData -> {
         ret.name_$eq(petData.name());
         ret.closeness_$eq(petData.closeness());
         ret.level_$eq(petData.level());
         ret.fullness_$eq(petData.fullness());
         ret.summoned_$eq(petData.summoned());
         ret.petFlag_$eq(petData.flag());
      });

      return ret;
   }

   public void deleteFromDb(MapleCharacter owner, int petid) {
      DatabaseConnection.getInstance().withConnection(connection -> PetAdministrator.getInstance().deleteAllPetData(connection, petid));
      owner.resetExcluded(petid);
      MapleCashIdGenerator.getInstance().freeCashId(petid);
   }

   public int createPet(int itemid) {
      return createPet(itemid, Byte.parseByte("1"), 0, 100);
   }

   public int createPet(int itemid, byte level, int closeness, int fullness) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> PetAdministrator.getInstance().createPet(connection, itemid, level, closeness, fullness)).orElse(-1);
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
               owner.getClient().announce(MaplePacketCreator.showOwnPetLevelUp(slot));
               MasterBroadcaster.getInstance().sendToAllInMap(owner.getMap(), character -> MaplePacketCreator.showPetLevelUp(owner, slot));
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

      MasterBroadcaster.getInstance().sendToAllInMap(owner.getMap(), character -> MaplePacketCreator.petFoodResponse(owner.getId(), slot, enjoyed, false));
      PetProcessor.getInstance().saveToDb(pet);

      Item petz = owner.getInventory(MapleInventoryType.CASH).getItem(pet.position());
      if (petz != null) {
         owner.forceUpdateItem(petz);
      }
   }

   public void addPetFlag(MaplePet pet, MapleCharacter owner, PetFlag flag) {
      pet.petFlag_$eq(pet.petFlag() | flag.getValue());
      PetProcessor.getInstance().saveToDb(pet);

      Item petz = owner.getInventory(MapleInventoryType.CASH).getItem(pet.position());
      if (petz != null) {
         owner.forceUpdateItem(petz);
      }
   }

   public void removePetFlag(MaplePet pet, MapleCharacter owner, PetFlag flag) {
      pet.petFlag_$eq(pet.petFlag() & 0xFFFFFFFF ^ flag.getValue());
      PetProcessor.getInstance().saveToDb(pet);

      Item petz = owner.getInventory(MapleInventoryType.CASH).getItem(pet.position());
      if (petz != null) {
         owner.forceUpdateItem(petz);
      }
   }

   public Pair<Integer, Boolean> canConsume(MaplePet pet, int itemId) {
      return MapleItemInformationProvider.getInstance().canPetConsume(pet.id(), itemId);
   }
}