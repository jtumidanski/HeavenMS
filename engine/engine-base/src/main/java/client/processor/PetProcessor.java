package client.processor;

import java.util.Optional;

import client.MapleCharacter;
import client.database.data.PetData;
import client.inventory.Item;
import client.inventory.MaplePet;
import client.inventory.PetDataFactory;
import client.inventory.manipulator.MapleCashIdGenerator;
import constants.MapleInventoryType;
import constants.PetFlag;
import constants.game.ExpTable;
import database.DatabaseConnection;
import database.administrator.PetAdministrator;
import database.provider.PetProvider;
import server.MapleItemInformationProvider;
import tools.I18nMessage;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.ServerNoticeType;
import tools.packet.foreigneffect.ShowPetLevelUp;
import tools.packet.pet.PetFoodResponse;
import tools.packet.showitemgaininchat.ShowOwnPetLevelUp;
import tools.packet.spawn.ShowPet;
import tools.packet.stat.EnableActions;
import tools.packet.stat.UpdatePetStats;

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
      Optional<PetData> result = DatabaseConnection.getInstance()
            .withConnectionResult(connection -> PetProvider.getInstance().loadPet(connection, petId));
      if (result.isPresent()) {
         PetData petData = result.get();
         return MaplePet.newBuilder(itemId)
               .setPosition(position)
               .setUniqueId(petId)
               .setName(petData.name())
               .setCloseness(petData.closeness())
               .setLevel(petData.level())
               .setFullness(petData.fullness())
               .setSummoned(petData.summoned())
               .setFlag(petData.flag().shortValue())
               .build();
      } else {
         return MaplePet.newBuilder(itemId)
               .setPosition(position)
               .setUniqueId(petId)
               .build();
      }
   }

   public void deleteFromDb(MapleCharacter owner, int petId) {
      DatabaseConnection.getInstance()
            .withConnection(connection -> PetAdministrator.getInstance().deleteAllPetData(connection, petId));
      owner.resetExcluded(petId);
      MapleCashIdGenerator.getInstance().freeCashId(petId);
   }

   public int createPet(int itemId) {
      return createPet(itemId, Byte.parseByte("1"), 0, 100);
   }

   public int createPet(int itemId, byte level, int closeness, int fullness) {
      return DatabaseConnection.getInstance().withConnectionResult(
            connection -> PetAdministrator.getInstance().createPet(connection, itemId, level, closeness, fullness)).orElse(-1);
   }

   public void saveToDb(MaplePet pet) {
      DatabaseConnection.getInstance().withConnection(connection -> PetAdministrator.getInstance()
            .updatePet(connection, pet.name(), pet.level(), pet.closeness(), pet.fullness(), pet.summoned(), pet.petFlag(),
                  pet.uniqueId()));
   }

   public void gainClosenessFullness(MapleCharacter owner, byte slot, int incCloseness, int incFullness) {
      boolean enjoyed;
      MaplePet pet = owner.getPet(slot);

      //will NOT increase pet's closeness if tried to feed pet with 100% fullness
      if (pet.fullness() < 100 || incFullness == 0) {   //incFullness == 0: command given
         int newFullness = Math.min(pet.fullness() + incFullness, 100);
         pet = owner.updateAndGetPet(slot, myPet -> myPet.feed(newFullness));

         if (incCloseness > 0 && pet.closeness() < 30000) {
            int newCloseness = Math.min(pet.closeness() + incCloseness, 30000);
            pet = owner.updateAndGetPet(slot, myPet -> myPet.gainCloseness(newCloseness));
            while (newCloseness >= ExpTable.getClosenessNeededForLevel(pet.level())) {
               pet = owner.updateAndGetPet(slot, myPet -> myPet.increaseLevel((byte) 1));
               PacketCreator.announce(owner, new ShowOwnPetLevelUp(slot));
               MasterBroadcaster.getInstance().sendToAllInMap(owner.getMap(), new ShowPetLevelUp(owner.getId(), slot));
            }
         }

         enjoyed = true;
      } else {
         int newCloseness = Math.max(pet.closeness() - 1, 0);
         pet = owner.updateAndGetPet(slot, myPet -> myPet.gainCloseness(newCloseness));
         if (pet.level() > 1 && newCloseness < ExpTable.getClosenessNeededForLevel(pet.level() - 1)) {
            pet = owner.updateAndGetPet(slot, myPet -> myPet.increaseLevel((byte) -1));
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

   public void addPetFlag(MapleCharacter owner, byte slot, PetFlag flag) {
      MaplePet pet = owner.updateAndGetPet(slot, myPet -> myPet.setPetFlag(myPet.petFlag() | flag.getValue()));
      PetProcessor.getInstance().saveToDb(pet);

      Item petItem = owner.getInventory(MapleInventoryType.CASH).getItem(pet.position());
      if (petItem != null) {
         owner.forceUpdateItem(petItem);
      }
   }

   public void removePetFlag(MapleCharacter owner, byte slot, PetFlag flag) {
      MaplePet pet = owner.updateAndGetPet(slot, myPet -> myPet.setPetFlag(myPet.petFlag() ^ flag.getValue()));
      PetProcessor.getInstance().saveToDb(pet);

      Item petItem = owner.getInventory(MapleInventoryType.CASH).getItem(pet.position());
      if (petItem != null) {
         owner.forceUpdateItem(petItem);
      }
   }

   public Pair<Integer, Boolean> canConsume(MaplePet pet, int itemId) {
      return MapleItemInformationProvider.getInstance().canPetConsume(pet.id(), itemId);
   }

   public void runFullnessSchedule(MapleCharacter character, byte slot) {
      MaplePet pet = character.getPet(slot);
      if (pet == null) {
         return;
      }

      int newFullness = pet.fullness() - PetDataFactory.getHunger(pet.id());
      if (newFullness <= 5) {
         pet = character.updateAndGetPet(slot, myPet -> myPet.degradeFullness(15));
         saveToDb(pet);
         unequipPet(character, slot, true);
         MessageBroadcaster.getInstance()
               .sendServerNotice(character, ServerNoticeType.LIGHT_BLUE, I18nMessage.from("PET_FULLNESS_LOW"));
      } else {
         pet = character.updateAndGetPet(slot, myPet -> myPet.degradeFullness(newFullness));
         PetProcessor.getInstance().saveToDb(pet);
         Item petItem = character.getInventory(MapleInventoryType.CASH).getItem(pet.position());
         if (petItem != null) {
            character.forceUpdateItem(petItem);
         }
      }
   }

   public void unequipPet(MapleCharacter character, byte slot, boolean shift_left) {
      unequipPet(character, slot, shift_left, false);
   }

   private void unequipPet(MapleCharacter character, byte slot, boolean shift_left, boolean hunger) {
      MaplePet pet = character.getPet(slot);

      if (pet != null) {
         pet = character.updateAndGetPet(slot, myPet -> myPet.isSummoned(false));
         PetProcessor.getInstance().saveToDb(pet);
      }

      MasterBroadcaster.getInstance()
            .sendToAllInMap(character.getMap(), new ShowPet(character, pet, true, hunger), true, character);
      character.removePet(pet, shift_left);
      character.commitExcludedItems();

      PacketCreator.announce(character.getClient(), new UpdatePetStats(character.getPets()));
      PacketCreator.announce(character.getClient(), new EnableActions());
   }
}