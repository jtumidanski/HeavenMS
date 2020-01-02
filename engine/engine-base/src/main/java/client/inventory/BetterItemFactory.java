package client.inventory;

import client.processor.PetProcessor;
import scala.Option;

public class BetterItemFactory {
   private static BetterItemFactory instance;

   public static BetterItemFactory getInstance() {
      if (instance == null) {
         instance = new BetterItemFactory();
      }
      return instance;
   }

   private BetterItemFactory() {
   }

   public Item create(int id, short position, short quantity, int petId) {
      int adjustedPetId = petId;
      MaplePet pet = null;

      if (petId > -1) {
         pet = PetProcessor.getInstance().loadFromDb(id, position, petId);
         if (pet == null) {
            adjustedPetId = -1;
         }
      }
      return new Item(id, position, quantity, Option.apply(pet), adjustedPetId);
   }
}