package client.database.utility;

import client.database.data.PetData;
import entity.Pet;

public class PetTransformer implements SqlTransformer<PetData, Pet> {
   @Override
   public PetData transform(Pet resultSet) {
      return new PetData(
            resultSet.getName(),
            (byte) Math.min(resultSet.getLevel(), 30),
            Math.min(resultSet.getCloseness(), 30000),
            Math.min(resultSet.getFullness(), 100),
            resultSet.getSummoned() == 1,
            resultSet.getFlag());
   }
}
